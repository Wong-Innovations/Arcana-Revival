package com.wonginnovations.arcana.client.research.impls;

import com.wonginnovations.arcana.client.research.RequirementRenderer;
import com.wonginnovations.arcana.systems.research.impls.ItemTagRequirement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;
import java.util.Optional;

public class ItemTagRequirementRenderer implements RequirementRenderer<ItemTagRequirement>{
	
	public void render(GuiGraphics guiGraphics, int x, int y, ItemTagRequirement requirement, int ticks, float partialTicks, Player player) {
		// pick an item
		if (Minecraft.getInstance().level == null) return;
		Optional<HolderSet.Named<Item>> itemRegistry = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ITEM).getTag(requirement.getTag());
		if (itemRegistry.isEmpty()) return;
		List<Item> items = itemRegistry.get().stream().map(Holder::get).toList();
		ItemStack stack = new ItemStack(items.get((ticks / 30) % items.size()));

//        RenderHelper.enableStandardItemLighting();
//        RenderSystem.disableLighting();
		guiGraphics.renderItem(stack, x, y);
		guiGraphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y);
	}
	
	public List<Component> tooltip(ItemTagRequirement requirement, Player player) {
		// pick an item
		if (Minecraft.getInstance().level == null) return null;
		Optional<HolderSet.Named<Item>> itemRegistry = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.ITEM).getTag(requirement.getTag());
		if (itemRegistry.isEmpty()) return null;
		List<Item> items = itemRegistry.get().stream().map(Holder::get).toList();
		ItemStack stack = new ItemStack(items.get((player.tickCount / 30) % items.size()));

		List<Component> tooltip = stack.getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL);
		if(requirement.getAmount() != 0)
			tooltip.set(0, Component.translatable("requirement.item.num", requirement.getAmount(), tooltip.get(0)));
		else
			tooltip.set(0, Component.translatable("requirement.item.have", tooltip.get(0)));
		tooltip.add(Component.translatable("requirement.tag.accepts_any", requirement.getTagName().toString()).withStyle(ChatFormatting.DARK_GRAY));
		return tooltip;
	}
	
	public boolean shouldDrawTickOrCross(ItemTagRequirement requirement, int amount) {
		return amount == 0;
	}
}