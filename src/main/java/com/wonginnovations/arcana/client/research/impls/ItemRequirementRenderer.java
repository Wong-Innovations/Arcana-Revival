package com.wonginnovations.arcana.client.research.impls;

import com.wonginnovations.arcana.client.research.RequirementRenderer;
import com.wonginnovations.arcana.systems.research.impls.ItemRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ItemRequirementRenderer implements RequirementRenderer<ItemRequirement>{
	
	public void render(GuiGraphics guiGraphics, int x, int y, ItemRequirement requirement, int ticks, float partialTicks, Player player) {
//		RenderHelper.enableStandardItemLighting();
//		RenderSystem.disableLighting();
		guiGraphics.renderItem(requirement.getStack(), x, y);
		guiGraphics.renderItemDecorations(Minecraft.getInstance().font, requirement.getStack(), x, y);
	}
	
	public List<Component> tooltip(ItemRequirement requirement, Player player) {
		List<Component> tooltip = requirement.getStack().getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
		if (requirement.getAmount() != 0)
			tooltip.set(0, Component.translatable("requirement.item.num", requirement.getAmount(), tooltip.get(0)));
		else
			tooltip.set(0, Component.translatable("requirement.item.have", tooltip.get(0)));
		return tooltip;
	}
	
	public boolean shouldDrawTickOrCross(ItemRequirement requirement, int amount) {
		return amount == 0;
	}
}