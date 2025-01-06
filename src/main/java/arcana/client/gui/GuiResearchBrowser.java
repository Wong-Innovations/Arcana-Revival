package arcana.client.gui;

import arcana.Arcana;
import arcana.common.lib.utils.InventoryUtils;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.capabilities.ModCapabilities;
import arcana.api.internal.CommonInternals;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchCategory;
import arcana.api.research.ResearchEntry;
import arcana.api.research.ResearchStage;
import arcana.client.lib.UtilsFX;
import arcana.common.config.ConfigResearch;
import arcana.common.lib.network.PacketHandler;
import arcana.common.lib.network.playerdata.PacketSyncProgressToServer;
import arcana.common.lib.research.ResearchManager;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class GuiResearchBrowser extends Screen {
    private static int guiBoundsLeft;
    private static int guiBoundsTop;
    private static int guiBoundsRight;
    private static int guiBoundsBottom;
    protected int mouseX;
    protected int mouseY;
    protected float screenZoom;
    protected double curMouseX;
    protected double curMouseY;
    protected double guiMapX;
    protected double guiMapY;
    protected double tempMapX;
    protected double tempMapY;
    private int isMouseButtonDown;
    public static double lastX = -9999.0;
    public static double lastY = -9999.0;
    private int screenX;
    private int screenY;
    private int startX;
    private int startY;
    private LinkedList<ResearchEntry> research;
    static String selectedCategory;
    private ResearchEntry currentHighlight;
    private Player player;
    long popuptime;
    String popupmessage;
    private EditBox searchField;
    private static boolean searching;
    private ArrayList<String> categoriesTC;
    private ArrayList<String> categoriesOther;
    static int catScrollPos;
    static int catScrollMax;
    public int addonShift;
    private ArrayList<String> invisible;
    private boolean isButtonDown;
    ArrayList<Pair<String, SearchResult>> searchResults;
    ResourceLocation tx1;

    public GuiResearchBrowser() {
        super(GameNarrator.NO_TITLE);
        this.mouseX = 0;
        this.mouseY = 0;
        this.screenZoom = 1.0f;
        this.isMouseButtonDown = 0;
        this.startX = 16;
        this.startY = 16;
        this.research = new LinkedList<>();
        this.currentHighlight = null;
        this.player = null;
        this.popuptime = 0L;
        this.popupmessage = "";
        this.categoriesTC = new ArrayList<>();
        this.categoriesOther = new ArrayList<>();
        this.addonShift = 0;
        this.invisible = new ArrayList<>();
        this.searchResults = new ArrayList<>();
        this.tx1 = new ResourceLocation(Arcana.MODID, "textures/gui/gui_research_browser.png");
        this.tempMapX = lastX;
        this.guiMapX = lastX;
        this.curMouseX = lastX;
        this.tempMapY = lastY;
        this.guiMapY = lastY;
        this.curMouseY = lastY;
        this.player = Minecraft.getInstance().player;
    }

    public GuiResearchBrowser(double x, double y) {
        super(GameNarrator.NO_TITLE);
        mouseX = 0;
        mouseY = 0;
        screenZoom = 1.0f;
        isMouseButtonDown = 0;
        startX = 16;
        startY = 16;
        research = new LinkedList<>();
        currentHighlight = null;
        player = null;
        popuptime = 0L;
        popupmessage = "";
        categoriesTC = new ArrayList<>();
        categoriesOther = new ArrayList<>();
        addonShift = 0;
        invisible = new ArrayList<>();
        searchResults = new ArrayList<>();
        tx1 = new ResourceLocation(Arcana.MODID, "textures/gui/gui_research_browser.png");
        tempMapX = x;
        guiMapX = x;
        curMouseX = x;
        tempMapY = y;
        guiMapY = y;
        curMouseY = y;
        player = Minecraft.getInstance().player;
    }

    public void updateResearch() {
        this.clearWidgets();
        this.addRenderableWidget(new GuiSearchButton(1, this.height - 17, 16, 16, Component.translatable("tc.search")));
        (this.searchField = new EditBox(this.font, 20, 20, 89, font.lineHeight, Component.empty())).setMaxLength(15);
//        this.minecraft.keyboardHandler.setSendRepeatsToGui(true); // uhhhhh hopefully not needed anymore?
        this.searchField.setBordered(true);
        this.searchField.setVisible(false);
        this.searchField.setTextColor(0xFFFFFF);
        if (searching) {
            this.searchField.setVisible(true);
            this.searchField.setCanLoseFocus(false);
            this.searchField.setFocused(true);
            this.searchField.setValue("");
            this.updateSearch();
        }
        this.screenX = this.width - 32;
        this.screenY = this.height - 32;
        this.research.clear();
        if (selectedCategory == null) {
            Collection<String> cats = ResearchCategories.researchCategories.keySet();
            selectedCategory = cats.iterator().next();
        }
        int limit = (int) Math.floor((this.screenY - 28) / 24.0f);
        this.addonShift = 0;
        int count = 0;
        this.categoriesTC.clear();
        this.categoriesOther.clear();
        Label_0283:
        for (String rcl : ResearchCategories.researchCategories.keySet()) {
            int rt = 0;
            int rco = 0;
            Collection<ResearchEntry> col = ResearchCategories.getResearchCategory(rcl).research.values();
            for (final ResearchEntry res : col) {
                if (res.hasMeta(ResearchEntry.EnumResearchMeta.AUTOUNLOCK)) {
                    continue;
                }
                ++rt;
                if (!ModCapabilities.knowsResearch(this.player, res.getKey())) {
                    continue;
                }
                ++rco;
            }
            int v = (int) (rco / (float) rt * 100.0f);
            ResearchCategory rc = ResearchCategories.getResearchCategory(rcl);
            if (rc.researchKey != null && !ModCapabilities.knowsResearchStrict(this.player, rc.researchKey)) {
                continue;
            }
            for (String tcc : ConfigResearch.TCCategories) {
                if (tcc.equals(rcl)) {
                    this.categoriesTC.add(rcl);
                    this.addRenderableWidget(new GuiCategoryButton(rc, rcl, false, 20 + this.categoriesTC.size(), 1, 10 + this.categoriesTC.size() * 24, 16, 16, Component.translatable("tc.research_category." + rcl), v));
                    continue Label_0283;
                }
            }
            if (++count > limit + catScrollPos) {
                continue;
            }
            if (count - 1 < catScrollPos) {
                continue;
            }
            this.categoriesOther.add(rcl);
            this.addRenderableWidget(new GuiCategoryButton(rc, rcl, true, 50 + this.categoriesOther.size(), this.width - 17, 10 + this.categoriesOther.size() * 24, 16, 16, Component.translatable("tc.research_category." + rcl), v));
        }
        if (count > limit || count < catScrollPos) {
            this.addonShift = (this.screenY - 28) % 24 / 2;
            this.addRenderableWidget(new GuiScrollButton(false, 3, this.width - 14, 20, 10, 11, Component.empty()));
            this.addRenderableWidget(new GuiScrollButton(true, 4, this.width - 14, this.screenY + 1, 10, 11, Component.empty()));
        }
        catScrollMax = count - limit;
        if (selectedCategory == null || selectedCategory.isEmpty()) {
            return;
        }
        Collection<ResearchEntry> col2 = ResearchCategories.getResearchCategory(selectedCategory).research.values();
        for (ResearchEntry res : col2) {
            this.research.add(res);
        }
        guiBoundsLeft = 99999;
        guiBoundsTop = 99999;
        guiBoundsRight = -99999;
        guiBoundsBottom = -99999;

        for (ResearchEntry res : this.research) {
            if (res != null && this.isVisible(res)) {
                if (res.getDisplayColumn() * 24 - this.screenX + 48 < guiBoundsLeft) {
                    guiBoundsLeft = res.getDisplayColumn() * 24 - this.screenX + 48;
                }
                if (res.getDisplayColumn() * 24 - 24 > guiBoundsRight) {
                    guiBoundsRight = res.getDisplayColumn() * 24 - 24;
                }
                if (res.getDisplayRow() * 24 - this.screenY + 48 < guiBoundsTop) {
                    guiBoundsTop = res.getDisplayRow() * 24 - this.screenY + 48;
                }
                if (res.getDisplayRow() * 24 - 24 <= guiBoundsBottom) {
                    continue;
                }
                guiBoundsBottom = res.getDisplayRow() * 24 - 24;
            }
        }
    }

    private boolean isVisible(ResearchEntry res) {
        if (ModCapabilities.knowsResearch(this.player, res.getKey())) {
            return true;
        }
        if (this.invisible.contains(res.getKey()) || (res.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN) && !this.canUnlockResearch(res))) {
            return false;
        }
        if (res.getParents() == null && res.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN)) {
            return false;
        }
        if (res.getParents() != null) {
            for (final String r : res.getParents()) {
                final ResearchEntry ri = ResearchCategories.getResearch(r);
                if (ri != null && !this.isVisible(ri)) {
                    this.invisible.add(r);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean canUnlockResearch(ResearchEntry res) {
        return ResearchManager.doesPlayerHaveRequisites(this.player, res.getKey());
    }

    @Override
    public void onClose() {
        lastX = this.guiMapX;
        lastY = this.guiMapY;
//        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        super.onClose();
    }

    @Override
    protected void init() {
        this.updateResearch();
        if (lastX == -9999.0 || this.guiMapX > guiBoundsRight || this.guiMapX < guiBoundsLeft) {
            double n = (double) (guiBoundsLeft + guiBoundsRight) / 2;
            this.tempMapX = n;
            this.guiMapX = n;
        }
        if (lastY == -9999.0 || this.guiMapY > guiBoundsBottom || this.guiMapY < guiBoundsTop) {
            double n2 = (double) (guiBoundsBottom + guiBoundsTop) / 2;
            this.tempMapY = n2;
            this.guiMapY = n2;
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (searching && this.searchField.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            this.updateSearch();
        } else if (pKeyCode == this.minecraft.options.keyInventory.getKey().getValue()) {
            this.minecraft.setScreen(null);
            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    private void updateSearch() {
        this.searchResults.clear();
        this.invisible.clear();
        String s1 = this.searchField.getValue().toLowerCase();
        for (String cat : this.categoriesTC) {
            if (cat.toLowerCase().contains(s1)) {
                this.searchResults.add(Pair.of(I18n.get("tc.research_category." + cat), new SearchResult(cat, null, true)));
            }
        }
        for (String cat : this.categoriesOther) {
            if (cat.toLowerCase().contains(s1)) {
                this.searchResults.add(Pair.of(I18n.get("tc.research_category." + cat), new SearchResult(cat, null, true)));
            }
        }
        ArrayList<ResourceLocation> dupCheck = new ArrayList<>();
        for (String pre : ModCapabilities.getKnowledge(this.player).getResearchList()) {
            ResearchEntry ri = ResearchCategories.getResearch(pre);
            if (ri != null) {
                if (ri.getLocalizedName() == null) {
                    continue;
                }
                if (ri.getLocalizedName().toLowerCase().contains(s1)) {
                    this.searchResults.add(Pair.of(ri.getLocalizedName(), new SearchResult(pre, null)));
                }
                int stage = ModCapabilities.getKnowledge(this.player).getResearchStage(pre);
                if (ri.getStages() == null) {
                    continue;
                }
                int s2 = Math.min(ri.getStages().length - 1, stage + 1);
                ResearchStage page = ri.getStages()[s2];
                if (page == null || page.getRecipes() == null) {
                    continue;
                }
                for (ResourceLocation rec : page.getRecipes()) {
                    if (!dupCheck.contains(rec)) {
                        dupCheck.add(rec);
                        Object recipeObject = CommonInternals.getCatalogRecipe(rec);
                        if (recipeObject == null) {
                            recipeObject = CommonInternals.getCatalogRecipeFake(rec);
                        }
                    }
                }
            }
        }
        Collections.sort(this.searchResults);
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int mx, int my, float pPartialTick) {
        PoseStack poseStack = pGuiGraphics.pose();
        if (!searching) {
            if (isButtonDown) {
                if ((this.isMouseButtonDown == 0 || this.isMouseButtonDown == 1) && mx >= this.startX && mx < this.startX + this.screenX && my >= this.startY && my < this.startY + this.screenY) {
                    if (this.isMouseButtonDown == 0) {
                        this.isMouseButtonDown = 1;
                    } else {
                        this.guiMapX -= (mx - this.mouseX) * (double) this.screenZoom;
                        this.guiMapY -= (my - this.mouseY) * (double) this.screenZoom;
                        double guiMapX = this.guiMapX;
                        this.curMouseX = guiMapX;
                        this.tempMapX = guiMapX;
                        double guiMapY = this.guiMapY;
                        this.curMouseY = guiMapY;
                        this.tempMapY = guiMapY;
                    }
                    this.mouseX = mx;
                    this.mouseY = my;
                }
                if (this.tempMapX < guiBoundsLeft * (double) this.screenZoom) {
                    this.tempMapX = guiBoundsLeft * (double) this.screenZoom;
                }
                if (this.tempMapY < guiBoundsTop * (double) this.screenZoom) {
                    this.tempMapY = guiBoundsTop * (double) this.screenZoom;
                }
                if (this.tempMapX >= guiBoundsRight * (double) this.screenZoom) {
                    this.tempMapX = guiBoundsRight * this.screenZoom - 1.0f;
                }
                if (this.tempMapY >= guiBoundsBottom * (double) this.screenZoom) {
                    this.tempMapY = guiBoundsBottom * this.screenZoom - 1.0f;
                }
            } else {
                this.isMouseButtonDown = 0;
            }
        }
        this.renderBackground(pGuiGraphics);
        int locX = Mth.floor(this.curMouseX + (this.guiMapX - this.curMouseX) * pPartialTick);
        int locY = Mth.floor(this.curMouseY + (this.guiMapY - this.curMouseY) * pPartialTick);
        if (locX < guiBoundsLeft * this.screenZoom) {
            locX = (int) (guiBoundsLeft * this.screenZoom);
        }
        if (locY < guiBoundsTop * this.screenZoom) {
            locY = (int) (guiBoundsTop * this.screenZoom);
        }
        if (locX >= guiBoundsRight * this.screenZoom) {
            locX = (int) (guiBoundsRight * this.screenZoom - 1.0f);
        }
        if (locY >= guiBoundsBottom * this.screenZoom) {
            locY = (int) (guiBoundsBottom * this.screenZoom - 1.0f);
        }
        this.genResearchBackgroundFixedPre(poseStack);
        if (!searching) {
            poseStack.pushPose();
            poseStack.scale(1.0f / this.screenZoom, 1.0f / this.screenZoom, 1.0f);
            this.genResearchBackgroundZoomable(pGuiGraphics, mx, my, pPartialTick, locX, locY);
            poseStack.popPose();
        } else {
            this.searchField.render(pGuiGraphics, mx, my, pPartialTick);
            int q = 0;
            for (Pair p : this.searchResults) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                SearchResult sr = (SearchResult) p.getRight();
                int color = sr.cat ? 0xDDAAAA : ((sr.recipe == null) ? 0xDDDDDD : 0xAAAADD);
                if (sr.recipe != null) {
                    RenderSystem.setShaderTexture(0, this.tx1);
                    poseStack.pushPose();
                    poseStack.scale(0.5f, 0.5f, 0.5f);
                    pGuiGraphics.blit(tx1, 44, (32 + q * 10) * 2, 224, 48, 16, 16);
                    poseStack.popPose();
                }
                if (mx > 22 && mx < 18 + this.screenX && my >= 32 + q * 10 && my < 40 + q * 10) {
                    color = ((sr.recipe == null) ? 0xFFFFFF : (sr.cat ? 0xFFCCCC : 0xCCCCFF));
                }
                pGuiGraphics.drawString(this.font, (String) p.getLeft(), 32, 32 + q * 10, color);
                ++q;
                if (32 + (q + 1) * 10 > this.screenY) {
                    pGuiGraphics.drawString(this.font, I18n.get("tc.search.more"), 22, 34 + q * 10, 0xAAAAAA);
                    break;
                }
            }
        }
        this.genResearchBackgroundFixedPost(pGuiGraphics, mx, my, pPartialTick);
        if (popuptime > System.currentTimeMillis()) {
            ArrayList<String> text = new ArrayList<>();
            text.add(popupmessage);
            UtilsFX.drawCustomTooltip(pGuiGraphics, this, this.font, text, 10, 34, -99);
        }
    }

    @Override
    public void tick() {
        this.curMouseX = this.guiMapX;
        this.curMouseY = this.guiMapY;
        final double var1 = this.tempMapX - this.guiMapX;
        final double var2 = this.tempMapY - this.guiMapY;
        if (var1 * var1 + var2 * var2 < 4.0) {
            this.guiMapX += var1;
            this.guiMapY += var2;
        } else {
            this.guiMapX += var1 * 0.85;
            this.guiMapY += var2 * 0.85;
        }
        this.searchField.tick();
    }

    private void genResearchBackgroundFixedPre(PoseStack pPoseStack) {
        pPoseStack.translate(0, 0, -pPoseStack.last().pose().m32()); // set blitOffset(0)
        RenderSystem.depthFunc(GlConst.GL_GEQUAL);
        pPoseStack.pushPose();
        pPoseStack.translate(0.0f, 0.0f, -200.0f);
//        RenderSystem.enableTexture(); // not needed?
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    protected void genResearchBackgroundZoomable(GuiGraphics pGuiGraphics, int mx, int my, float par3, int locX, int locY) {
        PoseStack poseStack = pGuiGraphics.pose();
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, ResearchCategories.getResearchCategory(selectedCategory).background);
        this.drawTexturedModalRectWithDoubles(poseStack, (startX - 2) * screenZoom, (startY - 2) * screenZoom, locX / 2.0, locY / 2.0, (screenX + 4) * screenZoom, (screenY + 4) * screenZoom);
        if (ResearchCategories.getResearchCategory(selectedCategory).background2 != null) {
            RenderSystem.setShaderTexture(0, ResearchCategories.getResearchCategory(selectedCategory).background2);
            drawTexturedModalRectWithDoubles(poseStack, (startX - 2) * screenZoom, (startY - 2) * screenZoom, locX / 1.5, locY / 1.5, (screenX + 4) * screenZoom, (screenY + 4) * screenZoom);
        }
        RenderSystem.disableBlend();
        poseStack.popPose();
        RenderSystem.enableDepthTest();
        //TODO: GL_LEQUAL supposed to remove all that is out of bounds
        RenderSystem.depthFunc(GlConst.GL_LEQUAL);
        RenderSystem.setShaderTexture(0, tx1);
        if (ModCapabilities.getKnowledge(this.player).getResearchList() != null) {
            for (ResearchEntry source : this.research) {
                if (source.getParents() != null && source.getParents().length > 0) {
                    for (int a = 0; a < source.getParents().length; ++a) {
                        if (source.getParents()[a] != null && ResearchCategories.getResearch(source.getParentsClean()[a]) != null && ResearchCategories.getResearch(source.getParentsClean()[a]).getCategory().equals(selectedCategory)) {
                            ResearchEntry parent = ResearchCategories.getResearch(source.getParentsClean()[a]);
                            if (parent.getSiblings() == null || !Arrays.asList(parent.getSiblings()).contains(source.getKey())) {
                                boolean knowsParent = ModCapabilities.knowsResearchStrict(this.player, source.getParents()[a]);
                                final boolean b = this.isVisible(source) && !source.getParents()[a].startsWith("~");
                                if (b) {
                                    if (knowsParent) {
                                        this.drawLine(pGuiGraphics, source.getDisplayColumn(), source.getDisplayRow(), parent.getDisplayColumn(), parent.getDisplayRow(), 0.6f, 0.6f, 0.6f, locX, locY, 3.0f, true, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    } else if (this.isVisible(parent)) {
                                        this.drawLine(pGuiGraphics, source.getDisplayColumn(), source.getDisplayRow(), parent.getDisplayColumn(), parent.getDisplayRow(), 0.2f, 0.2f, 0.2f, locX, locY, 2.0f, true, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    }
                                }
                            }
                        }
                    }
                }
                if (source.getSiblings() != null && source.getSiblings().length > 0) {
                    for (int a = 0; a < source.getSiblings().length; ++a) {
                        if (source.getSiblings()[a] != null && ResearchCategories.getResearch(source.getSiblings()[a]) != null && ResearchCategories.getResearch(source.getSiblings()[a]).getCategory().equals(selectedCategory)) {
                            final ResearchEntry sibling = ResearchCategories.getResearch(source.getSiblings()[a]);
                            final boolean knowsSibling = ModCapabilities.knowsResearchStrict(this.player, sibling.getKey());
                            if (this.isVisible(source)) {
                                if (!source.getSiblings()[a].startsWith("~")) {
                                    if (knowsSibling) {
                                        this.drawLine(pGuiGraphics, sibling.getDisplayColumn(), sibling.getDisplayRow(), source.getDisplayColumn(), source.getDisplayRow(), 0.3f, 0.3f, 0.4f, locX, locY, 1.0f, false, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    } else if (this.isVisible(sibling)) {
                                        this.drawLine(pGuiGraphics, sibling.getDisplayColumn(), sibling.getDisplayRow(), source.getDisplayColumn(), source.getDisplayRow(), 0.1875f, 0.1875f, 0.25f, locX, locY, 0.0f, false, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.currentHighlight = null;
        for (ResearchEntry researchEntry : research) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
            boolean hasWarp = false;
            if (researchEntry.getStages() != null) {
                for (final ResearchStage stage : researchEntry.getStages()) {
                    if (stage.getWarp() > 0) {
                        hasWarp = true;
                        break;
                    }
                }
            }
            int col = researchEntry.getDisplayColumn() * 24 - locX;
            int row = researchEntry.getDisplayRow() * 24 - locY;
            if (col >= -24 && row >= -24 && col <= screenX * screenZoom && row <= screenY * screenZoom) {
                int iconX = startX + col;
                int iconY = startY + row;
                if (isVisible(researchEntry)) {
                    // TODO: drawForbidden
//                    if (hasWarp) {
//                        drawForbidden(iconX + 8, iconY + 8);
//                    }
                    if (ModCapabilities.getKnowledge(this.player).isResearchComplete(researchEntry.getKey())) {
                        float color = 1.0f;
                        RenderSystem.setShaderColor(color, color, color, 1.0f);
                    } else if (this.canUnlockResearch(researchEntry)) {
                        float color = (float) Math.sin(Blaze3D.getTime() * 1000f % 600L / 600.0 * Mth.PI * 2.0) * 0.25f + 0.75f;
                        RenderSystem.setShaderColor(color, color, color, 1.0f);
                    } else {
                        float color = 0.3f;
                        RenderSystem.setShaderColor(color, color, color, 1.0f);
                    }
                    RenderSystem.setShaderTexture(0, tx1);
                    RenderSystem.enableCull();
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
                    if (researchEntry.hasMeta(ResearchEntry.EnumResearchMeta.ROUND)) {
                        pGuiGraphics.blit(tx1, iconX - 8, iconY - 8, 144, 48 + (researchEntry.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN) ? 32 : 0), 32, 32);
                    } else {
                        int ix = 80;
                        int iy = 48;
                        if (researchEntry.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN)) {
                            iy += 32;
                        }
                        if (researchEntry.hasMeta(ResearchEntry.EnumResearchMeta.HEX)) {
                            ix += 32;
                        }
                        pGuiGraphics.blit(tx1, iconX - 8, iconY - 8, ix, iy, 32, 32);
                    }
                    if (researchEntry.hasMeta(ResearchEntry.EnumResearchMeta.SPIKY)) {
                        pGuiGraphics.blit(tx1, iconX - 8, iconY - 8, 176, 48 + (researchEntry.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN) ? 32 : 0), 32, 32);
                    }
                    boolean bw = false;
                    if (!this.canUnlockResearch(researchEntry)) {
                        final float color = 0.1f;
                        RenderSystem.setShaderColor(color, color, color, 1.0f);
                        bw = true;
                    }
                    if (ModCapabilities.getKnowledge(this.player).hasResearchFlag(researchEntry.getKey(), IPlayerKnowledge.EnumResearchFlag.RESEARCH)) {
                        poseStack.pushPose();
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                        poseStack.translate((float) (iconX - 9), (float) (iconY - 9), 0.0f);
                        poseStack.scale(0.5f, 0.5f, 1.0f);
                        pGuiGraphics.blit(tx1, 0, 0, 176, 16, 32, 32);
                        poseStack.popPose();
                    }
                    if (ModCapabilities.getKnowledge(this.player).hasResearchFlag(researchEntry.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE)) {
                        poseStack.pushPose();
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                        poseStack.translate((float) (iconX - 9), (float) (iconY + 9), 0.0f);
                        poseStack.scale(0.5f, 0.5f, 1.0f);
                        pGuiGraphics.blit(tx1, 0, 0, 208, 16, 32, 32);
                        poseStack.popPose();
                    }
                    drawResearchIcon(pGuiGraphics, researchEntry, iconX, iconY, poseStack.last().pose().m32(), bw);
                    if (!this.canUnlockResearch(researchEntry)) {
                        bw = false;
                    }
                    if (mx >= this.startX && my >= this.startY && mx < this.startX + this.screenX && my < this.startY + this.screenY && mx >= (iconX - 2) / this.screenZoom && mx <= (iconX + 18) / this.screenZoom && my >= (iconY - 2) / this.screenZoom && my <= (iconY + 18) / this.screenZoom) {
                        this.currentHighlight = researchEntry;
                    }
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
        RenderSystem.disableDepthTest();
    }

    public static void drawResearchIcon(GuiGraphics pGuiGraphics, ResearchEntry iconResearch, int iconX, int iconY, float zLevel, boolean bw) {
        PoseStack poseStack = pGuiGraphics.pose();
        if (iconResearch.getIcons() != null && iconResearch.getIcons().length > 0) {
            int idx = (int) (System.currentTimeMillis() / 1000L % iconResearch.getIcons().length);
            poseStack.pushPose();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
            if (iconResearch.getIcons()[idx] instanceof ResourceLocation) {
                RenderSystem.setShaderTexture(0, (ResourceLocation) iconResearch.getIcons()[idx]);
                if (bw) {
                    RenderSystem.setShaderColor(0.2f, 0.2f, 0.2f, 1.0f);
                }
                int w = GL11.glGetTexLevelParameteri(GlConst.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
                int h = GL11.glGetTexLevelParameteri(GlConst.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
                if (h > w && h % w == 0) {
                    int m = h / w;
                    float q = 16.0f / m;
                    float idx2 = (float) System.currentTimeMillis() / 150L % m * q;
                    UtilsFX.drawTexturedQuadF(poseStack, (float) iconX, (float) iconY, 0.0f, idx2, 16.0f, q, zLevel);
                } else if (w > h && w % h == 0) {
                    int m = w / h;
                    float q = 16.0f / m;
                    float idx2 = (float) System.currentTimeMillis() / 150L % m * q;
                    UtilsFX.drawTexturedQuadF(poseStack, (float) iconX, (float) iconY, idx2, 0.0f, q, 16.0f, zLevel);
                } else {
                    UtilsFX.drawTexturedQuadFull(poseStack, (float) iconX, (float) iconY, zLevel);
                }
            } else if (iconResearch.getIcons()[idx] instanceof ItemStack) {
                pGuiGraphics.renderItemDecorations(Minecraft.getInstance().font, InventoryUtils.cycleItemStack(iconResearch.getIcons()[idx]), iconX, iconY);
            }
            RenderSystem.disableBlend();
            poseStack.popPose();
        }
    }

    private void genResearchBackgroundFixedPost(GuiGraphics pGuiGraphics, int mx, int my, float pPartialTick) {
        PoseStack poseStack = pGuiGraphics.pose();
        RenderSystem.setShaderTexture(0, this.tx1);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        for (int c = 16; c < this.width - 16; c += 64) {
            int p = 64;
            if (c + p > this.width - 16) {
                p = this.width - 16 - c;
            }
            if (p > 0) {
                pGuiGraphics.blit(tx1, c, -2, 48, 13, p, 22);
                pGuiGraphics.blit(tx1, c, this.height - 20, 48, 13, p, 22);
            }
        }
        for (int c = 16; c < this.height - 16; c += 64) {
            int p = 64;
            if (c + p > this.height - 16) {
                p = this.height - 16 - c;
            }
            if (p > 0) {
                pGuiGraphics.blit(tx1, -2, c, 13, 48, 22, p);
                pGuiGraphics.blit(tx1, this.width - 20, c, 13, 48, 22, p);
            }
        }
        pGuiGraphics.blit(tx1, -2, -2, 13, 13, 22, 22);
        pGuiGraphics.blit(tx1, -2, this.height - 20, 13, 13, 22, 22);
        pGuiGraphics.blit(tx1, this.width - 20, -2, 13, 13, 22, 22);
        pGuiGraphics.blit(tx1, this.width - 20, this.height - 20, 13, 13, 22, 22);
        poseStack.popPose();
        poseStack.translate(0, 0, -poseStack.last().pose().m32()); // set blitOffset(0)
        RenderSystem.depthFunc(GlConst.GL_LEQUAL);
        RenderSystem.disableDepthTest();
//        RenderSystem.enableTexture();
        super.render(pGuiGraphics, mx, my, pPartialTick);
        if (this.currentHighlight != null) {
            ArrayList<String> text = new ArrayList<>();
            text.add("§6" + this.currentHighlight.getLocalizedName());
            if (this.canUnlockResearch(this.currentHighlight)) {
                if (!ModCapabilities.getKnowledge(this.player).isResearchComplete(this.currentHighlight.getKey()) && this.currentHighlight.getStages() != null) {
                    int stage = ModCapabilities.getKnowledge(this.player).getResearchStage(this.currentHighlight.getKey());
                    if (stage > 0) {
                        text.add("@@" + ChatFormatting.AQUA + I18n.get("tc.research.stage") + " " + stage + "/" + this.currentHighlight.getStages().length + ChatFormatting.RESET);
                    } else {
                        text.add("@@" + ChatFormatting.GREEN + I18n.get("tc.research.begin") + ChatFormatting.RESET);
                    }
                }
            } else {
                text.add("@@§c" + I18n.get("tc.researchmissing"));
                int a = 0;
                for (String p2 : this.currentHighlight.getParents()) {
                    if (!ModCapabilities.knowsResearchStrict(this.player, p2)) {
                        String s = "?";
                        try {
                            s = ResearchCategories.getResearch(this.currentHighlight.getParentsClean()[a]).getLocalizedName();
                        } catch (Exception ignored) {}
                        text.add("@@§e - " + s);
                    }
                    ++a;
                }
            }
            if (ModCapabilities.getKnowledge(this.player).hasResearchFlag(this.currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.RESEARCH)) {
                text.add("@@" + I18n.get("tc.research.newresearch"));
            }
            if (ModCapabilities.getKnowledge(this.player).hasResearchFlag(this.currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE)) {
                text.add("@@" + I18n.get("tc.research.newpage"));
            }
            UtilsFX.drawCustomTooltip(pGuiGraphics, this, this.font, text, mx + 3, my - 3, 0xFFFFFF9D);
        }
        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean mouseClicked(double mx, double my, int pButton) {
        if (!searching && this.currentHighlight != null && !ModCapabilities.knowsResearch(this.player, this.currentHighlight.getKey()) && this.canUnlockResearch(this.currentHighlight)) {
            this.updateResearch();
            PacketHandler.sendToServer(new PacketSyncProgressToServer(this.currentHighlight.getKey(), true));
            Minecraft.getInstance().setScreen(new GuiResearchPage(currentHighlight, null, guiMapX, guiMapY));
            this.popuptime = System.currentTimeMillis() + 3000L;
            this.popupmessage = Component.translatable("tc.research.popup", this.currentHighlight.getLocalizedName()).getString();
        } else if (currentHighlight != null && ModCapabilities.knowsResearch(player, currentHighlight.getKey())) {
            ModCapabilities.getKnowledge(player).clearResearchFlag(currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.RESEARCH);
            ModCapabilities.getKnowledge(player).clearResearchFlag(currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
//            PacketHandler.sendToServer(new PacketSyncResearchFlagsToServer(mc.player, currentHighlight.getKey()));
            int stage = ModCapabilities.getKnowledge(player).getResearchStage(currentHighlight.getKey());
            if (stage > 1 && stage >= currentHighlight.getStages().length) {
                PacketHandler.sendToServer(new PacketSyncProgressToServer(currentHighlight.getKey(), false, true, false));
            }
            Minecraft.getInstance().setScreen(new GuiResearchPage(currentHighlight, null, guiMapX, guiMapY));
        } else if (searching) {
            int q = 0;
            for (Pair p : this.searchResults) {
                SearchResult sr = (SearchResult) p.getRight();
                if (mx > 22 && mx < 18 + this.screenX && my >= 32 + q * 10 && my < 40 + q * 10) {
                    if (this.categoriesTC.contains(sr.key) || this.categoriesOther.contains(sr.key)) {
                        searching = false;
                        this.searchField.setVisible(false);
                        this.searchField.setCanLoseFocus(true);
                        this.searchField.setFocused(false);
                        selectedCategory = sr.key;
                        this.updateResearch();
                        double n = (double) (guiBoundsLeft + guiBoundsRight) / 2;
                        this.tempMapX = n;
                        this.guiMapX = n;
                        double n2 = (double) (guiBoundsBottom + guiBoundsTop) / 2;
                        this.tempMapY = n2;
                        this.guiMapY = n2;
                        break;
                    }
                }
                ++q;
                if (32 + (q + 1) * 10 > this.screenY) {
                    break;
                }
            }
        }

        if (pButton == 0) {
            this.isButtonDown = true;
        }
        return super.mouseClicked(mx, my, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            this.isButtonDown = false;
        }

        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (pDelta < 0) {
            this.screenZoom += 0.25f;
        } else if (pDelta > 0) {
            this.screenZoom -= 0.25f;
        }

        this.screenZoom = Mth.clamp(this.screenZoom, 1.0f, 2.0f);

        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void drawLine(GuiGraphics pGuiGraphics, int x, int y, int x2, int y2, float r, float g, float b, int locX, int locY, float zMod, boolean arrow, boolean flipped) {
        PoseStack pose = pGuiGraphics.pose();
        pose.translate(0, 0, zMod);
        boolean bigCorner = false;
        int xd;
        int yd;
        int xm;
        int ym;
        int xx;
        int yy;
        if (flipped) {
            xd = Math.abs(x2 - x);
            yd = Math.abs(y2 - y);
            xm = ((xd == 0) ? 0 : ((x2 - x > 0) ? -1 : 1));
            ym = ((yd == 0) ? 0 : ((y2 - y > 0) ? -1 : 1));
            if (xd > 1 && yd > 1) {
                bigCorner = true;
            }
            xx = x2 * 24 - 4 - locX + this.startX;
            yy = y2 * 24 - 4 - locY + this.startY;
        } else {
            xd = Math.abs(x - x2);
            yd = Math.abs(y - y2);
            xm = ((xd == 0) ? 0 : ((x - x2 > 0) ? -1 : 1));
            ym = ((yd == 0) ? 0 : ((y - y2 > 0) ? -1 : 1));
            if (xd > 1 && yd > 1) {
                bigCorner = true;
            }
            xx = x * 24 - 4 - locX + this.startX;
            yy = y * 24 - 4 - locY + this.startY;
        }
        pose.pushPose();
        // GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(r, g, b, 1.0f);
        if (arrow) {
            if (flipped) {
                final int xx2 = x * 24 - 8 - locX + this.startX;
                final int yy2 = y * 24 - 8 - locY + this.startY;
                if (xm < 0) {
                    pGuiGraphics.blit(tx1, xx2, yy2, 160, 112, 32, 32);
                } else if (xm > 0) {
                    pGuiGraphics.blit(tx1, xx2, yy2, 128, 112, 32, 32);
                } else if (ym > 0) {
                    pGuiGraphics.blit(tx1, xx2, yy2, 64, 112, 32, 32);
                } else if (ym < 0) {
                    pGuiGraphics.blit(tx1, xx2, yy2, 96, 112, 32, 32);
                }
            } else if (ym < 0) {
                pGuiGraphics.blit(tx1, xx - 4, yy - 4, 64, 112, 32, 32);
            } else if (ym > 0) {
                pGuiGraphics.blit(tx1, xx - 4, yy - 4, 96, 112, 32, 32);
            } else if (xm > 0) {
                pGuiGraphics.blit(tx1, xx - 4, yy - 4, 160, 112, 32, 32);
            } else if (xm < 0) {
                pGuiGraphics.blit(tx1, xx - 4, yy - 4, 128, 112, 32, 32);
            }
        }
        int v = 1;
        int h = 0;
        while (v < yd - (bigCorner ? 1 : 0)) {
            pGuiGraphics.blit(tx1, xx + xm * 24 * h, yy + ym * 24 * v, 0, 228, 24, 24);
            ++v;
        }
        if (bigCorner) {
            if (xm < 0 && ym > 0) {
                pGuiGraphics.blit(tx1, xx + xm * 24 * h - 24, yy + ym * 24 * v, 0, 180, 48, 48);
            }
            if (xm > 0 && ym > 0) {
                pGuiGraphics.blit(tx1, xx + xm * 24 * h, yy + ym * 24 * v, 48, 180, 48, 48);
            }
            if (xm < 0 && ym < 0) {
                pGuiGraphics.blit(tx1, xx + xm * 24 * h - 24, yy + ym * 24 * v - 24, 96, 180, 48, 48);
            }
            if (xm > 0 && ym < 0) {
                pGuiGraphics.blit(tx1, xx + xm * 24 * h, yy + ym * 24 * v - 24, 144, 180, 48, 48);
            }
        } else {
            if (xm < 0 && ym > 0) {
                pGuiGraphics.blit(tx1, xx + xm * 24 * h, yy + ym * 24 * v, 48, 228, 24, 24);
            }
            if (xm > 0 && ym > 0) {
                pGuiGraphics.blit(tx1, xx + xm * 24 * h, yy + ym * 24 * v, 72, 228, 24, 24);
            }
            if (xm < 0 && ym < 0) {
                pGuiGraphics.blit(tx1, xx + xm * 24 * h, yy + ym * 24 * v, 96, 228, 24, 24);
            }
            if (xm > 0 && ym < 0) {
                pGuiGraphics.blit(tx1, xx + xm * 24 * h, yy + ym * 24 * v, 120, 228, 24, 24);
            }
        }
        v += (bigCorner ? 1 : 0);
        for (h += (bigCorner ? 2 : 1); h < xd; ++h) {
            pGuiGraphics.blit(tx1, xx + xm * 24 * h, yy + ym * 24 * v, 24, 228, 24, 24);
        }
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend();
        pose.popPose();
        pose.translate(0, 0, -zMod);
    }

    public void drawTexturedModalRectWithDoubles(PoseStack pose, float xCoord, float yCoord, double minU, double minV, double maxU, double maxV) {
        float uScale = 1f / 256;
        float vScale = 1f / 256;

        Matrix4f matrix = pose.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix, xCoord, (float) (yCoord + maxV), pose.last().pose().m32()).uv((float) (minU * uScale), (float) ((minV + maxV) * vScale)).endVertex();
        bufferBuilder.vertex(matrix, (float) (xCoord + maxU), (float) (yCoord + maxV), pose.last().pose().m32()).uv((float) ((minU + maxU) * uScale), (float) (((minV + maxV) * vScale))).endVertex();
        bufferBuilder.vertex(matrix, (float) (xCoord + maxU), yCoord, pose.last().pose().m32()).uv((float) ((minU + maxU) * uScale), (float) ((minV * vScale))).endVertex();
        bufferBuilder.vertex(matrix, xCoord, yCoord, pose.last().pose().m32()).uv((float) (minU * uScale), (float) ((minV * vScale))).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
    }

    private static class SearchResult implements Comparable<SearchResult> {
        String key;
        ResourceLocation recipe;
        boolean cat;

        private SearchResult(String key, ResourceLocation rec) {
            this.key = key;
            this.recipe = rec;
            this.cat = false;
        }

        private SearchResult(String key, ResourceLocation recipe, boolean cat) {
            this.key = key;
            this.recipe = recipe;
            this.cat = cat;
        }

        @Override
        public int compareTo(@NotNull SearchResult arg0) {
            int k = this.key.compareTo(arg0.key);
            return (k == 0 && this.recipe != null && arg0.recipe != null) ? this.recipe.compareTo(arg0.recipe) : k;
        }
    }

    private class GuiCategoryButton extends Button {
        ResearchCategory rc;
        String key;
        boolean flip;
        int completion;

        public GuiCategoryButton(ResearchCategory rc, String key, boolean flip, int buttonId, int pX, int pY, int pWidth, int pHeight, Component pMessage, int completion) {
            super(pX, pY, pWidth, pHeight, pMessage, button -> {
                searching = false;
                searchField.setVisible(false);
                searchField.setCanLoseFocus(true);
                searchField.setFocused(false);
                selectedCategory = ((GuiCategoryButton) button).key;
                updateResearch();
                double n = (double) (guiBoundsLeft + guiBoundsRight) / 2;
                tempMapX = n;
                guiMapX = n;
                double n2 = (double) (guiBoundsBottom + guiBoundsTop) / 2;
                tempMapY = n2;
                guiMapY = n2;
            }, (button) -> Component.literal(key));
            this.rc = rc;
            this.key = key;
            this.flip = flip;
            this.completion = completion;
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            PoseStack poseStack = pGuiGraphics.pose();
            if (visible) {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderTexture(0, tx1);
                poseStack.pushPose();
                if (!selectedCategory.equals(this.key)) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                } else {
                    RenderSystem.setShaderColor(0.6f, 1.0f, 1.0f, 1.0f);
                }
                pGuiGraphics.blit(tx1, this.getX() - 3, this.getY() - 3 + addonShift, 13, 13, 22, 22);
                poseStack.popPose();
                poseStack.pushPose();
                RenderSystem.setShaderTexture(0, this.rc.icon);
                if (selectedCategory.equals(this.key) || this.isHovered) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                } else {
                    RenderSystem.setShaderColor(0.66f, 0.66f, 0.66f, 0.8f);
                }
                UtilsFX.drawTexturedQuadFull(poseStack, (float) this.getX(), (float) (this.getY() + addonShift), -80.0);
                poseStack.popPose();
                RenderSystem.setShaderTexture(0, tx1);
                boolean nr = false;
                boolean np = false;
                for (String rk : rc.research.keySet()) {
                    if (ModCapabilities.knowsResearch(player, rk)) {
                        if (!nr && ModCapabilities.getKnowledge(player).hasResearchFlag(rk, IPlayerKnowledge.EnumResearchFlag.RESEARCH)) {
                            nr = true;
                        }
                        if (!np && ModCapabilities.getKnowledge(player).hasResearchFlag(rk, IPlayerKnowledge.EnumResearchFlag.PAGE)) {
                            np = true;
                        }
                        if (nr && np) {
                            break;
                        }
                    }
                }
                if (nr) {
                    poseStack.pushPose();
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.7f);
                    poseStack.translate(getX() - 2, getY() + addonShift - 2, 0.0);
                    poseStack.scale(0.25f, 0.25f, 1.0f);
                    pGuiGraphics.blit(tx1, 0, 0, 176, 16, 32, 32);
                    poseStack.popPose();
                }
                if (np) {
                    poseStack.pushPose();
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.7f);
                    poseStack.translate(getX() - 2, getY() + addonShift + 9, 0.0);
                    poseStack.scale(0.25f, 0.25f, 1.0f);
                    pGuiGraphics.blit(tx1, 0, 0, 208, 16, 32, 32);
                    poseStack.popPose();
                }
                if (isHovered) {
                    String dp = this.getMessage().getString() + " (" + this.completion + "%)";
                    pGuiGraphics.drawString(minecraft.font, dp, flip ? (screenX + 9 - minecraft.font.width(dp)) : (getX() + 22), getY() + 4 + addonShift, 0xFFFFFF);
                    int t = 9;
                    if (nr) {
                        pGuiGraphics.drawString(minecraft.font, I18n.get("tc.research.newresearch"), flip ? (screenX + 9 - minecraft.font.width(I18n.get("tc.research.newresearch"))) : (getX() + 22), getY() + 4 + t + addonShift, 0xFFFFFF);
                        t += 9;
                    }
                    if (np) {
                        pGuiGraphics.drawString(minecraft.font, I18n.get("tc.research.newpage"), flip ? (screenX + 9 - minecraft.font.width(I18n.get("tc.research.newpage"))) : (getX() + 22), getY() + 4 + t + addonShift, 0xFFFFFF);
                    }
                }
            }
        }
    }

    private class GuiScrollButton extends Button {
        boolean flip;

        public GuiScrollButton(boolean flip, int buttonId, int x, int y, int widthIn, int heightIn, Component buttonText) {
            super(x, y, widthIn, heightIn, buttonText, button -> {}, (button) -> Component.empty());
            this.flip = flip;
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            PoseStack poseStack = pGuiGraphics.pose();
            if (this.visible) {
                this.isHovered = (mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height);
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderTexture(0, tx1);
                poseStack.pushPose();
                if (this.isHovered) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                } else {
                    RenderSystem.setShaderColor(0.7f, 0.7f, 0.7f, 1.0f);
                }
                pGuiGraphics.blit(tx1, this.getX(), this.getY(), 51, this.flip ? 71 : 55, 10, 11);
                poseStack.popPose();
            }
        }
    }

    private class GuiSearchButton extends Button {
        public GuiSearchButton(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
            super(pX, pY, pWidth, pHeight, pMessage, button -> {
                selectedCategory = "";
                searching = true;
                searchField.setVisible(true);
                searchField.setCanLoseFocus(false);
                searchField.setFocused(true);
                searchField.setValue("");
                updateSearch();
            }, (button) -> Component.literal("Search Button"));
        }

        @Override
        public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
            PoseStack poseStack = pGuiGraphics.pose();
            if (this.visible) {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderTexture(0, tx1);
                poseStack.pushPose();
                if (this.isHovered) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                } else {
                    RenderSystem.setShaderColor(0.8f, 0.8f, 0.8f, 1.0f);
                }
                pGuiGraphics.blit(tx1, this.getX(), this.getY(), 160, 16, 16, 16);
                poseStack.popPose();
            }
        }
    }
}
