package com.wonginnovations.arcana.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.blocks.pipes.AspectSpeck;
import com.wonginnovations.arcana.blocks.pipes.TubeBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PipeBlockEntityRenderer implements BlockEntityRenderer<TubeBlockEntity>{

	public PipeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
	}
	
	private static final Random shakeRng = new Random();
	
	public void render(TubeBlockEntity te, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
		for (AspectSpeck speck : te.getSpecks()) {
			poseStack.pushPose();
			// so
			// x ->
			//   <- x
			// both have pos 0
			// just opposite directions
			// start at centre
			poseStack.translate(.5, .5, .5);
			// move by -dir*0.5
			poseStack.translate(-speck.direction.getStepX() * 0.5, -speck.direction.getStepY() * 0.5, -speck.direction.getStepZ() * 0.5);
			// move the speck by its progress
			float pos = speck.pos + partialTicks * speck.speed / 20f;
			poseStack.translate(pos * speck.direction.getStepX(), pos * speck.direction.getStepY(), pos * speck.direction.getStepZ());
			// debugging
			poseStack.translate(0, .25, 0);
			// shake specks that are stuck
			if (speck.stuck)
				poseStack.translate(shakeRng.nextFloat() / 16f, shakeRng.nextFloat() / 16f, shakeRng.nextFloat() / 16f);
			float v = /*speck.payload.getAmount() / 3f*/1;
			poseStack.scale(0.5f * v, 0.5f * v, 0.5f * v);
			// render
			Minecraft mc = Minecraft.getInstance();
			ItemRenderer itemRenderer = mc.getItemRenderer();
			ItemStack item = AspectUtils.getItemStackForAspect(speck.payload.getAspect());
			itemRenderer.render(item, ItemDisplayContext.GROUND, false, poseStack, buffer, light, overlay, itemRenderer.getModel(item, mc.level, mc.player, 0));
			poseStack.popPose();
		}
	}
}