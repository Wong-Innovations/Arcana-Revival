package com.wonginnovations.arcana.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.capabilities.Researcher;
import com.wonginnovations.arcana.client.research.EntrySectionRenderer;
import com.wonginnovations.arcana.client.research.RequirementRenderer;
import com.wonginnovations.arcana.client.research.impls.StringSectionRenderer;
import com.wonginnovations.arcana.network.Connection;
import com.wonginnovations.arcana.network.PkModifyPins;
import com.wonginnovations.arcana.systems.research.EntrySection;
import com.wonginnovations.arcana.systems.research.Pin;
import com.wonginnovations.arcana.systems.research.Requirement;
import com.wonginnovations.arcana.systems.research.ResearchEntry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public class ResearchEntryScreen extends Screen {
	public ResourceLocation bg;
	ResearchEntry entry;
	int index;
	Screen parentScreen;
	
	Button left, right, cont, ret;
	List<PinButton> pins;
	
	// there is: golem, crucible, crafting, infusion circle, arcane crafting, structure, wand(, arrow), crafting result
	public static final String OVERLAY_SUFFIX = "_gui_overlay.png";
	public static final String SUFFIX = "_gui.png";
	
	// 256 x 181 @ 0,0
	public static final int PAGE_X = 17;
	public static final int PAGE_Y = 10;
	public static final int PAGE_WIDTH = 105;
	public static final int PAGE_HEIGHT = 155;
	public static final int RIGHT_X_OFFSET = 119;
	public static final int HEIGHT_OFFSET = -10;
	
	public static float TEXT_SCALING = ArcanaConfig.BOOK_TEXT_SCALING.get().floatValue();
	
	public ResearchEntryScreen(ResearchEntry entry, Screen parentScreen) {
		super(Component.literal(""));
		this.entry = entry;
		this.parentScreen = parentScreen;
		bg = new ResourceLocation(entry.key().getNamespace(), "textures/gui/research/" + entry.category().book().getPrefix() + SUFFIX);
	}

	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		renderBackground(guiGraphics);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		guiGraphics.blit(bg, (width - 256) / 2, (height - 181) / 2 + HEIGHT_OFFSET, 0, 0, 256, 181);
		
		// Main rendering
		if (totalLength() > index) {
			EntrySection section = getSectionAtIndex(index);
			if (section != null)
				EntrySectionRenderer.get(section).render(guiGraphics, section, sectionIndex(index), width, height, mouseX, mouseY, false, getMinecraft().player);
		}
		if (totalLength() > index + 1) {
			EntrySection section = getSectionAtIndex(index + 1);
			if (section != null)
				EntrySectionRenderer.get(section).render(guiGraphics, section, sectionIndex(index + 1), width, height, mouseX, mouseY, true, getMinecraft().player);
		}
		
		// Requirements
		Researcher r = Researcher.getFrom(getMinecraft().player);
		if (r.entryStage(entry) < entry.sections().size() && !entry.sections().get(r.entryStage(entry)).getRequirements().isEmpty()) {
			List<Requirement> requirements = entry.sections().get(r.entryStage(entry)).getRequirements();
			final int y = (height - 181) / 2 + 180;
			final int reqWidth = 20;
			final int baseX = (width / 2) - (reqWidth * requirements.size() / 2);
			for (int i = 0, size = requirements.size(); i < size; i++) {
				Requirement requirement = requirements.get(i);
				renderer(requirement).render(guiGraphics, baseX + i * reqWidth + 2, y, requirement, getMinecraft().player.tickCount, partialTicks, getMinecraft().player);
				renderAmount(guiGraphics, requirement, baseX + i * reqWidth + 2, y, requirement.getAmount(), requirement.satisfied(getMinecraft().player));
			}
			// Show tooltips
			for (int i = 0, size = requirements.size(); i < size; i++)
				if (mouseX >= 20 * i + baseX + 2 && mouseX <= 20 * i + baseX + 18 && mouseY >= y && mouseY <= y + 18) {
					List<Component> tooltip = renderer(requirements.get(i)).tooltip(requirements.get(i), getMinecraft().player);
					List<String> lines = new ArrayList<>();
					for (int i1 = 0, tooltipSize = tooltip.size(); i1 < tooltipSize; i1++) {
						String s = tooltip.get(i1).getString();
						s = (i1 == 0 ? ChatFormatting.WHITE : ChatFormatting.GRAY) + s;
						lines.add(s);
					}
					guiGraphics.renderTooltip(minecraft.font, lines.stream().map(Component::literal).collect(Collectors.toList()), Optional.empty(), mouseX, mouseY);
					break;
				}
		}
		
		// After-renders (such as tooltips)
		if (totalLength() > index) {
			EntrySection section = getSectionAtIndex(index);
			if (section != null)
				EntrySectionRenderer.get(section).renderAfter(guiGraphics, section, sectionIndex(index), width, height, mouseX, mouseY, false, getMinecraft().player);
		}
		if (totalLength() > index + 1) {
			EntrySection section = getSectionAtIndex(index + 1);
			if (section != null)
				EntrySectionRenderer.get(section).renderAfter(guiGraphics, section, sectionIndex(index + 1), width, height, mouseX, mouseY, true, getMinecraft().player);
		}
		
		// Pin tooltips
		pins.forEach(button -> button.renderAfter(guiGraphics, mouseX, mouseY));
	}
	
	public void init() {
		super.init();
		final int y = (height - 181) / 2 + 190 + HEIGHT_OFFSET;
		final int x = width / 2 - 6;
		final int dist = 127;
		left = addRenderableWidget(new ChangePageButton(x - dist, y, false, button -> {
			if (canTurnLeft())
				index -= 2;
			updateButtons();
		}));
		right = addRenderableWidget(new ChangePageButton(x + dist, y, true, button -> {
			if (canTurnRight())
				index += 2;
			updateButtons();
		}));
		String text = I18n.get("researchEntry.continue");
		ExtendedButton button = new ExtendedButton(x - minecraft.font.width(text) / 2 + 2, y + 20, minecraft.font.width(text) + 10, 18, Component.literal(text), __ -> {
			Connection.sendTryAdvance(entry.key());
			// need to update visuals when an advance packet is received...
			updateButtons();
		}) {
			// I can't be bothered to make a new type for something which will use this behaviours exactly once.
			// If I ever need this behaviour elsewhere, I'll move it to a proper class.
			public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partial) {
				active = Researcher.getFrom(minecraft.player).entryStage(entry) < entry.sections().size() && entry.sections().get(Researcher.getFrom(getMinecraft().player).entryStage(entry)).getRequirements().stream().allMatch(it -> it.satisfied(getMinecraft().player));
				super.renderWidget(guiGraphics, mouseX, mouseY, partial);
			}
		};
		cont = addRenderableWidget(button);
		ret = addRenderableWidget(new ReturnToBookButton(width / 2 - 7, (height - 181) / 2 - 26, b -> Minecraft.getInstance().setScreen(parentScreen)));
		pins = new ArrayList<>();
		updateButtons();
	}
	
	public void updateButtons() {
		left.visible = canTurnLeft();
		right.visible = canTurnRight();
		Researcher researcher = Researcher.getFrom(getMinecraft().player);
		cont.visible = researcher.entryStage(entry) < getVisibleSections().size();
		
		pins.forEach(button -> {
			renderables.remove(button);
			this.children().remove(button);
		});
		pins.clear();
		List<Pin> collect = entry.getAllPins(getMinecraft().level).filter(p -> researcher.entryStage(p.getEntry()) >= p.getStage()).toList();
		for (int i = 0, size = collect.size(); i < size; i++) {
			Pin pin = collect.get(i);
			PinButton e = new PinButton((width / 2) + PAGE_WIDTH + 21, (height - PAGE_HEIGHT) / 2 + i * (size > 7 ? 21 : 22) - (size > 7 ? 15 : 0), pin);
			pins.add(e);
			addRenderableWidget(e);
		}
	}
	
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers))
			return true;
		else {
			Stream<KeyMapping> mouseKey = Arrays.stream(getMinecraft().options.keyMappings)
					.filter(keyMapping -> keyMapping.getKey().getValue() == keyCode);
			if (mouseKey.anyMatch(km -> getMinecraft().options.keyInventory.isActiveAndMatches(km.getKey())))
				Minecraft.getInstance().setScreen(parentScreen);
			else if (keyCode == 292)
				StringSectionRenderer.clearCache();
			return false;
		}
	}
	
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		// mouse button 1 is right click, just return
		if (mouseButton == 1) {
			Minecraft.getInstance().setScreen(parentScreen);
			return true;
		}
		// mouse button 0 is left click, defer to entries and requirements
		Researcher r = Researcher.getFrom(getMinecraft().player);
		if (r.entryStage(entry) < entry.sections().size() && entry.sections().get(r.entryStage(entry)).getRequirements().size() > 0) {
			List<Requirement> requirements = entry.sections().get(r.entryStage(entry)).getRequirements();
			final int y = (height - 181) / 2 + 180;
			final int reqSize = 20;
			final int baseX = (width / 2) - (reqSize * requirements.size() / 2);
			for (int i = 0, size = requirements.size(); i < size; i++)
				if (mouseX >= reqSize * i + baseX && mouseX <= reqSize * i + baseX + reqSize && mouseY >= y && mouseY <= y + reqSize)
					return requirements.get(i).onClick(entry, getMinecraft().player);
		}
		if (totalLength() > index) {
			EntrySection section = getSectionAtIndex(index);
			if (section != null)
				return EntrySectionRenderer.get(section).onClick(section, sectionIndex(index), width, height, mouseX, mouseY, false, getMinecraft().player);
		}
		if (totalLength() > index + 1) {
			EntrySection section = getSectionAtIndex(index + 1);
			if (section != null)
				return EntrySectionRenderer.get(section).onClick(section, sectionIndex(index + 1), width, height, mouseX, mouseY, true, getMinecraft().player);
		}
		return false;
	}
	
	public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
		if (scroll > 0 && canTurnLeft()) {
			index -= 2;
			updateButtons();
			return true;
		}
		if (scroll < 0 && canTurnRight()) {
			index += 2;
			updateButtons();
			return true;
		}
		return false;
	}
	
	private boolean canTurnRight() {
		return index < totalLength() - 2;
	}
	
	private boolean canTurnLeft() {
		return index > 0;
	}
	
	private int totalLength() {
		return entry.sections().stream().filter(this::visible).mapToInt(this::span).sum();
	}
	
	// What entry we're looking at
	private EntrySection getSectionAtIndex(int index) {
		if (index == 0)
			return entry.sections().get(0);
		int cur = 0;
		for (EntrySection section : getVisibleSections()) {
			if (cur <= index && cur + span(section) > index)
				return section;
			cur += span(section);
		}
		return null;
	}
	
	// How far along in the entry we are
	private int sectionIndex(int index) {
		int cur = 0;
		for (EntrySection section : getVisibleSections()) {
			if (cur <= index && cur + span(section) > index)
				return index - cur;
			cur += span(section);
		}
		return 0; // throw/show an error
	}
	
	// Index of the given stage
	int indexOfStage(int stage) {
		int cur = 0;
		List<EntrySection> sections = getVisibleSections();
		for (int i = 0, size = sections.size(); i < size; i++) {
			EntrySection section = sections.get(i);
			if (i == stage)
				return cur;
			cur += span(section);
		}
		return 0; // throw/show an error
	}
	
	private List<EntrySection> getVisibleSections() {
		return entry.sections().stream().filter(this::visible).collect(Collectors.toList());
	}
	
	private boolean visible(EntrySection section) {
		// cant use getMinecraft here because this is called from ResearchBookScreen before this is set
		return Researcher.getFrom(Minecraft.getInstance().player).entryStage(entry) >= entry.sections().indexOf(section);
	}
	
	private <T extends Requirement> RequirementRenderer<T> renderer(T requirement) {
		return RequirementRenderer.get(requirement);
	}
	
	private void renderAmount(GuiGraphics guiGraphics, Requirement requirement, int x, int y, int amount, boolean complete) {
		if (renderer(requirement).shouldDrawTickOrCross(requirement, amount)) {
			//display tick or cross
			RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			// ensure it renders over items
//			setBlitOffset(300);
			guiGraphics.blit(bg, x + 10, y + 9, complete ? 0 : 8, 247, 8, 9);
//			setBlitOffset(0);
		} else {
			String s = String.valueOf(amount);
			guiGraphics.drawString(minecraft.font, s, (float)(x + 17 - minecraft.font.width(s)), (float)(y + 9), complete ? 0xaaffaa : 0xffaaaa, true);
		}
	}
	
	private int span(EntrySection section) {
		return EntrySectionRenderer.get(section).span(section, Minecraft.getInstance().player);
	}
	
	public boolean isPauseScreen() {
		return false;
	}
	
	class ChangePageButton extends Button {
		
		boolean right;
		
		public ChangePageButton(int x, int y, boolean right, OnPress pressable) {
			super(x, y, 12, 6, Component.literal(""), pressable, DEFAULT_NARRATION);
			this.right = right;
		}
		
		@ParametersAreNonnullByDefault
		public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			if (visible) {
				isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + this.width && mouseY < getY() + this.height;
				float mult = isHovered ? 1f : 0.5f;
				RenderSystem.setShaderColor(mult, mult, mult, 1f);
				int texX = right ? 12 : 0;
				int texY = 185;
				guiGraphics.blit(bg, getX(), getY(), texX, texY, width, height);
				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			}
		}
	}
	
	class ReturnToBookButton extends Button {
		
		public ReturnToBookButton(int x, int y, OnPress event) {
			super(x, y, 15, 8, Component.literal(""), event, DEFAULT_NARRATION);
		}
		
		@ParametersAreNonnullByDefault
		public void renderWidget(GuiGraphics guiGraphics,  int mouseX, int mouseY, float partialTicks) {
			if (visible) {
				isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + this.width && mouseY < getY() + this.height;
				float mult = isHovered ? 1f : 0.5f;
				RenderSystem.setShaderColor(mult, mult, mult, 1f);
				int texX = 41;
				int texY = 204;
				guiGraphics.blit(bg, getX(), getY(), texX, texY, width, height);
				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
			}
		}
	}
	
	class PinButton extends Button {
		
		Pin pin;
		
		public PinButton(int x, int y, Pin pin) {
			super(x, y, 18, 18, Component.literal(""), b -> {
				if (Minecraft.getInstance().screen instanceof ResearchEntryScreen) {
					// can't reference variables here directly
					ResearchEntryScreen screen = (ResearchEntryScreen)Minecraft.getInstance().screen;
					if (!Screen.hasControlDown()) {
						// if stage index is an even number, skip there; else skip to before it.
						int stageIndex = screen.indexOfStage(pin.getStage());
						screen.index = stageIndex % 2 == 0 ? stageIndex : stageIndex - 1;
						screen.updateButtons();
					} else {
						Researcher from = Researcher.getFrom(Minecraft.getInstance().player);
						List<Integer> pinned = from.getPinned().get(pin.getEntry().key());
						if (pinned != null) {
							if (!pinned.contains(pin.getStage())) {
								from.addPinned(pin.getEntry().key(), pin.getStage());
								Connection.sendModifyPins(pin, PkModifyPins.Diff.pin);
							} else {
								from.removePinned(pin.getEntry().key(), pin.getStage());
								Connection.sendModifyPins(pin, PkModifyPins.Diff.unpin);
							}
						} else {
							// well we know for sure its not been pinned so we have no pins here
							from.addPinned(pin.getEntry().key(), pin.getStage());
							Connection.sendModifyPins(pin, PkModifyPins.Diff.pin);
						}
					}
				}
			}, DEFAULT_NARRATION);
			visible = true;
			this.pin = pin;
		}
		
		public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			if (visible) {
				RenderSystem.setShaderColor(1, 1, 1, 1);
				
				int stageIndex = indexOfStage(pin.getStage());
				int xOffset = index == (stageIndex % 2 == 0 ? stageIndex : stageIndex - 1) ? 6 : isHovered ? 4 : 0;

				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
				guiGraphics.blit(bg, getX() - 2, getY() - 1, 16 + (6 - xOffset), 238, 34 - (6 - xOffset), 18);

				ClientUiUtil.renderIcon(guiGraphics, pin.getIcon(), getX() + xOffset - 1, getY() - 1, 0);
			}
		}
		
		public void renderAfter(GuiGraphics guiGraphics, int mouseX, int mouseY) {
			// check if we're already pinned
			List<Integer> pinned = Researcher.getFrom(getMinecraft().player).getPinned().get(entry.key());
			String tooltip = ChatFormatting.AQUA + I18n.get(pinned != null && pinned.contains(pin.getStage()) ? "researchEntry.unpin" : "researchEntry.pin");
			
			isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
			if (isHovered)
				guiGraphics.renderTooltip(minecraft.font, Lists.newArrayList(Component.literal(pin.getIcon().getStack().getDisplayName().getString()), Component.literal(tooltip)), Optional.empty(), mouseX, mouseY);
		}
	}
}