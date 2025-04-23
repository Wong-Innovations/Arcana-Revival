package com.wonginnovations.arcana.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

public class SpriteRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean fullbright;

    public SpriteRenderer(EntityRendererProvider.Context context, ItemRenderer renderer, float scale, boolean fullbright) {
        super(context);
        this.itemRenderer = renderer;
        this.scale = scale;
        this.fullbright = fullbright;
    }

    public SpriteRenderer(EntityRendererProvider.Context context, ItemRenderer renderer) {
        this(context, renderer, 1.0F, false);
    }

    protected int getBlockLightLevel(@NotNull T entity, @NotNull BlockPos blockPos) {
        return this.fullbright ? 15 : super.getBlockLightLevel(entity, blockPos);
    }

    public void render(@NotNull T entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25)) {
            poseStack.pushPose();
            poseStack.scale(this.scale, this.scale, this.scale);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            this.itemRenderer.render(entity.getItem(), ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, this.itemRenderer.getModel(entity.getItem(), entity.level(), Minecraft.getInstance().player, 0));
            poseStack.popPose();
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull Entity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
