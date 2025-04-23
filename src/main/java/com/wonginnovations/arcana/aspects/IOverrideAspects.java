package com.wonginnovations.arcana.aspects;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IOverrideAspects {
	
	List<AspectStack> getAspectStacks(ItemStack stack);
}