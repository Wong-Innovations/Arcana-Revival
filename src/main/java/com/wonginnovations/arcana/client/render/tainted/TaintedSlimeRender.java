package com.wonginnovations.arcana.client.render.tainted;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.entities.tainted.TaintedSlimeEntity;
import net.minecraft.client.model.SlimeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SlimeOuterLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TaintedSlimeRender extends MobRenderer<TaintedSlimeEntity, SlimeModel<TaintedSlimeEntity>> {
	private static final ResourceLocation SLIME_TEXTURES = new ResourceLocation(Arcana.MODID,"textures/entity/tainted_slime.png");

	public TaintedSlimeRender(EntityRendererProvider.Context context) {
		super(context, new SlimeModel<>(context.bakeLayer(ModelLayers.SLIME)), 0.25F);
		this.addLayer(new SlimeOuterLayer<>(this, context.getModelSet()));
	}

	public void render(@NotNull TaintedSlimeEntity entityIn, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
		preRenderCallback(entityIn, poseStack, partialTicks);
		this.shadowRadius = 0.25F * (float)entityIn.getSize();
		super.render(entityIn, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	protected void preRenderCallback(TaintedSlimeEntity entitylivingbaseIn, PoseStack poseStack, float partialTickTime) {
		float f = 0.999F;
		poseStack.scale(0.999F, 0.999F, 0.999F);
		poseStack.translate(0.0D, 0.001F, 0.0D);
		float f1 = (float)entitylivingbaseIn.getSize();
		float f2 = Mth.lerp(partialTickTime, entitylivingbaseIn.oSquish, entitylivingbaseIn.squish) / (f1 * 0.5F + 1.0F);
		float f3 = 1.0F / (f2 + 1.0F);
		poseStack.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
	}

	/**
	 * Returns the location of an entity's texture.
	 */
	public @NotNull ResourceLocation getTextureLocation(@NotNull TaintedSlimeEntity entity) {
		return SLIME_TEXTURES;
	}
}