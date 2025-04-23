package com.wonginnovations.arcana.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArcanumModel extends Model {
	private final ModelPart coverRight;
	private final ModelPart coverLeft;
	//private final ModelRenderer bookSpine = (new ModelRenderer(48, 48, 31, 9)).addBox(4.0F, -4.0F, -1.0F, 1.0F, 12.0F, 2.0F);
	private final ModelPart pagesRight;
	private final ModelPart pagesLeft;
	private final List<ModelPart> parts;

	public ArcanumModel() {
		super(RenderType::entitySolid);

		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		//setRotationAngle(coverLeft,(float)Math.PI / 2F,(float)Math.PI / 2F,(float)Math.PI / 2F);

		pagesLeft = partDefinition.addOrReplaceChild(
				"pages_left",
				CubeListBuilder.create()
						.texOffs(0, 23)
						.addBox(-3.0F, -3.5F, -3.0F, 7.0F, 11.0F, 1.0F),
				PartPose.ZERO)
				.bake(48, 48);

		pagesRight = partDefinition.addOrReplaceChild(
				"pages_left",
				CubeListBuilder.create()
						.texOffs(0, 35)
						.addBox(-3.0F, -3.5F, -2.0F, 7.0F, 11.0F, 1.0F),
				PartPose.ZERO)
				.bake(48, 48);

		this.coverRight = partDefinition.addOrReplaceChild(
				"cover_left",
				CubeListBuilder.create()
						.texOffs(27, 21)
						.addBox(-4.0F, -4.0F, -1F, 9.0F, 12.0F, 1.0F),
				PartPose.offset(0.0F, 0.0F, -1.0F))
				.bake(48, 48);

		this.coverLeft = partDefinition.addOrReplaceChild(
				"cover_left",
				CubeListBuilder.create()
						.texOffs(27, 33)
						.addBox(-4.0F, -4.0F, -5F, 9.0F, 12.0F, 1.0F),
				PartPose.offset(0.0F, 0.0F, 1.0F))
				.bake(48, 48);

		//this.bookSpine.rotateAngleY = ((float)Math.PI / 2F);

		this.parts = ImmutableList.of(this.coverRight, this.coverLeft, /*this.bookSpine,*/ this.pagesRight, this.pagesLeft/*, this.flippingPageRight, this.flippingPageLeft*/);
	}

	@Override
	public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.parts.forEach(part -> {
			part.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		});
	}

	// TODO: page flipping animations?
//	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
//		modelRenderer.rotateAngleX = x;
//		modelRenderer.rotateAngleY = y;
//		modelRenderer.rotateAngleZ = z;
//	}
//
//	public void func_228247_a_(float p_228247_1_, float p_228247_2_, float p_228247_3_, float p_228247_4_) {
//		float f = (Mth.sin(p_228247_1_ * 0.02F) * 0.1F + 0.95F) * p_228247_4_;
//		this.coverRight.yRot = (float)Math.PI + f;
//		this.coverLeft.yRot = -f;
//		this.pagesRight.yRot = f;
//		this.pagesLeft.rotateAngleY = -f;
//		//this.flippingPageRight.rotateAngleY = f - f * 2.0F * p_228247_2_;
//		//this.flippingPageLeft.rotateAngleY = f - f * 2.0F * p_228247_3_;
//		this.pagesRight. = Mth.sin(f);
//		this.pagesLeft.rotationPointX = Mth.sin(f);
//		//this.flippingPageRight.rotationPointX = Mth.sin(f);
//		//this.flippingPageLeft.rotationPointX = Mth.sin(f);
//	}
}
