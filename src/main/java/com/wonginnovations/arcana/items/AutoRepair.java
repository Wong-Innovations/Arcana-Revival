package com.wonginnovations.arcana.items;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Helper class that implements auto-repair functionality, used by void metal tools.
 *
 * @author Luna
 * @see com.wonginnovations.arcana.items.armor.AutoRepairArmorItem
 * @see com.wonginnovations.arcana.items.tools.AutoRepairSwordItem
 * @see com.wonginnovations.arcana.items.tools.AutoRepairShovelItem
 * @see com.wonginnovations.arcana.items.tools.AutoRepairPickaxeItem
 * @see com.wonginnovations.arcana.items.tools.AutoRepairHoeItem
 * @see com.wonginnovations.arcana.items.tools.AutoRepairAxeItem
 */
@ParametersAreNonnullByDefault
public class AutoRepair {
	
	private static final String TAG = "arcana:repair_timer";
	private static final int FULL_TIMER = 70;
	
	public static boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}
	
	public static boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
		return newStack.getItem() != oldStack.getItem();
	}
	
	public static void inventoryTick(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected) {
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.contains(TAG))
			tag.putInt(TAG, FULL_TIMER);
		if (tag.getInt(TAG) > 0)
			tag.putInt(TAG, tag.getInt(TAG) - 1);
		else {
			tag.putInt(TAG, FULL_TIMER);
			stack.hurtAndBreak(-1, (LivingEntity)entity, __ -> {});
		}
	}
}