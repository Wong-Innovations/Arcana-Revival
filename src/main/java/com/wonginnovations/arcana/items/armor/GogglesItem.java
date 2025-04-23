package com.wonginnovations.arcana.items.armor;

import com.wonginnovations.arcana.items.settings.GogglePriority;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;

public class GogglesItem extends ArmorItem {
	
	public GogglePriority priority;
	
	public GogglesItem(ArmorMaterial material, Properties properties, GogglePriority priority) {
		super(material, Type.HELMET, properties);
		this.priority = priority;
	}
}