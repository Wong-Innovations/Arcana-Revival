package arcana.client.lib.events;

import arcana.Arcana;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.common.lib.crafting.CraftingManager;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.research.ResearchCategory;
import arcana.client.lib.UtilsFX;
import arcana.common.items.tools.ItemThaumometer;
import arcana.common.world.aura.AuraChunk;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.util.concurrent.LinkedBlockingQueue;

public class HudHandler {
    public static ResourceLocation HUD = new ResourceLocation(Arcana.MODID, "textures/gui/hud.png");
    public LinkedBlockingQueue<KnowledgeGainTracker> knowledgeGainTrackers;
    public static ResourceLocation[] KNOW_TYPE = new ResourceLocation[]{new ResourceLocation(Arcana.MODID, "textures/research/knowledge_theory.png"), new ResourceLocation(Arcana.MODID, "textures/research/knowledge_observation.png")};
    public static AuraChunk currentAura = new AuraChunk(null, (short) 0, 0.0f, 0.0f);
    DecimalFormat secondsFormatter;

    public HudHandler() {
        this.knowledgeGainTrackers = new LinkedBlockingQueue<>();
        secondsFormatter = new DecimalFormat("#######.#");
    }

    @OnlyIn(Dist.CLIENT)
    void renderHuds(GuiGraphics pGuiGraphics, Minecraft mc, float renderTickTime, Player player, long time) {
        PoseStack poseStack = pGuiGraphics.pose();
        poseStack.pushPose();
        Window sr = Minecraft.getInstance().getWindow();
        int ww = sr.getGuiScaledWidth();
        int hh = sr.getGuiScaledHeight();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        if (mc.isWindowActive() && !mc.options.hideGui) {
            RenderSystem.setShaderTexture(0, HUD);
            ItemStack handStack = player.getItemInHand(InteractionHand.MAIN_HAND);
            boolean rT = false;
            int start = 0;
            for (int a = 0; a < 2; ++a) {
                if (!rT && handStack.getItem() instanceof ItemThaumometer) {
                    renderThaumometerHud(pGuiGraphics, mc, renderTickTime, player, time, ww, hh, start);
                    rT = true;
                    start += 80;
                }
                handStack = player.getItemInHand(InteractionHand.OFF_HAND);
            }
        }
        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    void renderThaumometerHud(GuiGraphics pGuiGraphics, Minecraft mc, float partialTicks, Player player, long time, int ww, int hh, int shifty) {
        PoseStack poseStack = pGuiGraphics.pose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        float base = Mth.clamp(HudHandler.currentAura.getBase() / 525.0f, 0.0f, 1.0f);
        float vis = Mth.clamp(HudHandler.currentAura.getVis() / 525.0f, 0.0f, 1.0f);
        float flux = Mth.clamp(HudHandler.currentAura.getFlux() / 525.0f, 0.0f, 1.0f);
        float count = Minecraft.getInstance().getCameraEntity().tickCount + partialTicks;
        float count2 = Minecraft.getInstance().getCameraEntity().tickCount / 3.0f + partialTicks;
        if (flux + vis > 1.0f) {
            float m = 1.0f / (flux + vis);
            base *= m;
            vis *= m;
            flux *= m;
        }
        float start = 10.0f + (1.0f - vis) * 64.0f;
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        poseStack.translate(2.0, shifty, 0.0);
        if (vis > 0.0f) {
            poseStack.pushPose();
            RenderSystem.setShaderColor(0.7f, 0.4f, 0.9f, 1.0f);
            RenderSystem.enableBlend();
            poseStack.translate(5.0, start, 0.0);
            poseStack.scale(1.0F, vis, 1.0F);
            UtilsFX.drawTexturedQuad(poseStack, 0.0f, 0.0f, 88.0f, 56.0f, 8.0f, 64.0f, -90.0);
            poseStack.popPose();

            poseStack.pushPose();
            RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, 1);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.5f);
            poseStack.translate(5.0, start, 0.0);
            UtilsFX.drawTexturedQuad(poseStack, 0.0f, 0.0f, 96.0f, 56.0f + count % 64.0f, 8.0f, vis * 64.0f, -90.0);
            RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
            poseStack.popPose();

            if (player.isCrouching()) {
                poseStack.pushPose();
                poseStack.translate(16.0, start, 0.0);
                poseStack.scale(0.5F, 0.5F, 0.5F);
                String msg = secondsFormatter.format(HudHandler.currentAura.getVis());
                pGuiGraphics.drawString(mc.font, msg, 0, 0, 0XEEAAFF, true);
                poseStack.popPose();
                RenderSystem.setShaderTexture(0, HUD);
            }
            poseStack.popPose();
        }

        poseStack.pushPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        UtilsFX.drawTexturedQuad(poseStack, 3.0f, 1.0f, 72.0f, 48.0f, 16.0f, 80.0f, -90.0);
        poseStack.popPose();

        start = 8.0f + (1.0f - base) * 64.0f;

        poseStack.pushPose();
        RenderSystem.enableBlend();
        UtilsFX.drawTexturedQuad(poseStack, 4.0f, start, 117.0f, 61.0f, 14.0f, 5.0f, -90.0);
        poseStack.popPose();

        poseStack.popPose();
    }

    public void renderAspectsInGui(GuiGraphics guiGraphics, ItemStack stack, int sd, int sx, int sy) {
        AspectList tags = CraftingManager.getObjectTags(stack);
        if (tags != null) {
            PoseStack poseStack = guiGraphics.pose();
            poseStack.pushPose();
            int x;
            int y;
            int index = 0;
            if (tags.size() > 0) {
                Aspect[] var11 = tags.getAspectsSortedByAmount();

                for (Aspect tag : var11) {
                    if (tag != null) {
                        x = sx + index * 18;
                        y = sy + sd - 16;
                        UtilsFX.drawColoredTag(guiGraphics, x, y, tag, (float) tags.getAmount(tag), 0, guiGraphics.pose().last().pose().m32());
                        ++index;
                    }
                }
            }

            poseStack.popPose();
        }
    }

    public static class KnowledgeGainTracker {
        IPlayerKnowledge.EnumKnowledgeType type;
        ResearchCategory category;
        int progress;
        int max;
        long seed;
        boolean sparks;

        public KnowledgeGainTracker(IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int progress, long seed) {
            this.sparks = false;
            this.type = type;
            this.category = category;
            if (type == IPlayerKnowledge.EnumKnowledgeType.THEORY) {
                progress += 10;
            }
            this.progress = progress;
            this.max = progress;
            this.seed = seed;
        }
    }
}
