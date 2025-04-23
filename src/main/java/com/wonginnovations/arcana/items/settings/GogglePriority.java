package com.wonginnovations.arcana.items.settings;

import com.wonginnovations.arcana.items.armor.GogglesItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum GogglePriority {
	SHOW_NONE,
	SHOW_NODE,
	SHOW_ASPECTS;
	
	public static GogglePriority getClientGogglePriority() {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return SHOW_NONE;
		ItemStack headgear = player.getItemBySlot(EquipmentSlot.HEAD);
		return !headgear.isEmpty() && headgear.getItem() instanceof GogglesItem ? ((GogglesItem)headgear.getItem()).priority : SHOW_NONE;
	}
}
