package com.wonginnovations.arcana.client.model.tainted;

import com.wonginnovations.arcana.entities.tainted.TaintedEntity;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TaintedSheepModel<T extends TaintedEntity> extends QuadrupedModel<T> {
//	private float headXRot;

	public TaintedSheepModel(ModelPart root) {
		super(root, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshDefinition = QuadrupedModel.createBodyMesh(12, CubeDeformation.NONE);
		PartDefinition sheepParts = meshDefinition.getRoot();
		sheepParts.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -4.0F, -6.0F, 6.0F, 6.0F, 8.0F), PartPose.offset(0.0F, 6.0F, -8.0F));
		sheepParts.addOrReplaceChild("body", CubeListBuilder.create().texOffs(28, 8).addBox(-4.0F, -10.0F, -7.0F, 8.0F, 16.0F, 6.0F), PartPose.offsetAndRotation(0.0F, 5.0F, 2.0F, ((float)Math.PI / 2F), 0.0F, 0.0F));
		return LayerDefinition.create(meshDefinition, 64, 32);
	}

	public void prepareMobModel(@NotNull T entity, float limbSwing, float limbSwingAmount, float partialTick) {
		super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
//		this.head.y = 6.0F + entity.getHeadEatPositionScale(pPartialTick) * 9.0F;
//		this.headXRot = entity.getHeadEatAngleScale(pPartialTick);
	}

	/**
	 * Sets this entity's model rotation angles
	 */
	public void setupAnim(@NotNull T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		super.setupAnim(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
//		this.head.xRot = this.headXRot;
	}
}