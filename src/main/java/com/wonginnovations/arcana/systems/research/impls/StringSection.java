package com.wonginnovations.arcana.systems.research.impls;

import com.wonginnovations.arcana.systems.research.EntrySection;
import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/**
 * An entry section that displays text over any number of pages.
 */
public class StringSection extends EntrySection{
	
	public static final String TYPE = "string";
	
	String content;
	
	public StringSection(String content) {
		this.content = content;
	}
	
	public String getType() {
		return TYPE;
	}
	
	public CompoundTag getData() {
		CompoundTag tag = new CompoundTag();
		tag.putString("text", getText());
		return tag;
	}
	
	public String getText() {
		return content;
	}
	
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		StringSection section = (StringSection)o;
		return content.equals(section.content);
	}
	
	public int hashCode() {
		return Objects.hash(content);
	}
}