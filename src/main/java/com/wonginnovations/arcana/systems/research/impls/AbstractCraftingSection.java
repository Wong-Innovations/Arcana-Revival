package com.wonginnovations.arcana.systems.research.impls;

import com.wonginnovations.arcana.systems.research.EntrySection;
import com.wonginnovations.arcana.systems.research.Icon;
import com.wonginnovations.arcana.systems.research.Pin;
import com.wonginnovations.arcana.systems.research.ResearchEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;
import java.util.stream.Stream;

public abstract class AbstractCraftingSection extends EntrySection{
	
	ResourceLocation recipe;
	
	public AbstractCraftingSection(ResourceLocation recipe) {
		this.recipe = recipe;
	}
	
	public AbstractCraftingSection(String s) {
		this(new ResourceLocation(s));
	}
	
	public CompoundTag getData() {
		CompoundTag compound = new CompoundTag();
		compound.putString("recipe", recipe.toString());
		return compound;
	}
	
	public ResourceLocation getRecipe() {
		return recipe;
	}
	
	public Stream<Pin> getPins(int index, Level level, ResearchEntry entry) {
		// if the recipe exists,
		Optional<? extends Recipe<?>> recipe = level.getRecipeManager().byKey(this.recipe);
		if (recipe.isPresent()) {
			// get the item as the icon
			ItemStack output = recipe.get().getResultItem(level.registryAccess());
			Icon icon = new Icon(ForgeRegistries.ITEMS.getKey(output.getItem()), output);
			// and return a pin that points to this
			return Stream.of(new Pin(output.getItem(), entry, index, icon));
		}
		return super.getPins(index, level, entry);
	}
}