package com.wonginnovations.arcana.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.systems.research.ResearchEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CompletePuzzleToast implements Toast {
	// good thing we don't have any other research books that work }:D
	private static final ItemStack ICON = new ItemStack(ArcanaItems.ARCANUM.get());
	
	private ResearchEntry entry;
	
	public CompletePuzzleToast(@Nullable ResearchEntry entry) {
		this.entry = entry;
	}
	
	// draw
	public Visibility render(GuiGraphics pGuiGraphics, ToastComponent pToastComponent, long pTimeSinceLastVisible) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		pGuiGraphics.blit(TEXTURE, 0, 0, 0, 32, 160, 32);
		// Puzzle Complete!
		// <Research Name>
		boolean present = entry != null;
		pGuiGraphics.drawString(Minecraft.getInstance().font, I18n.get("puzzle.toast.title"), 30, present ? 7 : 12, 0xff500050);
		if (present)
			pGuiGraphics.drawString(Minecraft.getInstance().font, I18n.get(entry.name()), 30, 18, 0xff000000);
		pGuiGraphics.renderItem(ICON, 8, 8);
		pGuiGraphics.renderItemDecorations(Minecraft.getInstance().font, ICON, 8, 8);
		return pTimeSinceLastVisible >= 5000 ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
	}
}