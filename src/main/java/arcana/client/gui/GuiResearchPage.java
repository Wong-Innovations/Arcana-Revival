package arcana.client.gui;

import arcana.Arcana;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import arcana.api.ArcanaApiHelper;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.capabilities.ModCapabilities;
import arcana.api.crafting.IArcaneRecipe;
import arcana.api.crafting.ShapedArcaneRecipe;
import arcana.api.internal.CommonInternals;
import arcana.api.research.*;
import arcana.client.lib.UtilsFX;
import arcana.client.lib.events.HudHandler;
import arcana.common.config.ModRecipeProvider;
import arcana.common.lib.ModSounds;
import arcana.common.lib.network.PacketHandler;
import arcana.common.lib.network.playerdata.PacketSyncProgressToServer;
import arcana.common.lib.utils.InventoryUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GuiResearchPage extends Screen {
    public static LinkedList<ResourceLocation> history = new LinkedList<>();
    static ResourceLocation shownRecipe;
    private static int aspectsPage = 0;
    static boolean cycleMultiblockLines = false;
    private static final PageImage PILINE = PageImage.parse("arcana:textures/gui/gui_researchbook.png:24:184:95:6:1");
    private static final PageImage PIDIV = PageImage.parse("arcana:textures/gui/gui_researchbook.png:28:192:140:6:1");
    ArrayList<List> reference = new ArrayList<>();
    protected int paneWidth = 256;
    protected int paneHeight = 181;
    protected double guiMapX;
    protected double guiMapY;
    private ResearchEntry research;
    private int currentStage = 0;
    int lastStage = 0;
    boolean hold = false;
    private int page = 0;
    private int maxPages = 0;
    private int maxAspectPages = 0;
    AspectList knownPlayerAspects = new AspectList();
    IPlayerKnowledge playerKnowledge;
    int rhash = 0;
    float transX = 0.0f;
    float transY = 0.0f;
    long lastCheck = 0L;
    float pt;
    ResourceLocation tex1 = new ResourceLocation(Arcana.MODID, "textures/gui/gui_researchbook.png");
    ResourceLocation tex2 = new ResourceLocation(Arcana.MODID, "textures/gui/gui_researchbook_overlay.png");
    ResourceLocation tex3 = new ResourceLocation(Arcana.MODID, "textures/aspects/_back.png");
    ResourceLocation tex4 = new ResourceLocation(Arcana.MODID, "textures/gui/paper.png");
    ResourceLocation dummyResearch = new ResourceLocation(Arcana.MODID, "textures/aspects/_unknown.png");
    int hrx = 0;
    int hry = 0;
    int recipePage = 0;
    int recipePageMax = 0;
    private long lastCycle;
    private boolean showingAspects = false;
    private boolean showingKnowledge = false;
    LinkedHashMap<ResourceLocation, ArrayList> recipeLists = new LinkedHashMap<>();
    LinkedHashMap<ResourceLocation, ArrayList> recipeOutputs = new LinkedHashMap<>();
    LinkedHashMap<ResourceLocation, ArrayList> drilldownLists = new LinkedHashMap<>();
    boolean hasRecipePages = false;
    private int cycle = -1;
    boolean allowWithPagePopup = false;
    List<String> tipText = null;
    private ArrayList<Page> pages = new ArrayList<>();
    boolean isComplete = false;
    boolean hasAllRequisites = false;
    boolean[] hasCraft = null;
    boolean[] hasKnow = null;
    public HashMap<Integer, String> keyCache = new HashMap<>();

    public GuiResearchPage(ResearchEntry researchEntry, ResourceLocation recipe, double x, double y) {
        super(GameNarrator.NO_TITLE);
        this.research = researchEntry;
        guiMapX = x;
        guiMapY = y;
        this.minecraft = Minecraft.getInstance();
        this.playerKnowledge = ModCapabilities.getKnowledge(this.minecraft.player);
        parsePages();
        for (Aspect a : Aspect.aspects.values()) {
            if (ModCapabilities.knowsResearch(minecraft.player, "!" + a.getTag().toLowerCase())) {
                knownPlayerAspects.add(a, 1);
            }
        }
        maxAspectPages = ((knownPlayerAspects != null) ? Mth.ceil(knownPlayerAspects.size() / 5.0f) : 0);
        if (recipe != null) {
            GuiResearchPage.shownRecipe = recipe;
        }
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        hasRecipePages = false;
        long nano = System.nanoTime();
        if (nano > lastCheck) {
            parsePages();
            if (hold) {
                lastCheck = nano + 250000000L;
            } else {
                lastCheck = nano + 2000000000L;
            }
            if (currentStage > lastStage) {
                hold = false;
            }
        }
        pt = pPartialTick;
        this.renderBackground(pGuiGraphics);
        this.genResearchBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        int sw = (width - paneWidth) / 2;
        int sh = (height - paneHeight) / 2;
        if (!GuiResearchPage.history.isEmpty()) {
            int mx = pMouseX - (sw + 118);
            int my = pMouseY - (sh + 190);
            if (mx >= 0 && my >= 0 && mx < 20 && my < 12) {
                pGuiGraphics.drawString(minecraft.font, I18n.get("recipe.return"), (float) pMouseX, (float) pMouseY, 0XFFFFFF, true);
            }
        }
    }

    protected void genResearchBackground(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        PoseStack poseStack = pGuiGraphics.pose();
        int sw = (width - paneWidth) / 2;
        int sh = (height - paneHeight) / 2;
        float centerX = (this.width - this.paneWidth * 1.3f) / 2.0f;
        float centerY = (this.height - this.paneHeight * 1.3f) / 2.0f;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, this.tex1);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 0.0f);
        poseStack.scale(1.3f, 1.3f, 1.0f);
        pGuiGraphics.blit(tex1, 0, 0, 0, 0, this.paneWidth, this.paneHeight);
        poseStack.popPose();
        reference.clear();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        int current = 0;
        for (Page value : this.pages) {
            if ((current == this.page || current == this.page + 1) && current < this.maxPages) {
                drawPage(pGuiGraphics, value, current % 2, sw, sh - 10, pMouseX, pMouseY);
            }
            if (++current > page + 1) {
                break;
            }
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, tex1);
        float bob = (float) (Math.sin(minecraft.player.tickCount / 3.0f) * 0.2f + 0.1f);
        if (!GuiResearchPage.history.isEmpty()) {
            drawTexturedModalRectScaled(poseStack, sw + 118, sh + 190, 38, 202, 20, 12, bob);
        }
        if (page > 0 && GuiResearchPage.shownRecipe == null) {
            drawTexturedModalRectScaled(poseStack, sw - 16, sh + 190, 0, 184, 12, 8, bob);
        }
        if (page < maxPages - 2 && GuiResearchPage.shownRecipe == null) {
            drawTexturedModalRectScaled(poseStack, sw + 262, sh + 190, 12, 184, 12, 8, bob);
        }
        if (tipText != null) {
            UtilsFX.drawCustomTooltip(pGuiGraphics, this, this.minecraft.font, tipText, pMouseX, pMouseY + 12, 11);
            tipText = null;
        }
    }

    private void drawPage(GuiGraphics pGuiGraphics, Page pageParm, int side, int x, int y, int mx, int my) {
        PoseStack poseStack = pGuiGraphics.pose();
        if (this.lastCycle < System.currentTimeMillis()) {
            ++cycle;
            this.lastCycle = System.currentTimeMillis() + 1000L;
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        if (page == 0 && side == 0) {
            pGuiGraphics.blit(tex1, x + 4, y - 7, 24, 184, 96, 4);
            pGuiGraphics.blit(tex1, x + 4, y + 10, 24, 184, 96, 4);
            int offset = this.minecraft.font.width(research.getLocalizedName());
            if (offset <= 140) {
                pGuiGraphics.drawString(minecraft.font, research.getLocalizedName(), x - 15 + 140 / 2 - offset / 2, y, 0x202020);
            } else {
                float vv = 140.0f / offset;
                poseStack.pushPose();
                poseStack.translate(x - 15 + (float) 140 / 2 - (float) offset / 2 * vv, y + 1.0f * vv, 0.0f);
                poseStack.scale(vv, vv, vv);
                pGuiGraphics.drawString(minecraft.font, research.getLocalizedName(), 0, 0, 0x202020);
                poseStack.popPose();
            }
            y += 28;
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        for (Object content : pageParm.contents) {
            if (content instanceof String) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                String ss = ((String) content).replace("~B", "");
                pGuiGraphics.drawString(minecraft.font, ss, x - 15 + side * 152, y - 6, 0);
                y += this.minecraft.font.lineHeight;
                if (!((String) content).endsWith("~B")) {
                    continue;
                }
                y += (int) (this.minecraft.font.lineHeight * 0.66);
            } else {
                if (!(content instanceof PageImage)) {
                    continue;
                }
                PageImage pi = (PageImage) content;
                poseStack.pushPose();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.setShaderTexture(0, pi.loc);
                RenderSystem.enableBlend();
                int pad = (140 - pi.aw) / 2;
                poseStack.translate((float) (x - 15 + side * 152 + pad), (float) (y - 5), 0.0f);
                poseStack.scale(pi.scale, pi.scale, pi.scale);
                pGuiGraphics.blit(pi.loc, 0, 0, pi.x, pi.y, pi.w, pi.h);
                poseStack.popPose();
                y += pi.ah + 2;
            }
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        if (playerKnowledge.isResearchComplete("FIRSTSTEPS")) {
            y = (height - paneHeight) / 2 + 9;
            RenderSystem.setShaderTexture(0, this.tex1);
            int le = mouseInside(x - 48, y, 25, 16, mx, my) ? 0 : 3;
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawPopupAt(x - 48, y, 25, 16, mx, my, "tc.aspect.name");
            pGuiGraphics.blit(tex1, x - 48 + le, y, 76, 232, 24 - le, 16);
            pGuiGraphics.blit(tex1, x - 28, y, 100, 232, 4, 16);
        }
        if (playerKnowledge.isResearchComplete("KNOWLEDGETYPES") && !research.getKey().equals("KNOWLEDGETYPES")) {
            y = (height - paneHeight) / 2 + 32;
            RenderSystem.setShaderTexture(0, this.tex1);
            int le = mouseInside(x - 48, y, 25, 16, mx, my) ? 0 : 3;
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            drawPopupAt(x - 48, y, 25, 16, mx, my, "tc.knowledge.name");
            pGuiGraphics.blit(tex1, x - 49 + le, y, 44, 232, 24 - le, 16);
            pGuiGraphics.blit(tex1, x - 29, y, 100, 232, 4, 16);
        }
        ResearchStage stage = this.research.getStages()[this.currentStage];
        if (stage.getRecipes() != null) {
            this.drawRecipeBookmarks(pGuiGraphics, x, mx, my, stage);
        }
        if (this.page == 0 && side == 0 && !this.isComplete) {
            this.drawRequirements(pGuiGraphics, x, mx, my, stage);
        }
        if (playerKnowledge.isResearchComplete("KNOWLEDGETYPES") && research.getKey().equals("KNOWLEDGETYPES")) {
            drawKnowledges(pGuiGraphics, x, (height - paneHeight) / 2 - 16 + 210, mx, my, true);
        }
        if (showingAspects) {
            drawAspectsInsert(pGuiGraphics, mx, my);
        } else if (showingKnowledge) {
            drawKnowledgesInsert(pGuiGraphics, mx, my);
        }
        if (GuiResearchPage.shownRecipe != null) {
            drawRecipe(pGuiGraphics, mx, my);
        }
    }

    private void drawKnowledgesInsert(GuiGraphics pGuiGraphics, int mx, int my) {
        PoseStack poseStack = pGuiGraphics.pose();
        allowWithPagePopup = true;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, tex4);
        int x = (width - 256) / 2;
        int y = (height - 256) / 2;
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        pGuiGraphics.blit(tex4, x, y, 0, 0, 255, 255);
        RenderSystem.enableDepthTest();
        poseStack.pushPose();
        drawKnowledges(pGuiGraphics, x + 60, (height - paneHeight) / 2 + 75, mx, my, false);
        poseStack.popPose();
        RenderSystem.setShaderTexture(0, tex1);
        allowWithPagePopup = false;
    }

    private void drawKnowledges(GuiGraphics pGuiGraphics, int x, int y, int mx, int my, boolean inpage) {
        PoseStack poseStack = pGuiGraphics.pose();
        y -= 18;
        boolean drewSomething = false;
        int amt = 0;
        int par = 0;
        int tc = 0;
        int ka = ResearchCategories.researchCategories.values().size();
        for (IPlayerKnowledge.EnumKnowledgeType type : IPlayerKnowledge.EnumKnowledgeType.values()) {
            int fc = 0;
            int hs = (int) (164.0f / ka);
            boolean b = false;
            for (ResearchCategory category : ResearchCategories.researchCategories.values()) {
                if (!type.hasFields() && category != null) {
                    continue;
                }
                amt = playerKnowledge.getKnowledge(type, category);
                par = playerKnowledge.getKnowledgeRaw(type, category) % type.getProgression();
                if (amt <= 0 && par <= 0) {
                    continue;
                }
                drewSomething = true;
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                poseStack.pushPose();
                RenderSystem.setShaderTexture(0, HudHandler.KNOW_TYPE[type.ordinal()]);
                poseStack.translate((float) (x - 10 + (inpage ? 18 : hs) * fc), (float) (y - tc * (inpage ? 20 : 28)), 0.0f);
                poseStack.scale(0.0625f, 0.0625f, 0.0625f);
                pGuiGraphics.blit(HudHandler.KNOW_TYPE[type.ordinal()], 0, 0, 0, 0, 255, 255);
                if (type.hasFields() && category != null) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.75f);
                    RenderSystem.setShaderTexture(0, category.icon);
                    poseStack.translate(0.0f, 0.0f, 1.0f);
                    poseStack.scale(0.66f, 0.66f, 0.66f);
                    pGuiGraphics.blit(category.icon, 66, 66, 0, 0, 255, 255);
                }
                poseStack.popPose();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                poseStack.translate(0.0f, 0.0f, 5.0f);
                String s = "" + amt;
                int m = minecraft.font.width(s);
                pGuiGraphics.drawString(minecraft.font, s, (float) (x - 10 + 16 - m + (inpage ? 18 : hs) * fc), (float) (y - tc * (inpage ? 20 : 28) + 8), 0XFFFFFF, true);
                s = I18n.get("tc.type." + type.toString().toLowerCase());
                if (type.hasFields() && category != null) {
                    s = s + ": " + ResearchCategories.getCategoryName(category.key);
                }
                drawPopupAt(x - 10 + (inpage ? 18 : hs) * fc, y - tc * (inpage ? 20 : 28), mx, my, s);
                if (par > 0) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.75f);
                    RenderSystem.setShaderTexture(0, tex1);
                    int l = (int) (par / (float) type.getProgression() * 16.0f);
                    pGuiGraphics.blit(tex1, x - 10 + (inpage ? 18 : hs) * fc, y + 17 - tc * (inpage ? 20 : 28), 0, 232, l, 2);
                    pGuiGraphics.blit(tex1, x - 10 + (inpage ? 18 : hs) * fc + l, y + 17 - tc * (inpage ? 20 : 28), l, 234, 16 - l, 2);
                }
                poseStack.translate(0.0f, 0.0f, -5.0f);
                ++fc;
                b = true;
            }
            if (b) {
                ++tc;
            }
        }
        if (inpage && drewSomething) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, tex1);
            pGuiGraphics.blit(tex1, x + 4, y - tc * (inpage ? 20 : 28) + 12, 24, 184, 96, 8);
        }
    }

    private void drawRequirements(GuiGraphics pGuiGraphics, int x, int mx, int my, ResearchStage stage) {
        PoseStack poseStack = pGuiGraphics.pose();
        int y = (this.height - this.paneHeight) / 2 - 16 + 210;
        poseStack.pushPose();
        boolean b = false;
        if (stage.getCraft() != null) {
            y -= 18;
            b = true;
            int shift = 24;
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.25f);
            RenderSystem.setShaderTexture(0, this.tex1);
            RenderSystem.enableBlend();
            pGuiGraphics.blit(tex1, x - 12, y - 1, 200, 200, 56, 16);
            this.drawPopupAt(x - 15, y, mx, my, "tc.need.craft");
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            if (this.hasCraft != null) {
                if (this.hasCraft.length != stage.getCraft().length) {
                    this.hasCraft = new boolean[stage.getCraft().length];
                }
                int ss2 = 18;
                if (stage.getCraft().length > 6) {
                    ss2 = 110 / stage.getCraft().length;
                }
                for (int idx2 = 0; idx2 < stage.getCraft().length; ++idx2) {
                    ItemStack stack = InventoryUtils.cycleItemStack(stage.getCraft()[idx2], idx2);
                    this.drawStackAt(pGuiGraphics, stack, x - 15 + shift, y, mx, my, true);
                    if (hasCraft[idx2]) {
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                        RenderSystem.setShaderTexture(0, this.tex1);
                        RenderSystem.enableBlend();
                        RenderSystem.disableDepthTest();
                        pGuiGraphics.blit(tex1, x - 15 + shift + 8, y, 159, 207, 10, 10);
                        RenderSystem.enableDepthTest();
                    }
                    shift += ss2;
                }
            }
        }
        if (stage.getKnow() != null) {
            y -= 18;
            b = true;
            int shift = 24;
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.25f);
            RenderSystem.setShaderTexture(0, tex1);
            RenderSystem.enableBlend();
            pGuiGraphics.blit(tex1, x - 12, y - 1, 200, 184, 56, 16);
            drawPopupAt(x - 15, y, mx, my, "tc.need.know");
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            if (hasKnow != null) {
                if (hasKnow.length != stage.getKnow().length) {
                    hasKnow = new boolean[stage.getKnow().length];
                }
                int ss2 = 18;
                if (stage.getKnow().length > 6) {
                    ss2 = 110 / stage.getKnow().length;
                }
                for (int idx2 = 0; idx2 < stage.getKnow().length; ++idx2) {
                    ResearchStage.Knowledge kn = stage.getKnow()[idx2];
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    RenderSystem.enableBlend();
                    poseStack.pushPose();
                    RenderSystem.setShaderTexture(0, HudHandler.KNOW_TYPE[kn.type.ordinal()]);
                    poseStack.translate((float) (x - 15 + shift), (float) y, 0.0f);
                    poseStack.scale(0.0625F, 0.0625F, 0.0625F);
                    pGuiGraphics.blit(HudHandler.KNOW_TYPE[kn.type.ordinal()], 0, 0, 0, 0, 255, 255);
                    if (kn.type.hasFields() && kn.category != null) {
                        RenderSystem.setShaderTexture(0, kn.category.icon);
                        poseStack.translate(32.0f, 32.0f, 1.0f);
                        poseStack.pushPose();
                        poseStack.scale(0.75F, 0.75F, 0.75F);
                        pGuiGraphics.blit(kn.category.icon, 0, 0, 0, 0, 255, 255);
                        poseStack.popPose();
                    }
                    poseStack.popPose();
                    String am = "" + (hasKnow[idx2] ? "" : ChatFormatting.RED) + kn.amount;
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    poseStack.pushPose();
                    poseStack.translate((float) (x - 15 + shift + 16 - minecraft.font.width(am) / 2), (float) (y + 12), 5.0f);
                    poseStack.scale(0.5F, 0.5F, 0.5F);
                    pGuiGraphics.drawString(minecraft.font, am, 0, 0, 0XFFFFFF, true);
                    poseStack.popPose();
                    if (hasKnow[idx2]) {
                        poseStack.pushPose();
                        poseStack.translate(0.0f, 0.0f, 1.0f);
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                        RenderSystem.enableBlend();
                        RenderSystem.setShaderTexture(0, tex1);
                        pGuiGraphics.blit(tex1, x - 15 + shift + 8, y, 159, 207, 10, 10);
                        poseStack.popPose();
                    }
                    String s = I18n.get("tc.type." + kn.type.toString().toLowerCase());
                    if (kn.type.hasFields() && kn.category != null) {
                        s = s + ": " + ResearchCategories.getCategoryName(kn.category.key);
                    }
                    drawPopupAt(x - 15 + shift, y, mx, my, s);
                    shift += ss2;
                }
            }
        }
        if (b) {
            y -= 12;
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, this.tex1);
            RenderSystem.enableBlend();
            pGuiGraphics.blit(tex1, x + 4, y - 2, 24, 184, 96, 8);
            if (this.hasAllRequisites) {
                this.hrx = x + 20;
                this.hry = y - 6;
                if (this.hold) {
                    String s2 = I18n.get("tc.stage.hold");
                    int m = this.minecraft.font.width(s2);

                    pGuiGraphics.drawString(minecraft.font, s2, x + 52 - m / 2.0f, (float) (y - 4), 0XFFFFFF, true);
                } else {
                    if (mouseInside(hrx, hry, 64, 12, mx, my)) {
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    } else {
                        RenderSystem.setShaderColor(0.8f, 0.8f, 0.9f, 1.0f);
                    }
                    RenderSystem.setShaderTexture(0, this.tex1);
                    pGuiGraphics.blit(tex1, hrx, hry, 84, 216, 64, 12);
                    String s2 = I18n.get("tc.stage.complete");
                    int m = this.minecraft.font.width(s2);
                    pGuiGraphics.drawString(minecraft.font, s2, x + 52 - m / 2.0f, (float) (y - 4), 0XFFFFFF, true);
                }
            }
        }
        poseStack.popPose();
    }

    private void drawRecipeBookmarks(GuiGraphics pGuiGraphics, int x, int mx, int my, ResearchStage stage) {
        PoseStack poseStack = pGuiGraphics.pose();
        Random rng = new Random(this.rhash);
        poseStack.pushPose();
        int y = (this.height - this.paneHeight) / 2 - 8;
        this.allowWithPagePopup = true;
        if (!this.recipeOutputs.isEmpty()) {
            int space = Math.min(25, 200 / recipeOutputs.size());
            for (ResourceLocation rk : recipeOutputs.keySet()) {
                List list = recipeOutputs.get(rk);
                if (list != null && !list.isEmpty()) {
                    int i = cycle % list.size();
                    if (list.get(i) == null) {
                        continue;
                    }
                    int sh = rng.nextInt(3);
                    int le = rng.nextInt(3) + (mouseInside(x + 280, y - 1, 30, 16, mx, my) ? 0 : 3);
                    RenderSystem.enableBlend();
                    RenderSystem.setShaderTexture(0, this.tex1);
                    if (rk.equals(GuiResearchPage.shownRecipe)) {
                        RenderSystem.setShaderColor(1.0f, 0.5f, 0.5f, 1.0f);
                    } else {
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                    pGuiGraphics.blit(tex1, x + 280 + sh, y - 1, 120 + le, 232, 28, 16);
                    pGuiGraphics.blit(tex1, x + 280 + sh, y - 1, 116, 232, 4, 16);
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    UtilsFX.hideStackOverlay = true;
                    if (list.get(i) instanceof ItemStack) {
                        drawStackAt(pGuiGraphics, (ItemStack) list.get(i), x + 287 + sh - le, y - 1, mx, my, false);
                    }
                    UtilsFX.hideStackOverlay = false;
                    y += space;
                }
            }
        }
        this.allowWithPagePopup = false;
        poseStack.popPose();
    }

    private void generateRecipesLists(ResearchStage stage, ResearchAddendum[] addenda) {
        recipeLists.clear();
        recipeOutputs.clear();
        if (stage == null || stage.getRecipes() == null) {
            return;
        }
        for (ResourceLocation rk : stage.getRecipes()) {
            addRecipesToList(rk, recipeLists, recipeOutputs, rk);
        }
        if (addenda == null) {
            return;
        }
        for (ResearchAddendum addendum : addenda) {
            if (addendum.getRecipes() != null && ModCapabilities.knowsResearchStrict(this.minecraft.player, addendum.getResearch())) {
                for (ResourceLocation rk2 : addendum.getRecipes()) {
                    addRecipesToList(rk2, recipeLists, recipeOutputs, rk2);
                }
            }
        }
    }

    private void addRecipesToList(ResourceLocation rk, LinkedHashMap<ResourceLocation, ArrayList> recipeLists2, LinkedHashMap<ResourceLocation, ArrayList> recipeOutputs2, ResourceLocation rkey) {
        Object recipe = CommonInternals.getCatalogRecipe(rk);
        if (recipe == null) {
            recipe = CommonInternals.getCatalogRecipeFake(rk);
        }
        if (recipe == null) {
            var optional = this.minecraft.level.getRecipeManager().byKey(rk);
            if (optional.isPresent()) {
                recipe = optional.get();
            }
        }
        if (recipe == null) {
            recipe = ModRecipeProvider.recipeGroups.get(rk.toString());
        }
        if (recipe == null) {
            return;
        }
        if (recipe instanceof ArrayList) {
            for (ResourceLocation rl : (ArrayList<ResourceLocation>) recipe) {
                addRecipesToList(rl, recipeLists2, recipeOutputs2, rk);
            }
        } else {
            if (!recipeLists2.containsKey(rkey)) {
                recipeLists2.put(rkey, new ArrayList());
                recipeOutputs2.put(rkey, new ArrayList());
            }
            ArrayList list = recipeLists2.get(rkey);
            ArrayList outputs = recipeOutputs2.get(rkey);
            if (recipe instanceof IArcaneRecipe re3) {
                ItemStack is = InventoryUtils.cycleItemStack(re3.getResultItem(null), 0);
                if (is != null && !is.isEmpty() && ModCapabilities.knowsResearchStrict(minecraft.player, re3.getResearch())) {
                    list.add(re3);
                    outputs.add(re3.getResultItem(null));
                }
            } else if (recipe instanceof CraftingRecipe) {
                CraftingRecipe re4 = (CraftingRecipe) recipe;
                list.add(re4);
                outputs.add(re4.getResultItem(null));
            }
        }

    }

    private void drawRecipe(GuiGraphics pGuiGraphics, int mx, int my) {
        PoseStack poseStack = pGuiGraphics.pose();
        allowWithPagePopup = true;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, tex4);
        int x = (width - 256) / 2;
        int y = (height - 256) / 2;
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        pGuiGraphics.blit(tex4, x, y, 0, 0, 255, 255);
        RenderSystem.enableDepthTest();
        List list = recipeLists.get(GuiResearchPage.shownRecipe);
        if (list == null || list.isEmpty()) {
            list = drilldownLists.get(GuiResearchPage.shownRecipe);
        }
        if (list != null && !list.isEmpty()) {
            hasRecipePages = (list.size() > 1);
            recipePageMax = list.size() - 1;
            if (recipePage > recipePageMax) {
                recipePage = recipePageMax;
            }
            Object recipe = list.get(recipePage % list.size());
            if (recipe != null) {
                if (recipe instanceof IArcaneRecipe) {
                    drawArcaneCraftingPage(pGuiGraphics, x + 128, y + 128, mx, my, (IArcaneRecipe) recipe);
                } else if (recipe instanceof CraftingRecipe) {
                    drawCraftingPage(pGuiGraphics, x + 128, y + 128, mx, my, (CraftingRecipe) recipe);
                }
            }
            if (hasRecipePages) {
                RenderSystem.setShaderTexture(0, tex1);
                float bob = (float) (Math.sin(minecraft.player.tickCount / 3.0f) * 0.2f + 0.1f);
                if (recipePage > 0) {
                    drawTexturedModalRectScaled(poseStack, x + 40, y + 232, 0, 184, 12, 8, bob);
                }
                if (recipePage < recipePageMax) {
                    drawTexturedModalRectScaled(poseStack, x + 204, y + 232, 12, 184, 12, 8, bob);
                }
            }
        }
        allowWithPagePopup = false;
    }

    private void drawAspectsInsert(GuiGraphics pGuiGraphics, int mx, int my) {
        PoseStack poseStack = pGuiGraphics.pose();
        allowWithPagePopup = true;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, tex4);
        int x = (width - 256) / 2;
        int y = (height - 256) / 2;
        RenderSystem.disableDepthTest();
        pGuiGraphics.blit(tex4, x, y, 0, 0, 255, 255);
        RenderSystem.enableDepthTest();
        drawAspectPage(pGuiGraphics, x + 60, y + 24, mx, my);
        allowWithPagePopup = false;
    }

    private void drawAspectPage(GuiGraphics pGuiGraphics, int x, int y, int mx, int my) {
        PoseStack poseStack = pGuiGraphics.pose();
        if (knownPlayerAspects != null && knownPlayerAspects.size() > 0) {
            poseStack.pushPose();
            int mposx = mx;
            int mposy = my;
            int count = -1;
            int start = GuiResearchPage.aspectsPage * 5;
            for (Aspect aspect : knownPlayerAspects.getAspectsSortedByName()) {
                if (++count >= start) {
                    if (count > start + 4) {
                        break;
                    }
                    if (aspect.getImage() != null) {
                        int tx = x;
                        int ty = y + count % 5 * 40;
                        if (mposx >= tx && mposy >= ty && mposx < tx + 40 && mposy < ty + 40) {
                            RenderSystem.setShaderTexture(0, tex3);
                            poseStack.pushPose();
                            RenderSystem.enableBlend();
                            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                            poseStack.translate(x - 2, y + count % 5 * 40 - 2, 0.0);
                            poseStack.scale(2.0f, 2.0f, 0.0f);
                            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.5f);
                            pGuiGraphics.blit(tex3, 0, 0, 0, 0, 16, 16, 16, 16);
                            poseStack.popPose();
                        }
                        poseStack.pushPose();
                        poseStack.translate(x + 2, y + 2 + count % 5 * 40, 0.0);
                        poseStack.scale(1.5f, 1.5f, 1.5f);
                        UtilsFX.drawTag(pGuiGraphics, 0, 0, aspect, 0.0f, 0, poseStack.last().pose().m32());
                        poseStack.popPose();
                        poseStack.pushPose();
                        poseStack.translate(x + 16, y + 29 + count % 5 * 40, 0.0);
                        poseStack.scale(0.5f, 0.5f, 0.5f);
                        String text = aspect.getName();
                        int offset = minecraft.font.width(text) / 2;
                        pGuiGraphics.drawString(minecraft.font, text, -offset, 0, 0x505050);
                        poseStack.popPose();
                        if (aspect.getComponents() != null) {
                            poseStack.pushPose();
                            poseStack.translate(x + 60, y + 4 + count % 5 * 40, 0.0);
                            poseStack.scale(1.25f, 1.25f, 1.25f);
                            if (playerKnowledge.isResearchKnown("!" + aspect.getComponents()[0].getTag().toLowerCase())) {
                                UtilsFX.drawTag(pGuiGraphics, 0, 0, aspect.getComponents()[0], 0.0f, 0, poseStack.last().pose().m32());
                            } else {
                                RenderSystem.setShaderTexture(0, dummyResearch);
                                RenderSystem.setShaderColor(0.8f, 0.8f, 0.8f, 1.0f);
                                RenderSystem.enableBlend();
                                pGuiGraphics.blit(dummyResearch, 0, 0, 0, 0, 16, 16, 16, 16);
                            }
                            poseStack.popPose();
                            poseStack.pushPose();
                            poseStack.translate(x + 102, y + 4 + count % 5 * 40, 0.0);
                            poseStack.scale(1.25f, 1.25f, 1.25f);
                            if (playerKnowledge.isResearchKnown("!" + aspect.getComponents()[1].getTag().toLowerCase())) {
                                UtilsFX.drawTag(pGuiGraphics, 0, 0, aspect.getComponents()[1], 0.0f, 0, poseStack.last().pose().m32());
                            } else {
                                RenderSystem.setShaderTexture(0, dummyResearch);
                                RenderSystem.setShaderColor(0.8f, 0.8f, 0.8f, 1.0f);
                                RenderSystem.enableBlend();
                                pGuiGraphics.blit(dummyResearch, 0, 0, 0, 0, 16, 16, 16, 16);
                            }
                            poseStack.popPose();
                            if (playerKnowledge.isResearchKnown("!" + aspect.getComponents()[0].getTag().toLowerCase())) {
                                text = aspect.getComponents()[0].getName();
                                offset = minecraft.font.width(text) / 2;
                                poseStack.pushPose();
                                poseStack.translate(x + 22 + 50, y + 29 + count % 5 * 40, 0.0);
                                poseStack.scale(0.5f, 0.5f, 0.5f);
                                pGuiGraphics.drawString(minecraft.font, text, -offset, 0, 5263440);
                                poseStack.popPose();
                            }
                            if (playerKnowledge.isResearchKnown("!" + aspect.getComponents()[1].getTag().toLowerCase())) {
                                text = aspect.getComponents()[1].getName();
                                offset = minecraft.font.width(text) / 2;
                                poseStack.pushPose();
                                poseStack.translate(x + 22 + 92, y + 29 + count % 5 * 40, 0.0);
                                poseStack.scale(0.5f, 0.5f, 0.5f);
                                pGuiGraphics.drawString(minecraft.font, text, -offset, 0, 5263440);
                                poseStack.popPose();
                            }
                            pGuiGraphics.drawString(minecraft.font, "=", x + 9 + 32, y + 12 + count % 5 * 40, 10066329);
                            pGuiGraphics.drawString(minecraft.font, "+", x + 10 + 79, y + 12 + count % 5 * 40, 10066329);
                        } else {
                            pGuiGraphics.drawString(minecraft.font, I18n.get("tc.aspect.primal"), x + 54, y + 12 + count % 5 * 40, 0x777777);
                        }
                    }
                }
            }
            RenderSystem.setShaderTexture(0, tex1);
            float bob = Mth.sin(minecraft.player.tickCount / 3.0f) * 0.2f + 0.1f;
            if (GuiResearchPage.aspectsPage > 0) {
                drawTexturedModalRectScaled(poseStack, x - 20, y + 208, 0, 184, 12, 8, bob);
            }
            if (GuiResearchPage.aspectsPage < maxAspectPages - 1) {
                drawTexturedModalRectScaled(poseStack, x + 144, y + 208, 12, 184, 12, 8, bob);
            }
            poseStack.popPose();
        }
    }

    private void drawArcaneCraftingPage(GuiGraphics pGuiGraphics, int x, int y, int mx, int my, IArcaneRecipe recipe) {
        PoseStack poseStack = pGuiGraphics.pose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, tex2);
        poseStack.pushPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        poseStack.translate((float) x, (float) y, 0.0f);
        poseStack.scale(2.0f, 2.0f, 1.0f);
        pGuiGraphics.blit(tex2, -26, -26, 112, 15, 52, 52);
        pGuiGraphics.blit(tex2, -8, -46, 20, 3, 16, 16);
        poseStack.popPose();
        poseStack.pushPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.4f);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        poseStack.translate((float) x, (float) y, 0.0f);
        poseStack.scale(2.0f, 2.0f, 1.0f);
        pGuiGraphics.blit(tex2, -6, 40, 68, 76, 12, 12);
        poseStack.popPose();
        String text = "" + recipe.getVis();
        int offset = minecraft.font.width(text);
        pGuiGraphics.drawString(minecraft.font, text, x - offset / 2, y + 90, 5263440);
        drawPopupAt(x - offset / 2 - 15, y + 75, 30, 30, mx, my, "wandtable.text1");
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.translate(0.0, 0.0, 100.0);
        drawStackAt(pGuiGraphics, InventoryUtils.cycleItemStack(recipe.getResultItem(null), 0), x - 8, y - 84, mx, my, false);
        AspectList crystals = recipe.getCrystals();
        if (crystals != null) {
            int a = 0;
            int sz = crystals.size();
            for (Aspect aspect : crystals.getAspects()) {
                drawStackAt(pGuiGraphics, InventoryUtils.cycleItemStack(ArcanaApiHelper.makeCrystal(aspect, crystals.getAmount(aspect)), a), x + 4 - sz * 10 + a * 20, y + 59, mx, my, true);
                ++a;
            }
        }
        if (recipe instanceof ShapedArcaneRecipe) {
            text = I18n.get("recipe.type.arcane");
            offset = minecraft.font.width(text);
            pGuiGraphics.drawString(minecraft.font, text, x - offset / 2, y - 104, 5263440);
            int rw = ((ShapedArcaneRecipe) recipe).getRecipeWidth();
            int rh = ((ShapedArcaneRecipe) recipe).getRecipeHeight();
            NonNullList<Ingredient> items = recipe.getIngredients();
            for (int i = 0; i < rw && i < 3; ++i) {
                for (int j = 0; j < rh && j < 3; ++j) {
                    if (items.get(i + j * rw) != null) {
                        drawStackAt(pGuiGraphics, InventoryUtils.cycleItemStack(items.get(i + j * rw), i + j * rw), x - 40 + i * 32, y - 40 + j * 32, mx, my, true);
                    }
                }
            }
        }
        poseStack.popPose();
    }

    private void drawCraftingPage(GuiGraphics pGuiGraphics, int x, int y, int mx, int my, CraftingRecipe recipe) {
        PoseStack poseStack = pGuiGraphics.pose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        if (recipe == null) {
            return;
        }
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, tex2);
        poseStack.pushPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        poseStack.translate((float) x, (float) y, 0.0f);
        poseStack.scale(2.0f, 2.0f, 1.0f);
        pGuiGraphics.blit(tex2, -26, -26, 60, 15, 51, 52);
        pGuiGraphics.blit(tex2, -8, -46, 20, 3, 16, 16);
        poseStack.popPose();
        drawStackAt(pGuiGraphics, InventoryUtils.cycleItemStack(recipe.getResultItem(null), 0), x - 8, y - 84, mx, my, false);
        if (recipe instanceof ShapedRecipe) {
            String text = I18n.get("recipe.type.workbench");
            int offset = minecraft.font.width(text);
            pGuiGraphics.drawString(minecraft.font, text, x - offset / 2, y - 104, 5263440);
            int rw = ((ShapedRecipe) recipe).getRecipeWidth();
            int rh = ((ShapedRecipe) recipe).getRecipeHeight();
            NonNullList<Ingredient> items = recipe.getIngredients();
            for (int i = 0; i < rw && i < 3; ++i) {
                for (int j = 0; j < rh && j < 3; ++j) {
                    if (items.get(i + j * rw) != null) {
                        drawStackAt(pGuiGraphics, InventoryUtils.cycleItemStack(items.get(i + j * rw), i + j * rw), x - 40 + i * 32, y - 40 + j * 32, mx, my, true);
                    }
                }
            }
        }
        if (recipe instanceof ShapelessRecipe) {
            String text = I18n.get("recipe.type.workbenchshapeless");
            int offset = minecraft.font.width(text);
            pGuiGraphics.drawString(minecraft.font, text, x - offset / 2, y - 104, 5263440);
            NonNullList<Ingredient> items2 = recipe.getIngredients();
            for (int k = 0; k < items2.size() && k < 9; ++k) {
                if (items2.get(k) != null) {
                    drawStackAt(pGuiGraphics, InventoryUtils.cycleItemStack(items2.get(k), k), x - 40 + k % 3 * 32, y - 40 + k / 3 * 32, mx, my, true);
                }
            }
        }
        poseStack.popPose();
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == minecraft.options.keyInventory.getKey().getValue() || pKeyCode == InputConstants.KEY_ESCAPE) {
            if (GuiResearchPage.shownRecipe != null || showingAspects || showingKnowledge) {
                GuiResearchPage.shownRecipe = null;
                showingAspects = false;
                showingKnowledge = false;
                Minecraft.getInstance().player.playSound(ModSounds.page.get(), 0.4f, 1.1f);
            } else {
                minecraft.setScreen(new GuiResearchBrowser(guiMapX, guiMapY));
            }
            return true;
        } else {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
    }

    private void nextPage() {
        if (page < maxPages - 2) {
            page += 2;
            lastCycle = 0L;
            cycle = -1;
            Minecraft.getInstance().player.playSound(ModSounds.page.get(), 0.66f, 1.0f);
        }
    }

    private void prevPage() {
        if (page >= 2) {
            page -= 2;
            lastCycle = 0L;
            cycle = -1;
            Minecraft.getInstance().player.playSound(ModSounds.page.get(), 0.66f, 1.0f);
        }
    }

    private void goBack() {
        if (!GuiResearchPage.history.isEmpty()) {
            Minecraft.getInstance().player.playSound(ModSounds.page.get(), 0.66f, 1.0f);
            GuiResearchPage.shownRecipe = GuiResearchPage.history.pop();
        } else {
            GuiResearchPage.shownRecipe = null;
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int centerX = (width - paneWidth) / 2;
        int centerY = (height - paneHeight) / 2;
        double mx = pMouseX - hrx;
        double my = pMouseY - hry;
        if (GuiResearchPage.shownRecipe == null && !hold && hasAllRequisites && mx >= 0 && my >= 0 && mx < 64 && my < 12) {
            PacketHandler.INSTANCE.sendToServer(new PacketSyncProgressToServer(research.getKey(), false, true, true));
            Minecraft.getInstance().player.playSound(ModSounds.write.get(), 0.66f, 1.0f);
            lastCheck = 0L;
            lastStage = currentStage;
            hold = true;
            keyCache.clear();
            drilldownLists.clear();
        }
        if (knownPlayerAspects != null && playerKnowledge.isResearchComplete("FIRSTSTEPS")) {
            mx = pMouseX - (centerX - 48);
            my = pMouseY - (centerY + 8);
            if (mx >= 0 && my >= 0 && mx < 25 && my < 16) {
                GuiResearchPage.shownRecipe = null;
                showingKnowledge = false;
                showingAspects = !showingAspects;
                GuiResearchPage.history.clear();
                if (GuiResearchPage.aspectsPage > maxAspectPages) {
                    GuiResearchPage.aspectsPage = 0;
                }
                Minecraft.getInstance().player.playSound(ModSounds.page.get(), 0.7f, 0.9f);
            }
        }
        if (playerKnowledge.isResearchComplete("KNOWLEDGETYPES") && !research.getKey().equals("KNOWLEDGETYPES")) {
            mx = pMouseX - (centerX - 48);
            my = pMouseY - (centerY + 31);
            if (mx >= 0 && my >= 0 && mx < 25 && my < 16) {
                GuiResearchPage.shownRecipe = null;
                showingAspects = false;
                showingKnowledge = !showingKnowledge;
                GuiResearchPage.history.clear();
                Minecraft.getInstance().player.playSound(ModSounds.page.get(), 0.7f, 0.9f);
            }
        }
        if (!recipeLists.isEmpty()) {
            int aa = 0;
            int space = Math.min(25, 200 / recipeLists.size());
            for (ResourceLocation rk : recipeLists.keySet()) {
                mx = pMouseX - (centerX + 280);
                my = pMouseY - (centerY - 8 + aa * space);
                if (mx >= 0 && my >= 0 && mx < 30 && my < 16) {
                    if (rk.equals(GuiResearchPage.shownRecipe)) {
                        GuiResearchPage.shownRecipe = null;
                    } else {
                        GuiResearchPage.shownRecipe = rk;
                    }
                    showingAspects = false;
                    showingKnowledge = false;
                    GuiResearchPage.history.clear();
                    Minecraft.getInstance().player.playSound(ModSounds.page.get(), 0.7f, 0.9f);
                    break;
                }
                ++aa;
            }
        }
        mx = pMouseX - (centerX + 205);
        my = pMouseY - (centerY + 192);
        if (hasRecipePages && recipePage < recipePageMax && mx >= 0 && my >= 0 && mx < 14 && my < 14) {
            ++recipePage;
            Minecraft.getInstance().player.playSound(ModSounds.page.get(), 0.7f, 0.9f);
        }
        mx = pMouseX - (centerX + 38);
        my = pMouseY - (centerY + 192);
        if (hasRecipePages && recipePage > 0 && mx >= 0 && my >= 0 && mx < 14 && my < 14) {
            --recipePage;
            Minecraft.getInstance().player.playSound(ModSounds.page.get(), 0.7f, 0.9f);
        }
        mx = pMouseX - (centerX + 261);
        my = pMouseY - (centerY + 189);
        if (GuiResearchPage.shownRecipe == null && mx >= 0 && my >= 0 && mx < 14 && my < 10) {
            nextPage();
        }
        mx = pMouseX - (centerX - 17);
        my = pMouseY - (centerY + 189);
        if (GuiResearchPage.shownRecipe == null && mx >= 0 && my >= 0 && mx < 14 && my < 10) {
            prevPage();
        }
        mx = pMouseX - (centerX + 118);
        my = pMouseY - (centerY + 190);
        if (mx >= 0 && my >= 0 && mx < 20 && my < 12) {
            goBack();
        }
        if (!reference.isEmpty()) {
            for (List coords : reference) {
                if (pMouseX >= (int) coords.get(0) && pMouseY >= (int) coords.get(1) && pMouseX < (int) coords.get(0) + 16 && pMouseY < (int) coords.get(1) + 16) {
                    try {
                        Minecraft.getInstance().player.playSound(ModSounds.page.get(), 0.66f, 1.0f);
                    } catch (Exception ignored) {
                    }
                    if (GuiResearchPage.shownRecipe != null) {
                        GuiResearchPage.history.push(new ResourceLocation(GuiResearchPage.shownRecipe.getNamespace(), GuiResearchPage.shownRecipe.getPath()));
                    }
                    GuiResearchPage.shownRecipe = (ResourceLocation) coords.get(2);
                    recipePage = Integer.parseInt((String) coords.get(3));
                    if (!drilldownLists.containsKey(GuiResearchPage.shownRecipe)) {
                        addRecipesToList(GuiResearchPage.shownRecipe, drilldownLists, new LinkedHashMap<>(), GuiResearchPage.shownRecipe);
                    }
                    break;
                }
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void drawPopupAt(int x, int y, int mx, int my, String text) {
        if ((GuiResearchPage.shownRecipe == null || this.allowWithPagePopup) && mx >= x && my >= y && mx < x + 16 && my < y + 16) {
            ArrayList<String> s = new ArrayList<>();
            s.add(Component.translatable(text).getString());
            this.tipText = s;
        }
    }

    void drawPopupAt(int x, int y, int w, int h, int mx, int my, String text) {
        if ((GuiResearchPage.shownRecipe == null || allowWithPagePopup) && mx >= x && my >= y && mx < x + w && my < y + h) {
            ArrayList<String> s = new ArrayList<>();
            s.add(Component.translatable(text).getString());
            tipText = s;
        }
    }

    private boolean mouseInside(int x, int y, int w, int h, int mx, int my) {
        return mx >= x && my >= y && mx < x + w && my < y + h;
    }

    void drawStackAt(GuiGraphics pGuiGraphics, ItemStack itemstack, int x, int y, int mx, int my, boolean clickthrough) {
        UtilsFX.renderItemStack(pGuiGraphics, this.minecraft, itemstack, x, y, null);
        if ((GuiResearchPage.shownRecipe == null || allowWithPagePopup) && mx >= x && my >= y && mx < x + 16 && my < y + 16 && itemstack != null && !itemstack.isEmpty() && itemstack.getItem() != null) {
            if (clickthrough) {
                ArrayList<String> addtext = new ArrayList<>(itemstack.getTooltipLines(this.minecraft.player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL).stream().map(Component::getString).toList());
                String ref = getCraftingRecipeKey(this.minecraft.player, itemstack);
                if (ref != null) {
                    String[] sr = ref.split(";", 2);
                    if (sr.length > 1) {
                        ResourceLocation res = new ResourceLocation(sr[0]);
                        if (res.getPath().equals("UNKNOWN")) {
                            addtext.add(ChatFormatting.DARK_RED + "" + ChatFormatting.ITALIC + I18n.get("recipe.unknown"));
                        } else {
                            addtext.add(ChatFormatting.BLUE + "" + ChatFormatting.ITALIC + I18n.get("recipe.clickthrough"));
                            reference.add(Arrays.asList(mx, my, (Comparable) res, sr[1]));
                        }
                    }
                }
                tipText = addtext;
            } else {
                tipText = itemstack.getTooltipLines(this.minecraft.player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL).stream().map(Component::getString).toList();
            }
        }
    }

    public void drawTexturedModalRectScaled(PoseStack pPoseStack, int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight, float scale) {
        pPoseStack.pushPose();
        float var7 = 0.00390625f;
        float var8 = 0.00390625f;
        pPoseStack.translate(pX + pUWidth / 2.0f, pY + pVHeight / 2.0f, 0.0f);
        pPoseStack.scale(1.0f + scale, 1.0f + scale, 1.0f);
        var matrix = pPoseStack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(matrix, -pUWidth / 2.0f, pVHeight / 2.0f, pPoseStack.last().pose().m32()).uv((pUOffset + 0) * var7, (pVOffset + pVHeight) * var8).endVertex();
        builder.vertex(matrix, pUWidth / 2.0f, pVHeight / 2.0f, pPoseStack.last().pose().m32()).uv((pUOffset + pUWidth) * var7, (pVOffset + pVHeight) * var8).endVertex();
        builder.vertex(matrix, pUWidth / 2.0f, -pVHeight / 2.0f, pPoseStack.last().pose().m32()).uv((pUOffset + pUWidth) * var7, (pVOffset + 0) * var8).endVertex();
        builder.vertex(matrix, -pUWidth / 2.0f, -pVHeight / 2.0f, pPoseStack.last().pose().m32()).uv((pUOffset + 0) * var7, (pVOffset + 0) * var8).endVertex();
        BufferUploader.drawWithShader(builder.end());
        pPoseStack.popPose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void parsePages() {
        this.checkRequisites();
        this.pages.clear();
        if (this.research.getStages() == null) {
            return;
        }
        boolean complete = false;
        this.currentStage = ModCapabilities.getKnowledge(this.minecraft.player).getResearchStage(this.research.getKey()) - 1;
        while (this.currentStage >= this.research.getStages().length) {
            --this.currentStage;
            complete = true;
        }
        if (currentStage < 0) {
            this.currentStage = 0;
        }
        ResearchStage stage = this.research.getStages()[this.currentStage];
        ResearchAddendum[] addenda = null;
        if (research.getAddenda() != null && complete) {
            addenda = research.getAddenda();
        }
        this.generateRecipesLists(stage, addenda);
        String rawText = stage.getTextLocalized();
        if (addenda != null) {
            int ac = 0;
            for (ResearchAddendum addendum : addenda) {
                if (ModCapabilities.knowsResearchStrict(this.minecraft.player, addendum.getResearch())) {
                    ++ac;
                    Component text = Component.translatable("tc.addendumtext", ac);
                    rawText = rawText + "<PAGE>" + text.getString() + "<BR>" + addendum.getTextLocalized();
                }
            }
        }
        rawText = rawText.replaceAll("<BR>", "~B\n\n");
        rawText = rawText.replaceAll("<BR/>", "~B\n\n");
        rawText = rawText.replaceAll("<LINE>", "~L");
        rawText = rawText.replaceAll("<LINE/>", "~L");
        rawText = rawText.replaceAll("<DIV>", "~D");
        rawText = rawText.replaceAll("<DIV/>", "~D");
        rawText = rawText.replaceAll("<PAGE>", "~P");
        rawText = rawText.replaceAll("<PAGE/>", "~P");
        ArrayList<PageImage> images = new ArrayList<>();
        String[] split;
        String[] imgSplit = split = rawText.split("<IMG>");
        for (String s : split) {
            int i = s.indexOf("</IMG>");
            if (i >= 0) {
                String clean = s.substring(0, i);
                PageImage pi = PageImage.parse(clean);
                if (pi == null) {
                    rawText = rawText.replaceFirst(clean, "\n");
                } else {
                    images.add(pi);
                    rawText = rawText.replaceFirst(clean, "~I");
                }
            }
        }
        rawText = rawText.replaceAll("<IMG>", "");
        rawText = rawText.replaceAll("</IMG>", "");
        List<String> firstPassText = new ArrayList<>();
        String[] temp = rawText.split("~P");
        for (int a = 0; a < temp.length; ++a) {
            String t = temp[a];
            String[] temp2 = t.split("~D");
            for (int x = 0; x < temp2.length; ++x) {
                String t2 = temp2[x];
                String[] temp3 = t2.split("~L");
                for (int b = 0; b < temp3.length; ++b) {
                    String t3 = temp3[b];
                    String[] temp4 = t3.split("~I");
                    for (int c = 0; c < temp4.length; ++c) {
                        String t4 = temp4[c];
                        firstPassText.add(t4);
                        if (c != temp4.length - 1) {
                            firstPassText.add("~I");
                        }
                    }
                    if (b != temp3.length - 1) {
                        firstPassText.add("~L");
                    }
                }
                if (x != temp2.length - 1) {
                    firstPassText.add("~D");
                }
            }
            if (a != temp.length - 1) {
                firstPassText.add("~P");
            }
        }

        List<String> parsedText = new ArrayList<>();
        for (String s2 : firstPassText) {
            List<String> pt1 = this.minecraft.font.getSplitter().splitLines(s2, 140, Style.EMPTY).stream().map(FormattedText::getString).toList();
            for (String ln : pt1) {
                parsedText.add(ln);
            }
        }
        int lineHeight = this.minecraft.font.lineHeight;
        int heightRemaining = 182;
        int dividerSpace = 0;
        if (research.getKey().equals("KNOWLEDGETYPES")) {
            heightRemaining -= 2;
            int tc = 0;
            int amt = 0;
            for (IPlayerKnowledge.EnumKnowledgeType type : IPlayerKnowledge.EnumKnowledgeType.values()) {
                for (ResearchCategory category : ResearchCategories.researchCategories.values()) {
                    if (!type.hasFields() && category != null) {
                        continue;
                    }
                    amt = playerKnowledge.getKnowledgeRaw(type, category);
                    if (amt > 0) {
                        ++tc;
                        break;
                    }
                }
            }
            heightRemaining -= 20 * tc;
            dividerSpace = 12;
        }
        if (!isComplete) {
            if (stage.getCraft() != null) {
                heightRemaining -= 18;
                dividerSpace = 15;
            }
            if (stage.getKnow() != null) {
                heightRemaining -= 18;
                dividerSpace = 15;
            }
        }
        heightRemaining -= dividerSpace;
        Page page1 = new Page();
        ArrayList<PageImage> tempImages = new ArrayList<>();
        for (String line : parsedText) {
            if (line.contains("~I")) {
                if (!images.isEmpty()) {
                    tempImages.add(images.remove(0));
                }
                line = "";
            }
            if (line.contains("~L")) {
                tempImages.add(GuiResearchPage.PILINE);
                line = "";
            }
            if (line.contains("~D")) {
                tempImages.add(GuiResearchPage.PIDIV);
                line = "";
            }
            if (line.contains("~P")) {
                heightRemaining = 210;
                this.pages.add(page1.copy());
                page1 = new Page();
                line = "";
            }
            if (!line.isEmpty()) {
                line = line.trim();
                page1.contents.add(line);
                heightRemaining -= lineHeight;
                if (line.endsWith("~B")) {
                    heightRemaining -= (int) (lineHeight * 0.66);
                }
            }
            while (!tempImages.isEmpty() && heightRemaining >= tempImages.get(0).ah + 2) {
                heightRemaining -= tempImages.get(0).ah + 2;
                page1.contents.add(tempImages.remove(0));
            }
            if (heightRemaining < lineHeight && !page1.contents.isEmpty()) {
                heightRemaining = 210;
                this.pages.add(page1.copy());
                page1 = new Page();
            }
        }
        if (!page1.contents.isEmpty()) {
            this.pages.add(page1.copy());
        }
        page1 = new Page();
        heightRemaining = 210;
        while (!tempImages.isEmpty()) {
            if (heightRemaining < tempImages.get(0).ah + 2) {
                heightRemaining = 210;
                pages.add(page1.copy());
                page1 = new Page();
            } else {
                heightRemaining -= tempImages.get(0).ah + 2;
                page1.contents.add(tempImages.remove(0));
            }
        }
        if (!page1.contents.isEmpty()) {
            this.pages.add(page1.copy());
        }
        this.rhash = research.getKey().hashCode() + currentStage * 50;
        this.maxPages = pages.size();
    }

    private void checkRequisites() {
        if (this.research.getStages() != null) {
            this.isComplete = this.playerKnowledge.isResearchComplete(this.research.getKey());
            while (currentStage >= research.getStages().length) {
                --currentStage;
            }
            if (currentStage < 0) {
                return;
            }
            hasAllRequisites = true;
            hasCraft = null;
            hasKnow = null;
            ResearchStage stage = this.research.getStages()[this.currentStage];
            Object[] c = stage.getCraft();
            if (c != null) {
                this.hasCraft = new boolean[c.length];
                for (int a2 = 0; a2 < c.length; ++a2) {
                    if (!this.playerKnowledge.isResearchKnown("[#]" + stage.getCraftReference()[a2])) {
                        this.hasAllRequisites = false;
                        this.hasCraft[a2] = false;
                    } else {
                        this.hasCraft[a2] = true;
                    }
                }
            }
            ResearchStage.Knowledge[] k = stage.getKnow();
            if (k != null) {
                hasKnow = new boolean[k.length];
                for (int a4 = 0; a4 < k.length; ++a4) {
                    int pk = playerKnowledge.getKnowledge(k[a4].type, k[a4].category);
                    if (pk < k[a4].amount) {
                        hasAllRequisites = false;
                        hasKnow[a4] = false;
                    } else {
                        hasKnow[a4] = true;
                    }
                }
            }
        }
    }

    private int findRecipePage(ResourceLocation rk, ItemStack stack, int start) {
        Object recipe = CommonInternals.getCatalogRecipe(rk);
        if (recipe == null) {
            recipe = CommonInternals.getCatalogRecipeFake(rk);
        }
        if (recipe == null) {
            var optional = this.minecraft.level.getRecipeManager().byKey(rk);
            if (optional.isPresent()) {
                recipe = optional.get();
            }
        }
        if (recipe == null) {
            recipe = ModRecipeProvider.recipeGroups.get(rk.toString());
        }
        if (recipe == null) {
            return -1;
        }
        if (recipe instanceof ArrayList) {
            int g = 0;
            for (ResourceLocation rl : (ArrayList<ResourceLocation>) recipe) {
                int q = findRecipePage(rl, stack, g);
                if (q >= 0) {
                    return q;
                }
                ++g;
            }
        }
        if (recipe instanceof CraftingRecipe && ((CraftingRecipe) recipe).getResultItem(null).equals(stack, true)) {
            if (recipe instanceof IArcaneRecipe && !ModCapabilities.knowsResearchStrict(minecraft.player, ((IArcaneRecipe) recipe).getResearch())) {
                return -99;
            }
            return start;
        } else {
            return -1;
        }
    }

    private String getCraftingRecipeKey(Player player, ItemStack stack) {
        int key = stack.serializeNBT().toString().hashCode();
        if (keyCache.containsKey(key)) {
            return keyCache.get(key);
        }
        for (ResearchCategory rcl : ResearchCategories.researchCategories.values()) {
            for (ResearchEntry ri : rcl.research.values()) {
                if (ri.getStages() == null) {
                    continue;
                }
                for (int a = 0; a < ri.getStages().length; ++a) {
                    ResearchStage stage = ri.getStages()[a];
                    if (stage.getRecipes() != null) {
                        for (ResourceLocation rec : stage.getRecipes()) {
                            int result = findRecipePage(rec, stack, 0);
                            if (result != -1) {
                                String s = rec.toString();
                                if (result == -99) {
                                    s = new ResourceLocation("UNKNOWN").toString();
                                } else {
                                    s = s + ";" + result;
                                }
                                keyCache.put(key, s);
                                return s;
                            }
                        }
                    }
                }
            }
        }
        keyCache.put(key, null);
        return null;
    }

    private static class Page {
        ArrayList contents = new ArrayList();

        public Page copy() {
            Page p = new Page();
            p.contents.addAll(this.contents);
            return p;
        }
    }

    private static class PageImage {
        int x;
        int y;
        int w;
        int h;
        int aw;
        int ah;
        float scale;
        ResourceLocation loc;

        public static PageImage parse(String text) {
            String[] s = text.split(":");
            if (s.length != 7) {
                return null;
            }
            try {
                PageImage pi = new PageImage();
                pi.loc = new ResourceLocation(s[0], s[1]);
                pi.x = Integer.parseInt(s[2]);
                pi.y = Integer.parseInt(s[3]);
                pi.w = Integer.parseInt(s[4]);
                pi.h = Integer.parseInt(s[5]);
                pi.scale = Float.parseFloat(s[6]);
                pi.aw = (int) (pi.w * pi.scale);
                pi.ah = (int) (pi.h * pi.scale);
                if (pi.ah > 208 || pi.aw > 140) {
                    return null;
                }
                return pi;
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
