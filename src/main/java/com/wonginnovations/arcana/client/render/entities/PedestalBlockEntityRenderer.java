package com.wonginnovations.arcana.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.entities.PedestalBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PedestalBlockEntityRenderer implements BlockEntityRenderer<PedestalBlockEntity>{

	public PedestalBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
	}
	
	public void render(PedestalBlockEntity tileEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		poseStack.pushPose();
		
		ItemStack item = tileEntity.getItem();
		// translation above the pedestal + bobbing
		float bob = Mth.sin(((float)tileEntity.getLevel().getGameTime() + partialTicks) / 10.0F) * 0.2F + 0.2F;
		poseStack.translate(.5f, 1.3f + bob / 2, .5f);
		// spin
		float spin = (((float)tileEntity.getLevel().getGameTime() + partialTicks) / 20.0F);
		poseStack.mulPose(Axis.YP.rotation(spin));
		Minecraft mc = Minecraft.getInstance();
		ItemRenderer itemRenderer = mc.getItemRenderer();
		itemRenderer.render(item, ItemDisplayContext.GROUND, false, poseStack, buffer, combinedLight, combinedOverlay, itemRenderer.getModel(item, mc.level, mc.player, 0));

		poseStack.popPose();
	}
}