package com.wonginnovations.arcana.client.render.entities;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import com.wonginnovations.arcana.client.ClientUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.blocks.pipes.ValveBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.wonginnovations.arcana.blocks.bases.SixWayBlock.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectValveBlockEntityRenderer implements BlockEntityRenderer<ValveBlockEntity>{
	
	public static final ResourceLocation GEAR_TEX = new ResourceLocation(Arcana.MODID, "block/essentia_tube_valve");
	private static TextureAtlasSprite gearSprite;
	private static ImmutableList<BakedQuad> gearModel;

	public AspectValveBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
	}

	// TODO: createBodyLayer needed maybe? fix error below

	@SuppressWarnings("deprecation")
	public void render(ValveBlockEntity te, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		if (gearModel == null) {
			if (gearSprite == null)
				gearSprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(GEAR_TEX);
			gearModel = ImmutableList.copyOf(ClientUtils.getQuadsForSprite(0, gearSprite, Transformation.identity()));
		}
		poseStack.pushPose();
		poseStack.translate(.5, .5, .5);
		// rotate to pick an empty side
		BlockState state = te.getLevel().getBlockState(te.getBlockPos());
		if (state.getValue(UP))
			if (!state.getValue(NORTH))
				poseStack.mulPose(Axis.XN.rotationDegrees(90));
			else if (!state.getValue(EAST))
				poseStack.mulPose(Axis.ZN.rotationDegrees(90));
			else if (!state.getValue(SOUTH))
				poseStack.mulPose(Axis.XP.rotationDegrees(90));
			else if (!state.getValue(WEST))
				poseStack.mulPose(Axis.ZP.rotationDegrees(90));
			else if (!state.getValue(DOWN))
				poseStack.mulPose(Axis.XN.rotationDegrees(180));
		// set base gear height
		poseStack.translate(0, .75, 0);
		// modify height and rotation based on state
		if (te.enabled()) {
			// display higher up
			// if lastChangedTick is less than 20 different from the current tick, transition
			float tickDiff = Math.min(10, (te.getLevel().getGameTime() + partialTicks) - te.getLastChangedTick());
			float heightDiff = (tickDiff / 10) * .07f;
			float rotationDiff = (tickDiff / 10) * 135;
			poseStack.translate(0, heightDiff, 0);
			poseStack.mulPose(Axis.YN.rotationDegrees(rotationDiff + 45));
		} else {
			float tickDiff = Math.min(10, (te.getLevel().getGameTime() + partialTicks) - te.getLastChangedTick());
			float heightDiff = (1 - (tickDiff / 10)) * .07f;
			float rotationDiff = (1 - (tickDiff / 10)) * 135;
			poseStack.translate(0, heightDiff, 0);
			poseStack.mulPose(Axis.YN.rotationDegrees(rotationDiff + 45));
		}
		// render
		VertexConsumer builder = buffer.getBuffer(RenderType.cutout());
		PoseStack.Pose pose = poseStack.last();
		poseStack.translate(-.5, 0, -.5);
		poseStack.mulPose(Axis.XP.rotationDegrees(90));
		renderGearModel(pose, builder, combinedLight, combinedOverlay);
		poseStack.popPose();
	}
	
	private void renderGearModel(PoseStack.Pose pose, VertexConsumer builder, int combinedLight, int combinedOverlay) {
		for (BakedQuad quad : gearModel)
			builder.putBulkData(pose, quad, 1, 1, 1, combinedLight, combinedOverlay);
	}
}
