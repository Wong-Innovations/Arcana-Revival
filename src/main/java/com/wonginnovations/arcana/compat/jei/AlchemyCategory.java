package com.wonginnovations.arcana.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.items.recipes.AlchemyRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.wonginnovations.arcana.ArcanaVariables.arcLoc;
import static com.wonginnovations.arcana.compat.ArcanaJeiPlugin.ALCHEMY_UUID;

public class AlchemyCategory implements IRecipeCategory<AlchemyRecipe> {

	private final IDrawable background;
	private final IDrawable icon;

	public AlchemyCategory(IJeiHelpers jeiHelpers) {
		this.background = jeiHelpers.getGuiHelper().createDrawable(arcLoc("textures/gui/compat/alchemy.png"),0,0,102,78);
		this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(new ItemStack(ArcanaBlocks.CRUCIBLE.get()));
	}

	@Override
	public @NotNull RecipeType<AlchemyRecipe> getRecipeType() {
		return ALCHEMY_UUID;
	}

//	@Override
//	public Class<? extends AlchemyRecipe> getRecipeClass() {
//		return AlchemyRecipe.class;
//	}

	@Override
	public @NotNull Component getTitle() {
		return Component.literal("Alchemy");
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@SuppressWarnings("removal")
	@Override
	public IDrawable getBackground() {
		return background;
	}

//	@SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
//	@Override
//	public void setIngredients(AlchemyRecipe recipe, IIngredients iIngredients) {
//		iIngredients.setInputLists(AspectIngredient.TYPE, Collections.singletonList(recipe.getAspects().stream().map(AspectIngredient::fromStack).collect(Collectors.toList())));
//		iIngredients.setInputLists(VanillaTypes.ITEM_STACK,JEIIngredientStackListBuilder.make(recipe.getIngredients().toArray(new Ingredient[recipe.getIngredients().size()])).build());
//		iIngredients.setInputIngredients(recipe.getIngredients());
//		iIngredients.setOutput(VanillaTypes.ITEM_STACK, recipe.getResultItem());
//	}

	@Override
	public void setRecipe(@NotNull IRecipeLayoutBuilder iRecipeLayout, @NotNull AlchemyRecipe recipe, @NotNull IFocusGroup focuses) {
		iRecipeLayout.addSlot(RecipeIngredientRole.CATALYST, 13, 1).addIngredients(recipe.getIngredients().get(0));
		iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 60, 1).addItemStack(recipe.getResultItem(null));
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 3; j++) {
				int n = (i*3)+j;
				int sizedHeight = recipe.getAspects().size() > 3 ? 30 : 39;
				IRecipeSlotBuilder slot = iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 42+(j*18),sizedHeight+(i*18));
				if (n < recipe.getAspects().size())
					slot.addIngredient(AspectIngredient.TYPE, AspectIngredient.fromStack(recipe.getAspects().get(n)));
			}
		}

//		IGuiItemStackGroup igroup = iRecipeLayout.getItemStacks();
//		IGuiIngredientGroup<AspectIngredient> agroup = iRecipeLayout.getIngredientsGroup(AspectIngredient.TYPE);
//		igroup.init(0,true,13,1);
//		igroup.set(0, Arrays.asList(recipe.getIngredients().get(0).getMatchingStacks()));
//		igroup.init(1,false,60,1);
//		igroup.set(1,recipe.getRecipeOutput());
//
//		for (int i = 0; i < 6; i++) {
//			for (int j = 0; j < 3; j++) {
//				int n = (i*3)+j;
//				int sizedHeight = recipe.getAspects().size() > 3 ? 30 : 39;
//				agroup.init(n,true,42+(j*18),sizedHeight+(i*18));
//				if (n < recipe.getAspects().size())
//					agroup.set(n, AspectIngredient.fromStack(recipe.getAspects().get(n)));
//			}
//		}

	}
}