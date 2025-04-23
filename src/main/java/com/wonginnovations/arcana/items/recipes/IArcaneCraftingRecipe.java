package com.wonginnovations.arcana.items.recipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.UndecidedAspectStack;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IArcaneCraftingRecipe extends Recipe<AspectCraftingContainer> {

	@Override
	RecipeType<?> getType();

	@Override
	default ItemStack getToastSymbol() {
		return new ItemStack(ArcanaBlocks.ARCANE_CRAFTING_TABLE.get());
	}
	
	boolean matchesIgnoringAspects(AspectCraftingContainer inv, Level levelIn);
	
	UndecidedAspectStack[] getAspectStacks();
}