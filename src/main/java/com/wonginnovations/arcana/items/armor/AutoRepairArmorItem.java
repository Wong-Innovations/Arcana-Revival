package com.wonginnovations.arcana.items.armor;

import com.wonginnovations.arcana.items.AutoRepair;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AutoRepairArmorItem extends ArmorItem {
	
	public AutoRepairArmorItem(ArmorMaterial materialIn, ArmorItem.Type slot, Properties builder) {
		super(materialIn, slot, builder);
	}
	
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return AutoRepair.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
	}
	
	public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return AutoRepair.shouldCauseBlockBreakReset(oldStack, newStack);
	}
	
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
		super.inventoryTick(stack, level, entity, itemSlot, isSelected);
		AutoRepair.inventoryTick(stack, level, entity, itemSlot, isSelected);
	}
}