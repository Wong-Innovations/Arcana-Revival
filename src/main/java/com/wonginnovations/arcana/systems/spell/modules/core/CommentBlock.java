package com.wonginnovations.arcana.systems.spell.modules.core;

import com.wonginnovations.arcana.systems.spell.modules.SpellModule;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;

import static com.wonginnovations.arcana.systems.spell.SpellState.SPELL_RESOURCES;

public class CommentBlock extends SpellModule {

	public String comment = "";
	public int startX = 0, startY = 0;
	boolean dragging = false, set = false;

	@Override
	public String getName() {
		return "comment";
	}

	@Override
	public int getInputAmount() {
		return 0;
	}


	@Override
	public int getOutputAmount() {
		return 0;
	}

	@Override
	public boolean canConnect(SpellModule connectingModule, boolean special) {
		return false;
	}

	@Override
	public void fromNBT(CompoundTag compound) {
		super.fromNBT(compound);
		comment = compound.getString("comment");
	}

	@Override
	public CompoundTag toNBT(CompoundTag compound) {
		super.toNBT(compound);
		compound.putString("comment", comment);
		return compound;
	}

	@Override
	public boolean mouseDown(int x, int y) {
		this.startX = x;
		this.startY = y;
		this.dragging = true;
		return true;
	}

	@Override
	public int getHeight() {
		return Math.abs(startX - x);
	}

	@Override
	public int getWidth() {
		return Math.abs(startY - y);
	}


	@Override
	public void renderUnderMouse(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean floating) {
		guiGraphics.blit(SPELL_RESOURCES, mouseX, mouseY, 176, 0, 16, 16);
	}

	@Override
	public void renderInMinigame(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean floating) {
		int left = Math.min(x, startX);
		int top = Math.min(y, startY);
		guiGraphics.blit(SPELL_RESOURCES, left, top, 128, 0, getWidth(), getHeight(), 48, 48);
	}
}
