package com.wonginnovations.arcana.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.aspects.UndecidedAspectStack;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.items.recipes.ArcaneCraftingShapedRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.wonginnovations.arcana.ArcanaVariables.arcLoc;
import static com.wonginnovations.arcana.compat.ArcanaJeiPlugin.ARCANE_WORKBENCH_UUID;

public class ArcaneCraftingCategory implements IRecipeCategory<ArcaneCraftingShapedRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public ArcaneCraftingCategory(IJeiHelpers jeiHelpers) {
        this.background = jeiHelpers.getGuiHelper().createDrawable(arcLoc("textures/gui/compat/arcanew.png"),0,0,130,130);
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(new ItemStack(ArcanaBlocks.ARCANE_CRAFTING_TABLE.get()));
    }

    @Override
    public @NotNull RecipeType<ArcaneCraftingShapedRecipe> getRecipeType() {
        return ARCANE_WORKBENCH_UUID;
    }

//    @Override
//    public Class<? extends ArcaneCraftingShapedRecipe> getRecipeClass() {
//        return ArcaneCraftingShapedRecipe.class;
//    }

    @Override
    public @NotNull Component getTitle() {
        return Component.literal("Arcane Crafting");
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
    public void setRecipe(@NotNull IRecipeLayoutBuilder iRecipeLayout, @NotNull ArcaneCraftingShapedRecipe recipe, @NotNull IFocusGroup focuses) {
        //Researcher.getFrom(Minecraft.getInstance().player).isPuzzleCompleted(ResearchBooks.puzzles.get(Arcana.arcLoc("flux_build_research")));
        //recipe.

        IRecipeSlotBuilder inputSlot;
        for (int i = 0; i < recipe.getRecipeHeight(); i++) {
            for (int j = 0; j < recipe.getRecipeWidth(); j++) {
                int n = (i*recipe.getRecipeWidth())+j;
                inputSlot = iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 23+(j*23),34+(i*23));
                if (n < recipe.getIngredients().size())
                    inputSlot.addIngredients(recipe.getIngredients().get(n));
            }
        }

        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 108,57).addItemStack(recipe.getResultItem(null));

        IRecipeSlotBuilder aspectSlot;
        for (UndecidedAspectStack stack : recipe.getAspectStacks()) {
            Aspect aspect = stack.stack.getAspect();
            if (aspect == Aspects.AIR) {
                aspectSlot = iRecipeLayout.addSlot(RecipeIngredientRole.CATALYST, 46, 8); // Air
                aspectSlot.addIngredient(AspectIngredient.TYPE, AspectIngredient.fromStack(stack.stack));
            }
            if (aspect == Aspects.FIRE) {
                aspectSlot = iRecipeLayout.addSlot(RecipeIngredientRole.CATALYST, 3, 32); // Fire
                aspectSlot.addIngredient(AspectIngredient.TYPE, AspectIngredient.fromStack(stack.stack));
            }
            if (aspect == Aspects.EARTH) {
                aspectSlot = iRecipeLayout.addSlot(RecipeIngredientRole.CATALYST, 3, 82); // Earth
                aspectSlot.addIngredient(AspectIngredient.TYPE, AspectIngredient.fromStack(stack.stack));
            }
            if (aspect == Aspects.WATER) {
                aspectSlot = iRecipeLayout.addSlot(RecipeIngredientRole.CATALYST, 89, 32); // Water
                aspectSlot.addIngredient(AspectIngredient.TYPE, AspectIngredient.fromStack(stack.stack));
            }
            if (aspect == Aspects.ORDER) {
                aspectSlot = iRecipeLayout.addSlot(RecipeIngredientRole.CATALYST, 89, 82); // Order
                aspectSlot.addIngredient(AspectIngredient.TYPE, AspectIngredient.fromStack(stack.stack));
            }
            if (aspect == Aspects.CHAOS) {
                aspectSlot = iRecipeLayout.addSlot(RecipeIngredientRole.CATALYST, 46, 106); // Chaos
                aspectSlot.addIngredient(AspectIngredient.TYPE, AspectIngredient.fromStack(stack.stack));
            }
            if (stack.any) {
                aspectSlot = iRecipeLayout.addSlot(RecipeIngredientRole.CATALYST, 3, 106); // Any
                aspectSlot.addIngredient(AspectIngredient.TYPE, new AspectIngredient(stack.stack.getAspect(), stack.stack.getAmount(), true).primalsOnly());
            }
        }
    }
}