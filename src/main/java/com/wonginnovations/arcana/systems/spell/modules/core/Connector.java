package com.wonginnovations.arcana.systems.spell.modules.core;

import com.wonginnovations.arcana.systems.spell.modules.SpellModule;
import net.minecraft.client.gui.GuiGraphics;

import static com.wonginnovations.arcana.systems.spell.SpellState.SPELL_RESOURCES;

public class Connector extends SpellModule {
	public boolean startMarked = false;
	@Override
	public String getName() {
		return "connector";
	}

	@Override
	public int getInputAmount() {
		return 1;
	}

	@Override
	public boolean canConnect(SpellModule connectingModule, boolean special) {
		return true;
	}

	@Override
	public int getOutputAmount() {
		return 1;
	}

	@Override
	public int getWidth() {
		return 16;
	}

	@Override
	public int getHeight() {
		return 16;
	}

	@Override
	public void renderUnderMouse(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean floating) {
		guiGraphics.blit(SPELL_RESOURCES, mouseX - getWidth() / 2, mouseY - getHeight() / 2, 208, 0, getWidth(), getHeight());
	}

	@Override
	public void renderInMinigame(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean floating) {
		if (startMarked) {
			guiGraphics.blit(SPELL_RESOURCES, x - getWidth() / 2, y - getHeight() / 2, 192, 0, getWidth(), getHeight());
		}
	}
}
