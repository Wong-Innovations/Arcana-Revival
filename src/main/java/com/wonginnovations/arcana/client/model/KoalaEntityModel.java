package com.wonginnovations.arcana.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wonginnovations.arcana.entities.KoalaEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class KoalaEntityModel<T extends KoalaEntity> extends EntityModel<T> {
    private final ModelPart Body;
    private final ModelPart Tail;
    private final ModelPart Head;
//    private final ModelPart Ears;
    private final ModelPart Right;
    private final ModelPart Left;
    private final ModelPart Nose;
//    private final ModelPart Legs;
//    private final ModelPart Back;
    private final ModelPart RightBack;
    private final ModelPart LeftBack;
//    private final ModelPart Front;
    private final ModelPart RightFront;
    private final ModelPart LeftFront;

    public KoalaEntityModel() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition dairParts = meshDefinition.getRoot();

        Body = dairParts.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 18)
                        .addBox(-2.5F, -5.5F, -0.5F, 5.0F, 9.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 20.5F, -0.5F, 1.5708F, 0.0F, 0.0F))
                .bake(64, 64);

        Tail = dairParts.addOrReplaceChild(
                "tail",
                CubeListBuilder.create()
                        .texOffs(22, 23)
                        .addBox(-1.0F, 2.5F, 2.0F, 2.0F, 2.0F, 2.0F),
                PartPose.ZERO)
                .bake(64, 64);

        Head = dairParts.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.0F, -4.0F, -2.0F, 6.0F, 5.0F, 5.0F),
                PartPose.offset(0.0F, -4.5F, 2.5F))
                .bake(64, 64);

//        Ears = new ModelRenderer(this);
//        Ears.setRotationPoint(-3.0F, -1.5F, 2.0F);
//        Head.addChild(Ears);
//        setRotationAngle(Ears, 1.5708F, 0.0F, -3.1416F);

        Right = dairParts.addOrReplaceChild(
                "right_ear",
                CubeListBuilder.create()
                        .texOffs(6, 10)
                        .addBox(-6.5F, -3.5F, 3.0F, 3.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(5.5F, 3.0F, -3.5F, 1.5708F, 0.0F, -3.1416F))
                .bake(64, 64);

        Left = dairParts.addOrReplaceChild(
                "left_ear",
                CubeListBuilder.create()
                        .texOffs(6, 10)
                        .addBox(-7.0F, 0.0F, 1.5F, 3.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(-1.0F, -0.5F, -2.0F, 1.5708F, 0.0F, -3.1416F))
                .bake(64, 64);

        Nose = dairParts.addOrReplaceChild(
                "nose",
                CubeListBuilder.create()
                        .texOffs(0, 10)
                        .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, -3.5F, -0.5F, 1.5708F, 3.1416F, -3.1416F))
                .bake(64, 64);

//        Legs = new ModelRenderer(this);
//        Legs.setRotationPoint(-3.0F, 0.5F, -3.5F);
//        Body.addChild(Legs);
//        setRotationAngle(Legs, 1.5708F, 0.0F, 0.0F);

//        Back = new ModelRenderer(this);
//        Back.setRotationPoint(0.0F, 0.0F, 0.0F);
//        Legs.addChild(Back);

        RightBack = dairParts.addOrReplaceChild(
                "right_back",
                CubeListBuilder.create()
                        .texOffs(18, 29)
                        .addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, -1.0F, 1.5708F, 0.0F, 0.0F))
                .bake(64, 64);

        LeftBack = dairParts.addOrReplaceChild(
                "left_back",
                CubeListBuilder.create()
                        .texOffs(18, 29)
                        .addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(6.0F, 4.0F, -1.0F, 1.5708F, 0.0F, 0.0F))
                .bake(64, 64);

//        Front = new ModelRenderer(this);
//        Front.setRotationPoint(0.0F, 0.0F, 0.0F);
//        Legs.addChild(Front);

        RightFront = dairParts.addOrReplaceChild(
                "right_front",
                CubeListBuilder.create()
                        .texOffs(18, 29)
                        .addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 4.0F, 1.5708F, 0.0F, 0.0F))
                .bake(64, 64);

        LeftFront = dairParts.addOrReplaceChild(
                "left_front",
                CubeListBuilder.create()
                        .texOffs(18, 29)
                        .addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(6.0F, 4.0F, 4.0F, 1.5708F, 0.0F, 0.0F))
                .bake(64, 64);
    }

    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.Head, this.Right, this.Left, this.Nose);
    }

    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.Body, this.RightBack, this.LeftBack, this.RightFront, this.LeftFront, this.Tail);
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.Head.xRot = headPitch / (180F / (float)Math.PI);
        this.Head.zRot = netHeadYaw / (180F / (float)Math.PI);
        this.RightBack.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.LeftBack.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.RightFront.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.LeftFront.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
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

//    @Override
//    public void setLivingAnimations(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
//        super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTick);
//    }
}
