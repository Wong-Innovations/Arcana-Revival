package com.wonginnovations.arcana.client.gui;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.containers.PumpMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.wonginnovations.arcana.Arcana.arcLoc;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PumpScreen extends AbstractContainerScreen<PumpMenu> {
	
	private static final ResourceLocation BG = arcLoc("textures/gui/container/aspect_pump.png");
	
	public PumpScreen(PumpMenu screenContainer, Inventory inv, Component title) {
		super(screenContainer, inv, title);
	}
	
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
		renderBackground(guiGraphics);
		guiGraphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
	
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}
	
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		String s = title.getString();
		guiGraphics.drawString(font, s, (float)(imageWidth / 2 - font.width(s) / 2), 6, 0x404040, true);
		guiGraphics.drawString(font, menu.playerInventory.getDisplayName().getString(), 8, (float)(imageHeight - 96 + 2), 0x404040, true);
	}
}
