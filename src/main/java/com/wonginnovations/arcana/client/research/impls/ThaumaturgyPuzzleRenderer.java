package com.wonginnovations.arcana.client.research.impls;

import com.wonginnovations.arcana.client.research.PuzzleRenderer;
import com.wonginnovations.arcana.containers.slots.AspectSlot;
import com.wonginnovations.arcana.systems.research.impls.Thaumaturgy;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class ThaumaturgyPuzzleRenderer implements PuzzleRenderer<Thaumaturgy> {
	@Override
	public void render(GuiGraphics guiGraphics, Thaumaturgy puzzle, List<AspectSlot> puzzleSlots, List<Slot> puzzleItemSlots, int screenWidth, int screenHeight, int mouseX, int mouseY, Player player) {
	}
}
