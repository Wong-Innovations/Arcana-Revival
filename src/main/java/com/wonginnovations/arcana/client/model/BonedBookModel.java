package com.wonginnovations.arcana.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class BonedBookModel extends Model {

	public BonedBookModel(Function<ResourceLocation, RenderType> pRenderType) {
		super(pRenderType);
	}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

	}

	// TODO: figure out what book this is in game?
/*	private final ModelRenderer bone;

	public BonedBookJavaModel() {
		textureWidth = 16;
		textureHeight = 16;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 16.0F, 0.0F);
		bone.setTextureOffset(0, 6).addBox(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F, 0.0F, false);
		bone.setTextureOffset(0, 6).addBox(0.0F, -4.0F, -0.01F, 5.0F, 8.0F, 1.0F, 0.0F, false);
		bone.setTextureOffset(0, 6).addBox(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F, 0.0F, false);
		bone.setTextureOffset(0, 6).addBox(0.0F, -4.0F, 0.0F, 5.0F, 8.0F, 0.005F, 0.0F, false);
		bone.setTextureOffset(0, 6).addBox(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F, 0.0F, false);
		bone.setTextureOffset(0, 6).addBox(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F, 0.0F, false);
		bone.setTextureOffset(0, 6).addBox(0.0F, -4.0F, -0.99F, 5.0F, 8.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void setRotationAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		//previously the render function, render code was moved to a method below
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		bone.render(matrixStack, buffer, packedLight, packedOverlay);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}*/
}
