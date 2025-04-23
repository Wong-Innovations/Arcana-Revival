package com.wonginnovations.arcana.client.research.impls;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.client.research.PuzzleRenderer;
import com.wonginnovations.arcana.containers.slots.AspectSlot;
import com.wonginnovations.arcana.systems.research.impls.Guesswork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GuessworkPuzzleRenderer /* extends AbstractGui */ implements PuzzleRenderer<Guesswork>{
	
	private static final ResourceLocation texture = new ResourceLocation(Arcana.MODID, "textures/gui/research/arcanum_gui_overlay.png");
	
	private static final String[] hintSymbols = {"#", "A", "S", "D", "F", "&", "Q", "@", "~"};
	private static final int[] hintColors = {0x8a8a8a, 0x32a852, 0x58e8da, 0x7a58e8, 0xc458e8, 0xe858d5, 0xe85858, 0x136e13, 0x6e6613, 0x99431c, 0x43355e};
	
	public void render(GuiGraphics guiGraphics, Guesswork puzzle, List<AspectSlot> puzzleSlots, List<Slot> puzzleItemSlots, int screenWidth, int screenHeight, int mouseX, int mouseY, Player player) {
		drawPaper(guiGraphics, screenWidth, screenHeight);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		// render result
		int rX = paperLeft(screenWidth) + 78;
		int rY = paperTop(screenHeight) + 13;
		guiGraphics.blit(texture, rX, rY, 1, 167, 58, 20);
		int ulX = paperLeft(screenWidth) + 70;
		int ulY = paperTop(screenHeight) + 49;
		guiGraphics.blit(texture, ulX, ulY, 145, 1, 72, 72);
//		RenderHelper.enableStandardItemLighting();
		guiGraphics.renderItem(player.level().getRecipeManager().byKey(puzzle.getRecipe()).orElse(null).getResultItem(player.level().registryAccess()), rX + 29 - 8, rY + 10 - 8);
		guiGraphics.renderItemDecorations(f(), player.level().getRecipeManager().byKey(puzzle.getRecipe()).orElse(null).getResultItem(player.level().registryAccess()), rX + 29 - 8, rY + 10 - 8);
		
		List<Map.Entry<ResourceLocation, String>> indexHelper = new ArrayList<>(puzzle.getHints().entrySet());
		Recipe<?> recipe = player.level().getRecipeManager().byKey(puzzle.getRecipe()).orElse(null);
		
		if (recipe != null) {
			for (int y = 0; y < 3; y++)
				for (int x = 0; x < 3; x++) {
					int index = x + y * 3;
					if (recipe.getIngredients().size() > index && recipe.getIngredients().get(index).getItems().length > 0) {
						ResourceLocation name = ForgeRegistries.ITEMS.getKey(recipe.getIngredients().get(index).getItems()[0].getItem());
						int hint = indexMatchingKey(indexHelper, name);
						if (hint != -1)
							guiGraphics.drawString(f(), hintSymbols[hint % hintSymbols.length], ulX + 20 + x * 23, ulY + y * 23, hintColors[hint % hintColors.length]);
					}
				}
			
			int hintY = ulY + 101;
			int hintBaseX = ulX - 70;
			String text = "Hints: ";
			guiGraphics.drawString(f(), text, hintBaseX, hintY, 0x8a8a8a);
			for (int i = 0; i < indexHelper.size(); i++) {
				int hintX = hintBaseX + f().width(text) + i * 12;
				guiGraphics.drawString(f(), hintSymbols[i % hintSymbols.length], hintX, hintY, hintColors[i % hintColors.length]);
			}
			for (int i = 0; i < indexHelper.size(); i++) {
				Map.Entry<ResourceLocation, String> entry = indexHelper.get(i);
				int hintX = hintBaseX + f().width(text) + i * 12;
				if (mouseX >= hintX - 1 && mouseX < hintX + 11 && mouseY >= hintY - 1 && mouseY < hintY + 11)
					guiGraphics.renderTooltip(f(), Lists.newArrayList(Component.literal(I18n.get(entry.getValue()))), Optional.empty(), mouseX, mouseY);
			}
		}
	}
	
	private static int indexMatchingKey(List<Map.Entry<ResourceLocation, String>> indexHelper, ResourceLocation key) {
		for (int i = 0; i < indexHelper.size(); i++)
			if (indexHelper.get(i).getKey().equals(key))
				return i;
		return -1;
	}
}