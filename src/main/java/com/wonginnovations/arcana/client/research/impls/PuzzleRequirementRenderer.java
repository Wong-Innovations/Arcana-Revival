package com.wonginnovations.arcana.client.research.impls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wonginnovations.arcana.client.research.RequirementRenderer;
import com.wonginnovations.arcana.systems.research.Puzzle;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import com.wonginnovations.arcana.systems.research.impls.Fieldwork;
import com.wonginnovations.arcana.systems.research.impls.PuzzleRequirement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PuzzleRequirementRenderer implements RequirementRenderer<PuzzleRequirement>{
	
	public void render(GuiGraphics guiGraphics, int x, int y, PuzzleRequirement requirement, int ticks, float partialTicks, Player player) {
		ResourceLocation icon = getFrom(requirement).getIcon();
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		guiGraphics.blit(icon != null ? icon : getFrom(requirement).getDefaultIcon(), x, y, 0, 0, 16, 16, 16, 16);
	}
	
	public List<Component> tooltip(PuzzleRequirement requirement, Player player) {
		if (!(getFrom(requirement) instanceof Fieldwork)) {
			String desc = getFrom(requirement).getDesc();
			String puzzleDesc = desc != null ? desc : getFrom(requirement).getDefaultDesc();
			if (requirement.satisfied(player))
				return Arrays.asList(Component.translatable(puzzleDesc), Component.translatable("requirement.puzzle.complete"));
			return Arrays.asList(Component.translatable(puzzleDesc), Component.translatable("requirement.puzzle.get_note.1"), Component.translatable("requirement.puzzle.get_note.2"));
		} else
			return Collections.singletonList(Component.translatable(getFrom(requirement).getDesc()));
	}
	
	private Puzzle getFrom(PuzzleRequirement pr) {
		return ResearchBooks.puzzles.get(pr.getPuzzleId());
	}
}