package com.wonginnovations.arcana.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class EnchantedFilterItem extends Item {
	
	// Alembic efficiency, Pump speck size
	public int efficiencyBoost;
	// Alembic time per distill, Pump speck speed
	public int speedBoost;
	
	public EnchantedFilterItem(Properties properties, int efficiencyBoost, int speedBoost) {
		super(properties);
		this.efficiencyBoost = efficiencyBoost;
		this.speedBoost = speedBoost;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, levelIn, tooltip, flagIn);
		if (efficiencyBoost != 0)
			tooltip.add(plusMinus("item.arcana.enchanted_filter.efficiency_desc", efficiencyBoost));
		if (speedBoost != 0)
			tooltip.add(plusMinus("item.arcana.enchanted_filter.speed_desc", speedBoost));
	}
	
	private static Component plusMinus(String key, int count) {
		if (count >= 0)
			return Component.translatable(key, Component.literal(repeat("+", count))).withStyle(ChatFormatting.GREEN);
		return Component.translatable(key, Component.literal(repeat("-", -count))).withStyle(ChatFormatting.RED);
	}
	
	@Nonnull
	private static String repeat(@Nullable String base, int count) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < count; i++)
			builder.append(base);
		return builder.toString();
	}
}