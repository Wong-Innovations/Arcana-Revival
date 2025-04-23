package com.wonginnovations.arcana.items.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.aspects.AspectInfluencingRecipe;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.ItemAspectRegistry;
import com.wonginnovations.arcana.capabilities.Researcher;
import com.wonginnovations.arcana.systems.research.Parent;
import com.wonginnovations.arcana.util.StreamUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AlchemyRecipe implements Recipe<AlchemyInventory>, AspectInfluencingRecipe {

	public static List<AlchemyRecipe> RECIPES = new ArrayList<>();
	
	Ingredient in;
	ItemStack out;
	List<AspectStack> aspectsIn;
	List<Parent> requiredResearch;
	ResourceLocation id;
	
	public AlchemyRecipe(Ingredient in, ItemStack out, List<AspectStack> aspectsIn, List<Parent> requiredResearch, ResourceLocation id) {
		this.in = in;
		this.out = out;
		this.aspectsIn = aspectsIn;
		this.requiredResearch = requiredResearch;
		this.id = id;
		
		if (RECIPES.stream().noneMatch(m -> m.id.toString().equals(this.id.toString())))
			RECIPES.add(this);
	}

	@Override
	public boolean matches(AlchemyInventory inv, Level level) {
		// correct item
		return in.test(inv.stack)
				// and correct research
				&& requiredResearch.stream().allMatch(parent -> parent.satisfiedBy(Researcher.getFrom(inv.getCrafter())))
				// and correct aspects
				&& aspectsIn.stream().allMatch(stack -> inv.getAspectMap().containsKey(stack.getAspect()) && inv.getAspectMap().get(stack.getAspect()).getAmount() >= stack.getAmount());
	}

	@Override
	public ItemStack assemble(AlchemyInventory pContainer, RegistryAccess pRegistryAccess) {
		return null;
	}

	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return true;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
		return out.copy();
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public List<AspectStack> getAspects() {
		return aspectsIn;
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> list = NonNullList.create();
		list.add(in);
		return list;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ArcanaRecipes.Serializers.ALCHEMY.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ArcanaRecipes.Types.ALCHEMY.get();
	}

	@Override
	public void influence(List<AspectStack> in) {
		in.addAll(aspectsIn.stream().map(stack -> new AspectStack(stack.getAspect(), (int)(stack.getAmount() * ArcanaConfig.ALCHEMY_ASPECT_CARRY_FRACTION.get()))).toList());
	}
	
	public static class Serializer implements RecipeSerializer<AlchemyRecipe> {

		@Override
		public @NotNull AlchemyRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject pJson) {
			Ingredient ingredient = Ingredient.fromJson(pJson.get("in"));
			ItemStack out = ShapedRecipe.itemStackFromJson(pJson.getAsJsonObject("out"));
			List<AspectStack> aspects = ItemAspectRegistry.parseAspectStackList(pRecipeId, pJson.getAsJsonArray("aspects")).orElseThrow(() -> new JsonSyntaxException("Missing aspects in " + pRecipeId + "!"));
			List<Parent> research = StreamUtils.toStream(pJson.getAsJsonArray("research"))
					.map(JsonElement::getAsString)
					.map(Parent::parse)
					.collect(Collectors.toList());
			return new AlchemyRecipe(ingredient, out, aspects, research, pRecipeId);
		}

		@Override
		public @Nullable AlchemyRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
			Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
			ItemStack out = pBuffer.readItem();
			int size = pBuffer.readVarInt();
			List<AspectStack> aspects = new ArrayList<>(size);
			for (int i = 0; i < size; i++)
				aspects.add(new AspectStack(AspectUtils.getAspectByName(pBuffer.readUtf()), pBuffer.readFloat()));
			
			size = pBuffer.readVarInt();
			List<Parent> requiredResearch = new ArrayList<>(size);
			for (int i = 0; i < size; i++)
				requiredResearch.add(Parent.parse(pBuffer.readUtf()));
			
			return new AlchemyRecipe(ingredient, out, aspects, requiredResearch, pRecipeId);
		}

		@Override
		public void toNetwork(FriendlyByteBuf pBuffer, AlchemyRecipe pRecipe) {
			pRecipe.in.toNetwork(pBuffer);
			pBuffer.writeItemStack(pRecipe.out, true);
			pBuffer.writeVarInt(pRecipe.aspectsIn.size());
			for (AspectStack stack : pRecipe.aspectsIn) {
				pBuffer.writeUtf(stack.getAspect().name());
				pBuffer.writeFloat(stack.getAmount());
			}
			pBuffer.writeVarInt(pRecipe.requiredResearch.size());
			for (Parent research : pRecipe.requiredResearch)
				pBuffer.writeUtf(research.asString());
		}
	}
}