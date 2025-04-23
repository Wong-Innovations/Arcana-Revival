package com.wonginnovations.arcana.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.blocks.entities.ResearchTableBlockEntity;
import com.wonginnovations.arcana.client.research.PuzzleRenderer;
import com.wonginnovations.arcana.containers.ResearchTableMenu;
import com.wonginnovations.arcana.containers.slots.AspectSlot;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.systems.research.Puzzle;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class ResearchTableScreen extends AspectContainerScreen<ResearchTableMenu> {
	public static final int WIDTH = 378;
	public static final int HEIGHT = 280;

	private static final ResourceLocation BG = new ResourceLocation(Arcana.MODID, "textures/gui/container/gui_researchbook.png");

	ResearchTableBlockEntity te;
	int page = 0;

	Button leftArrow, rightArrow;
	EditBox searchWidget;

	public ResearchTableScreen(ResearchTableMenu screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
		this.te = screenContainer.te;
		this.aspectContainer = screenContainer;
		imageWidth = WIDTH;
		imageHeight = HEIGHT;
		te.batteryIsDirty = true;
	}

	protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		renderBackground(guiGraphics);
		guiGraphics.blit(BG, leftPos, topPos, 0, 0, WIDTH, HEIGHT, 378, 378);
		if (!te.note().isEmpty() && te.note().getItem() == ArcanaItems.RESEARCH_NOTE.get()) {
			CompoundTag compound = te.note().getTag();
			if (compound != null) {
				Puzzle puzzle = ResearchBooks.puzzles.get(new ResourceLocation(compound.getString("puzzle")));
				if (puzzle != null) {
					PuzzleRenderer.get(puzzle).render(guiGraphics, puzzle, aspectContainer.puzzleSlots, aspectContainer.puzzleItemSlots, width, height, mouseX, mouseY, minecraft.player);
					if (te.ink().isEmpty() || te.ink().getDamageValue() >= 100) {
						// tell them "no u cant do research without a pen"
						int color = 0x44000000;
						guiGraphics.fillGradient(leftPos + 137, topPos + 31, leftPos + 360, topPos + 174, color, color);
						String noInk = te.ink().isEmpty() ? I18n.get("researchTable.ink_needed") : I18n.get("researchTable.ink_refill_needed");
						guiGraphics.drawString(font, noInk, leftPos + 141 + (213 - font.width(noInk)) / 2f, topPos + 35 + (134 - font.lineHeight) / 2f, -1, true);
					}
				}
			}
		}
		searchWidget.render(guiGraphics, mouseX, mouseY, partialTicks);
	}

	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		if (!te.note().isEmpty() && te.note().getItem() == ArcanaItems.RESEARCH_NOTE.get()) {
			CompoundTag compound = te.note().getTag();
			if (compound != null) {
				Puzzle puzzle = ResearchBooks.puzzles.get(new ResourceLocation(compound.getString("puzzle")));
				if (puzzle != null) {
					PuzzleRenderer.get(puzzle).renderAfter(guiGraphics, puzzle, aspectContainer.puzzleSlots, aspectContainer.puzzleItemSlots, width, height, mouseX, mouseY, minecraft.player);
				}
			}
		}
	}

	@Override
	public void containerTick() {
		super.containerTick();
		if (this.searchWidget != null) {
			this.searchWidget.tick();
			searchWidget.setSuggestion(searchWidget.getValue().isEmpty() ? I18n.get("researchTable.search") : "");
		}
		this.updateAspectSearch();
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (searchWidget.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
			return true;
		} else {
			return searchWidget.isFocused() && searchWidget.isVisible() && p_keyPressed_1_ != 256 || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
		}
	}

	private void updateAspectSearch() {
		if (!searchWidget.getValue().isEmpty()) {
			ResearchTableMenu container = aspectContainer;
			List<AspectSlot> slots = container.scrollableSlots;
			for (int i = 0; i < slots.size(); i++) {
				AspectSlot slot = slots.get(i);
				slot.visible = (i >= 36 * page && i < 36 * (page + 1)) && AspectUtils.getLocalizedAspectDisplayName(slot.getAspect()).toLowerCase().contains(this.searchWidget.getValue().toLowerCase());
			}
		} else {
			refreshSlotVisibility();
		}
	}

	@Override
	protected void init() {
		super.init();
		searchWidget = new EditBox(font, leftPos + 13, topPos + 14, 120, 15, Component.translatable("researchTable.search"));
		searchWidget.setMaxLength(30);
		searchWidget.setBordered(false);
		searchWidget.setTextColor(16777215);
		searchWidget.setVisible(true);
		searchWidget.setCanLoseFocus(false);
		searchWidget.setFocused(true);
		addWidget(searchWidget);
		leftArrow = addRenderableWidget(new ChangeAspectPageButton(leftPos + 11, topPos + 183, false, this::actionPerformed));
		rightArrow = addRenderableWidget(new ChangeAspectPageButton(leftPos + 112, topPos + 183, true, this::actionPerformed));
		te.batteryIsDirty = true;
	}

	protected void actionPerformed(@Nonnull Button button) {
		if (button == leftArrow && page > 0) {
			page--;
		}
		ResearchTableMenu container = aspectContainer;
		if (button == rightArrow && container.scrollableSlots.size() > 36 * (page + 1)) {
			page++;
		}
		if (searchWidget.getValue().isEmpty()) {
			refreshSlotVisibility();
		}
	}

	protected void refreshSlotVisibility() {
		ResearchTableMenu container = aspectContainer;
		List<AspectSlot> slots = container.scrollableSlots;
		for (int i = 0; i < slots.size(); i++) {
			AspectSlot slot = slots.get(i);
			slot.visible = i >= 36 * page && i < 36 * (page + 1);
		}
	}

	class ChangeAspectPageButton extends Button {
		boolean right;

		public ChangeAspectPageButton(int x, int y, boolean right, Button.OnPress onPress) {
			super(x, y, 15, 11, Component.literal(""), onPress, DEFAULT_NARRATION);
			this.right = right;
		}

		@Override
		public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			if (visible) {
				isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < leftPos + getX() + width && mouseY < topPos + getY() + height;
				int teX = right ? 120 : 135;
				int teY = 307;
				ResearchTableMenu container = aspectContainer;
				// first check if there are multiple pages
				if (container != null) {
					if (container.scrollableSlots.size() > 36) {
						if (right) {
							// if I am not on the last page
							if (container.scrollableSlots.size() > 36 * (page + 1)) {
								teY -= 11;
								if (isHovered) {
									teY -= 11;
								}
							}
						} else {
							// if I am not on the first page
							if (page > 0) {
								teY -= 11;
								if (isHovered) {
									teY -= 11;
								}
							}
						}
					}
				}
				// then just draw
//				RenderSystem.disableLighting();
				RenderSystem.setShaderColor(1, 1, 1, 1);
				guiGraphics.blit(BG, getX(), getY(), teX, teY, width, height, 378, 378);
			}
		}
	}
}