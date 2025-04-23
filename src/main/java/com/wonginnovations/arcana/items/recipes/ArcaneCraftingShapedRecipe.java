package com.wonginnovations.arcana.items.recipes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.wonginnovations.arcana.capabilities.Researcher;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.UndecidedAspectStack;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.aspects.handlers.AspectHolder;
import com.wonginnovations.arcana.systems.research.Parent;
import com.wonginnovations.arcana.util.StreamUtils;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.IShapedRecipe;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArcaneCraftingShapedRecipe implements IArcaneCraftingRecipe, IShapedRecipe<AspectCraftingContainer> {
	static int MAX_WIDTH = 3;
	static int MAX_HEIGHT = 3;

	public static List<ArcaneCraftingShapedRecipe> RECIPES = new ArrayList<>();

	final int width;
	final int height;
	final NonNullList<Ingredient> recipeItems;
	final ItemStack result;
	private final ResourceLocation id;
	final String group;
	private final UndecidedAspectStack[] aspectStacks;
	private List<Parent> requiredResearch;

	public ArcaneCraftingShapedRecipe(ResourceLocation idIn, String groupIn, int recipeWidthIn, int recipeHeightIn, NonNullList<Ingredient> recipeItemsIn, ItemStack recipeOutputIn, UndecidedAspectStack[] aspectStacks, List<Parent> requiredResearch) {
		this.id = idIn;
		this.group = groupIn;
		this.width = recipeWidthIn;
		this.height = recipeHeightIn;
		this.recipeItems = recipeItemsIn;
		this.result = recipeOutputIn;
		this.aspectStacks = aspectStacks;
		this.requiredResearch = requiredResearch;
		
		if (RECIPES.stream().noneMatch(m -> m.getId().toString().equals(this.getId().toString())))
			RECIPES.add(this);
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public @NotNull RecipeType<?> getType() {
		return ArcanaRecipes.Types.ARCANE_CRAFTING_SHAPED.get();
	}

	public RecipeSerializer<?> getSerializer() {
		return ArcanaRecipes.Serializers.ARCANE_CRAFTING_SHAPED.get();
	}

	public NonNullList<Ingredient> getIngredients() {
		return recipeItems;
	}
	
	public UndecidedAspectStack[] getAspectStacks() {
		return aspectStacks;
	}
	
	public boolean matches(AspectCraftingContainer inv, Level level, boolean considerAspects) {
		if (!requiredResearch.stream().allMatch(parent -> parent.satisfiedBy(Researcher.getFrom(inv.getCrafter()))))
			return false;
		if (considerAspects && aspectStacks.length != 0) {
			if (inv.getWandSlot() == null)
				return false;
			if (inv.getWandSlot().getItem() == ItemStack.EMPTY)
				return false;
			AspectHandler handler = AspectHandler.getFrom(inv.getWandSlot().getItem());
			if (!this.checkAspectMatch(inv, handler))
				return false;
		}
		for (int i = 0; i <= inv.getWidth() - this.width; ++i) {
			for (int j = 0; j <= inv.getHeight() - this.height; ++j) {
				if (this.checkMatch(inv, i, j, true))
					return true;
				if (this.checkMatch(inv, i, j, false))
					return true;
		/*if (requiredResearch.stream().allMatch(parent -> parent.satisfiedBy(Researcher.getFrom(inv.getCrafter())))) {
			if (considerAspects && aspectStacks.length != 0) {
				if (inv.getWandSlot() == null)
					return false;
				if (inv.getWandSlot().getStack() == ItemStack.EMPTY)
					return false;
				IAspectHandler handler = IAspectHandler.getFrom(inv.getWandSlot().getStack());
				if (!this.checkAspectMatch(inv, handler))
					return false;
			}
			for (int i = 0; i <= inv.getWidth() - this.recipeWidth; ++i) {
				for (int j = 0; j <= inv.getHeight() - this.recipeHeight; ++j) {
					if (this.checkMatch(inv, i, j, true))
						return true;
					if (this.checkMatch(inv, i, j, false))
						return true;
				}*/
			}
		}
		return false;
	}

	@Override
	public boolean matches(AspectCraftingContainer inv, Level level) {
		return matches(inv, level, true);
	}

	@Override
	public ItemStack assemble(AspectCraftingContainer pContainer, RegistryAccess pRegistryAccess) {
		return getResultItem(pRegistryAccess).copy();
	}

	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return pWidth >= this.width && pHeight >= this.height;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
		return this.result;
	}

	@Override
	public ItemStack getToastSymbol() {
		return this.result;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public int getRecipeWidth() {
		return this.width;
	}

	@Override
	public int getRecipeHeight() {
		return this.height;
	}

	public boolean matchesIgnoringAspects(AspectCraftingContainer inv, Level level) {
		return matches(inv, level, false);
	}

	private boolean checkAspectMatch(AspectCraftingContainer inv, @Nullable AspectHandler handler) {
		if (handler == null || handler.countHolders() == 0)
			return false;

		boolean satisfied = true;
		boolean anySatisfied = false;
		boolean hasAny = false;
		for (AspectHolder holder : handler.getHolders()) {
			for (UndecidedAspectStack stack : aspectStacks) {
				if (stack.any) {
					hasAny = true;
					if (holder.getStack().getAmount() >= stack.stack.getAmount())
						anySatisfied = true;
				}
				else if (holder.getStack().getAspect() == stack.stack.getAspect()) {
					if (holder.getStack().getAmount() < stack.stack.getAmount())
						satisfied = false;
				}
			}
		}
		return satisfied && (!hasAny || anySatisfied);
	}

	/**
	 * Checks if the region of a crafting inventory is match for the recipe.
	 */
	private boolean checkMatch(AspectCraftingContainer craftingInventory, int width, int height, boolean mirrored) {
		for (int i = 0; i < craftingInventory.getWidth(); ++i) {
			for (int j = 0; j < craftingInventory.getHeight(); ++j) {
				int k = i - width;
				int l = j - height;
				Ingredient ingredient = Ingredient.EMPTY;
				if (k >= 0 && l >= 0 && k < this.width && l < this.height) {
					if (mirrored) {
						ingredient = this.getIngredients().get(this.width - k - 1 + l * this.width);
					} else {
						ingredient = this.getIngredients().get(k + l * this.width);
					}
				}

				if (!ingredient.test(craftingInventory.getItem(i + j * craftingInventory.getWidth()))) {
					return false;
				}
			}
		}

		return true;
	}

	private static UndecidedAspectStack[] deserializeAspects(JsonArray aspectsArray) {
		ArrayList<UndecidedAspectStack> aspectStacks = new ArrayList<UndecidedAspectStack>();
		aspectsArray.forEach(aspectsObject -> {
			JsonObject object = aspectsObject.getAsJsonObject();
			String aspect = object.get("aspect").getAsString();
			int amount = object.get("amount").getAsInt();
			UndecidedAspectStack aspectStack;
			if (aspect.equalsIgnoreCase("any")||aspect.equalsIgnoreCase("arcana:any")) {
				aspectStack = UndecidedAspectStack.createAny(amount);
			} else {
				aspectStack = UndecidedAspectStack.create(Aspect.fromResourceLocation(new ResourceLocation(aspect)),amount,false);
			}
			aspectStacks.add(aspectStack);
		});
		return aspectStacks.toArray(new UndecidedAspectStack[aspectStacks.size()]);
	}

	/**
	 * Copy from ShapedRecipe
	 */
	static NonNullList<Ingredient> dissolvePattern(String[] pPattern, Map<String, Ingredient> pKeys, int pPatternWidth, int pPatternHeight) {
		NonNullList<Ingredient> nonnulllist = NonNullList.withSize(pPatternWidth * pPatternHeight, Ingredient.EMPTY);
		Set<String> set = Sets.newHashSet(pKeys.keySet());
		set.remove(" ");

		for(int i = 0; i < pPattern.length; ++i) {
			for(int j = 0; j < pPattern[i].length(); ++j) {
				String s = pPattern[i].substring(j, j + 1);
				Ingredient ingredient = pKeys.get(s);
				if (ingredient == null) {
					throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
				}

				set.remove(s);
				nonnulllist.set(j + pPatternWidth * i, ingredient);
			}
		}

		if (!set.isEmpty()) {
			throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
		} else {
			return nonnulllist;
		}
	}

	public static String[] shrink(String... pToShrink) {
		int i = Integer.MAX_VALUE;
		int j = 0;
		int k = 0;
		int l = 0;

		for(int i1 = 0; i1 < pToShrink.length; ++i1) {
			String s = pToShrink[i1];
			i = Math.min(i, firstNonSpace(s));
			int j1 = lastNonSpace(s);
			j = Math.max(j, j1);
			if (j1 < 0) {
				if (k == i1) {
					++k;
				}

				++l;
			} else {
				l = 0;
			}
		}

		if (pToShrink.length == l) {
			return new String[0];
		} else {
			String[] astring = new String[pToShrink.length - l - k];

			for(int k1 = 0; k1 < astring.length; ++k1) {
				astring[k1] = pToShrink[k1 + k].substring(i, j + 1);
			}

			return astring;
		}
	}

	private static int firstNonSpace(String pEntry) {
		int i;
		for(i = 0; i < pEntry.length() && pEntry.charAt(i) == ' '; ++i) {
		}

		return i;
	}

	private static int lastNonSpace(String pEntry) {
		int i;
		for(i = pEntry.length() - 1; i >= 0 && pEntry.charAt(i) == ' '; --i) {
		}

		return i;
	}

	public static String[] patternFromJson(JsonArray pPatternArray) {
		String[] astring = new String[pPatternArray.size()];
		if (astring.length > MAX_HEIGHT) {
			throw new JsonSyntaxException("Invalid pattern: too many rows, " + MAX_HEIGHT + " is maximum");
		} else if (astring.length == 0) {
			throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
		} else {
			for(int i = 0; i < astring.length; ++i) {
				String s = GsonHelper.convertToString(pPatternArray.get(i), "pattern[" + i + "]");
				if (s.length() > MAX_WIDTH) {
					throw new JsonSyntaxException("Invalid pattern: too many columns, " + MAX_WIDTH + " is maximum");
				}

				if (i > 0 && astring[0].length() != s.length()) {
					throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
				}

				astring[i] = s;
			}

			return astring;
		}
	}

	public static Map<String, Ingredient> keyFromJson(JsonObject pKeyEntry) {
		Map<String, Ingredient> map = Maps.newHashMap();

		for(Map.Entry<String, JsonElement> entry : pKeyEntry.entrySet()) {
			if (entry.getKey().length() != 1) {
				throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
			}

			if (" ".equals(entry.getKey())) {
				throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
			}

			map.put(entry.getKey(), Ingredient.fromJson(entry.getValue(), false));
		}

		map.put(" ", Ingredient.EMPTY);
		return map;
	}

	@SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
	public static class Serializer implements RecipeSerializer<ArcaneCraftingShapedRecipe> {

		public ArcaneCraftingShapedRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			String s = GsonHelper.getAsString(json, "group", "");
			Map<String, Ingredient> map = keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
			String[] astring = shrink(patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
			int i = astring[0].length();
			int j = astring.length;
			NonNullList<Ingredient> nonnulllist = dissolvePattern(astring, map, i, j);
			ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
			UndecidedAspectStack[] aspectStack_list = ArcaneCraftingShapedRecipe.deserializeAspects(GsonHelper.getAsJsonArray(json, "aspects"));
			List<Parent> research = StreamUtils.toStream(GsonHelper.getAsJsonArray(json, "research", null))
					.map(JsonElement::getAsString)
					.map(Parent::parse)
					.collect(Collectors.toList());
			return new ArcaneCraftingShapedRecipe(recipeId, s, i, j, nonnulllist, itemstack, aspectStack_list, research);
		}

		public ArcaneCraftingShapedRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			int i = buffer.readVarInt();
			int j = buffer.readVarInt();
			String s = buffer.readUtf();
			NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);
            nonnulllist.forEach(ignored -> Ingredient.fromNetwork(buffer));
			ItemStack itemstack = buffer.readItem();

			ArrayList<UndecidedAspectStack> aspectStacksArray = new ArrayList<UndecidedAspectStack>();
			int stackAmount = buffer.readInt();
			for (int l = 0; l < stackAmount; l++) {
				aspectStacksArray.add(readUndecidedAspectStack(buffer));
			}
			
			int size = buffer.readVarInt();
			List<Parent> requiredResearch = new ArrayList<>(size);
			for (int n = 0; n < size; n++)
				requiredResearch.add(Parent.parse(buffer.readUtf()));

			return new ArcaneCraftingShapedRecipe(recipeId, s, i, j, nonnulllist, itemstack,aspectStacksArray.toArray(new UndecidedAspectStack[aspectStacksArray.size()]),requiredResearch);
		}

		public void toNetwork(FriendlyByteBuf buffer, ArcaneCraftingShapedRecipe recipe) {
			buffer.writeVarInt(recipe.width);
			buffer.writeVarInt(recipe.height);
			buffer.writeUtf(recipe.group);
			for(Ingredient ingredient : recipe.recipeItems) {
				ingredient.toNetwork(buffer);
			}
			buffer.writeItem(recipe.result);

			buffer.writeInt(recipe.aspectStacks.length);
			for (UndecidedAspectStack aspectStack : recipe.aspectStacks)
				writeUndecidedAspectStack(buffer,aspectStack);
			
			buffer.writeVarInt(recipe.requiredResearch.size());
			for (Parent research : recipe.requiredResearch)
				buffer.writeUtf(research.asString());
		}

		protected void writeUndecidedAspectStack(FriendlyByteBuf buffer, UndecidedAspectStack stack) {
			buffer.writeBoolean(stack.any);
			buffer.writeUtf(stack.stack.getAspect().name());
			buffer.writeFloat(stack.stack.getAmount());
		}

		protected UndecidedAspectStack readUndecidedAspectStack(FriendlyByteBuf buffer) {
			boolean any = buffer.readBoolean();
			String aspect = buffer.readUtf();
			int amount = buffer.readInt();
			return UndecidedAspectStack.create(AspectUtils.getAspectByName(aspect),amount,any);
		}
	}
}