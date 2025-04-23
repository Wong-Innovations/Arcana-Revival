package com.wonginnovations.arcana.client.research.impls;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.client.gui.ClientUiUtil;
import com.wonginnovations.arcana.client.research.PuzzleRenderer;
import com.wonginnovations.arcana.containers.slots.AspectSlot;
import com.wonginnovations.arcana.systems.research.impls.Chemistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static com.wonginnovations.arcana.aspects.AspectUtils.areAspectsConnected;

public class ChemistryPuzzleRenderer /* extends AbstractGui */ implements PuzzleRenderer<Chemistry>{
	
	private static final ResourceLocation TEX = new ResourceLocation(Arcana.MODID, "textures/gui/research/chemistry_overlay.png");
	
	public void render(GuiGraphics guiGraphics, Chemistry puzzle, List<AspectSlot> puzzleSlots, List<Slot> puzzleItemSlots, int screenWidth, int screenHeight, int mouseX, int mouseY, Player player) {
		drawPaper(guiGraphics, screenWidth, screenHeight);
		// how to generate a hexagonal grid?
		// 20x20 per hex
		// +23x, +-18y moving right
		// +11x on odd rows
		int gridWidth = 8, gridHeight = 6;
		for (int y = 0; y < gridHeight; y++) {
			for (int x = 0; x < gridWidth; x++) {
				int xx = x * 22 + (y % 2 == 0 ? 11 : 0);
				int yy = y * 19;
				int scX = xx + paperLeft(screenWidth) + (214 - (23 * gridWidth - 2)) / 2;
				int scY = yy + paperTop(screenHeight) + (134 - (19 * gridHeight + 1)) / 2;
				int index = x + y * gridWidth;
				Aspect slot = puzzle.getAspectInSlot(index);
				if (slot != null) {
					ClientUiUtil.renderAspect(guiGraphics, slot, scX + 2, scY + 2);
				} else {
					guiGraphics.blit(TEX, scX, scY, 0, 0, 20, 20);
					if (puzzleSlots != null && puzzleSlots.size() > index)
						slot = puzzleSlots.get(index).getAspect();
				}
				if (puzzleSlots != null && !puzzleSlots.isEmpty()) {
					// a slot is connected to:
					//    a slot next to it (+/-1 X)
					//    a slot right below or right above it (+/-1 Y)
					//    a slot also below or also above it (+/-1 Y, - (if odd)/+1 (if even) X)
					if (x > 0 && areAspectsConnected(slot, getAt(index - 1, puzzle, puzzleSlots))) {
						// render 20,12 8x3
						guiGraphics.blit(TEX, scX - 5, scY + 9, 20, 12, 8, 3);
					}
					if (x < gridWidth - 1 && areAspectsConnected(slot, getAt(index + 1, puzzle, puzzleSlots))) {
						// render 42,12 8x3
						guiGraphics.blit(TEX, scX + 17, scY + 9, 42, 12, 8, 3);
					}
					if (y < gridHeight - 1) {
						int belowInd = x + (y + 1) * gridWidth;
						if (y % 2 == 0) {
							if (areAspectsConnected(slot, getAt(belowInd, puzzle, puzzleSlots))) {
								// render 26,0 7x7
								guiGraphics.blit(TEX, scX + 1, scY + 16, 26, 18, 7, 7);
							}
							if (belowInd + 1 < gridWidth * gridHeight && areAspectsConnected(slot, getAt(belowInd + 1, puzzle, puzzleSlots))) {
								// render 37,0 7x7
								guiGraphics.blit(TEX, scX + 12, scY + 16, 37, 18, 7, 7);
							}
						} else {
							if (areAspectsConnected(slot, getAt(belowInd, puzzle, puzzleSlots))) {
								// render 37,0 7x7
								guiGraphics.blit(TEX, scX + 12, scY + 16, 37, 18, 7, 7);
							}
							if (x > 0 && areAspectsConnected(slot, getAt(belowInd - 1, puzzle, puzzleSlots))) {
								// render 26,0 7x7
								guiGraphics.blit(TEX, scX + 1, scY + 16, 26, 18, 7, 7);
							}
						}
					}
					if (y > 0) {
						int aboveInd = x + (y - 1) * gridWidth;
						if (y % 2 == 0) {
							if (areAspectsConnected(slot, getAt(aboveInd, puzzle, puzzleSlots))) {
								// render 26,17 7x7
								guiGraphics.blit(TEX, scX + 1, scY - 3, 26, 0, 7, 7);
							}
							if (x > 0 && areAspectsConnected(slot, getAt(aboveInd + 1, puzzle, puzzleSlots))) {
								// render 37,17 7x7
								guiGraphics.blit(TEX, scX + 12, scY - 3, 37, 0, 7, 7);
							}
						} else {
							if (areAspectsConnected(slot, getAt(aboveInd, puzzle, puzzleSlots))) {
								// render 37,17 7x7
								guiGraphics.blit(TEX, scX + 12, scY - 3, 37, 0, 7, 7);
							}
							if (x > 0 && areAspectsConnected(slot, getAt(aboveInd - 1, puzzle, puzzleSlots))) {
								// render 26,17 7x7
								guiGraphics.blit(TEX, scX + 1, scY - 3, 26, 0, 7, 7);
							}
						}
					}
				}
			}
		}
	}
	
	public void renderAfter(GuiGraphics guiGraphics, Chemistry puzzle, List<AspectSlot> puzzleSlots, List<Slot> puzzleItemSlots, int screenWidth, int screenHeight, int mouseX, int mouseY, Player player) {
		// display node tooltip
		int gridWidth = 8, gridHeight = 6;
		for (Integer index : puzzle.getAspectSlotIndexes()) {
			int x = index % gridWidth;
			int y = index / gridWidth;
			int xx = x * 22 + (y % 2 == 0 ? 11 : 0);
			int yy = y * 19;
			int scX = xx + paperLeft(screenWidth) + (214 - (23 * gridWidth - 2)) / 2 + 2;
			int scY = yy + paperTop(screenHeight) + (134 - (19 * gridHeight + 1)) / 2 + 2;
			if (mouseX >= scX && mouseX < scX + 16 && mouseY >= scY && mouseY < scY + 16) {
				ClientUiUtil.drawAspectTooltip(guiGraphics, puzzle.getAspectInSlot(index), "", mouseX, mouseY, screenWidth, screenHeight);
				break;
			}
		}
	}
	
	private Aspect getAt(int index, Chemistry puzzle, List<AspectSlot> puzzleSlots) {
		Aspect slot = puzzle.getAspectInSlot(index);
		if (slot != null)
			return slot;
		else
			return puzzleSlots.get(index).getAspect();
	}
}