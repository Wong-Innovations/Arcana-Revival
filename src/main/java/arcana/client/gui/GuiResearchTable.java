package arcana.client.gui;

import arcana.Arcana;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchCategory;
import arcana.api.research.theorycraft.ITheorycraftAid;
import arcana.api.research.theorycraft.ResearchTableData;
import arcana.api.research.theorycraft.TheorycraftManager;
import arcana.client.gui.plugins.GuiImageButton;
import arcana.client.lib.UtilsFX;
import arcana.common.blockentities.crafting.BlockEntityResearchTable;
import arcana.common.container.ContainerResearchTable;
import arcana.common.lib.ModSounds;
import arcana.common.lib.network.PacketHandler;
import arcana.common.lib.network.misc.PacketStartTheoryToServer;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GuiResearchTable extends AbstractContainerScreen<ContainerResearchTable> {
    private BlockEntityResearchTable table;
    Player player;
    ResourceLocation txBackground = new ResourceLocation(Arcana.MODID, "textures/gui/gui_research_table.png");
    ResourceLocation txBase = new ResourceLocation(Arcana.MODID, "textures/gui/gui_base.png");
    ResourceLocation txPaper = new ResourceLocation(Arcana.MODID, "textures/gui/paper.png");
    ResourceLocation txPaperGilded = new ResourceLocation(Arcana.MODID, "textures/gui/papergilded.png");
    ResourceLocation txQuestion = new ResourceLocation(Arcana.MODID, "textures/aspects/_unknown.png");
    ResearchTableData.CardChoice lastDraw;
    float[] cardHover;
    float[] cardZoomOut;
    float[] cardZoomIn;
    boolean[] cardActive;
    boolean cardSelected;
    public HashMap<String, Integer> tempCatTotals;
    long nexCatCheck;
    long nextCheck;
    int dummyInspirationStart;
    Set<String> currentAids;
    Set<String> selectedAids;
    GuiImageButton buttonCreate;
    GuiImageButton buttonComplete;
    GuiImageButton buttonScrap;
    public ArrayList<ResearchTableData.CardChoice> cardChoices;

    public GuiResearchTable(ContainerResearchTable pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.nextCheck = 0;
        this.dummyInspirationStart = 0;
        this.currentAids = new HashSet<>();
        this.selectedAids = new HashSet<>();
        this.buttonCreate = new GuiImageButton(leftPos + 128, topPos + 22, 49, 11, Component.translatable("button.create.theory"), Supplier::get, txBase, 37, 66, 51, 13, 0x88FFAA, (b) -> {
            playButtonClick();
            PacketHandler.INSTANCE.sendToServer(new PacketStartTheoryToServer(table.getBlockPos(), selectedAids));
        });
        this.buttonComplete = new GuiImageButton(leftPos + 191, topPos + 96, 49, 11, Component.translatable("button.complete.theory"), Supplier::get, txBase, 37, 66, 51, 13, 0x88FFAA, (b) -> {
            playButtonClick();
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 7);
            tempCatTotals.clear();
            lastDraw = null;
        });
        this.buttonScrap = new GuiImageButton(leftPos + 128, topPos + 168, 49, 11, Component.translatable("button.scrap.theory"), Supplier::get, txBase, 37, 66, 51, 13, 0xFF2222, (b) -> {
            playButtonClick();
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 9);
            tempCatTotals.clear();
            lastDraw = null;
            table.data = null;
            cardChoices.clear();
        });
        this.cardHover = new float[]{0.0f, 0.0f, 0.0f};
        this.cardZoomOut = new float[]{0.0f, 0.0f, 0.0f};
        this.cardZoomIn = new float[]{0.0f, 0.0f, 0.0f};
        this.cardActive = new boolean[]{true, true, true};
        this.cardSelected = false;
        this.tempCatTotals = new HashMap<>();
        this.cardChoices = new ArrayList<>();
        this.table = pMenu.blockEntity;
        this.imageWidth = 255;
        this.imageHeight = 255;
        this.player = pPlayerInventory.player;
        if (table.data != null) {
            for (String cat : this.table.data.categoryTotals.keySet()) {
                this.tempCatTotals.put(cat, this.table.data.categoryTotals.get(cat));
            }
            syncFromTableChoices();
            this.lastDraw = this.table.data.lastDraw;
        }
    }

    private void syncFromTableChoices() {
        cardChoices.clear();
        for (ResearchTableData.CardChoice cc : table.data.cardChoices) {
            cardChoices.add(cc);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        PoseStack poseStack = pGuiGraphics.pose();
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        int xx = leftPos;
        int yy = topPos;
        if (table.data == null) {
            if (!currentAids.isEmpty()) {
                int side = Math.min(currentAids.size(), 6);
                int c = 0;
                int r = 0;
                poseStack.pushPose();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.2f);
                RenderSystem.setShaderTexture(0, txBase);
                for (String key : currentAids) {
                    ITheorycraftAid mutator = TheorycraftManager.aids.get(key);
                    if (mutator == null) {
                        continue;
                    }
                    int x = xx + 128 + 20 * c - side * 10;
                    int y = yy + 85 + 35 * r;
                    if (isHovering(x - xx, y - yy, 16, 16, pMouseX, pMouseY) && !selectedAids.contains(key)) {
                        pGuiGraphics.blit(txBase, x, y, 0, 96, 16, 16);
                    }
                    if (++c < side) {
                        continue;
                    }
                    ++r;
                    c = 0;
                }
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                poseStack.popPose();
            }
        } else {
            int sx = 128;
            int cw = 110;
            int sz = cardChoices.size();
            int a = 0;
            if (!cardSelected) {
                for (ResearchTableData.CardChoice cardChoice : cardChoices) {
                    if (cardZoomOut[a] >= 1.0f) {
                        float dx = (float) (55 + sx - 55 * sz + cw * a - 65);
                        float fx = 65.0f + dx * cardZoomOut[a];
                        float qx = 191.0f - fx;
                        if (cardActive[a]) {
                            fx += qx * cardZoomIn[a];
                        }
                        drawSheetOverlay(pGuiGraphics, fx, 100.0, cardChoice, pMouseX, pMouseY);
                        ++a;
                    }
                }
            }
            int qq = 0;
            table.getItem(0);
            if (table.getItem(0).isEmpty() || table.getItem(0).getDamageValue() == table.getItem(0).getMaxDamage()) {
                sx = Math.max(font.width(I18n.get("block.researchtable.noink.0")), font.width(I18n.get("block.researchtable.noink.1"))) / 2;
                UtilsFX.drawCustomTooltip(pGuiGraphics, this, font, Arrays.asList(I18n.get("block.researchtable.noink.0"), I18n.get("block.researchtable.noink.1")), xx - sx + 116, yy + 60 + qq, 11, true);
                qq += 40;
            }
            table.getItem(1);
            if (table.getItem(1).isEmpty()) {
                sx = font.width(I18n.get("block.researchtable.nopaper.0")) / 2;
                UtilsFX.drawCustomTooltip(pGuiGraphics, this, font, List.of(I18n.get("block.researchtable.nopaper.0")), xx - sx + 116, yy + 60 + qq, 11, true);
            }
        }
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int mx, int my) {
        PoseStack poseStack = pGuiGraphics.pose();
        checkButtons();
        int xx = leftPos;
        int yy = topPos;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, txBackground);
        pGuiGraphics.blit(txBackground, xx, yy, 0, 0, 255, 255);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (table.data == null) {
            if (nextCheck < player.tickCount) {
                currentAids = table.checkSurroundingAids();
                dummyInspirationStart = ResearchTableData.getAvailableInspiration(player);
                nextCheck = player.tickCount + 100;
            }
            RenderSystem.setShaderTexture(0, txBase);
            poseStack.pushPose();
            poseStack.translate(xx + 128 - dummyInspirationStart * 5, yy + 55, 0.0);
            poseStack.scale(0.5f, 0.5f, 0.0f);
            for (int a = 0; a < dummyInspirationStart; ++a) {
                pGuiGraphics.blit(txBase, 20 * a, 0, (dummyInspirationStart - selectedAids.size() <= a) ? 48 : 32, 96, 16, 16);
            }
            poseStack.popPose();
            if (!currentAids.isEmpty()) {
                int side = Math.min(currentAids.size(), 6);
                int c = 0;
                int r = 0;
                for (String key : currentAids) {
                    ITheorycraftAid mutator = TheorycraftManager.aids.get(key);
                    if (mutator == null) {
                        continue;
                    }
                    int x = xx + 128 + 20 * c - side * 10;
                    int y = yy + 85 + 35 * r;
                    if (selectedAids.contains(key)) {
                        RenderSystem.setShaderTexture(0, txBase);
                        pGuiGraphics.blit(txBase, x, y, 0, 96, 16, 16);
                    }
                    if (mutator.getAidObject() instanceof ItemStack || mutator.getAidObject() instanceof Block) {
                        poseStack.pushPose();
                        ItemStack s = (ItemStack) ((mutator.getAidObject() instanceof ItemStack) ? mutator.getAidObject() : new ItemStack((Block) mutator.getAidObject()));
                        pGuiGraphics.renderItem(s, x, y);
                        RenderSystem.enableDepthTest();
                        poseStack.popPose();
                    }
                    if (++c < side) {
                        continue;
                    }
                    ++r;
                    c = 0;
                }
            }
        } else {
            checkCards();
            RenderSystem.setShaderTexture(0, txBase);

            poseStack.pushPose();
            poseStack.translate(xx + 15, yy + 150, 0.0);
            if (table.data != null) {
                for (int a = 0; a < table.data.bonusDraws; ++a) {
                    pGuiGraphics.blit(txBase, a * 2, a, 64, 96, 16, 16);
                }
            }
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(xx + 128 - table.data.inspirationStart * 5, yy + 16, 0.0);
            poseStack.scale(0.5f, 0.5f, 0.0f);
            for (int a = 0; a < table.data.inspirationStart; ++a) {
                pGuiGraphics.blit(txBase, 20 * a, 0, (table.data.inspiration <= a) ? 48 : 32, 96, 16, 16);
            }
            poseStack.popPose();

            int sheets = 0;
            table.getItem(1);
            sheets = 1 + table.getItem(1).getCount() / 4;
            Random r2 = new Random(55L);
            if (sheets > 0 && !table.data.isComplete()) {
                for (int a2 = 0; a2 < sheets; ++a2) {
                    drawSheet(pGuiGraphics, xx + 65, yy + 100, 6.0f, r2, 1.0f, 1.0f, null);
                }
                boolean highlight = false;
                int var7 = mx - (25 + xx);
                int var8 = my - (55 + yy);
                if (cardChoices.isEmpty() && var7 >= 0 && var8 >= 0 && var7 < 75 && var8 < 90) {
                    highlight = true;
                }
                poseStack.pushPose();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, highlight ? 1.0f : 0.5f);
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, txQuestion);
                poseStack.translate(xx + 65, yy + 100, 0.0);
                poseStack.scale((float) (highlight ? 1.75 : 1.5), (float) (highlight ? 1.75 : 1.5), 0.0F);
                UtilsFX.drawTexturedQuadFull(poseStack, -8.0f, -8.0f, 0.0);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                poseStack.popPose();
            }

            for (Long seed : table.data.savedCards) {
                r2 = new Random(seed);
                drawSheet(pGuiGraphics, xx + 191, yy + 100, 6.0f, r2, 1.0f, 1.0f, null);
            }

            if (lastDraw != null) {
                r2 = new Random(lastDraw.card.getSeed());
                drawSheet(pGuiGraphics, xx + 191, yy + 100, 6.0f, r2, 1.0f, 1.0f, lastDraw);
            }

            ArrayList<String> sparkle = new ArrayList<String>();
            if (nexCatCheck < player.tickCount) {
                for (String cat : ResearchCategories.researchCategories.keySet()) {
                    int t0 = 0;
                    if (table.data.categoryTotals.containsKey(cat)) {
                        t0 = table.data.categoryTotals.get(cat);
                    }
                    int t2 = 0;
                    if (tempCatTotals.containsKey(cat)) {
                        t2 = tempCatTotals.get(cat);
                    }
                    if (t0 == 0 && t2 == 0) {
                        tempCatTotals.remove(cat);
                    } else {
                        if (t2 > t0) {
                            --t2;
                        }
                        if (t2 < t0) {
                            ++t2;
                            sparkle.add(cat);
                        }
                        tempCatTotals.put(cat, t2);
                    }
                }
                nexCatCheck = player.tickCount + 1;
            }
            HashMap<String, Integer> unsortedMap = new HashMap<String, Integer>();
            String hf = null;
            int lf = 0;
            for (String cat2 : tempCatTotals.keySet()) {
                int cf = tempCatTotals.get(cat2);
                if (cf == 0) {
                    continue;
                }
                if (cf > lf) {
                    lf = cf;
                    hf = cat2;
                }
                unsortedMap.put(cat2, cf);
            }
            if (hf != null) {
                unsortedMap.put(hf, unsortedMap.get(hf));
            }
            Comparator<Map.Entry<String, Integer>> valueComparator = (e1, e2) -> e2.getValue().compareTo(e1.getValue());
            Map<String, Integer> sortedMap = unsortedMap.entrySet().stream().sorted(valueComparator).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            RenderSystem.enableBlend();
            int i = 0;
            for (String field : sortedMap.keySet()) {
                poseStack.pushPose();
                poseStack.translate((float) (xx + 253), (float) (yy + 16 + i * 18 + ((i > 0) ? 4 : 0)), 0.0f);
                poseStack.scale(0.0625f, 0.0625f, 0.0625f);
                RenderSystem.enableBlend();
                RenderSystem.setShaderTexture(0, ResearchCategories.getResearchCategory(field).icon);
                pGuiGraphics.blit(ResearchCategories.getResearchCategory(field).icon, 0, 0, 0, 0, 255, 255);
                poseStack.popPose();
                poseStack.translate(0.0f, 0.0f, 5.0f);
                String s2 = sortedMap.get(field) + "%";
                if (i > table.data.penaltyStart) {
                    int q = sortedMap.get(field) / 3;
                    s2 = s2 + " (-" + q + ")";
                }
                pGuiGraphics.drawString(font, s2, (float) (xx + 276), (float) (yy + 20 + i * 18 + ((i > table.data.penaltyStart) ? 4 : 0)), table.data.categoriesBlocked.contains(field) ? 6316128 : ((i <= table.data.penaltyStart) ? 57536 : 16777215), true);
                if (sparkle.contains(field)) {
                    for (int q = 0; q < 2; ++q) {
                        float rr = player.getRandom().nextIntBetweenInclusive(255, 255) / 255.0f;
                        float gg = player.getRandom().nextIntBetweenInclusive(189, 255) / 255.0f;
                        float bb = player.getRandom().nextIntBetweenInclusive(64, 255) / 255.0f;
//                        FXDispatcher.INSTANCE.drawSimpleSparkleGui(player.getRandom(), xx + 276 + player.getRandom().nextFloat() * font.width(s2), yy + 20 + player.getRandom().nextFloat() * 8.0f + i * 18 + ((i > table.data.penaltyStart) ? 4 : 0), player.level.random.nextGaussian() * 0.5, player.level.random.nextGaussian() * 0.5, 24.0f, rr, gg, bb, 0, 0.9f, -1.0f);
                    }
                }
                int var9 = mx - (xx + 256);
                int var10 = my - (yy + 16 + i * 18 + ((i > table.data.penaltyStart) ? 4 : 0));
                if (var9 >= 0 && var10 >= 0 && var9 < 16 && var10 < 16) {
                    poseStack.pushPose();
                    UtilsFX.drawCustomTooltip(pGuiGraphics, this, font, List.of(ResearchCategories.getCategoryName(field)), mx + 8, my + 8, 11);
                    poseStack.popPose();
                }
                ++i;
            }
            int sx = 128;
            int cw = 110;
            int sz = cardChoices.size();
            int a3 = 0;
            for (ResearchTableData.CardChoice cardChoice : cardChoices) {
                r2 = new Random(cardChoice.card.getSeed());
                int var11 = mx - (5 + sx - 55 * sz + xx + cw * a3);
                int var12 = my - (100 + yy - 60);
                if (cardZoomOut[a3] >= 0.95 && !cardSelected) {
                    if (var11 >= 0 && var12 >= 0 && var11 < 100 && var12 < 120) {
                        float[] cardHover = this.cardHover;
                        cardHover[a3] += Math.max((0.25f - this.cardHover[a3]) / 3.0f * pPartialTick, 0.0025f);
                    } else {
                        float[] cardHover2 = cardHover;
                        cardHover2[a3] -= 0.1f * pPartialTick;
                    }
                }
                if (a3 == sz - 1 || cardZoomOut[a3 + 1] > 0.6) {
                    float f = cardZoomOut[a3];
                    float[] cardZoomOut = this.cardZoomOut;
                    cardZoomOut[a3] += Math.max((1.0f - this.cardZoomOut[a3]) / 5.0f * pPartialTick, 0.0025f);
                    if (this.cardZoomOut[a3] > 0.0f && f == 0.0f) {
                        playButtonPageFlip();
                    }
                }
                float prevZoomIn = cardZoomIn[a3];
                if (cardSelected) {
                    float[] cardZoomIn = this.cardZoomIn;
                    cardZoomIn[a3] += (float) (cardActive[a3] ? Math.max((1.0f - this.cardZoomIn[a3]) / 3.0f * pPartialTick, 0.0025) : (0.3f * pPartialTick));
                    cardHover[a3] = 1.0f - this.cardZoomIn[a3];
                }
                cardZoomIn[a3] = Mth.clamp(cardZoomIn[a3], 0.0f, 1.0f);
                cardHover[a3] = Mth.clamp(cardHover[a3], 0.0f, 0.25f);
                cardZoomOut[a3] = Mth.clamp(cardZoomOut[a3], 0.0f, 1.0f);
                float dx = (float) (55 + sx - 55 * sz + xx + cw * a3 - (xx + 65));
                float fx = xx + 65 + dx * cardZoomOut[a3];
                float qx = xx + 191 - fx;
                if (cardActive[a3]) {
                    fx += qx * cardZoomIn[a3];
                }
                drawSheet(pGuiGraphics, fx, yy + 100, 6.0f + cardZoomOut[a3] * 2.0f - cardZoomIn[a3] * 2.0f + cardHover[a3], r2, cardActive[a3] ? 1.0f : (1.0f - cardZoomIn[a3]), Math.max(1.0f - cardZoomOut[a3], cardZoomIn[a3]), cardChoice);
                if (cardSelected && cardActive[a3] && cardZoomIn[a3] >= 1.0f && prevZoomIn < 1.0f) {
                    playButtonWrite();
                    cardChoices.clear();
                    cardSelected = false;
                    lastDraw = table.data.lastDraw;
                    break;
                }
                ++a3;
            }
        }
    }

    private void drawSheet(GuiGraphics pGuiGraphics, double x, double y, float scale, Random r, float alpha, float tilt, ResearchTableData.CardChoice cardChoice) {
        PoseStack poseStack = pGuiGraphics.pose();
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        poseStack.translate(x + r.nextGaussian(), y + r.nextGaussian(), 0.0);
        poseStack.scale(scale, scale, 0.0f);
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) (r.nextGaussian() * tilt)));

        poseStack.pushPose();
        ResourceLocation tex;
        if (cardChoice != null && cardChoice.fromAid) {
            RenderSystem.setShaderTexture(0, txPaperGilded);
            tex = txPaperGilded;
        } else {
            RenderSystem.setShaderTexture(0, txPaper);
            tex = txPaper;
        }
        if (r.nextBoolean()) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        }
        if (r.nextBoolean()) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
        }
        RenderSystem.disableCull();
        pGuiGraphics.blit(tex, -8, -8, 0, 0, 16, 16, 16, 16);
        RenderSystem.enableCull();
        poseStack.popPose();

        if (cardChoice != null && alpha == 1.0f) {
            if (cardChoice.card.getResearchCategory() != null) {
                ResearchCategory rc = ResearchCategories.getResearchCategory(cardChoice.card.getResearchCategory());
                if (rc != null) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha / 6.0f);
                    poseStack.pushPose();
                    poseStack.scale(0.5f, 0.5f, 0.0f);
                    RenderSystem.setShaderTexture(0, rc.icon);
                    UtilsFX.drawTexturedQuadFull(poseStack, -8.0f, -8.0f, 0.0);
                    poseStack.popPose();
                }
            }

            poseStack.pushPose();
            poseStack.scale(0.0625f, 0.0625f, 0.0f);
            RenderSystem.setShaderColor(0.0f, 0.0f, 0.0f, alpha);
            String name = ChatFormatting.BOLD + cardChoice.card.getLocalizedName() + ChatFormatting.RESET;
            int sz = font.width(name);

            pGuiGraphics.drawString(font, name, -sz / 2, -65, 0);
            drawSplitString(pGuiGraphics, font, Component.literal(cardChoice.card.getLocalizedText()), -70, -48, 140, 0);
            poseStack.popPose();

            poseStack.pushPose();
            RenderSystem.setShaderTexture(0, txBase);
            RenderSystem.enableBlend();
            poseStack.scale(0.0625f, 0.0625f, 0.0f);
            int cc = cardChoice.card.getInspirationCost();
            boolean add = false;
            if (cc < 0) {
                add = true;
                cc = Math.abs(cc) + 1;
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
            for (int a = 0; a < cc; ++a) {
                if (a == 0 && add) {
                    pGuiGraphics.blit(txBase, -10 * cc + 20 * a, -95, 48, 0, 16, 16);
                } else {
                    pGuiGraphics.blit(txBase, -10 * cc + 20 * a, -95, 32, 96, 16, 16);
                }
            }
            poseStack.popPose();

            if (cardChoice.card.getRequiredItems() != null) {
                ItemStack[] items = cardChoice.card.getRequiredItems();
                poseStack.pushPose();
                for (int a2 = 0; a2 < items.length; ++a2) {
                    if (items[a2] == null || items[a2].isEmpty()) {
                        poseStack.pushPose();
                        RenderSystem.setShaderTexture(0, txQuestion);
                        poseStack.scale(0.125f, 0.125f, 0.0f);
                        RenderSystem.setShaderColor(0.75f, 0.75f, 0.75f, alpha);
                        poseStack.translate(-9 * items.length + 18 * a2, 35.0, 0.0);
                        UtilsFX.drawTexturedQuadFull(poseStack, 0.0f, 0.0f, 0.0);
                        poseStack.popPose();
                    } else {
                        poseStack.pushPose();
                        poseStack.scale(0.125f, 0.125f, 0.0f);

                        var posestack = RenderSystem.getModelViewStack();
                        posestack.pushPose();
                        posestack.mulPoseMatrix(poseStack.last().pose());
                        RenderSystem.applyModelViewMatrix();
                        pGuiGraphics.renderItemDecorations(font, items[a2], -9 * items.length + 18 * a2, 35);
                        posestack.popPose();
                        RenderSystem.applyModelViewMatrix();

                        RenderSystem.enableDepthTest();
                        poseStack.popPose();
                        try {
                            if (cardChoice.card.getRequiredItemsConsumed()[a2]) {
                                poseStack.pushPose();
                                RenderSystem.setShaderTexture(0, txBase);
                                RenderSystem.enableBlend();
                                poseStack.scale(0.125f, 0.125f, 0.0f);
                                float s = (float) Math.sin((player.tickCount + a2 * 2 + minecraft.getPartialTick()) / 2.0f) * 0.03f;
                                poseStack.translate(-2 - 9 * items.length + 18 * a2, 45.0f + s * 10.0f, 0.0);
                                poseStack.scale(0.5f, 0.5f + s, 0.0f);
                                pGuiGraphics.blit(txBase, 0, 0, 64, 120, 16, 16);
                                poseStack.popPose();
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }

    private void drawSheetOverlay(GuiGraphics pGuiGraphics, double x, double y, ResearchTableData.CardChoice cardChoice, int mx, int my) {
        PoseStack poseStack = pGuiGraphics.pose();
        poseStack.pushPose();
        if (cardChoice != null && cardChoice.card.getRequiredItems() != null) {
            ItemStack[] items = cardChoice.card.getRequiredItems();
            for (int a = 0; a < items.length; ++a) {
                if (isHovering((int) (x - 9 * items.length + 18 * a), (int) (y + 36.0), 15, 15, mx, my)) {
                    if (items[a] == null || items[a].isEmpty()) {
                        pGuiGraphics.renderTooltip(font, Component.translatable("tc.card.unknown"), mx, my);
                    } else {
                        pGuiGraphics.renderTooltip(font, items[a], mx, my);
                    }
                }
            }
        }
        poseStack.popPose();
    }

    private void drawCards() {
        cardSelected = false;
        cardHover = new float[]{0.0f, 0.0f, 0.0f};
        cardZoomOut = new float[]{0.0f, 0.0f, 0.0f};
        cardZoomIn = new float[]{0.0f, 0.0f, 0.0f};
        cardActive = new boolean[]{true, true, true};
        int draw = 2;
        if (table.data.bonusDraws > 0) {
            ++draw;
            ResearchTableData data = table.data;
            --data.bonusDraws;
        }
        this.minecraft.gameMode.handleInventoryButtonClick(menu.containerId, draw);
        cardChoices.clear();
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(buttonCreate);
        buttonCreate.setX(leftPos + 128);
        buttonCreate.setY(topPos + 22);

        addRenderableWidget(buttonComplete);
        buttonComplete.setX(leftPos + 191);
        buttonComplete.setY(topPos + 96);

        addRenderableWidget(buttonScrap);
        buttonScrap.setX(leftPos + 128);
        buttonScrap.setY(topPos + 168);
    }

    private void checkButtons() {
        buttonComplete.active = false;
        buttonComplete.visible = false;
        buttonScrap.active = false;
        buttonScrap.visible = false;
        if (table.data != null) {
            buttonCreate.active = false;
            buttonCreate.visible = false;
            if (table.data.isComplete()) {
                buttonComplete.active = true;
                buttonComplete.visible = true;
            } else {
                buttonScrap.active = true;
                buttonScrap.visible = true;
            }
        } else {
            buttonCreate.visible = true;
            table.getItem(1);
            buttonCreate.active = table.getItem(0).getDamageValue() != table.getItem(0).getMaxDamage();
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int xx = (width - imageWidth) / 2;
        int yy = (height - imageHeight) / 2;
        if (table.data == null) {
            if (!currentAids.isEmpty()) {
                int side = Math.min(currentAids.size(), 6);
                int c = 0;
                int r = 0;
                for (String key : currentAids) {
                    ITheorycraftAid mutator = TheorycraftManager.aids.get(key);
                    if (mutator == null) {
                        continue;
                    }
                    int x = 128 + 20 * c - side * 10;
                    int y = 85 + 35 * r;
                    if (isHovering(x, y, 16, 16, pMouseX, pMouseY)) {
                        if (selectedAids.contains(key)) {
                            selectedAids.remove(key);
                        } else if (selectedAids.size() + 1 < dummyInspirationStart) {
                            selectedAids.add(key);
                        }
                    }
                    if (++c < side) {
                        continue;
                    }
                    ++r;
                    c = 0;
                }
            }
        } else {
            int sx = 128;
            int cw = 110;
            if (!cardChoices.isEmpty()) {
                int pressed = -1;
                for (int a = 0; a < cardChoices.size(); ++a) {
                    double var7 = pMouseX - (5 + sx - 55 * cardChoices.size() + xx + cw * a);
                    double var8 = pMouseY - (100 + yy - 60);
                    if (cardZoomOut[a] >= 0.95 && !cardSelected && var7 >= 0 && var8 >= 0 && var7 < 100 && var8 < 120) {
                        pressed = a;
                        break;
                    }
                }
                if (pressed >= 0) {
                    table.getItem(0);
                    if (table.getItem(0).getDamageValue() != table.getItem(0).getMaxDamage()) {
                        this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 4 + pressed);
                    }
                }
            } else {
                double var9 = pMouseX - (25 + xx);
                double var10 = pMouseY - (55 + yy);
                if (var9 >= 0 && var10 >= 0 && var9 < 75 && var10 < 90) {
                    table.getItem(1);
                    drawCards();
                }
            }
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    void checkCards() {
        if (!table.data.cardChoices.isEmpty() && cardChoices.isEmpty()) {
            syncFromTableChoices();
        }
        if (!cardSelected) {
            for (int a = 0; a < cardChoices.size(); ++a) {
                try {
                    if (table.data != null && table.data.cardChoices.size() > a && table.data.cardChoices.get(a).selected) {
                        for (int q = 0; q < cardChoices.size(); ++q) {
                            cardActive[q] = table.data.cardChoices.get(q).selected;
                        }
                        cardSelected = true;
//                        playButtonPageSelect();
                        this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, 1);
                        break;
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void playButtonPageFlip() {
        minecraft.getCameraEntity().playSound(ModSounds.page.get(), 1.0f, 1.0f);
    }

    private void playButtonClick() {
        minecraft.getCameraEntity().playSound(ModSounds.clack.get(), 0.4f, 1.0f);
    }

    private void playButtonWrite() {
        minecraft.getCameraEntity().playSound(ModSounds.write.get(), 0.3f, 1.0f);
    }

    private void drawSplitString(GuiGraphics pGuiGraphics, Font font, FormattedText pText, int pX, int pY, int pMaxWidth, int pColor) {
        for (FormattedCharSequence formattedcharsequence : font.split(pText, pMaxWidth)) {
            pGuiGraphics.drawString(font, formattedcharsequence, pX, pY, pColor);
            pY += 9;
        }
    }
}
