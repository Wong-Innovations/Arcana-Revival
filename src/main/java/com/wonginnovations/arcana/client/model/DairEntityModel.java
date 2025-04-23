package com.wonginnovations.arcana.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wonginnovations.arcana.entities.SpiritEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.jetbrains.annotations.NotNull;

public class DairEntityModel<T extends SpiritEntity> extends EntityModel<T> {
    public ModelPart Body;
    public ModelPart Noose;
    public ModelPart ShoulderPadLeft;
    public ModelPart ShoulderPadRight;
    public ModelPart ChainMail;
    public ModelPart Head;
    public ModelPart HornLeft;
    public ModelPart HornRight;

    public DairEntityModel() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition dairParts = meshDefinition.getRoot();

        Body = dairParts.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 31)
                        .addBox(-3.0F, 0.0F, -2.5F, 6.0F, 4.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 0.1745F, 0.0F, 0.0F))
                .bake(48, 48);

        Noose = dairParts.addOrReplaceChild(
                "noose",
                CubeListBuilder.create()
                        .texOffs(32, 43)
                        .addBox(-2.0F, -0.7F, -2.0F, 4.0F, 1.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0F, 0.0F))
                .bake(48, 48);

        Head = dairParts.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.0F, -6.4F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.ZERO)
                .bake(48, 48);

        HornLeft = dairParts.addOrReplaceChild(
                "horn_left",
                CubeListBuilder.create()
                        .texOffs(14, 13)
                        .addBox(3.0F, -7.0F, -1.0F, 1.0F, 3.0F, 2.0F),
                PartPose.ZERO)
                .bake(48, 48);

        HornLeft = dairParts.addOrReplaceChild(
                "horn_right",
                CubeListBuilder.create()
                        .texOffs(14, 13)
                        .addBox(-4.0F, -7.0F, -1.0F, 1.0F, 3.0F, 2.0F),
                PartPose.ZERO)
                .bake(48, 48);

        ChainMail = dairParts.addOrReplaceChild(
                "chain_mail",
                CubeListBuilder.create()
                        .texOffs(0, 40)
                        .addBox(-2.0F, 3.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                PartPose.ZERO)
                .bake(48, 48);

        ShoulderPadLeft = dairParts.addOrReplaceChild(
                "shoulder_pad_left",
                CubeListBuilder.create()
                        .texOffs(0, 12)
                        .addBox(-0.5F, 0.0F, -2.0F, 1.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(3.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2618F))
                .bake(48, 48);

        ShoulderPadRight = dairParts.addOrReplaceChild(
                "shoulder_pad_right",
                CubeListBuilder.create()
                        .texOffs(0, 12)
                        .addBox(-0.5F, 0.0F, -2.0F, 1.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(-3.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.2618F))
                .bake(48, 48);
    }

    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.Head, this.HornRight, this.HornLeft);
    }

    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.Body, this.ChainMail, this.Noose, this.ShoulderPadRight, this.ShoulderPadLeft);
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.Head.xRot = headPitch / (180F / (float)Math.PI);
        this.Head.yRot = netHeadYaw / (180F / (float)Math.PI);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.headParts().forEach(part -> part.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha));
        this.bodyParts().forEach(part -> part.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha));
    }

    // TODO: remove?
//    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
//        modelRenderer.rotateAngleX = x;
//        modelRenderer.rotateAngleY = y;
//        modelRenderer.rotateAngleZ = z;
//    }
}
