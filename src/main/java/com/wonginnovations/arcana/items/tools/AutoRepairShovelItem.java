package com.wonginnovations.arcana.items.tools;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.items.AutoRepair;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AutoRepairShovelItem extends ShovelItem {
	
	public AutoRepairShovelItem(Tier tier, float attackDamage, float attackSpeed, Properties builder) {
		super(tier, attackDamage, attackSpeed, builder);
	}
	
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return AutoRepair.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}
	
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
//		return AutoRepair.shouldCauseBlockBreakReset(oldStack, newStack);
		return false;
	}
	
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, level, entity, itemSlot, isSelected);
		AutoRepair.inventoryTick(stack, level, entity, itemSlot, isSelected);
	}
}