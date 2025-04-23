package com.wonginnovations.arcana.client.research.impls;

import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.client.gui.ClientUiUtil;
import com.wonginnovations.arcana.client.gui.ResearchEntryScreen;
import com.wonginnovations.arcana.items.recipes.AlchemyRecipe;
import com.wonginnovations.arcana.systems.research.impls.AlchemySection;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

import java.util.List;

import static com.wonginnovations.arcana.client.gui.ResearchEntryScreen.HEIGHT_OFFSET;

public class AlchemySectionRenderer extends AbstractCraftingSectionRenderer<AlchemySection>{
	
	void renderRecipe(GuiGraphics guiGraphics, Recipe<?> recipe, AlchemySection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		if (recipe instanceof AlchemyRecipe alchemyRecipe) {
            int x = right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X, y = ResearchEntryScreen.PAGE_Y;
			
			int ulX = x + (screenWidth - 256 + ResearchEntryScreen.PAGE_WIDTH) / 2 - 35, ulY = y + (screenHeight - 181 + ResearchEntryScreen.PAGE_HEIGHT) / 2 - 10 + HEIGHT_OFFSET;
			guiGraphics.blit(textures, ulX, ulY, 73, 1, 70, 70);
			guiGraphics.blit(textures, ulX + 19, ulY - 4, 23, 145, 17, 17);
			
			int inputX = ulX + 1, inputY = ulY - 5;
			ItemStack[] stacks = alchemyRecipe.getIngredients().get(0).getItems();
			item(guiGraphics, stacks[displayIndex(stacks.length, player)], inputX, inputY);
			
			// Display aspects
			List<AspectStack> aspects = alchemyRecipe.getAspects();
			int aspectsWidth = Math.min(3, aspects.size());
			int aspectStartX = ulX + 9 - (10 * (aspectsWidth - 3)), aspectStartY = ulY + 30;
			for (int i = 0, size = aspects.size(); i < size; i++) {
				AspectStack aspect = aspects.get(i);
				int xx = aspectStartX + (i % aspectsWidth) * 20;
				int yy = aspectStartY + (i / aspectsWidth) * 20;
				ClientUiUtil.renderAspectStack(guiGraphics, aspect, xx, yy);
			}
		} else
			error();
	}
	
	void renderRecipeTooltips(GuiGraphics guiGraphics, Recipe<?> recipe, AlchemySection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		if (recipe instanceof AlchemyRecipe alchemyRecipe) {
            int x = right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X, y = ResearchEntryScreen.PAGE_Y;
			int ulX = x + (screenWidth - 256 + ResearchEntryScreen.PAGE_WIDTH) / 2 - 35, ulY = y + (screenHeight - 181 + ResearchEntryScreen.PAGE_HEIGHT) / 2 - 10 + HEIGHT_OFFSET;
			int inputX = ulX + 1, inputY = ulY - 5;
			ItemStack[] stacks = alchemyRecipe.getIngredients().get(0).getItems();
			tooltipArea(guiGraphics, stacks[displayIndex(stacks.length, player)], mouseX, mouseY, /* screenWidth, screenHeight, */ inputX, inputY);
			List<AspectStack> aspects = alchemyRecipe.getAspects();
			int aspectsWidth = Math.min(3, aspects.size());
			int aspectStartX = ulX + 12 - (8 * (aspectsWidth - 3)), aspectStartY = ulY + 29;
			for (int i = 0, size = aspects.size(); i < size; i++) {
				AspectStack stack = aspects.get(i);
				int xx = aspectStartX + (i % aspectsWidth) * 19;
				int yy = aspectStartY + (i / aspectsWidth) * 19;
				aspectTooltipArea(guiGraphics, stack.getAspect(), mouseX, mouseY, screenWidth, screenHeight, xx, yy);
			}
		}
	}
}