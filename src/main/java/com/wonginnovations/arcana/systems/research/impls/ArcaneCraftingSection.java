package com.wonginnovations.arcana.systems.research.impls;

import net.minecraft.resources.ResourceLocation;

public class ArcaneCraftingSection extends AbstractCraftingSection{
	
	public static final String TYPE = "arcane_crafting";
	
	public ArcaneCraftingSection(ResourceLocation recipe) {
		super(recipe);
	}
	
	public ArcaneCraftingSection(String s) {
		super(s);
	}
	
	public String getType() {
		return TYPE;
	}
}