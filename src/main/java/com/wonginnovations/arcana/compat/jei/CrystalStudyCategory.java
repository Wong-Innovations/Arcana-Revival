package com.wonginnovations.arcana.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import com.wonginnovations.arcana.items.ArcanaItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import static com.wonginnovations.arcana.ArcanaVariables.arcLoc;
import static com.wonginnovations.arcana.compat.ArcanaJeiPlugin.CRYSTAL_UUID;

public class CrystalStudyCategory implements IRecipeCategory<DummyRecipe> {

    private final IDrawable background;
    private final IDrawable icon;

    public CrystalStudyCategory(IJeiHelpers jeiHelpers) {
        this.background = jeiHelpers.getGuiHelper().createDrawable(arcLoc("textures/gui/compat/crystal.png"),0,0,70,41);
        this.icon = jeiHelpers.getGuiHelper().createDrawableItemStack(new ItemStack(ArcanaItems.AIR_CRYSTAL_SEED.get()));
    }

    @Override
    public @NotNull RecipeType<DummyRecipe> getRecipeType() {
        return CRYSTAL_UUID;
    }

//    @Override
//    public Class<? extends DummyRecipe> getRecipeClass() {
//        return DummyRecipe.class;
//    }

    @Override
    public @NotNull Component getTitle() {
        return Component.literal("Crystal Study");
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
    public void setRecipe(@NotNull IRecipeLayoutBuilder iRecipeLayout, @NotNull DummyRecipe recipe, @NotNull IFocusGroup focuses) {
        iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 3,22).addItemStack(new ItemStack(Items.WRITABLE_BOOK));
        iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 49,22).addItemStack(new ItemStack(ArcanaItems.ARCANUM.get()));
    }
}