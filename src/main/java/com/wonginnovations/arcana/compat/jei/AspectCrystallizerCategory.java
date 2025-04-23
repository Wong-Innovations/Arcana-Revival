package com.wonginnovations.arcana.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.wonginnovations.arcana.ArcanaVariables.arcLoc;
import static com.wonginnovations.arcana.compat.ArcanaJeiPlugin.ASPECT_CRYSTALLIZER_UUID;

public class AspectCrystallizerCategory implements IRecipeCategory<AspectCrystallizerRecipeHandler> {

	private final IDrawable background;
	private final IDrawable icon;
	
	public AspectCrystallizerCategory(IJeiHelpers jeiHelpers) {
		this.background = jeiHelpers.getGuiHelper().createDrawable(arcLoc("textures/gui/compat/crystal.png"),0,43,94,34);
		this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(new ItemStack(ArcanaBlocks.ASPECT_CRYSTALLIZER.get()));
	}
	
	@Override
	public @NotNull RecipeType<AspectCrystallizerRecipeHandler> getRecipeType() {
		return ASPECT_CRYSTALLIZER_UUID;
	}
	
//	@Override
//	public Class<? extends AspectCrystallizerRecipeHandler> getRecipeClass() {
//		return AspectCrystallizerRecipeHandler.class;
//	}
	
	@Override
	public @NotNull Component getTitle() {
		return Component.literal("Aspect Crystallizer");
	}

	@SuppressWarnings("removal")
	@Override
	public IDrawable getBackground() {
		return background;
	}
	
	@Override
	public IDrawable getIcon() {
		return icon;
	}
	
	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder iRecipeLayout, @NotNull AspectCrystallizerRecipeHandler recipe, @NotNull IFocusGroup focuses) {
		iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 8,9).addIngredient(AspectIngredient.TYPE, AspectIngredient.fromSingleton(recipe.aspect));
		iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 67,8).addItemStack(new ItemStack(AspectUtils.aspectCrystalItems.get(recipe.aspect).get(),1));
	}
}
