package com.wonginnovations.arcana.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.containers.AspectMenu;
import com.wonginnovations.arcana.containers.slots.AspectSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class AspectContainerScreen<T extends AspectMenu> extends AbstractContainerScreen<T> {
	protected T aspectContainer;
	
	public AspectContainerScreen(T screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
	}
	
	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
//		RenderSystem.disableLighting();
		RenderSystem.enableBlend();
		for (AspectSlot slot : aspectContainer.getAspectSlots()) {
			if (slot.getInventory().get() != null && slot.visible) {
				// The laggiest code
				if (slot.getAspect() != Aspects.EMPTY && slot.getAspect() != null) {
					if (slot.getAmount() > 0) {
						RenderSystem.setShaderColor(1, 1, 1, 1);

						if (slot.shouldShowAmount()) {
							ClientUiUtil.renderAspectStack(guiGraphics, slot.getAspect(), slot.getAmount(), slot.x, slot.y, 0xFFFFFF);
						} else {
							ClientUiUtil.renderAspect(guiGraphics, slot.getAspect(), slot.x, slot.y);
						}
					} else {
						RenderSystem.setShaderColor(.5f, .5f, .5f, 1);
						ClientUiUtil.renderAspect(guiGraphics, slot.getAspect(), slot.x, slot.y);
					}
				}
				if (isMouseOverSlot(mouseX, mouseY, slot)) {
					guiGraphics.fillGradient(slot.x, slot.y, slot.x + 16, slot.y + 16, 300, 0x60ccfffc, 0x60ccfffc);
				}
				// ends here
			}
		}
	}

	@Override
	protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		super.renderTooltip(guiGraphics, mouseX, mouseY);
		for (AspectSlot slot : aspectContainer.getAspectSlots()) {
			if (slot.getInventory().get() != null && slot.visible) {
				if (isMouseOverSlot(mouseX, mouseY, slot)) {
					if (slot.getAspect() != Aspects.EMPTY && slot.getAspect() != null) {
						ClientUiUtil.drawAspectTooltip(guiGraphics, slot.getAspect(), slot.description, mouseX, mouseY, width, height);
					}
				}
			}
		}
	}
	
	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		renderTooltip(guiGraphics, mouseX, mouseY);
		if (aspectContainer.getHeldAspect() != null && aspectContainer.getHeldAspect() != Aspects.EMPTY) {
			ClientUiUtil.renderAspectStack(guiGraphics, aspectContainer.getHeldAspect(), aspectContainer.getHeldCount(), mouseX + 9, mouseY + 4, -1);
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		aspectContainer.handleClick((int)mouseX, (int)mouseY, mouseButton, this);
		return false;
	}
	
	protected boolean isMouseOverSlot(int mouseX, int mouseY, AspectSlot slot) {
		return mouseX >= leftPos + slot.x && mouseY >= topPos + slot.y && mouseX < leftPos + slot.x + 16 && mouseY < topPos + slot.y + 16;
	}
	
	public int getGuiLeft() {
		return super.getGuiLeft();
	}
	
	public int getGuiTop() {
		return super.getGuiTop();
	}
	
	public boolean isSlotVisible(AspectSlot slot) {
		return slot.visible;
	}
}