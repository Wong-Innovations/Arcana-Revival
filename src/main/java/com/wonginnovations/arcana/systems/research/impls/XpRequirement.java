package com.wonginnovations.arcana.systems.research.impls;

import com.wonginnovations.arcana.systems.research.Requirement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import static com.wonginnovations.arcana.Arcana.arcLoc;

public class XpRequirement extends Requirement{
	
	public static final ResourceLocation TYPE = arcLoc("xp");
	
	public boolean satisfied(Player player) {
		return player.experienceLevel >= getAmount();
	}
	
	public void take(Player player) {
		player.experienceLevel -= getAmount();
	}
	
	public ResourceLocation type() {
		return TYPE;
	}
	
	public CompoundTag data() {
		return new CompoundTag();
	}
}