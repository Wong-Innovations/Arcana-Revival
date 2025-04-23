package com.wonginnovations.arcana.client.gui;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.blocks.entities.FociForgeBlockEntity;
import com.wonginnovations.arcana.containers.FociForgeMenu;
import com.wonginnovations.arcana.containers.slots.AspectSlot;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.items.attachment.FocusItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.wonginnovations.arcana.containers.FociForgeMenu.ASPECT_H_COUNT;
import static com.wonginnovations.arcana.containers.FociForgeMenu.ASPECT_V_COUNT;

public class FociForgeScreen extends AspectContainerScreen<FociForgeMenu> {
	public static final int WIDTH = 397;
	public static final int HEIGHT = 283;

	public static final int SPELL_X = 119;
	public static final int SPELL_Y = 32;
	public static final int SPELL_WIDTH = 202;
	public static final int SPELL_HEIGHT = 133;

	public static final int ASPECT_SCROLL_X = 80;
	public static final int ASPECT_SCROLL_Y = 52;
	public static final int ASPECT_SCROLL_HEIGHT = 97;
	public static final int FOCI_SCROLL_X = 343;
	public static final int FOCI_SCROLL_Y = 40;
	public static final int FOCI_SCROLL_HEIGHT = 137;
	public static final int SCROLL_WIDTH = 12;
	public static final int SCROLL_HEIGHT = 15;
	public static final int FOCI_V_COUNT = 9;
	public static final int FOCI_APPLY_X = 338;
	public static final int FOCI_APPLY_Y = 12;
	public static final int APPLY_BTN_SIZE = 16;

	public static final ResourceLocation BG = new ResourceLocation(Arcana.MODID, "textures/gui/container/gui_fociforge.png");

	FociForgeBlockEntity te;
	float aspectScroll = 0, fociScroll = 0;
	boolean isScrollingAspect = false, isScrollingFoci = false, spellHasFocus = false;
	EditBox searchWidget;

	public FociForgeScreen(FociForgeMenu screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
		this.te = screenContainer.te;
		this.aspectContainer = screenContainer;
		this.aspectContainer.setSymbolic(true);
		imageWidth = WIDTH;
		imageHeight = HEIGHT;
		scrollAspectTo(0);
		scrollFociTo(0);
	}

	protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		renderBackground(guiGraphics);
		searchWidget.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.blit(BG, leftPos, topPos, 0, 0, WIDTH, HEIGHT, 397, 397);
		guiGraphics.blit(BG, leftPos + ASPECT_SCROLL_X, topPos + ASPECT_SCROLL_Y + (int)(ASPECT_SCROLL_HEIGHT * aspectScroll), 7, 345, SCROLL_WIDTH, SCROLL_HEIGHT, 397, 397);
		guiGraphics.blit(BG, leftPos + FOCI_SCROLL_X, topPos + FOCI_SCROLL_Y + (int)(FOCI_SCROLL_HEIGHT * fociScroll), 7, 345, SCROLL_WIDTH, SCROLL_HEIGHT, 397, 397);
		// Foci apply btn
		guiGraphics.blit(BG, leftPos + FOCI_APPLY_X, topPos + FOCI_APPLY_Y, 288, 313, APPLY_BTN_SIZE, APPLY_BTN_SIZE, 397, 397);
		te.spellState.render(guiGraphics, leftPos, topPos,leftPos + SPELL_X, topPos + SPELL_Y, SPELL_WIDTH, SPELL_HEIGHT, mouseX, mouseY);
	}

	@Override
	public void containerTick() {
		super.containerTick();
		if (this.searchWidget != null) {
			this.searchWidget.tick();
			searchWidget.setSuggestion(searchWidget.getValue().isEmpty() ? I18n.get("fociForge.search") : "");
			this.refreshSlotVisibility();
		}
	}

	@Override
	public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
		if (searchWidget.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
			return true;
		} else {
			return searchWidget.isFocused() && searchWidget.isVisible() && p_keyPressed_1_ != 256 || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
		}
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		double guiX = x - leftPos;
		double guiY = y - topPos;

		if (button == 0) {
			if (guiX >= ASPECT_SCROLL_X && guiX < ASPECT_SCROLL_X + SCROLL_WIDTH
				&& guiY >= ASPECT_SCROLL_Y && guiY < ASPECT_SCROLL_X + ASPECT_SCROLL_HEIGHT) {
				isScrollingAspect = true;
			} else if (guiX >= FOCI_SCROLL_X && guiX < FOCI_SCROLL_X + SCROLL_WIDTH
						&& guiY >= FOCI_SCROLL_Y && guiY < FOCI_SCROLL_X + FOCI_SCROLL_HEIGHT) {
				isScrollingFoci = true;
			} else if (guiX > SPELL_X && guiX < SPELL_X + SPELL_WIDTH
						&& guiY > SPELL_Y && guiY < SPELL_Y + SPELL_HEIGHT) {
				spellHasFocus = true;
			} else if (guiX >= FOCI_APPLY_X && guiY >= FOCI_APPLY_Y && guiX <= FOCI_APPLY_X + APPLY_BTN_SIZE && guiY <= FOCI_APPLY_Y + APPLY_BTN_SIZE) menu.changeFociSpell();
			te.spellState.mouseDown((int)(guiX - SPELL_X), (int)(guiY - SPELL_Y), button, aspectContainer.getHeldAspect());
		}

		return super.mouseClicked(x, y, button);
	}

	@Override
	public boolean mouseDragged(double x, double y, int button, double move_x, double move_y) {
		double guiX = x - leftPos;
		double guiY = y - topPos;
		if (button == 0) {
			if (isScrollingAspect) {
				this.aspectScroll = (float)((guiY - ASPECT_SCROLL_Y - 7.5F) / (ASPECT_SCROLL_HEIGHT));
				this.aspectScroll = Mth.clamp(this.aspectScroll, 0.0F, 1.0F);
				scrollAspectTo(this.aspectScroll);
				return true;
			} else if (isScrollingFoci) {
				this.fociScroll = (float) ((guiY - FOCI_SCROLL_Y - 7.5F) / (FOCI_SCROLL_HEIGHT));
				this.fociScroll = Mth.clamp(this.fociScroll, 0.0F, 1.0F);
				scrollFociTo(this.fociScroll);
				return true;
			} else if (spellHasFocus) {
				te.spellState.drag((int)(guiX - SPELL_X), (int)(guiY - SPELL_Y), button, move_x, move_y);
				return true;
			}
		}
		return super.mouseDragged(x, y, button, move_x, move_y);
	}

	// TODO: Move selection logic to rising edge trigger (mouseDown)
	@Override
	public boolean mouseReleased(double x, double y, int button) {
		double guiX = x - leftPos;
		double guiY = y - topPos;
		ItemStack heldItem = Arcana.proxy.getPlayerOnClient().inventoryMenu.getCarried();
		if (button == 0) {
			isScrollingAspect = false;
			isScrollingFoci = false;
			spellHasFocus = false;
			boolean success = te.spellState.mouseUp((int)(guiX - SPELL_X), (int)(guiY - SPELL_Y), button, aspectContainer.getHeldAspect(), heldItem);
			if (success) {
				aspectContainer.setHeldAspect(null);
			}
		} else if (button == 1) {
			boolean success = te.spellState.mouseUp((int)(guiX - SPELL_X), (int)(guiY - SPELL_Y), button, aspectContainer.getHeldAspect(), heldItem);
			if (success) {
				aspectContainer.setHeldAspect(null);
			}
		}

		return super.mouseReleased(x, y, button);
	}

	@Override
	protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
		for (AspectSlot slot : aspectContainer.getAspectSlots()) {
			if (slot.getInventory().get() != null && slot.visible) {
				if (isMouseOverSlot(mouseX, mouseY, slot)) {
					if (slot.getAspect() != null) {
						slot.description = I18n.get("tooltip.arcana.fociforge."+slot.getAspect().name().toLowerCase());
					}
				}
			}
		}
		super.renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	public boolean mouseScrolled(double x, double y, double bars) {
		if (x < this.leftPos + (WIDTH / 2.0)) {
			int extraRows = (Aspects.getWithoutPrimalsOrSins().size() + ASPECT_H_COUNT - 1) / ASPECT_H_COUNT - ASPECT_V_COUNT;
			this.aspectScroll = (float)(this.aspectScroll - bars / extraRows);
			this.aspectScroll = Mth.clamp(this.aspectScroll, 0.0F, 1.0F);
			scrollAspectTo(this.aspectScroll);
			this.refreshSlotVisibility();
		} else {
			int extraRows = FocusItem.DEFAULT_NUMSTYLES - ASPECT_V_COUNT;
			this.fociScroll = (float)(this.fociScroll - bars / extraRows);
			this.fociScroll = Mth.clamp(this.fociScroll, 0.0F, 1.0F);
			scrollFociTo(this.fociScroll);
		}
		return true;
	}

	// required for onClose to be called
	@Override
	public boolean shouldCloseOnEsc() {
		return true;
	}

	@Override
	public void onClose() {
		te.spellState.exitGui();
		super.onClose();
	}

	public void scrollAspectTo(float pos) {
		List<Aspect> searchAspects = AspectUtils.castContainingAspects();
		int extraRows = (searchAspects.size() + ASPECT_H_COUNT - 1) / ASPECT_H_COUNT - ASPECT_V_COUNT;
		int scroll = Math.max(0, Math.round(pos * extraRows));

		for (int row = 0; row < ASPECT_V_COUNT; row++) {
			for (int col = 0; col < ASPECT_H_COUNT; col++) {
				int slot = row * ASPECT_H_COUNT + col;
				int aspectNum = (scroll + row) * ASPECT_H_COUNT + col;
				if (aspectNum >= 0 && aspectNum < searchAspects.size()) {
					aspectContainer.scrollableSlots.get(slot).setAspect(searchAspects.get(aspectNum));
				} else {
					aspectContainer.scrollableSlots.get(slot).setAspect(Aspects.EMPTY);
				}
			}
		}
	}

	public void scrollFociTo(float pos) {
		int possibleFoci = FocusItem.DEFAULT_NUMSTYLES;

		int extraRows = possibleFoci - FOCI_V_COUNT;
		int scroll = Math.max(0, Math.round(pos * extraRows));

		for (int row = 0; row < FOCI_V_COUNT; row++) {
			int fociNum = scroll + row;
			if (te.focus() != ItemStack.EMPTY && fociNum >= 0 && fociNum < possibleFoci) {
				ItemStack dummyFoci = new ItemStack(ArcanaItems.DEFAULT_FOCUS.get(), 1);
				dummyFoci.getOrCreateTag().putInt("style", fociNum);
				menu.fociSlots.get(row).set(dummyFoci);
			} else {
				menu.fociSlots.get(row).set(ItemStack.EMPTY);
			}
		}
	}

	private static boolean slotMatchesSearch(AspectSlot slot, String str) {
		if (str.isEmpty())
			return true;
		return AspectUtils.getLocalizedAspectDisplayName(slot.getAspect()).toLowerCase().contains(str.toLowerCase());
	}

	@Override
	protected void init() {
		super.init();
		
		searchWidget = new EditBox(font, leftPos + 13, topPos + 36, 120, 15, Component.translatable("fociForge.search"));
		searchWidget.setMaxLength(30);
		searchWidget.setBordered(false);
		searchWidget.setTextColor(0xFFFFFF);
		searchWidget.setVisible(true);
		searchWidget.setCanLoseFocus(false);
		searchWidget.setFocused(true);

		addWidget(searchWidget);
	}

	protected void refreshSlotVisibility() {
		List<AspectSlot> slots = aspectContainer.getAspectSlots();
		for (AspectSlot slot : slots) {
			slot.visible = slotMatchesSearch(slot, searchWidget.getValue());
		}
	}
}
