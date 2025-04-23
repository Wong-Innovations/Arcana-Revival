package com.wonginnovations.arcana.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.capabilities.Researcher;
import com.wonginnovations.arcana.client.research.BackgroundLayerRenderers;
import com.wonginnovations.arcana.network.Connection;
import com.wonginnovations.arcana.network.PkModifyPins;
import com.wonginnovations.arcana.systems.research.*;
import com.wonginnovations.arcana.util.Pair;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.*;
import static net.minecraft.util.Mth.clamp;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;

@OnlyIn(Dist.CLIENT)
public class ResearchBookScreen extends Screen {
	ResearchBook book;
	List<ResearchCategory> categories;
	ResourceLocation texture;
	int tab = 0;
	Screen parentScreen;
	List<TooltipButton> tooltipButtons = new ArrayList<>();
	List<PinButton> pinButtons = new ArrayList<>();
	ItemStack sender;

	// public static final String SUFFIX = "_menu_gui.png";
	public static final String SUFFIX_RESIZABLE = "_menu_resizable.png";
	public static final ResourceLocation ARROWS_AND_BASES = new ResourceLocation(Arcana.MODID, "textures/gui/research/research_bases.png");

	public static final int MAX_PAN = 512;
	private static final int ZOOM_MULTIPLIER = 2;

	// drawing helper
	private final Arrows arrows = new Arrows();

	static float xPan = 0;
	static float yPan = 0;
	static float zoom = 0.7f;
	static float targetZoom = 0.7f;
	static boolean showZoom = false;

	public ResearchBookScreen(ResearchBook book, Screen parentScreen, ItemStack sender) {
		super(Component.literal(""));
		this.sender = sender;
		this.parentScreen = parentScreen;
		this.book = book;
		texture = new ResourceLocation(book.getKey().getNamespace(), "textures/gui/research/" + book.getPrefix() + SUFFIX_RESIZABLE);
		Player player = Minecraft.getInstance().player;
		categories = book.getCategories().stream().filter(category -> {
			// has no requirement
			if (category.requirement() == null)
				return true;
			// has an invalid requirement
			ResearchEntry entry = ResearchBooks.getEntry(category.requirement());
			if (entry == null)
				return false;
			// has a valid requirement - check if unlocked
			return Researcher.getFrom(player).entryStage(entry) >= entry.sections().size();
		}).collect(Collectors.toList());
		zoom = targetZoom;
	}

	public float getXOffset() {
		return ((width / 2f) * (1 / zoom)) + (xPan / 2f);
	}

	public float getYOffset() {
		return ((height / 2f) * (1 / zoom)) - (yPan / 2f);
	}
	
	public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		if (ArcanaConfig.BOOK_SMOOTH_ZOOM.get()) {
			float diff = targetZoom - zoom;
			zoom = zoom + Math.min(partialTicks * (2 / 3f), 1) * diff;
		} else
			zoom = targetZoom;
		renderBackground(guiGraphics);
		RenderSystem.enableBlend();
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		
		// draw stuff
		// 224x196 viewing area
		int scale = (int)getMinecraft().getWindow().getGuiScale();
		int x = (width - getFrameWidth()) / 2 + 16, y = (height - getFrameHeight()) / 2 + 17;
		int visibleWidth = getFrameWidth() - 32, visibleHeight = getFrameHeight() - 34;
		GL11.glScissor(x * scale, y * scale, visibleWidth * scale, visibleHeight * scale);
		GL11.glEnable(GL_SCISSOR_TEST);
		// scissors on
		
		renderResearchBackground(guiGraphics);
		renderEntries(guiGraphics, partialTicks);
		
		// scissors off
		GL11.glDisable(GL_SCISSOR_TEST);
		
//		setBlitOffset(299);
		renderFrame(guiGraphics);
//		setBlitOffset(0);
		renderEntryTooltip(guiGraphics, mouseX, mouseY);
		
		tooltipButtons.forEach(pin -> pin.renderAfter(guiGraphics, mouseX, mouseY));
		RenderSystem.enableBlend();
	}

	public void init() {
		super.init();

		// add buttons
		for (int i = 0, size = categories.size(); i < size; i++) {
			ResearchCategory category = categories.get(i);
			CategoryButton categoryButton = new CategoryButton(i, (width - getFrameWidth()) / 2 - 12, 32 + ((height - getFrameHeight()) / 2) + 20 * i, category);
			addRenderableWidget(categoryButton);
			tooltipButtons.add(categoryButton);
		}

		refreshPinButtons(minecraft);
	}

	void refreshPinButtons(Minecraft mc) {
		tooltipButtons.removeAll(pinButtons);
		this.renderables.removeAll(pinButtons);
		this.children().removeAll(pinButtons);
		pinButtons.clear();
		AtomicInteger i = new AtomicInteger();
		Researcher.getFrom(mc.player).getPinned().forEach((entryKey, pins) -> pins.forEach(pin -> {
			// create Pin object
			ResearchEntry entry = ResearchBooks.getEntry(entryKey);
			if (entry != null && entry.category().book().equals(book)) {
				PinButton pinButton = new PinButton((width + getFrameWidth()) / 2 + 1, 32 + ((height - getFrameHeight()) / 2) + i.get() * 22, new Pin(entry, pin, mc.level));
				addRenderableWidget(pinButton);
				tooltipButtons.add(pinButton);
				pinButtons.add(pinButton);
			}
			i.incrementAndGet();
		}));
	}

	private void renderResearchBackground(GuiGraphics guiGraphics) {
		// please don't ask
		float xScale = 1024f / (512 + 32 - getFrameWidth());
		float yScale = 1024f / (512 + 34 - getFrameHeight());
		float scale = Math.max(xScale, yScale);
		// minValue = 512 - (minValue + fHeight + variance)
		// 2*minValue = 512 - (fHeight + 1024 / scale)
		int width = getFrameWidth() - 32;
		float xOffset = xScale == scale ? 0 : (512 - (width + 1024 / scale)) / 2;
		int height = getFrameHeight() - 34;
		float yOffset = yScale == scale ? 0 : (512 - (height + 1024 / scale)) / 2;
		int x = (this.width - getFrameWidth()) / 2 + 16;
		int y = (this.height - getFrameHeight()) / 2 + 17;
		if (!categories.get(tab).getBgs().isEmpty())
			categories.get(tab).getBgs().forEach(layer -> BackgroundLayerRenderers.render(layer, guiGraphics, x, y, width, height, xPan, yPan, scale, xOffset, yOffset, zoom));
		else
			guiGraphics.blit(categories.get(tab).bg(), x, y, (-xPan + MAX_PAN) / scale + xOffset, (yPan + MAX_PAN) / scale + yOffset, width, height, MAX_PAN, MAX_PAN);
	}

	private void renderEntries(GuiGraphics guiGraphics, float partialTicks) {
		guiGraphics.pose().scale(zoom, zoom, 1f);
		for (ResearchEntry entry : categories.get(tab).entries()) {
			PageStyle style = style(entry);
			if (style != PageStyle.NONE) {
				// render base
				int base = base(entry);
				float mult = 1f;
				if (style == PageStyle.IN_PROGRESS)
					mult = (float)abs(sin((getMinecraft().player.tickCount + partialTicks) / 5f) * 0.75f) + .25f;
				else if (style == PageStyle.PENDING)
					mult = 0.2f;
				RenderSystem.setShaderColor(mult, mult, mult, 1f);
				//noinspection IntegerDivisionInFloatingPointContext
				guiGraphics.blit(ARROWS_AND_BASES, (int)((entry.x() * 30 + getXOffset() + 2)), (int)((entry.y() * 30 + getYOffset() + 2)), base % 4 * 26, base / 4 * 26, 26, 26);
				// render texture
				if (!entry.icons().isEmpty()) {
					Icon icon = entry.icons().get((getMinecraft().player.tickCount / 30) % entry.icons().size());
					int x = (int)(entry.x() * 30 + getXOffset() + 7);
					int y = (int)(entry.y() * 30 + getYOffset() + 7);
					ClientUiUtil.renderIcon(guiGraphics, icon, x, y, 100);
				}

				// for every visible parent
				// draw an arrow & arrowhead
				RenderSystem.enableBlend();
				for (Parent parent : entry.parents()) {
					ResearchEntry parentEntry = ResearchBooks.getEntry(parent.getEntry());
					if (parentEntry != null && parent.shouldShowLine() && parentEntry.category().equals(entry.category()) && style(parentEntry) != PageStyle.NONE) {
						int xdiff = abs(entry.x() - parentEntry.x());
						int ydiff = abs(entry.y() - parentEntry.y());
						// if there is a y-difference or x-difference of 0, draw a line
						if (xdiff == 0 || ydiff == 0)
							if (xdiff == 0) {
								arrows.drawVerticalLine(guiGraphics, parentEntry.x(), entry.y(), parentEntry.y());
								if (parent.shouldShowArrowhead())
									if (parentEntry.y() > entry.y())
										arrows.drawUpArrow(guiGraphics, entry.x(), entry.y() + 1);
									else
										arrows.drawDownArrow(guiGraphics, entry.x(), entry.y() - 1);
							} else {
								arrows.drawHorizontalLine(guiGraphics, parentEntry.y(), entry.x(), parentEntry.x());
								if (parent.shouldShowArrowhead())
									if (parentEntry.x() > entry.x())
										arrows.drawLeftArrow(guiGraphics, entry.x() + 1, entry.y());
									else
										arrows.drawRightArrow(guiGraphics, entry.x() - 1, entry.y());
							}
						else {
							boolean unreversed = !(entry.meta().contains("reverse") || parent.shouldReverseLine());
							if (xdiff < 2 || ydiff < 2) {
								// from entry's POV
								if (unreversed) {
									arrows.drawVerticalLine(guiGraphics, entry.x(), parentEntry.y(), entry.y());
									arrows.drawHorizontalLine(guiGraphics, parentEntry.y(), parentEntry.x(), entry.x());
									if (entry.x() > parentEntry.x()) {
										if (entry.y() > parentEntry.y()) {
											arrows.drawLdCurve(guiGraphics, entry.x(), parentEntry.y());
											if (parent.shouldShowArrowhead())
												arrows.drawDownArrow(guiGraphics, entry.x(), entry.y() - 1);
										} else {
											arrows.drawLuCurve(guiGraphics, entry.x(), parentEntry.y());
											if (parent.shouldShowArrowhead())
												arrows.drawUpArrow(guiGraphics, entry.x(), entry.y() + 1);
										}
									} else {
										if (entry.y() > parentEntry.y()) {
											arrows.drawRdCurve(guiGraphics, entry.x(), parentEntry.y());
											if (parent.shouldShowArrowhead())
												arrows.drawDownArrow(guiGraphics, entry.x(), entry.y() - 1);
										} else {
											arrows.drawRuCurve(guiGraphics, entry.x(), parentEntry.y());
											if (parent.shouldShowArrowhead())
												arrows.drawUpArrow(guiGraphics, entry.x(), entry.y() + 1);
										}
									}
									// RDs and RUs are called at the same time (when LDs and LUs should)
								} else {
									arrows.drawHorizontalLine(guiGraphics, entry.y(), entry.x(), parentEntry.x());
									arrows.drawVerticalLine(guiGraphics, parentEntry.x(), parentEntry.y(), entry.y());
									if (entry.x() > parentEntry.x()) {
										if (entry.y() > parentEntry.y()) {
											// ru
											arrows.drawRuCurve(guiGraphics, parentEntry.x(), entry.y());
											if (parent.shouldShowArrowhead())
												arrows.drawRightArrow(guiGraphics, entry.x() - 1, entry.y());
										} else {
											// rd
											arrows.drawRdCurve(guiGraphics, entry.x() - 1, entry.y());
											if (parent.shouldShowArrowhead())
												arrows.drawRightArrow(guiGraphics, entry.x() - 1, entry.y());
										}
									} else {
										if (entry.y() > parentEntry.y()) {
											// lu
											arrows.drawLuCurve(guiGraphics, entry.x() + 1, entry.y());
											if (parent.shouldShowArrowhead())
												arrows.drawLeftArrow(guiGraphics, entry.x() + 1, entry.y());
										} else {
											// ld
											arrows.drawLdCurve(guiGraphics, entry.x() + 1, parentEntry.y() - 1);
											if (parent.shouldShowArrowhead())
												arrows.drawLeftArrow(guiGraphics, entry.x() + 1, entry.y());
										}
									}
								}
							} else {
								if (unreversed) {
									arrows.drawHorizontalLineMinus1(guiGraphics, parentEntry.y(), parentEntry.x(), entry.x());
									arrows.drawVerticalLineMinus1(guiGraphics, entry.x(), entry.y(), parentEntry.y());
									if (entry.x() > parentEntry.x()) {
										if (entry.y() > parentEntry.y()) {
											arrows.drawLargeLdCurve(guiGraphics, entry.x() - 1, parentEntry.y());
											if (parent.shouldShowArrowhead())
												arrows.drawDownArrow(guiGraphics, entry.x(), entry.y() - 1);
										} else {
											arrows.drawLargeLuCurve(guiGraphics, entry.x() - 1, parentEntry.y() - 1);
											if (parent.shouldShowArrowhead())
												arrows.drawUpArrow(guiGraphics, entry.x(), entry.y() + 1);
										}
									} else {
										if (entry.y() > parentEntry.y()) {
											arrows.drawLargeRdCurve(guiGraphics, entry.x(), parentEntry.y());
											if (parent.shouldShowArrowhead())
												arrows.drawDownArrow(guiGraphics, entry.x(), entry.y() - 1);
										} else {
											arrows.drawLargeRuCurve(guiGraphics, entry.x(), parentEntry.y() - 1);
											if (parent.shouldShowArrowhead())
												arrows.drawUpArrow(guiGraphics, entry.x(), entry.y() + 1);
										}
									}
								} else {
									arrows.drawHorizontalLineMinus1(guiGraphics, entry.y(), entry.x(), parentEntry.x());
									arrows.drawVerticalLineMinus1(guiGraphics, parentEntry.x(), parentEntry.y(), entry.y());
									if (entry.x() > parentEntry.x()) {
										if (entry.y() > parentEntry.y())
											arrows.drawLargeRuCurve(guiGraphics, parentEntry.x(), entry.y() - 1);
										else
											arrows.drawLargeRdCurve(guiGraphics, parentEntry.x(), entry.y());
										if (parent.shouldShowArrowhead())
											arrows.drawRightArrow(guiGraphics, entry.x() - 1, entry.y());
									} else {
										if (entry.y() > parentEntry.y())
											arrows.drawLargeLuCurve(guiGraphics, entry.x() + 1, entry.y() - 1);
										else
											arrows.drawLargeLdCurve(guiGraphics, entry.x() + 1, entry.y());
										if (parent.shouldShowArrowhead())
											arrows.drawLeftArrow(guiGraphics, entry.x() + 1, entry.y());
									}
								}
							}
						}
					}
				}
			}
		}
		guiGraphics.pose().scale(1 / zoom, 1 / zoom, 1f);
	}

	public PageStyle style(ResearchEntry entry) {
		// locked entries are always locked
		if (entry.meta().contains("locked"))
			return PageStyle.PENDING;
		// if the page is at full progress, its complete.
		Researcher r = Researcher.getFrom(getMinecraft().player);
		if (r.entryStage(entry) >= entry.sections().size())
			return PageStyle.COMPLETE;
		// if its progress is greater than zero, then its in progress.
		if (r.entryStage(entry) > 0)
			return PageStyle.IN_PROGRESS;
		// if it has no parents *and* the "root" tag, its available to do and in progress.
		if (entry.meta().contains("root") && entry.parents().isEmpty())
			return PageStyle.IN_PROGRESS;
		// if it does not have the "hidden" tag:
		if (!entry.meta().contains("hidden")) {
			List<PageStyle> parentStyles = entry.parents().stream().map(parent -> Pair.of(ResearchBooks.getEntry(parent.getEntry()), parent)).map(p -> parentStyle(p.getFirst(), p.getSecond())).collect(Collectors.toList());
			// if all of its parents are complete, it is available to do and in progress.
			if (parentStyles.stream().allMatch(PageStyle.COMPLETE::equals))
				return PageStyle.IN_PROGRESS;
			// if at least one of its parents are in progress, its pending.
			if (parentStyles.stream().anyMatch(PageStyle.IN_PROGRESS::equals))
				return PageStyle.PENDING;
		}
		// otherwise, its invisible
		return PageStyle.NONE;
	}

	public PageStyle parentStyle(ResearchEntry entry, Parent parent) {
		// if the parent is greater than required, consider it complete
		Objects.requireNonNull(entry, "Tried to get the stage of a parent entry that doesn't exist: " + parent.getEntry().toString() + " (from " + parent.asString() + ")");
		Researcher r = Researcher.getFrom(getMinecraft().player);
		if (parent.getStage() == -1) {
			if (r.entryStage(entry) >= entry.sections().size())
				return PageStyle.COMPLETE;
		} else if (r.entryStage(entry) >= parent.getStage())
			return PageStyle.COMPLETE;
		// if its progress is greater than zero, then its in progress.
		if (r.entryStage(entry) > 0)
			return PageStyle.IN_PROGRESS;
		// if it has no parents *and* the "root" tag, its available to do and in progress.
		if (entry.meta().contains("root") && entry.parents().isEmpty())
			return PageStyle.IN_PROGRESS;
		// if it does not have the "hidden" tag:
		if (!entry.meta().contains("hidden")) {
			List<PageStyle> parentStyles = entry.parents().stream().map(p -> Pair.of(ResearchBooks.getEntry(p.getEntry()), p)).map(p -> parentStyle(p.getFirst(), p.getSecond())).collect(Collectors.toList());
			// if all of its parents are complete, it is available to do and in progress.
			if (parentStyles.stream().allMatch(PageStyle.COMPLETE::equals))
				return PageStyle.IN_PROGRESS;
			// if at least one of its parents are in progress, its pending.
			if (parentStyles.stream().anyMatch(PageStyle.IN_PROGRESS::equals))
				return PageStyle.PENDING;
		}
		// otherwise, its invisible
		return PageStyle.NONE;
	}

	private int base(ResearchEntry entry) {
		int base = 8;
		if (entry.meta().contains("purple_base"))
			base = 0;
		else if (entry.meta().contains("yellow_base"))
			base = 4;
		else if (entry.meta().contains("no_base"))
			return 12;

		if (entry.meta().contains("round_base"))
			return base + 1;
		else if (entry.meta().contains("square_base"))
			return base + 2;
		else if (entry.meta().contains("hexagon_base"))
			return base + 3;
		else if (entry.meta().contains("spiky_base"))
			return base;
		return base + 2;
	}

	private void renderEntryTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		for (ResearchEntry entry : categories.get(tab).entries()) {
			PageStyle style = null;
			if (hovering(entry, mouseX, mouseY) && (style = style(entry)) == PageStyle.COMPLETE || style == PageStyle.IN_PROGRESS) {
				//tooltip
				List<String> lines = Lists.newArrayList(I18n.get(entry.name()));
				if (entry.description() != null && !entry.description().equals(""))
					lines.add(ChatFormatting.GRAY + I18n.get(entry.description()));
				guiGraphics.renderTooltip(minecraft.font, lines.stream().map(Component::literal).collect(Collectors.toList()), Optional.empty(), mouseX, mouseY);
//				RenderSystem.disableStandardItemLighting();
				break;
			} else if (style == PageStyle.PENDING) { // style will be null if not hovering
				if (entry.meta().contains("locked")) {
					// display "locked"
					guiGraphics.renderTooltip(minecraft.font, Arrays.asList(Component.translatable("researchBook.locked"), Component.translatable("researchBook.locked.desc").withStyle(ChatFormatting.GRAY)), Optional.empty(), mouseX, mouseY);
				} else {
					// show known required research + "???" if there's unknown research
					List<String> lines = Lists.newArrayList(I18n.get("researchBook.missing_research"));
					boolean addedUnknown = false;
					for (Pair<ResearchEntry, Parent> parent : entry.parents().stream().map(p -> new Pair<>(book.getEntry(p.getEntry()), p)).collect(Collectors.toList())) {
						if (parentStyle(parent.getFirst(), parent.getSecond()) != PageStyle.NONE)
							lines.add(ChatFormatting.GRAY + "- " + I18n.get(parent.getFirst().name()) + ChatFormatting.RESET);
						else if (!addedUnknown) {
							lines.add(I18n.get("researchBook.unknown"));
							addedUnknown = true;
						}
					}
					guiGraphics.renderTooltip(minecraft.font, lines.stream().map(Component::literal).collect(Collectors.toList()), Optional.empty(), mouseX, mouseY);
				}
//				RenderSystem.disableStandardItemLighting();
				break;
			}
		}
	}

	private boolean hovering(ResearchEntry entry, int mouseX, int mouseY) {
		int x = (int)((entry.x() * 30 + getXOffset() + 2) * zoom);
		int y = (int)((entry.y() * 30 + getYOffset() + 2) * zoom);
		int scrx = (width - getFrameWidth()) / 2 + 16, scry = (height - getFrameHeight()) / 2 + 17;
		int visibleWidth = getFrameWidth() - 32, visibleHeight = getFrameHeight() - 34;
		return mouseX >= x && mouseX <= x + (26 * zoom) && mouseY >= y && mouseY <= y + (26 * zoom) && mouseX >= scrx && mouseX <= scrx + visibleWidth && mouseY >= scry && mouseY <= scry + visibleHeight;
	}

	private void renderFrame(GuiGraphics guiGraphics) {
		RenderSystem.enableBlend();
		// resizable gui!
		// 69x69 corners, 2px sides, then add decorations
		int width = getFrameWidth();
		int height = getFrameHeight();
		int x = (this.width - width) / 2;
		int y = (this.height - height) / 2;
		GuiDrawBox.drawContinuousTexturedBox(guiGraphics, texture, x, y, 0, 0, width, height, 140, 140, 69,69,69,69);
		// draw top
		guiGraphics.blit(texture, (x + (width / 2)) - 36, y, 140, 0, 72, 17);
		// draw bottom
		guiGraphics.blit(texture, (x + (width / 2)) - 36, (y + height) - 18, 140, 17, 72, 18);
		// draw left
		guiGraphics.blit(texture, x, (y + (height / 2)) - 35, 140, 35, 17, 70);
		// draw right
		guiGraphics.blit(texture, x + width - 17, (y + (height / 2)) - 35, 157, 35, 17, 70);
		if (showZoom) {
			guiGraphics.drawString(minecraft.font, "Zoom: " + zoom + " (" + targetZoom + ")", x + 22, y + 5, -1, false);
			guiGraphics.drawString(minecraft.font, "XPan: " + xPan, x + 22, y - 13 + getFrameHeight(), -1, false);
			guiGraphics.drawString(minecraft.font, "YPan: " + yPan, x + 112, y - 13 + getFrameHeight(), -1, false);
			guiGraphics.drawString(minecraft.font, "FWidth: " + getFrameWidth(), x + 212, y - 13 + getFrameHeight(), -1, false);
			guiGraphics.drawString(minecraft.font, "FHeight: " + getFrameHeight(), x + 272, y - 13 + getFrameHeight(), -1, false);
			RenderSystem.enableDepthTest();
		}
	}

	private int getFrameWidth() {
		int conf = ArcanaConfig.CUSTOM_BOOK_WIDTH.get();
		if (conf == -1)
			return min(width - 60, 512);
		else
			return clamp(conf, 220, width);
	}

	private int getFrameHeight() {
		int conf = ArcanaConfig.CUSTOM_BOOK_HEIGHT.get();
		if (conf == -1)
			return height - 40;
		else
			return clamp(conf, 220, height);
	}

	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		xPan += (deltaX * ZOOM_MULTIPLIER) / zoom;
		yPan -= (deltaY * ZOOM_MULTIPLIER) / zoom;
		xPan = clamp(xPan, -MAX_PAN, MAX_PAN);
		yPan = clamp(yPan, -MAX_PAN, MAX_PAN);
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (ResearchEntry entry : categories.get(tab).entries()) {
			PageStyle style;
			if (hovering(entry, (int)mouseX, (int)mouseY)) {
				if (button != 2) {
					if ((style = style(entry)) == PageStyle.COMPLETE || style == PageStyle.IN_PROGRESS)
						// left/right (& other) click: open page
						getMinecraft().setScreen(new ResearchEntryScreen(entry, this));
				} else
					// middle click: try advance
					Connection.sendTryAdvance(entry.key());
				break;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
		float amnt = 1.2f;
		if ((scroll < 0 && targetZoom > 0.5) || (scroll > 0 && targetZoom < 1))
			targetZoom *= scroll > 0 ? amnt : 1 / amnt;
		if (targetZoom > 1f)
			targetZoom = 1f;
		return super.mouseScrolled(mouseX, mouseY, scroll);
	}

	public boolean isPauseScreen() {
		return false;
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}
		Stream<KeyMapping> mouseKey = Arrays.stream(getMinecraft().options.keyMappings)
				.filter(keyMapping -> keyMapping.getKey().getValue() == keyCode);

		if (mouseKey.anyMatch(km -> getMinecraft().options.keyInventory.isActiveAndMatches(km.getKey()))) {
			onClose();
			return true;
		} else if (keyCode == 292) {
			// F3 pressed
			showZoom = !showZoom;
		}
		return false;
	}
	
	public void onClose() {
		if (sender != null)
			sender.getOrCreateTag().putBoolean("open", false);
		Minecraft.getInstance().setScreen(parentScreen);
	}

	/**
	 * Helper class containing methods to draw segments of lines. Made to avoid cluttering up the namespace.
	 * The lines texture must be bound before calling these methods.
	 */
	private final class Arrows{

		int gX2SX(int gX) {
			return (int)((gX * 30 + getXOffset()));
		}

		int gY2SY(int gY) {
			return (int)((gY * 30 + getYOffset()));
		}

		void drawHorizontalSegment(GuiGraphics guiGraphics, int gX, int gY) {
			guiGraphics.blit(ARROWS_AND_BASES, gX2SX(gX), gY2SY(gY), 104, 0, 30, 30);
		}

		void drawVerticalSegment(GuiGraphics guiGraphics, int gX, int gY) {
			guiGraphics.blit(ARROWS_AND_BASES, gX2SX(gX), gY2SY(gY), 134, 0, 30, 30);
		}

		void drawHorizontalLine(GuiGraphics guiGraphics, int y, int startGX, int endGX) {
			int temp = startGX;
			// *possibly* swap them
			startGX = min(startGX, endGX);
			endGX = max(endGX, temp);
			// *exclusive*
			for (int j = startGX + 1; j < endGX; j++) {
				drawHorizontalSegment(guiGraphics, j, y);
			}
		}

		void drawVerticalLine(GuiGraphics guiGraphics, int x, int startGY, int endGY) {
			int temp = startGY;
			// *possibly* swap them
			startGY = min(startGY, endGY);
			endGY = max(endGY, temp);
			// *exclusive*
			for (int j = startGY + 1; j < endGY; j++)
				drawVerticalSegment(guiGraphics, x, j);
		}

		void drawHorizontalLineMinus1(GuiGraphics guiGraphics, int y, int startGX, int endGX) {
			int temp = startGX;
			// take one
			if (startGX > endGX)
				endGX++;
			else
				endGX--;
			// *possibly* swap them
			startGX = min(startGX, endGX);
			endGX = max(endGX, temp);
			// *exclusive*
			for (int j = startGX + 1; j < endGX; j++)
				drawHorizontalSegment(guiGraphics, j, y);
		}

		void drawVerticalLineMinus1(GuiGraphics guiGraphics, int x, int startGY, int endGY) {
			int temp = startGY;
			// take one
			if (startGY > endGY)
				endGY++;
			else
				endGY--;
			// *possibly* swap them
			startGY = min(startGY, endGY);
			endGY = max(endGY, temp);
			// *exclusive*
			for (int j = startGY + 1; j < endGY; j++)
				drawVerticalSegment(guiGraphics, x, j);
		}

		void drawLuCurve(GuiGraphics guiGraphics, int gX, int gY) {
			guiGraphics.blit(ARROWS_AND_BASES, gX2SX(gX), gY2SY(gY), 164, 0, 30, 30);
		}

		void drawRuCurve(GuiGraphics guiGraphics, int gX, int gY) {
			guiGraphics.blit(ARROWS_AND_BASES, gX2SX(gX), gY2SY(gY), 194, 0, 30, 30);
		}

		void drawLdCurve(GuiGraphics guiGraphics, int gX, int gY) {
			guiGraphics.blit(ARROWS_AND_BASES, gX2SX(gX), gY2SY(gY), 224, 0, 30, 30);
		}

		void drawRdCurve(GuiGraphics guiGraphics, int gX, int gY) {
			guiGraphics.blit(ARROWS_AND_BASES, gX2SX(gX), gY2SY(gY), 104, 30, 30, 30);
		}

		void drawLargeLuCurve(GuiGraphics guiGraphics, int gX, int gY) {
			guiGraphics.blit(ARROWS_AND_BASES, gX2SX(gX), gY2SY(gY), 134, 30, 60, 60);
		}

		void drawLargeRuCurve(GuiGraphics guiGraphics, int gX, int gY) {
			guiGraphics.blit(ARROWS_AND_BASES, gX2SX(gX), gY2SY(gY), 194, 30, 60, 60);
		}

		void drawLargeLdCurve(GuiGraphics guiGraphics, int gX, int gY) {
			guiGraphics.blit(ARROWS_AND_BASES, gX2SX(gX), gY2SY(gY), 134, 90, 60, 60);
		}

		void drawLargeRdCurve(GuiGraphics guiGraphics, int gX, int gY) {
			guiGraphics.blit(ARROWS_AND_BASES, gX2SX(gX), gY2SY(gY), 194, 90, 60, 60);
		}

		void drawDownArrow(GuiGraphics guiGraphics, int gX, int gY) {
			guiGraphics.blit(ARROWS_AND_BASES, gX2SX(gX), gY2SY(gY) + 1, 104, 60, 30, 30);
		}

		void drawUpArrow(GuiGraphics guiGraphics, int gX, int gY) {
			guiGraphics.blit(ARROWS_AND_BASES, gX2SX(gX), gY2SY(gY) - 1, 104, 120, 30, 30);
		}

		void drawLeftArrow(GuiGraphics guiGraphics, int gX, int gY) {
			guiGraphics.blit(ARROWS_AND_BASES, gX2SX(gX) - 1, gY2SY(gY), 104, 90, 30, 30);
		}

		void drawRightArrow(GuiGraphics guiGraphics, int gX, int gY) {
			guiGraphics.blit(ARROWS_AND_BASES, gX2SX(gX) + 1, gY2SY(gY), 104, 150, 30, 30);
		}
	}

	interface TooltipButton {

		void renderAfter(GuiGraphics guiGraphics, int mouseX, int mouseY);
	}

	class CategoryButton extends Button implements TooltipButton{

		protected int categoryNum;
		protected ResearchCategory category;

		public CategoryButton(int categoryNum, int x, int y, ResearchCategory category) {
			super(x, y, 16, 16, Component.literal(""), button -> {
				if (Minecraft.getInstance().screen instanceof ResearchBookScreen)
					((ResearchBookScreen)Minecraft.getInstance().screen).tab = categoryNum;
			}, DEFAULT_NARRATION);
			this.categoryNum = categoryNum;
			this.category = category;
			visible = true;
		}

		@ParametersAreNonnullByDefault
		public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			if (visible) {
				RenderSystem.setShaderColor(1, 1, 1, 1);
//				RenderHelper.disableStandardItemLighting();

				guiGraphics.blit(category.icon(), getX() - (categoryNum == tab ? 6 : (isHovered) ? 4 : 0), getY(), 0, 0, 16, 16, 16, 16);

				isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
			}
		}

		public void renderAfter(GuiGraphics guiGraphics, int mouseX, int mouseY) {
			if (isHovered) {
				int completion = (!category.entries().isEmpty() && category.entries().stream().filter(x -> !x.meta().contains("locked")).count() != 0)
						? ((category.streamEntries().filter(x -> !x.meta().contains("locked")).mapToInt(x -> Researcher.getFrom(getMinecraft().player).entryStage(x) >= x.sections().size() ? 1 : 0).sum() * 100) / (int)category.entries().stream().filter(x -> !x.meta().contains("locked")).count())
						: 100;
				guiGraphics.renderTooltip(minecraft.font, Lists.newArrayList(Component.literal(I18n.get(category.name()).trim() + " (" + completion + "%)")), Optional.empty(), mouseX, mouseY);
			}
		}
	}

	class PinButton extends Button implements TooltipButton {

		Pin pin;

		public PinButton(int x, int y, Pin pin) {
			super(x, y, 18, 18, Component.literal(""), b -> {
				if (Screen.hasControlDown()) {
					// unpin
					Researcher from = Researcher.getFrom(Minecraft.getInstance().player);
					List<Integer> pinned = from.getPinned().get(pin.getEntry().key());
					if (pinned != null) {
						from.removePinned(pin.getEntry().key(), pin.getStage());
						Connection.sendModifyPins(pin, PkModifyPins.Diff.unpin);
					}
					// and remove this button
					ResearchBookScreen thisScreen = (ResearchBookScreen)Minecraft.getInstance().screen;
					thisScreen.refreshPinButtons(Minecraft.getInstance());
				} else {
					// lets jump over to the specific stage
					// first check if you even have the research
					ResearchEntry entry = pin.getEntry();
					if (entry != null && Researcher.getFrom(Minecraft.getInstance().player).entryStage(entry) >= pin.getStage()) {
						ResearchEntryScreen in = new ResearchEntryScreen(entry, Minecraft.getInstance().screen);
						int stageIndex = in.indexOfStage(pin.getStage());
						in.index = stageIndex % 2 == 0 ? stageIndex : stageIndex - 1;
						Minecraft.getInstance().setScreen(in);
					}
				}
			}, DEFAULT_NARRATION);
			this.pin = pin;
		}

		public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			if (visible) {
				int xOffset = isHovered ? 3 : 0;

				RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
				guiGraphics.blit(texture, getX() - 2, getY() - 1, 6 - xOffset, 140, 34 - (6 - xOffset), 18);

				ClientUiUtil.renderIcon(guiGraphics, pin.getIcon(), getX() + xOffset, getY() - 1, 0);
			}
		}

		public void renderAfter(GuiGraphics guiGraphics, int mouseX, int mouseY) {
			isHovered = mouseX >= getX() && mouseY >= getY() && mouseX < getX() + width && mouseY < getY() + height;
			if (isHovered)
				guiGraphics.renderTooltip(minecraft.font, Lists.newArrayList(Component.literal(pin.getIcon().getStack().getDisplayName().getString()), Component.literal(ChatFormatting.AQUA + I18n.get("researchBook.jump_to_pin")), Component.literal(ChatFormatting.AQUA + I18n.get("researchEntry.unpin"))), Optional.empty(), mouseX, mouseY);
		}
	}
}