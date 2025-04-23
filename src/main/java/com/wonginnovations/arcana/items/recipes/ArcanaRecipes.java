package com.wonginnovations.arcana.items.recipes;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.wonginnovations.arcana.Arcana.MODID;
import static com.wonginnovations.arcana.Arcana.arcLoc;

public class ArcanaRecipes /* extends RecipeProvider*/ {

//	public ArcanaRecipes(PackOutput pPackOutput) {
//		super(pPackOutput);
//	}

	public static class Serializers {
		public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);

		public static final RegistryObject<RecipeSerializer<WandsRecipe>> CRAFTING_WANDS = SERIALIZERS.register("crafting_special_wands", () -> new SimpleCraftingRecipeSerializer<>(WandsRecipe::new));
		public static final RegistryObject<RecipeSerializer<AlchemyRecipe>> ALCHEMY = SERIALIZERS.register("alchemy", AlchemyRecipe.Serializer::new);
		public static final RegistryObject<RecipeSerializer<ArcaneCraftingShapedRecipe>> ARCANE_CRAFTING_SHAPED = SERIALIZERS.register("arcane_crafting_shaped", ArcaneCraftingShapedRecipe.Serializer::new);
	}
	public static class Types {
		public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);

		public static final RegistryObject<RecipeType<IArcaneCraftingRecipe>> ARCANE_CRAFTING_SHAPED = TYPES.register("arcane_crafting_shaped", () -> RecipeType.simple(arcLoc("arcane_crafting_shaped")));
		public static final RegistryObject<RecipeType<AlchemyRecipe>> ALCHEMY = TYPES.register("alchemy", () -> RecipeType.simple(arcLoc("alchemy")));
	}
}