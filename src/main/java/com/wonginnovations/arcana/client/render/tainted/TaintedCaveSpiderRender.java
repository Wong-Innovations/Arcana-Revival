package com.wonginnovations.arcana.client.render.tainted;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.entities.tainted.TaintedCaveSpiderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SpiderRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TaintedCaveSpiderRender extends SpiderRenderer<TaintedCaveSpiderEntity> {
	private static final ResourceLocation CAVE_SPIDER_TEXTURES = new ResourceLocation(Arcana.MODID,"textures/entity/cave_spider.png");

	public TaintedCaveSpiderRender(EntityRendererProvider.Context context) {
		super(context);
		this.shadowRadius *= 0.55F;
	}

	protected void scale(@NotNull TaintedCaveSpiderEntity entitylivingbaseIn, @NotNull PoseStack poseStack, float partialTickTime) {
		poseStack.scale(0.55F, 0.55F, 0.55F);
	}

	/**
	 * Returns the location of an entity's texture.
	 */
	public @NotNull ResourceLocation getTextureLocation(@NotNull TaintedCaveSpiderEntity entity) {
		return CAVE_SPIDER_TEXTURES;
	}
}
