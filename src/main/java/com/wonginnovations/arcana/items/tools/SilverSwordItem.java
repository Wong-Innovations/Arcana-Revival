package com.wonginnovations.arcana.items.tools;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.systems.taint.Taint;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SilverSwordItem extends SwordItem{
	
	public SilverSwordItem(Tier tier, int attackDamage, float attackSpeed, Properties builder) {
		super(tier, attackDamage, attackSpeed, builder);
	}
	
	public float getDestroySpeed(ItemStack stack, BlockState state) {
		float speed = super.getDestroySpeed(stack, state);
		return Taint.isTainted(state.getBlock()) ? speed * 2 : speed;
	}
}