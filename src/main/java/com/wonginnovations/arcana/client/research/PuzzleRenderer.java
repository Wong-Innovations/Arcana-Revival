package com.wonginnovations.arcana.client.research;

import com.wonginnovations.arcana.client.gui.ResearchTableScreen;
import com.wonginnovations.arcana.client.research.impls.ChemistryPuzzleRenderer;
import com.wonginnovations.arcana.client.research.impls.ThaumaturgyPuzzleRenderer;
import com.wonginnovations.arcana.systems.research.impls.Chemistry;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.client.research.impls.GuessworkPuzzleRenderer;
import com.wonginnovations.arcana.containers.slots.AspectSlot;
import com.wonginnovations.arcana.systems.research.Puzzle;
import com.wonginnovations.arcana.systems.research.impls.Guesswork;
import com.wonginnovations.arcana.systems.research.impls.Thaumaturgy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PuzzleRenderer<T extends Puzzle> {
	ResourceLocation PAPER = new ResourceLocation(Arcana.MODID, "textures/gui/research/temp_puzzle_overlay.png");
	
	Map<String, PuzzleRenderer<?>> map = new HashMap<>();
	
	static void init() {
		// could easily be if/else, but I do want to avoid hardcoding
		map.put(Guesswork.TYPE, new GuessworkPuzzleRenderer());
		map.put(Chemistry.TYPE, new ChemistryPuzzleRenderer());
		map.put(Thaumaturgy.TYPE, new ThaumaturgyPuzzleRenderer());
	}
	
	static <T extends Puzzle> PuzzleRenderer<T> get(String type) {
		return (PuzzleRenderer<T>)map.get(type);
	}
	
	static <T extends Puzzle> PuzzleRenderer<T> get(Puzzle puzzle) {
		return get(puzzle.type());
	}
	
	void render(GuiGraphics guiGraphics, T puzzle, List<AspectSlot> puzzleSlots, List<Slot> puzzleItemSlots, int screenWidth, int screenHeight, int mouseX, int mouseY, Player player);
	
	default Minecraft mc() {
		return Minecraft.getInstance();
	}

	default Font f() {
		return mc().font;
	}
	
	default void drawPaper(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
		guiGraphics.blit(PAPER, guiLeft(screenWidth) + 141, guiTop(screenHeight) + 35, 0, 0, 214, 134, 214, 134);
	}
	
	default void renderAfter(GuiGraphics guiGraphics, T puzzle, List<AspectSlot> puzzleSlots, List<Slot> puzzleItemSlots, int screenWidth, int screenHeight, int mouseX, int mouseY, Player player) {}
	
	default int guiLeft(int screenWidth) {
		return (screenWidth - ResearchTableScreen.WIDTH) / 2;
	}
	
	default int guiTop(int screenHeight) {
		return (screenHeight - ResearchTableScreen.HEIGHT) / 2;
	}
	
	default int paperLeft(int screenWidth) {
		return guiLeft(screenWidth) + 141;
	}
	
	default int paperTop(int screenHeight) {
		return guiTop(screenHeight) + 35;
	}
}