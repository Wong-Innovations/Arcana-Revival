package com.wonginnovations.arcana.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.ItemAspectRegistry;
import com.wonginnovations.arcana.client.gui.ClientUiUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RenderTooltipHandler {
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onRenderTooltipPost(@Nonnull RenderTooltipEvent.Color event) {
		if (Screen.hasShiftDown() && !ItemAspectRegistry.isProcessing()) {
			List<AspectStack> stacks = ItemAspectRegistry.get(event.getItemStack());
			if (!stacks.isEmpty()) {
				GuiGraphics guiGraphics = event.getGraphics();
				guiGraphics.pose().pushPose();
				guiGraphics.pose().translate(0, 0, 500);
				RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
				guiGraphics.pose().translate(0F, 0F, 1F);
				
				int x = event.getX();
				int y = 10 * (event.getComponents().size() - 3) + 14 + event.getY();
				for (AspectStack stack : stacks) {
					ClientUiUtil.renderAspectStack(guiGraphics, stack, x, y);
					x += 20;
				}
				guiGraphics.pose().popPose();
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void makeTooltip(@Nonnull ItemTooltipEvent event) {
		if (Screen.hasShiftDown() && !ItemAspectRegistry.isProcessing()) {
			List<AspectStack> stacks = ItemAspectRegistry.get(event.getItemStack());
			if (!stacks.isEmpty()) {
				// amount of spaces that need inserting
				int filler = stacks.size() * 5;
				// repeat " " *filler
				StringBuilder sb = new StringBuilder();
				for (int __ = 0; __ < filler; __++) {
					String s = " ";
					sb.append(s);
				}
				String collect = sb.toString();
				event.getToolTip().add(Component.literal(collect));
				event.getToolTip().add(Component.literal(collect));
			}
		}
	}
}
