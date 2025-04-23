package com.wonginnovations.arcana.items.tools;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.systems.taint.Taint;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SilverHoeItem extends HoeItem{
	
	public SilverHoeItem(Tier tier, float attackSpeed, Properties builder) {
		super(tier, 1, attackSpeed, builder);
	}
	
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		float speed = super.getDestroySpeed(stack, state);
		return Taint.isTainted(state.getBlock()) ? speed * 2 : speed;
	}
}