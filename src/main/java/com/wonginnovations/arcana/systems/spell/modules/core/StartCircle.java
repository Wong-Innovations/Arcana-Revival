package com.wonginnovations.arcana.systems.spell.modules.core;

import com.wonginnovations.arcana.systems.spell.SpellState;
import com.wonginnovations.arcana.systems.spell.modules.SpellModule;
import net.minecraft.client.gui.GuiGraphics;

import static com.wonginnovations.arcana.systems.spell.SpellState.SPELL_RESOURCES;

public class StartCircle extends SpellModule {

	@Override
	public boolean isStartModule() {
		return true;
	}

	@Override
	public String getName() {
		return "start_circle";
	}

	@Override
	public int getInputAmount() {
		return 0;
	}

	@Override
	public int getOutputAmount() {
		return 5;
	}

	@Override
	public boolean canRaise(SpellState state) {
		return super.canRaise(state)
				&& state.isolated.isEmpty()
				&& bound.isEmpty();
	}

	@Override
	public int getHeight() {
		return 32;
	}

	@Override
	public int getWidth() {
		return 32;
	}

	@Override
	public void renderUnderMouse(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean floating) {
		guiGraphics.blit(SPELL_RESOURCES, mouseX - getWidth() / 2, mouseY - getHeight() / 2, 0, 16, getWidth(), getHeight());
	}

	@Override
	public void renderInMinigame(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean floating) {
		guiGraphics.blit(SPELL_RESOURCES, x - getWidth() / 2, y - getHeight() / 2, 0, 16, getWidth(), getHeight());
	}
}
