package com.wonginnovations.arcana.systems.research.impls;

import net.minecraft.resources.ResourceLocation;

public class AlchemySection extends AbstractCraftingSection{
	
	public static final String TYPE = "alchemy";
	
	public AlchemySection(ResourceLocation recipe) {
		super(recipe);
	}
	
	public AlchemySection(String s) {
		super(s);
	}
	
	public String getType() {
		return TYPE;
	}
}