package com.wonginnovations.arcana.client.render.tainted;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.client.model.tainted.TaintedSquidModel;
import com.wonginnovations.arcana.entities.tainted.TaintedSquidEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.wonginnovations.arcana.Arcana.arcLoc;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TaintedSquidRenderer extends MobRenderer<TaintedSquidEntity, TaintedSquidModel<TaintedSquidEntity>>{
	
	private static final ResourceLocation SQUID_TEXTURES = arcLoc("textures/entity/tainted_squid.png");
	
	public TaintedSquidRenderer(EntityRendererProvider.Context context) {
		super(context, new TaintedSquidModel<>(TaintedSquidModel.createBodyLayer().bakeRoot()), 0.7F);
	}
	
	/**
	 * Returns the location of an entity's texture.
	 */
	public ResourceLocation getTextureLocation(TaintedSquidEntity entity) {
		return SQUID_TEXTURES;
	}

	public void render(TaintedSquidEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		applyRotations(entity, poseStack, entityYaw, partialTicks);
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}
	
	protected void applyRotations(TaintedSquidEntity entity, PoseStack poseStack, float entityYaw, float partialTicks) {
		float f = Mth.lerp(partialTicks, entity.xBodyRotO, entity.xBodyRot);
		float f1 = Mth.lerp(partialTicks, entity.zBodyRotO, entity.zBodyRot);
		poseStack.translate(0.0D, 0.5D, 0.0D);
		poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
		poseStack.mulPose(Axis.XP.rotationDegrees(f));
		poseStack.mulPose(Axis.YP.rotationDegrees(f1));
		poseStack.translate(0.0D, -1.2F, 0.0D);
	}
}