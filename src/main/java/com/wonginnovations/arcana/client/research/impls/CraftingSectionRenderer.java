package com.wonginnovations.arcana.client.research.impls;

import com.wonginnovations.arcana.client.gui.ResearchEntryScreen;
import com.wonginnovations.arcana.systems.research.impls.CraftingSection;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.crafting.IShapedRecipe;

import static com.wonginnovations.arcana.client.gui.ResearchEntryScreen.HEIGHT_OFFSET;

public class CraftingSectionRenderer extends AbstractCraftingSectionRenderer<CraftingSection>{
	
	void renderRecipe(GuiGraphics guiGraphics, Recipe<?> recipe, CraftingSection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		if (recipe instanceof CraftingRecipe craftingRecipe) {
			int x = right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X, y = ResearchEntryScreen.PAGE_Y;
			int ulX = x + (screenWidth - 256 + ResearchEntryScreen.PAGE_WIDTH) / 2 - 32, ulY = y + (screenHeight - 181 + ResearchEntryScreen.PAGE_HEIGHT) / 2 - 10 + HEIGHT_OFFSET;
            guiGraphics.blit(textures, ulX - 4, ulY - 4, 145, 1, 72, 72);
			
			int width = recipe instanceof IShapedRecipe ? ((IShapedRecipe<?>)craftingRecipe).getRecipeWidth() : 3;
			int height = recipe instanceof IShapedRecipe ? ((IShapedRecipe<?>)craftingRecipe).getRecipeHeight() : 3;
			
			for (int xx = 0; xx < width; xx++)
				for (int yy = 0; yy < height; yy++) {
					int index = xx + yy * width;
					if (index < recipe.getIngredients().size()) {
						int itemX = ulX + xx * 24;
						int itemY = ulY + yy * 24;
						ItemStack[] stacks = recipe.getIngredients().get(index).getItems();
						if (stacks.length > 0)
							item(guiGraphics, stacks[displayIndex(stacks.length, player)], itemX, itemY);
					}
				}
		} else
			error();
	}
	
	void renderRecipeTooltips(GuiGraphics guiGraphics, Recipe<?> recipe, CraftingSection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		if (recipe instanceof CraftingRecipe craftingRecipe) {
			int x = right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X, y = ResearchEntryScreen.PAGE_Y;
			int ulX = x + (screenWidth - 256 + ResearchEntryScreen.PAGE_WIDTH) / 2 - 32, ulY = y + (screenHeight - 181 + ResearchEntryScreen.PAGE_HEIGHT) / 2 - 10 + HEIGHT_OFFSET;

            int width = recipe instanceof IShapedRecipe ? ((IShapedRecipe<?>)craftingRecipe).getRecipeWidth() : 3;
			int height = recipe instanceof IShapedRecipe ? ((IShapedRecipe<?>)craftingRecipe).getRecipeHeight() : 3;
			
			for (int xx = 0; xx < width; xx++)
				for (int yy = 0; yy < height; yy++) {
					int index = xx + yy * width;
					if (index < recipe.getIngredients().size()) {
						int itemX = ulX + xx * 24;
						int itemY = ulY + yy * 24;
						ItemStack[] stacks = recipe.getIngredients().get(index).getItems();
						if (stacks.length > 0)
							tooltipArea(guiGraphics, stacks[displayIndex(stacks.length, player)], mouseX, mouseY, /* screenWidth, screenHeight, */ itemX, itemY);
					}
				}
		}
	}
}
