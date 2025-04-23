package com.wonginnovations.arcana.systems.research.impls;

import com.wonginnovations.arcana.systems.research.EntrySection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ImageSection extends EntrySection{
	
	public static final String TYPE = "image";
	
	ResourceLocation image;
	
	public ImageSection(String image) {
		this(new ResourceLocation(image));
	}
	
	public ImageSection(ResourceLocation image) {
		this.image = image;
	}
	
	public String getType() {
		return TYPE;
	}
	
	public ResourceLocation getImage() {
		return image;
	}
	
	public CompoundTag getData() {
		CompoundTag tag = new CompoundTag();
		tag.putString("image", getImage().toString());
		return tag;
	}
}