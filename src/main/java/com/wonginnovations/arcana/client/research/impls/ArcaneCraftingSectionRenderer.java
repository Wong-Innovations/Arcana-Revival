package com.wonginnovations.arcana.client.research.impls;

import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.aspects.UndecidedAspectStack;
import com.wonginnovations.arcana.client.gui.ClientUiUtil;
import com.wonginnovations.arcana.client.gui.ResearchEntryScreen;
import com.wonginnovations.arcana.items.recipes.IArcaneCraftingRecipe;
import com.wonginnovations.arcana.systems.research.impls.ArcaneCraftingSection;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.crafting.IShapedRecipe;

import java.util.Collections;

import static com.wonginnovations.arcana.client.gui.ResearchEntryScreen.HEIGHT_OFFSET;

public class ArcaneCraftingSectionRenderer extends AbstractCraftingSectionRenderer<ArcaneCraftingSection>{
	
	void renderRecipe(GuiGraphics guiGraphics, Recipe<?> recipe, ArcaneCraftingSection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		if (recipe instanceof IArcaneCraftingRecipe craftingRecipe) {
            int x = right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X, y = ResearchEntryScreen.PAGE_Y;
			int ulX = x + (screenWidth - 256 + ResearchEntryScreen.PAGE_WIDTH) / 2 - 32;
			int ulY = y + (screenHeight - 181 + ResearchEntryScreen.PAGE_HEIGHT) / 2 - 10 + HEIGHT_OFFSET;
			if (craftingRecipe.getAspectStacks().length > 0)
				ulY -= 15;
			guiGraphics.blit(textures, ulX - 10, ulY - 10, 73, 75, 84, 84);
			
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
			// Display aspects
			UndecidedAspectStack[] stacks = craftingRecipe.getAspectStacks();
			// 1 aspect -> 0, 2-3 aspects -> 3 spacing, 4-5 aspects -> 2 spacing, 6 aspects -> 1 spacing
			int spacing = (stacks.length == 1) ? 0 : (stacks.length >= 6) ? 1 : (stacks.length < 4) ? 3 : 2;
			int aspectX = ulX + 73 / 2 - (craftingRecipe.getAspectStacks().length * (16 + spacing * 2)) / 2 - 4;
			int aspectY = ulY + 82;
			// Shadow behind the aspects for readability
			for (int i = 0, length = stacks.length; i < length; i++) {
				UndecidedAspectStack stack = stacks[i];
				Aspect display = stack.any ? Aspects.getPrimals().get(player.tickCount / 20 % Aspects.getPrimals().size()): stack.stack.getAspect();
				float amount = stack.stack.getAmount();
				ClientUiUtil.renderAspectStack(guiGraphics, display, amount, aspectX + i * (16 + 2 * spacing) + spacing, aspectY);
			}
		} else
			error();
	}
	
	void renderRecipeTooltips(GuiGraphics guiGraphics, Recipe<?> recipe, ArcaneCraftingSection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		if (recipe instanceof IArcaneCraftingRecipe craftingRecipe) {
            int x = right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X, y = ResearchEntryScreen.PAGE_Y;
			int ulX = x + (screenWidth - 256 + ResearchEntryScreen.PAGE_WIDTH) / 2 - 32;
			int ulY = y + (screenHeight - 181 + ResearchEntryScreen.PAGE_HEIGHT) / 2 - 10 + HEIGHT_OFFSET;
			if (craftingRecipe.getAspectStacks().length > 0)
				ulY -= 15;
			
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
							tooltipArea(guiGraphics, stacks[displayIndex(stacks.length, player)], mouseX, mouseY, itemX, itemY);
					}
				}
			
			// Display aspect tooltips
			UndecidedAspectStack[] stacks = craftingRecipe.getAspectStacks();
			int spacing = (stacks.length == 1) ? 0 : (stacks.length >= 6) ? 1 : (stacks.length < 4) ? 3 : 2;
			int aspectX = ulX + 73 / 2 - (craftingRecipe.getAspectStacks().length * (16 + spacing * 2)) / 2 - 4;
			int aspectY = ulY + 82;
			for (int i = 0, length = stacks.length; i < length; i++) {
				UndecidedAspectStack stack = stacks[i];
				String displayed = stack.any ? I18n.get("aspect.any") : I18n.get("aspect." + stack.stack.getAspect().name().toLowerCase());
				int areaX = aspectX + i * (16 + 2 * spacing) + spacing;
				if (mouseX >= areaX && mouseX < areaX + 16 && mouseY >= aspectY && mouseY < aspectY + 16)
					ClientUiUtil.drawAspectStyleTooltip(guiGraphics, Collections.singletonList(Component.literal(displayed)), mouseX, mouseY);
			}
		}
	}
}
