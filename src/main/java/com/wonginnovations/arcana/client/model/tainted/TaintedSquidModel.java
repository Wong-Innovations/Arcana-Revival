package com.wonginnovations.arcana.client.model.tainted;

import com.wonginnovations.arcana.entities.tainted.TaintedSquidEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.jetbrains.annotations.NotNull;

public class TaintedSquidModel<T extends TaintedSquidEntity> extends HierarchicalModel<T> {

	private final ModelPart[] tentacles = new ModelPart[8];
	private final ModelPart root;

	public TaintedSquidModel(ModelPart root) {
		this.root = root;
//		Arrays.setAll(this.tentacles, (p_170995_) -> {
//			return pRoot.getChild(createTentacleName(p_170995_));
//		});
//		int i = -16;
//		this.body = new ModelRenderer(this, 0, 0);
//		this.body.addBox(-6.0F, -8.0F, -6.0F, 12.0F, 16.0F, 12.0F);
//		this.body.rotationPointY += 8.0F;
//
//		for (int j = 0; j < this.legs.length; ++j) {
//			this.legs[j] = new ModelRenderer(this, 48, 0);
//			double d0 = (double)j * Math.PI * 2.0D / (double)this.legs.length;
//			float f = (float)Math.cos(d0) * 5.0F;
//			float f1 = (float)Math.sin(d0) * 5.0F;
//			this.legs[j].addBox(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F);
//			this.legs[j].rotationPointX = f;
//			this.legs[j].rotationPointZ = f1;
//			this.legs[j].rotationPointY = 15.0F;
//			d0 = (double)j * Math.PI * -2.0D / (double)this.legs.length + (Math.PI / 2D);
//			this.legs[j].rotateAngleY = (float)d0;
//		}
//
//		ImmutableList.Builder<ModelRenderer> builder = ImmutableList.builder();
//		builder.add(this.body);
//		builder.addAll(Arrays.asList(this.legs));
//		this.field_228296_f_ = builder.build();
	}

	private static String createTentacleName(int pIndex) {
		return "tentacle" + pIndex;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition squidParts = meshDefinition.getRoot();
		CubeDeformation cubedeformation = new CubeDeformation(0.02F);
		int i = -16;
		squidParts.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 16.0F, 12.0F, cubedeformation), PartPose.offset(0.0F, 8.0F, 0.0F));
		int j = 8;
		CubeListBuilder cubelistbuilder = CubeListBuilder.create().texOffs(48, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 18.0F, 2.0F);

		for(int k = 0; k < 8; ++k) {
			double d0 = (double)k * Math.PI * 2.0D / 8.0D;
			float f = (float)Math.cos(d0) * 5.0F;
			float f1 = 15.0F;
			float f2 = (float)Math.sin(d0) * 5.0F;
			d0 = (double)k * Math.PI * -2.0D / 8.0D + (Math.PI / 2D);
			float f3 = (float)d0;
			squidParts.addOrReplaceChild(createTentacleName(k), cubelistbuilder, PartPose.offsetAndRotation(f, 15.0F, f2, 0.0F, f3, 0.0F));
		}

		return LayerDefinition.create(meshDefinition, 64, 32);
	}

	/**
	 * Sets this entity's model rotation angles
	 */
	public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		for(ModelPart modelpart : this.tentacles) {
			modelpart.xRot = ageInTicks;
		}
	}

	public @NotNull ModelPart root() {
		return this.root;
	}
}
