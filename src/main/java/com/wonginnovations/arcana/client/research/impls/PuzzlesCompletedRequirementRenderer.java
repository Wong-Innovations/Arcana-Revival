package com.wonginnovations.arcana.client.research.impls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.capabilities.Researcher;
import com.wonginnovations.arcana.client.research.RequirementRenderer;
import com.wonginnovations.arcana.systems.research.impls.PuzzlesCompletedRequirement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;

public class PuzzlesCompletedRequirementRenderer implements RequirementRenderer<PuzzlesCompletedRequirement>{
	
	private static final ResourceLocation ICON = Arcana.arcLoc("textures/item/research_note_complete.png");
	
	public void render(GuiGraphics guiGraphics, int x, int y, PuzzlesCompletedRequirement requirement, int ticks, float partialTicks, Player player) {
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		guiGraphics.blit(ICON, x, y, 0, 0, 16, 16, 16, 16);
	}
	
	public List<Component> tooltip(PuzzlesCompletedRequirement requirement, Player player) {
		return Arrays.asList(Component.translatable("requirement.puzzles_completed", requirement.getAmount()), Component.translatable("requirement.puzzles_completed.progress", Researcher.getFrom(player).getPuzzlesCompleted(), requirement.getAmount()));
	}
}