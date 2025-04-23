package com.wonginnovations.arcana.client.render.tainted;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.entities.tainted.TaintedGhastEntity;
import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TaintedGhastRender extends MobRenderer<TaintedGhastEntity, GhastModel<TaintedGhastEntity>> {
	private static final ResourceLocation GHAST_TEXTURES = new ResourceLocation(Arcana.MODID,"textures/entity/ghast.png");
	private static final ResourceLocation GHAST_SHOOTING_TEXTURES = new ResourceLocation(Arcana.MODID,"textures/entity/ghast_shooting.png");

	public TaintedGhastRender(EntityRendererProvider.Context context) {
		super(context, new GhastModel<>(context.bakeLayer(ModelLayers.GHAST)), 1.5F);
	}

	/**
	 * Returns the location of an entity's texture.
	 */
	public @NotNull ResourceLocation getTextureLocation(TaintedGhastEntity entity) {
		return entity.isCharging() ? GHAST_SHOOTING_TEXTURES : GHAST_TEXTURES;
	}

	protected void scale(@NotNull TaintedGhastEntity entitylivingbaseIn, @NotNull PoseStack poseStack, float partialTickTime) {
		poseStack.scale(4.5F, 4.5F, 4.5F);
	}
}
