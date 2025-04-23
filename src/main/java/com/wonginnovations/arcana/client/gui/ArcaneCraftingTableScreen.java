package com.wonginnovations.arcana.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.aspects.UndecidedAspectStack;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.aspects.handlers.AspectHolder;
import com.wonginnovations.arcana.client.ClientUtils;
import com.wonginnovations.arcana.containers.ArcaneCraftingTableMenu;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.items.recipes.ArcaneCraftingShapedRecipe;
import com.wonginnovations.arcana.items.recipes.AspectCraftingContainer;
import com.wonginnovations.arcana.items.recipes.IArcaneCraftingRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static com.wonginnovations.arcana.Arcana.arcLoc;

@OnlyIn(Dist.CLIENT)
public class ArcaneCraftingTableScreen extends AbstractContainerScreen<ArcaneCraftingTableMenu> {
	private static final ResourceLocation BG = arcLoc("textures/gui/container/arcaneworkbench.png");
	
	public static final int WIDTH = 187;
	public static final int HEIGHT = 233;
	
	public ArcaneCraftingTableScreen(ArcaneCraftingTableMenu screenContainer, Inventory inv, Component title) {
		super(screenContainer, inv, title);
		imageWidth = WIDTH;
		imageHeight = HEIGHT;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
		// if player clicked "show Arcanum" button and open Arcanum gui. If player doesn't have in inventory ignore.
		int arcanumButtonLeft = getGuiLeft() + 158, arcanumButtonTop = getGuiTop() + 109;
		if (isPlayerHavingArcanum())
			if (mouseX >= arcanumButtonLeft && mouseX < arcanumButtonLeft + 20 && mouseY >= arcanumButtonTop && mouseY < arcanumButtonTop + 20)
				ClientUtils.openResearchBookUI(arcLoc("arcanum"), this, null);
		return super.mouseClicked(mouseX, mouseY, buttonId);
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		renderBackground(guiGraphics);
		guiGraphics.blit(BG, getGuiLeft(), getGuiTop(), 0, 0, getXSize(), getYSize());

		// draw "show Arcanum" button if player it has in inventory
		int arcanumButtonLeft = getGuiLeft() + 158, arcanumButtonTop = getGuiTop() + 109;
		if (isPlayerHavingArcanum()) {
			guiGraphics.blit(BG, arcanumButtonLeft, arcanumButtonTop, 213, 17, 20, 20);
			if (mouseX >= arcanumButtonLeft && mouseX < arcanumButtonLeft + 20 && mouseY >= arcanumButtonTop && mouseY < arcanumButtonTop + 20)
				guiGraphics.blit(BG, arcanumButtonLeft, arcanumButtonTop, 213, 38, 20, 20);
		}

		// draw necessary aspects
		LocalPlayer player = getMinecraft().player;
		if (player != null) {
			Optional<IArcaneCraftingRecipe> optional = getRecipe(ArcaneCraftingShapedRecipe.RECIPES, menu.craftMatrix, player.level());
			if (optional.isPresent()) {
				IArcaneCraftingRecipe recipe = optional.get();
				// display necessary aspects
				// the wand is present
				for (UndecidedAspectStack stack : recipe.getAspectStacks()) {
					int color = 0xffffff;
					float amount = stack.stack.getAmount();
					if (!stack.any) {
						if (stack.stack.getAspect() == Aspects.AIR)
							ClientUiUtil.renderAspectStack(guiGraphics, stack.stack, getGuiLeft() + 65, getGuiTop() + 15, color);
						else if (stack.stack.getAspect() == Aspects.WATER)
							ClientUiUtil.renderAspectStack(guiGraphics, stack.stack, getGuiLeft() + 108, getGuiTop() + 39, color);
						else if (stack.stack.getAspect() == Aspects.FIRE)
							ClientUiUtil.renderAspectStack(guiGraphics, stack.stack, getGuiLeft() + 22, getGuiTop() + 39, color);
						else if (stack.stack.getAspect() == Aspects.EARTH)
							ClientUiUtil.renderAspectStack(guiGraphics, stack.stack, getGuiLeft() + 22, getGuiTop() + 89, color);
						else if (stack.stack.getAspect() == Aspects.ORDER)
							ClientUiUtil.renderAspectStack(guiGraphics, stack.stack, getGuiLeft() + 108, getGuiTop() + 89, color);
						else if (stack.stack.getAspect() == Aspects.CHAOS)
							ClientUiUtil.renderAspectStack(guiGraphics, stack.stack, getGuiLeft() + 65, getGuiTop() + 113, color);
					} else
						ClientUiUtil.renderAspectStack(guiGraphics, Aspects.EXCHANGE, amount, getGuiLeft() + 108, getGuiTop() + 113, color);
				}
			} else {
				// check if there's a match, but the wand isn't present
				Optional<IArcaneCraftingRecipe> optionalWithoutWand = getRecipeIgnoringWands(ArcaneCraftingShapedRecipe.RECIPES, menu.craftMatrix, player.level());
				if (optionalWithoutWand.isPresent()) {
					IArcaneCraftingRecipe possible = optionalWithoutWand.get();
					// check which aspects are missing
					for (UndecidedAspectStack stack : possible.getAspectStacks()) {
						boolean satisfied = true;
						boolean anySatisfied = false;
						boolean hasAny = false;
						float amount = stack.stack.getAmount();
						AspectHandler handler = AspectHandler.getFrom(menu.craftMatrix.getWandSlot().getItem());
						if (handler == null || handler.countHolders() == 0)
							satisfied = false;
						else for (AspectHolder holder : handler.getHolders()) {
							if (stack.any) {
								hasAny = true;
								if (holder.getStack().getAmount() >= stack.stack.getAmount())
									anySatisfied = true;
							}
							else if (holder.getStack().getAspect() == stack.stack.getAspect()) {
								if (holder.getStack().getAmount() < stack.stack.getAmount())
									satisfied = false;
							}
						}
						int color = satisfied && (!hasAny || anySatisfied) ? 0xffffff : 0xff0000;
						guiGraphics.pose().pushPose();
						if (!(satisfied && (!hasAny || anySatisfied))) {
							float col = (float)(Math.abs(Math.sin((player.level().getGameTime() + partialTicks) / 4d)) * .5f + .5f);
							RenderSystem.setShaderColor(col, col, col, 1);
						}
						if (!stack.any) {
							if (stack.stack.getAspect() == Aspects.AIR)
								ClientUiUtil.renderAspectStack(guiGraphics, stack.stack, getGuiLeft() + 65, getGuiTop() + 15, color);
							else if (stack.stack.getAspect() == Aspects.WATER)
								ClientUiUtil.renderAspectStack(guiGraphics, stack.stack, getGuiLeft() + 108, getGuiTop() + 39, color);
							else if (stack.stack.getAspect() == Aspects.FIRE)
								ClientUiUtil.renderAspectStack(guiGraphics, stack.stack, getGuiLeft() + 22, getGuiTop() + 39, color);
							else if (stack.stack.getAspect() == Aspects.EARTH)
								ClientUiUtil.renderAspectStack(guiGraphics, stack.stack, getGuiLeft() + 22, getGuiTop() + 89, color);
							else if (stack.stack.getAspect() == Aspects.ORDER)
								ClientUiUtil.renderAspectStack(guiGraphics, stack.stack, getGuiLeft() + 108, getGuiTop() + 89, color);
							else if (stack.stack.getAspect() == Aspects.CHAOS)
								ClientUiUtil.renderAspectStack(guiGraphics, stack.stack, getGuiLeft() + 65, getGuiTop() + 113, color);
						} else
							ClientUiUtil.renderAspectStack(guiGraphics, Aspects.EXCHANGE, amount, getGuiLeft() + 108, getGuiTop() + 113, color);
						guiGraphics.pose().popPose();
					}
				}
			}
		}
	}
	
	protected Optional<IArcaneCraftingRecipe> getRecipe(List<ArcaneCraftingShapedRecipe> recipeList, AspectCraftingContainer inventory, Level level) {
		Optional<IArcaneCraftingRecipe> matching = Optional.empty();
		for (ArcaneCraftingShapedRecipe recipe : recipeList) {
			if (recipe.matches(inventory, level)) {
				matching = Optional.of(recipe);
				break;
			}
		}
		return matching;
	}
	
	protected Optional<IArcaneCraftingRecipe> getRecipeIgnoringWands(List<ArcaneCraftingShapedRecipe> recipeList, AspectCraftingContainer inventory, Level level) {
		Optional<IArcaneCraftingRecipe> matching = Optional.empty();
		for (ArcaneCraftingShapedRecipe recipe : recipeList) {
			if (recipe.matchesIgnoringAspects(inventory, level)) {
				matching = Optional.of(recipe);
				break;
			}
		}
		return matching;
	}

	private boolean isPlayerHavingArcanum() {
		return menu.playerInventory.hasAnyMatching(item -> item.getItem() == ArcanaItems.ARCANUM.get());
	}
	
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		guiGraphics.drawString(font, title.getString(), 10, -5, 0xA0A0A0);
	}
	
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		renderTooltip(guiGraphics, mouseX, mouseY);
	}
}