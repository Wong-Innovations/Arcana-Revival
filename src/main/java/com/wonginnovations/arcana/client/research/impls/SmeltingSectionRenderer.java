package com.wonginnovations.arcana.client.research.impls;

import com.wonginnovations.arcana.client.gui.ResearchEntryScreen;
import com.wonginnovations.arcana.systems.research.impls.SmeltingSection;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;

import static com.wonginnovations.arcana.client.gui.ResearchEntryScreen.HEIGHT_OFFSET;

public class SmeltingSectionRenderer extends AbstractCraftingSectionRenderer<SmeltingSection>{
	
	void renderRecipe(GuiGraphics guiGraphics, Recipe<?> recipe, SmeltingSection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		if (recipe instanceof AbstractCookingRecipe cookingRecipe) {
            int x = right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X, y = ResearchEntryScreen.PAGE_Y;
			int inputX = x + (screenWidth - 256 + ResearchEntryScreen.PAGE_WIDTH) / 2 - 8, inputY = y + (screenHeight - 181 + ResearchEntryScreen.PAGE_HEIGHT) / 2 + 8 + HEIGHT_OFFSET;

			guiGraphics.blit(textures, inputX - 9, inputY - 9, 219, 1, 34, 48);
			ItemStack[] stacks = cookingRecipe.getIngredients().get(0).getItems();
			item(guiGraphics, stacks[displayIndex(stacks.length, player)], inputX, inputY);
		} else
			error();
	}
	
	void renderRecipeTooltips(GuiGraphics guiGraphics, Recipe<?> recipe, SmeltingSection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		if (recipe instanceof AbstractCookingRecipe cookingRecipe) {
            int x = right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X, y = ResearchEntryScreen.PAGE_Y;
			int inputX = x + (screenWidth - 256 + ResearchEntryScreen.PAGE_WIDTH) / 2 - 8, inputY = y + (screenHeight - 181 + ResearchEntryScreen.PAGE_HEIGHT) / 2 + 8 + HEIGHT_OFFSET;
			ItemStack[] stacks = cookingRecipe.getIngredients().get(0).getItems();
			tooltipArea(guiGraphics, stacks[displayIndex(stacks.length, player)], mouseX, mouseY, /* screenWidth, screenHeight, */ inputX, inputY);
		}
	}
}
