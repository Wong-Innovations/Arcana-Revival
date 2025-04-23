package com.wonginnovations.arcana.client.research.impls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.client.gui.ClientUiUtil;
import com.wonginnovations.arcana.client.gui.ResearchEntryScreen;
import com.wonginnovations.arcana.client.research.EntrySectionRenderer;
import com.wonginnovations.arcana.systems.research.ResearchBook;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import com.wonginnovations.arcana.systems.research.impls.AbstractCraftingSection;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Optional;

import static com.wonginnovations.arcana.client.gui.ResearchEntryScreen.HEIGHT_OFFSET;

public abstract class AbstractCraftingSectionRenderer<T extends AbstractCraftingSection> implements EntrySectionRenderer<T>{
	
	protected ResourceLocation textures = null;
	
	public void render(GuiGraphics guiGraphics, T section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		// if recipe exists: render result at specified position, defer drawing recipe
		// otherwise: render error message
		ResearchBook book = ResearchBooks.getEntry(section.getEntry()).category().book();
		// don't make a new RLoc every frame
		if (textures == null || !textures.getNamespace().equals(book.getKey().getNamespace()))
			textures = new ResourceLocation(book.getKey().getNamespace(), "textures/gui/research/" + book.getPrefix() + ResearchEntryScreen.OVERLAY_SUFFIX);
		Optional<? extends Recipe<?>> optRecipe = player.level().getRecipeManager().byKey(section.getRecipe());
		optRecipe.ifPresent(recipe -> {
			// draw result
			ItemStack result = recipe.getResultItem(player.level().registryAccess());
			renderResult(guiGraphics, right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X, resultOffset(recipe, section, pageIndex, screenWidth, screenHeight, mouseX, mouseY, right, player), result, screenWidth, screenHeight);
			renderRecipe(guiGraphics, recipe, section, pageIndex, screenWidth, screenHeight, mouseX, mouseY, right, player);
		});
		// else display error
		if (optRecipe.isEmpty())
			error();
	}
	
	public void renderAfter(GuiGraphics guiGraphics, T section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		Optional<? extends Recipe<?>> optRecipe = player.level().getRecipeManager().byKey(section.getRecipe());
		optRecipe.ifPresent(recipe -> {
			// draw result
			ItemStack result = recipe.getResultItem(player.level().registryAccess());
			renderResultTooltips(guiGraphics, right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X, resultOffset(recipe, section, pageIndex, screenWidth, screenHeight, mouseX, mouseY, right, player), result, screenWidth, screenHeight, mouseX, mouseY);
			renderRecipeTooltips(guiGraphics, recipe, section, pageIndex, screenWidth, screenHeight, mouseX, mouseY, right, player);
		});
		if (optRecipe.isEmpty())
			error();
	}
	
	abstract void renderRecipe(GuiGraphics guiGraphics, Recipe<?> recipe, T section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player);
	
	abstract void renderRecipeTooltips(GuiGraphics guiGraphics, Recipe<?> recipe, T section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player);
	
	int resultOffset(Recipe<?> recipe, T section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		return ResearchEntryScreen.PAGE_Y;
	}
	
	private void renderResult(GuiGraphics guiGraphics, int x, int y, ItemStack result, int screenWidth, int screenHeight) {
		int rX = x + (screenWidth - 256) / 2 + (ResearchEntryScreen.PAGE_WIDTH - 58) / 2;
		int rY = y + (screenHeight - 181) / 2 + 16 + HEIGHT_OFFSET;
		guiGraphics.blit(textures, rX, rY, 1, 167, 58, 20);
		item(guiGraphics, result, rX + 29 - 8, rY + 10 - 8);
		int stX = x + (screenWidth - 256) / 2 + (ResearchEntryScreen.PAGE_WIDTH - fr().width(result.getHoverName().getString())) / 2;
		int stY = y + (screenHeight - 181) / 2 + 11 - fr().lineHeight + HEIGHT_OFFSET;
		guiGraphics.drawString(fr(), result.getDisplayName().getString(), stX, stY, 0, false);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.enableBlend();
//		RenderSystem.disableLighting();
	}
	
	protected void renderResultTooltips(GuiGraphics guiGraphics, int x, int y, ItemStack result, int screenWidth, int screenHeight, int mouseX, int mouseY) {
		int rX = x + (screenWidth - 256) / 2 + (ResearchEntryScreen.PAGE_WIDTH - 58) / 2 + 21;
		int rY = y + (screenHeight - 181) / 2 + 18 + HEIGHT_OFFSET;
		tooltipArea(guiGraphics, result, mouseX, mouseY, rX, rY);
	}
	
	protected void item(GuiGraphics guiGraphics, ItemStack stack, int x, int y) {
		guiGraphics.renderItem(stack, x, y);
		guiGraphics.renderItemDecorations(fr(), stack, x, y);
	}
	
	protected void tooltipArea(GuiGraphics guiGraphics, ItemStack stack, int mouseX, int mouseY, int areaX, int areaY) {
		if (mouseX >= areaX && mouseX < areaX + 16 && mouseY >= areaY && mouseY < areaY + 16)
			tooltip(guiGraphics, stack, mouseX, mouseY);
	}
	
	protected void aspectTooltipArea(GuiGraphics guiGraphics, Aspect aspect, int mouseX, int mouseY, int screenWidth, int screenHeight, int areaX, int areaY) {
		if (mouseX >= areaX && mouseX < areaX + 16 && mouseY >= areaY && mouseY < areaY + 16)
			ClientUiUtil.drawAspectTooltip(guiGraphics, aspect, "", mouseX, mouseY, screenWidth, screenHeight);
	}
	
	protected void tooltip(GuiGraphics guiGraphics, ItemStack stack, int mouseX, int mouseY) {
		guiGraphics.renderComponentTooltip(fr(), new ArrayList<>(stack.getTooltipLines(mc().player, mc().options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL)), mouseX, mouseY);
	}
	
	public int span(T section, Player player) {
		return 1;
	}
	
	protected void error() {
		// display error
	}
	
	protected int displayIndex(int max, @Nonnull Entity player) {
		return (player.tickCount / 30) % max;
	}
}