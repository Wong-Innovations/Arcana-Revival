package com.wonginnovations.arcana.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.blocks.entities.AspectCrystallizerBlockEntity;
import com.wonginnovations.arcana.containers.AspectCrystallizerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.wonginnovations.arcana.Arcana.arcLoc;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AspectCrystallizerScreen extends AbstractContainerScreen<AspectCrystallizerMenu> {
	
	private static final ResourceLocation BG = arcLoc("textures/gui/container/aspect_crystallizer.png");
	
	public AspectCrystallizerScreen(AspectCrystallizerMenu screenContainer, Inventory inv, Component title) {
		super(screenContainer, inv, title);
	}
	
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		renderBackground(guiGraphics);
		guiGraphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		Aspect aspect = Aspects.EMPTY;
		int pixHeight = 0;
		int pixProgress = 0;
		if (menu.te != null) {
			pixHeight = (int)((menu.te.vis.getHolder(0).getStack().getAmount() / 100f) * 52);
			aspect = menu.te.vis.getHolder(0).getStack().getAspect();
			pixProgress = (int)(((float)menu.te.progress / AspectCrystallizerBlockEntity.MAX_PROGRESS) * 22);
		}
		int color = aspect.getColorRange().get(1);
		RenderSystem.setShaderColor(((color & 0xff0000) >> 16) / 255f, ((color & 0xff00) >> 8) / 255f, (color & 0xff) / 255f, 1);
		guiGraphics.blit(BG, leftPos + 48, topPos + 69 - pixHeight, 176, 64, 16, pixHeight);
		
		RenderSystem.setShaderColor(1, 1, 1, 1);
		guiGraphics.blit(BG, leftPos + 72, topPos + 35, 176, 0, pixProgress, 16);
	}
	
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}
	
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		String s = title.getString();
		guiGraphics.drawString(font, s, (float)(imageWidth / 2 - font.width(s) / 2), 6.0F, 4210752, true);
		guiGraphics.drawString(font, menu.playerInventory.getDisplayName().getString(), 8.0F, (float)(imageHeight - 96 + 2), 4210752, true);
	}
}