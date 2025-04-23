package com.wonginnovations.arcana.systems.research.impls;

import com.wonginnovations.arcana.capabilities.Researcher;
import com.wonginnovations.arcana.network.Connection;
import com.wonginnovations.arcana.systems.research.Puzzle;
import com.wonginnovations.arcana.systems.research.Requirement;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import com.wonginnovations.arcana.systems.research.ResearchEntry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import static com.wonginnovations.arcana.Arcana.arcLoc;

public class PuzzleRequirement extends Requirement{
	
	public static final ResourceLocation TYPE = arcLoc("puzzle");
	
	protected ResourceLocation puzzleId;
	
	public PuzzleRequirement(ResourceLocation puzzleId) {
		this.puzzleId = puzzleId;
	}
	
	public boolean satisfied(Player player) {
		return Researcher.getFrom(player).isPuzzleCompleted(ResearchBooks.puzzles.get(puzzleId));
	}
	
	public void take(Player player) {
		// no-op
	}
	
	public ResourceLocation type() {
		return TYPE;
	}
	
	public CompoundTag data() {
		CompoundTag compound = new CompoundTag();
		compound.putString("puzzle", puzzleId.toString());
		return compound;
	}
	
	public boolean onClick(ResearchEntry entry, Player player) {
		Puzzle puzzle = ResearchBooks.puzzles.get(puzzleId);
		if (!(puzzle instanceof Fieldwork || satisfied(player)))
			Connection.sendGetNoteHandler(puzzleId, entry.key().toString());
		return false;
	}
	
	public ResourceLocation getPuzzleId() {
		return puzzleId;
	}
}