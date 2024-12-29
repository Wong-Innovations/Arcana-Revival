package arcana.client.gui;

import arcana.Arcana;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.crafting.IArcaneRecipe;
import arcana.common.blocks.world.ore.ShardType;
import arcana.common.lib.crafting.CraftingManager;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.awt.*;

public class GuiArcaneWorkbench extends AbstractContainerScreen<ContainerArcaneWorkbench> {
    private Inventory ip;
    ResourceLocation tex = new ResourceLocation(Arcana.MODID, "textures/gui/arcaneworkbench.png");

    public GuiArcaneWorkbench(ContainerArcaneWorkbench pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        ip = pPlayerInventory;
        this.imageWidth = 190;
        this.imageHeight = 234;
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        PoseStack poseStack = pGuiGraphics.pose();
        RenderSystem.setShaderTexture(0, tex);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        int centerX = (width - getXSize()) / 2;
        int centerY = (height - getYSize()) / 2;
        pGuiGraphics.blit(tex, centerX, centerY, 0, 0, getXSize(), getYSize());
        int cost = 0;
        IArcaneRecipe result = CraftingManager.findMatchingArcaneRecipe(menu.blockEntity.inventoryCraft, ip.player);
        AspectList crystals = null;
        if (result != null) {
            cost = result.getVis();
            crystals = result.getCrystals();
        }
        if (crystals != null) {
            RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, 1);
            for (Aspect a : crystals.getAspects()) {
                int id = ShardType.getMetaByAspect(a);
                Color col = new Color(a.getColor());
                RenderSystem.setShaderColor(col.getRed() / 255.0f, col.getGreen() / 255.0f, col.getBlue() / 255.0f, 0.33f);
                poseStack.pushPose();
                poseStack.translate(centerX + ContainerArcaneWorkbench.xx[id] + 7.5f, centerY + ContainerArcaneWorkbench.yy[id] + 8.0f, 0.0f);
                // I believe radians are wanted here
                poseStack.mulPose(new Quaternionf(0.0f, 0.0f, 1.0f, Math.toRadians(id * 60 + minecraft.cameraEntity.tickCount % 360 + pPartialTick)));
                poseStack.scale(0.5f, 0.5f, 0.0f);
                pGuiGraphics.blit(tex, -32, -32, 192, 0, 64, 64);
                poseStack.scale(1.0f, 1.0f, 1.0f);
                poseStack.popPose();
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        }
        RenderSystem.disableBlend();
        poseStack.pushPose();
        poseStack.translate((float) (centerX + 168), (float) (centerY + 46), 0.0f);
        poseStack.scale(0.5f, 0.5f, 0.0f);
        String text = menu.getAuraVisClient() + " " + I18n.get("workbench.available");
        int ll = font.width(text) / 2;
        pGuiGraphics.drawString(font, text, -ll, 0, (menu.getAuraVisClient() < cost) ? 0XEE6E6E : 0X6E6EEE);
        poseStack.scale(1.0f, 1.0f, 1.0f);
        poseStack.popPose();
        if (cost > 0) {
            if (menu.getAuraVisClient() < cost) {
                poseStack.pushPose();
                float rgb = 0.33f;
                RenderSystem.setShaderColor(rgb, rgb, rgb, 0.66f);
                RenderSystem.enableCull();
                RenderSystem.enableBlend();
                pGuiGraphics.renderItem(result.getResultItem(null), centerX + 160, centerY + 64);
                pGuiGraphics.renderItemDecorations(minecraft.font, result.getResultItem(null), centerX + 160, centerY + 64, "");
                RenderSystem.enableDepthTest();
                poseStack.popPose();
            }
            poseStack.pushPose();
            poseStack.translate((float) (centerX + 168), (float) (centerY + 38), 0.0f);
            poseStack.scale(0.5f, 0.5f, 0.0f);
            text = cost + " " + I18n.get("workbench.cost");
            ll = font.width(text) / 2;
            pGuiGraphics.drawString(font, text, -ll, 0, 0XC0FFFF);
            poseStack.scale(1.0f, 1.0f, 1.0f);
            poseStack.popPose();
        }
    }
}
