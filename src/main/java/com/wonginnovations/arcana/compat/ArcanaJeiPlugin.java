package com.wonginnovations.arcana.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.compat.jei.*;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.items.recipes.AlchemyRecipe;
import com.wonginnovations.arcana.items.recipes.ArcaneCraftingShapedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static com.wonginnovations.arcana.ArcanaVariables.arcLoc;

@JeiPlugin
public class ArcanaJeiPlugin implements IModPlugin {

    public static final RecipeType<ArcaneCraftingShapedRecipe> ARCANE_WORKBENCH_UUID = new RecipeType<>(arcLoc("arcane_crafting_jei"), ArcaneCraftingShapedRecipe.class);
    public static final RecipeType<AlchemyRecipe> ALCHEMY_UUID = new RecipeType<>(arcLoc("alchemy_uuid"), AlchemyRecipe.class);
    public static final RecipeType<DummyRecipe> CRYSTAL_UUID = new RecipeType<>(arcLoc("crystal_study"), DummyRecipe.class);
    public static final RecipeType<AspectCrystallizerRecipeHandler> ASPECT_CRYSTALLIZER_UUID = new RecipeType<>(arcLoc("crystallizer_study"), AspectCrystallizerRecipeHandler.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return arcLoc("jei");
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        IModPlugin.super.registerRecipes(registration);
        for (Aspect aspect : Aspects.getWithoutEmpty()) {
            new AspectCrystallizerRecipeHandler(aspect);
        }

        registration.addRecipes(ARCANE_WORKBENCH_UUID, ArcaneCraftingShapedRecipe.RECIPES);
        registration.addRecipes(ALCHEMY_UUID, AlchemyRecipe.RECIPES);
        registration.addRecipes(CRYSTAL_UUID, Collections.singletonList(new DummyRecipe()));
        registration.addRecipes(ASPECT_CRYSTALLIZER_UUID, AspectCrystallizerRecipeHandler.RECIPES);
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        IModPlugin.super.registerCategories(registration);
        registration.addRecipeCategories(new ArcaneCraftingCategory(registration.getJeiHelpers()));
        registration.addRecipeCategories(new AlchemyCategory(registration.getJeiHelpers()));
        registration.addRecipeCategories(new AspectCrystallizerCategory(registration.getJeiHelpers()));
        registration.addRecipeCategories(new CrystalStudyCategory(registration.getJeiHelpers()));
    }

    @Override
    public void registerIngredients(@NotNull IModIngredientRegistration registration) {
        IModPlugin.super.registerIngredients(registration);
        Collection<AspectIngredient> aspectIngredients = new ArrayList<>();
        for (int i = 0; i < Aspects.getWithoutEmpty().size(); i++) {
            aspectIngredients.add(AspectIngredient.fromSingleton(Aspects.getWithoutEmpty().get(i)));
        }
    
        registration.register(AspectIngredient.TYPE, aspectIngredients, new AspectIngredient.Helper(), new AspectIngredient.Renderer());
    }
    
    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        IModPlugin.super.registerRecipeCatalysts(registration);
        registration.addRecipeCatalyst(new ItemStack(ArcanaBlocks.ARCANE_CRAFTING_TABLE.get()),ARCANE_WORKBENCH_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaBlocks.CRUCIBLE.get()),ALCHEMY_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaBlocks.ASPECT_CRYSTALLIZER.get()),ASPECT_CRYSTALLIZER_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaItems.AIR_CRYSTAL_SEED.get()),CRYSTAL_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaItems.EARTH_CRYSTAL_SEED.get()),CRYSTAL_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaItems.FIRE_CRYSTAL_SEED.get()),CRYSTAL_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaItems.WATER_CRYSTAL_SEED.get()),CRYSTAL_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaItems.CHAOS_CRYSTAL_SEED.get()),CRYSTAL_UUID);
        registration.addRecipeCatalyst(new ItemStack(ArcanaItems.ORDER_CRYSTAL_SEED.get()),CRYSTAL_UUID);
    }
}