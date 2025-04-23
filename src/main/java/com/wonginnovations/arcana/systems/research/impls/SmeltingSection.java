package com.wonginnovations.arcana.systems.research.impls;

import net.minecraft.resources.ResourceLocation;

public class SmeltingSection extends AbstractCraftingSection{
	
	public static final String TYPE = "smelting";
	
	public SmeltingSection(ResourceLocation recipe) {
		super(recipe);
	}
	
	public SmeltingSection(String s) {
		super(s);
	}
	
	public String getType() {
		return TYPE;
	}
}
