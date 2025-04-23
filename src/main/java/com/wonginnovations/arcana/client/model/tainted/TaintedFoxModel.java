package com.wonginnovations.arcana.client.model.tainted;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wonginnovations.arcana.entities.tainted.TaintedEntity;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class TaintedFoxModel<T extends TaintedEntity> extends AgeableListModel<T> {
	public final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart tail;
	private float legMotionPos;

	public TaintedFoxModel(ModelPart root) {
		super(true, 8.0F, 3.35F);
		this.head = root.getChild("head");
		this.body = root.getChild("body");
		this.rightHindLeg = root.getChild("right_hind_leg");
		this.leftHindLeg = root.getChild("left_hind_leg");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.leftFrontLeg = root.getChild("left_front_leg");
		this.tail = this.body.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition foxParts = meshDefinition.getRoot();
		PartDefinition headParts = foxParts.addOrReplaceChild("head", CubeListBuilder.create().texOffs(1, 5).addBox(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F), PartPose.offset(-1.0F, 16.5F, -3.0F));
		headParts.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(8, 1).addBox(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), PartPose.ZERO);
		headParts.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(15, 1).addBox(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F), PartPose.ZERO);
		headParts.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(6, 18).addBox(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F), PartPose.ZERO);
		PartDefinition bodyParts = foxParts.addOrReplaceChild("body", CubeListBuilder.create().texOffs(24, 15).addBox(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 16.0F, -6.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
		CubeDeformation cubedeformation = new CubeDeformation(0.001F);
		CubeListBuilder leftLegShape = CubeListBuilder.create().texOffs(4, 24).addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, cubedeformation);
		CubeListBuilder rightLegShape = CubeListBuilder.create().texOffs(13, 24).addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, cubedeformation);
		foxParts.addOrReplaceChild("right_hind_leg", rightLegShape, PartPose.offset(-5.0F, 17.5F, 7.0F));
		foxParts.addOrReplaceChild("left_hind_leg", leftLegShape, PartPose.offset(-1.0F, 17.5F, 7.0F));
		foxParts.addOrReplaceChild("right_front_leg", rightLegShape, PartPose.offset(-5.0F, 17.5F, 0.0F));
		foxParts.addOrReplaceChild("left_front_leg", leftLegShape, PartPose.offset(-1.0F, 17.5F, 0.0F));
		bodyParts.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(30, 0).addBox(2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F), PartPose.offsetAndRotation(-4.0F, 15.0F, -1.0F, -0.05235988F, 0.0F, 0.0F));
		return LayerDefinition.create(meshDefinition, 48, 32);
	}

	public void prepareMobModel(@NotNull T entity, float limbSwing, float limbSwingAmount, float partialTick) {
		this.body.xRot = ((float)Math.PI / 2F);
		this.tail.xRot = -0.05235988F;
		this.rightHindLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.leftHindLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		this.rightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
		this.leftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		this.head.setPos(-1.0F, 16.5F, -3.0F);
		this.head.yRot = 0.0F;
//		this.head.zRot = entity.getHeadRollAngle(partialTick);
		this.rightHindLeg.visible = true;
		this.leftHindLeg.visible = true;
		this.rightFrontLeg.visible = true;
		this.leftFrontLeg.visible = true;
		this.body.setPos(0.0F, 16.0F, -6.0F);
		this.body.zRot = 0.0F;
		this.rightHindLeg.setPos(-5.0F, 17.5F, 7.0F);
		this.leftHindLeg.setPos(-1.0F, 17.5F, 7.0F);
//		if (entity.isCrouching()) {
//			this.body.xRot = 1.6755161F;
//			float f = entity.getCrouchAmount(partialTick);
//			this.body.setPos(0.0F, 16.0F + entity.getCrouchAmount(partialTick), -6.0F);
//			this.head.setPos(-1.0F, 16.5F + f, -3.0F);
//			this.head.yRot = 0.0F;
//		} else if (entity.isSleeping()) {
//			this.body.zRot = (-(float)Math.PI / 2F);
//			this.body.setPos(0.0F, 21.0F, -6.0F);
//			this.tail.xRot = -2.6179938F;
//			if (this.young) {
//				this.tail.xRot = -2.1816616F;
//				this.body.setPos(0.0F, 21.0F, -2.0F);
//			}
//
//			this.head.setPos(1.0F, 19.49F, -3.0F);
//			this.head.xRot = 0.0F;
//			this.head.yRot = -2.0943952F;
//			this.head.zRot = 0.0F;
//			this.rightHindLeg.visible = false;
//			this.leftHindLeg.visible = false;
//			this.rightFrontLeg.visible = false;
//			this.leftFrontLeg.visible = false;
//		}
//		else if (entity.isSitting()) {
//			this.body.xRot = ((float)Math.PI / 6F);
//			this.body.setPos(0.0F, 9.0F, -3.0F);
//			this.tail.xRot = ((float)Math.PI / 4F);
//			this.tail.setPos(-4.0F, 15.0F, -2.0F);
//			this.head.setPos(-1.0F, 10.0F, -0.25F);
//			this.head.xRot = 0.0F;
//			this.head.yRot = 0.0F;
//			if (this.young) {
//				this.head.setPos(-1.0F, 13.0F, -3.75F);
//			}
//
//			this.rightHindLeg.xRot = -1.3089969F;
//			this.rightHindLeg.setPos(-5.0F, 21.5F, 6.75F);
//			this.leftHindLeg.xRot = -1.3089969F;
//			this.leftHindLeg.setPos(-1.0F, 21.5F, 6.75F);
//			this.rightFrontLeg.xRot = -0.2617994F;
//			this.leftFrontLeg.xRot = -0.2617994F;
//		}
	}

	protected @NotNull Iterable<ModelPart> headParts() {
		return ImmutableList.of(this.head);
	}

	protected @NotNull Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg);
	}

	/**
	 * Sets this entity's model rotation angles
	 */
	public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.xRot = headPitch * ((float)Math.PI / 180F);
		this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		this.headParts().forEach(part -> {
			part.render(poseStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		});
		this.bodyParts().forEach(part -> {
			part.render(poseStack, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
		});
	}
}
