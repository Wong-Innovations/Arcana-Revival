package com.wonginnovations.arcana.systems.research.impls;

import com.wonginnovations.arcana.capabilities.Researcher;
import com.wonginnovations.arcana.systems.research.Requirement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import static com.wonginnovations.arcana.Arcana.arcLoc;

public class PuzzlesCompletedRequirement extends Requirement{
	
	public static final ResourceLocation TYPE = arcLoc("puzzles_completed");
	
	public boolean satisfied(Player player) {
		return Researcher.getFrom(player).getPuzzlesCompleted() >= getAmount();
	}
	
	public void take(Player player) {
		// no-op
	}
	
	public ResourceLocation type() {
		return TYPE;
	}
	
	public CompoundTag data() {
		return new CompoundTag();
	}
}