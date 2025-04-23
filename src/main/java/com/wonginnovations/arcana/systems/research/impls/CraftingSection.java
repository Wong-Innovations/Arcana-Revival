package com.wonginnovations.arcana.systems.research.impls;

import net.minecraft.resources.ResourceLocation;

public class CraftingSection extends AbstractCraftingSection{
	
	public static final String TYPE = "crafting";
	
	public CraftingSection(ResourceLocation recipe) {
		super(recipe);
	}
	
	public CraftingSection(String s) {
		super(s);
	}
	
	public String getType() {
		return TYPE;
	}
}