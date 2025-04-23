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

public class WillowEntityModel<T extends SpiritEntity> extends EntityModel<T> {
    private final ModelPart Head;
    private final ModelPart LeftHorn;
    private final ModelPart SideHeadGear;
    private final ModelPart HeadGearFront;
    private final ModelPart HeadGearBand2;
    private final ModelPart HeadGearBand;
    private final ModelPart LeftHornFront;
    private final ModelPart RightHorn;
    private final ModelPart RightHornFront;
    private final ModelPart Body;
    private final ModelPart Spine;
    private final ModelPart FeatherRightSide;
    private final ModelPart LeftArmPlate;
    private final ModelPart ArmorPlateFront;
    private final ModelPart Chainmail;
    private final ModelPart FeatherLeft;
    private final ModelPart FeatherLeftSide;
    private final ModelPart RightArmPlate;
    private final ModelPart FeatherRight;

    public WillowEntityModel() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition dairParts = meshDefinition.getRoot();

        Head = dairParts.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.ZERO)
                .bake(64, 64);

        LeftHorn = dairParts.addOrReplaceChild(
                "left_horn",
                CubeListBuilder.create()
                        .texOffs(28, 0)
                        .addBox(1.2F, -9.3F, 0.5F, 1.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1745F))
                .bake(64, 64);

        SideHeadGear = dairParts.addOrReplaceChild(
                "side_head_gear",
                CubeListBuilder.create()
                        .texOffs(0, 12)
                        .addBox(-4.0F, -4.0F, -1.0F, 8.0F, 3.0F, 3.0F),
                PartPose.ZERO)
                .bake(64, 64);

        HeadGearFront = dairParts.addOrReplaceChild(
                "head_gear_front",
                CubeListBuilder.create()
                        .texOffs(18, 0)
                        .addBox(-2.0F, -5.5F, -4.0F, 4.0F, 4.0F, 1.0F),
                PartPose.ZERO)
                .bake(64, 64);

        HeadGearBand2 = dairParts.addOrReplaceChild(
                "head_gear_band2",
                CubeListBuilder.create()
                        .texOffs(0, 29)
                        .addBox(-3.5F, -3.0F, -3.5F, 7.0F, 1.0F, 7.0F),
                PartPose.ZERO)
                .bake(64, 64);

        HeadGearBand = dairParts.addOrReplaceChild(
                "head_gear_band",
                CubeListBuilder.create()
                        .texOffs(0, 29)
                        .addBox(-3.5F, -5.0F, -3.5F, 7.0F, 1.0F, 7.0F),
                PartPose.ZERO)
                .bake(64, 64);

        LeftHornFront = dairParts.addOrReplaceChild(
                "left_horn_front",
                CubeListBuilder.create()
                        .texOffs(28, 0)
                        .addBox(1.2F, -7.7F, -2.5F, 1.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1745F))
                .bake(64, 64);

        RightHorn = dairParts.addOrReplaceChild(
                "right_horn",
                CubeListBuilder.create()
                        .texOffs(28, 0)
                        .addBox(-2.2F, -9.3F, 0.5F, 1.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1745F))
                .bake(64, 64);

        RightHornFront = dairParts.addOrReplaceChild(
                "right_horn_front",
                CubeListBuilder.create()
                        .texOffs(28, 0)
                        .addBox(-2.2F, -7.7F, -2.5F, 1.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1745F))
                .bake(64, 64);

        Body = dairParts.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(34, 0)
                        .addBox(-3.0F, 0.0F, -2.0F, 6.0F, 5.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0873F, 0.0F, 0.0F))
                .bake(64, 64);

        Spine = dairParts.addOrReplaceChild(
                "spine",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-0.5F, 4.1F, 0.0F, 1.0F, 5.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0873F, 0.0F, 0.0F))
                .bake(64, 64);

        FeatherRightSide = dairParts.addOrReplaceChild(
                "feather_right_side",
                CubeListBuilder.create()
                        .texOffs(0, 18)
                        .addBox(-1.9F, -4.7F, 1.5F, 2.0F, 4.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, -1.3963F, 0.0F))
                .bake(64, 64);

        LeftArmPlate = dairParts.addOrReplaceChild(
                "left_arm_plate",
                CubeListBuilder.create()
                        .texOffs(22, 15)
                        .addBox(3.0F, -0.2F, -2.0F, 1.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1396F))
                .bake(64, 64);

        ArmorPlateFront = dairParts.addOrReplaceChild(
                "armor_plate_front",
                CubeListBuilder.create()
                        .texOffs(18, 0)
                        .addBox(-2.0F, 0.6F, 1.9F, 4.0F, 4.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1396F))
                .bake(64, 64);

        Chainmail = dairParts.addOrReplaceChild(
                "chainmail",
                CubeListBuilder.create()
                        .texOffs(21, 9)
                        .addBox(-2.5F, 5.0F, -1.5F, 5.0F, 3.0F, 3.0F),
                PartPose.ZERO)
                .bake(64, 64);

        FeatherLeft = dairParts.addOrReplaceChild(
                "feather_left",
                CubeListBuilder.create()
                        .texOffs(0, 18)
                        .addBox(0.4F, -5.5F, 0.9F, 2.0F, 4.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.9599F, 0.5236F, 0.0F))
                .bake(64, 64);

        FeatherLeftSide = dairParts.addOrReplaceChild(
                "feather_left_side",
                CubeListBuilder.create()
                        .texOffs(0, 18)
                        .addBox(-0.1F, -4.7F, 1.5F, 2.0F, 4.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 1.3963F, 0.0F))
                .bake(64, 64);

        RightArmPlate = dairParts.addOrReplaceChild(
                "right_arm_plate",
                CubeListBuilder.create()
                        .texOffs(22, 15)
                        .addBox(-4.0F, -0.2F, -2.0F, 1.0F, 4.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1396F))
                .bake(64, 64);

        FeatherRight = dairParts.addOrReplaceChild(
                "feather_right",
                CubeListBuilder.create()
                        .texOffs(0, 18)
                        .addBox(-2.0F, -5.5F, 0.9F, 2.0F, 4.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.9599F, -0.5236F, 0.0F))
                .bake(64, 64);
    }

    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(Head, LeftHorn, SideHeadGear, HeadGearFront, HeadGearBand2, HeadGearBand, LeftHornFront, RightHorn, RightHornFront);
    }

    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(Body, Spine, FeatherRightSide, LeftArmPlate, ArmorPlateFront, Chainmail, FeatherLeft, FeatherLeftSide, RightArmPlate, FeatherRight);
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

//    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
//        modelRenderer.rotateAngleX = x;
//        modelRenderer.rotateAngleY = y;
//        modelRenderer.rotateAngleZ = z;
//    }
}
