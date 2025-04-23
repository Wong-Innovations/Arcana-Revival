package com.wonginnovations.arcana.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.aspects.handlers.AspectHolder;
import com.wonginnovations.arcana.capabilities.Researcher;
import com.wonginnovations.arcana.items.attachment.Core;
import com.wonginnovations.arcana.systems.research.Icon;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import com.wonginnovations.arcana.systems.research.ResearchEntry;
import com.wonginnovations.arcana.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ClientUiUtil{
	
	private static ResourceLocation RESEARCH_EXPERTISE = Arcana.arcLoc("research_expertise");
	
	public static void renderAspectStack(GuiGraphics guiGraphics, AspectStack stack, int x, int y) {
		renderAspectStack(guiGraphics, stack, x, y, UiUtil.tooltipColor(stack.getAspect()));
	}
	
	public static void renderAspectStack(GuiGraphics guiGraphics, AspectStack stack, int x, int y, int color) {
		renderAspectStack(guiGraphics, stack.getAspect(), stack.getAmount(), x, y, color);
	}
	
	public static void renderAspectStack(GuiGraphics guiGraphics, Aspect aspect, float amount, int x, int y) {
		renderAspectStack(guiGraphics, aspect, amount, x, y, UiUtil.tooltipColor(aspect));
	}
	
	public static void renderAspectStack(GuiGraphics guiGraphics, Aspect aspect, float amount, int x, int y, int color) {
		Minecraft mc = Minecraft.getInstance();
		// render aspect
		renderAspect(guiGraphics, aspect, x, y);
		// render amount
		PoseStack poseStack = guiGraphics.pose();
		// if there is a fractional part, round it
		String s = (amount % 1 > 0.1) ? String.format("%.1f", amount) : String.format("%.0f", amount);
		poseStack.translate(0, 0, /* mc.getItemRenderer().zLevel +*/ 200.0F);
		guiGraphics.drawString(mc.font, s, x + 19 - mc.font.width(s), y + 10, color, true);
	}
	
	public static void renderAspect(GuiGraphics guiGraphics, Aspect aspect, int x, int y) {
		guiGraphics.blit(AspectUtils.getAspectTextureLocation(aspect), x, y, 0, 0, 16, 16, 16, 16);
	}
	
	public static boolean shouldShowAspectIngredients() {
		// true if research expertise has been completed
		Researcher from = Researcher.getFrom(Minecraft.getInstance().player);
		ResearchEntry entry = ResearchBooks.getEntry(RESEARCH_EXPERTISE);
		// If the player is null, their researcher is null, or research expertise no longer exists, display anyways
		return entry == null || (from != null && from.entryStage(entry) >= entry.sections().size());
	}
	
	public static void drawAspectTooltip(GuiGraphics guiGraphics, Aspect aspect, String descriptions, int mouseX, int mouseY, int screenWidth, int screenHeight) {
		String name = AspectUtils.getLocalizedAspectDisplayName(aspect);
		
		List<Component> text = new ArrayList<>();
		text.add(Component.literal(name));
		if (!descriptions.isEmpty())
			for (String description : descriptions.split("\n"))
				text.add(Component.literal(description).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
		
		drawAspectStyleTooltip(guiGraphics, text, mouseX, mouseY);
		
		if (shouldShowAspectIngredients() && Screen.hasShiftDown()) {
			PoseStack poseStack = guiGraphics.pose();
			poseStack.pushPose();
			poseStack.translate(0, 0, 500);
			RenderSystem.setShaderColor(1, 1, 1, 1);
//			poseStack.translate(0, 0, Minecraft.getInstance().getItemRenderer().zLevel);
			
			// copied from GuiUtils#drawHoveringText but without text wrapping
			int tooltipTextWidth =  Minecraft.getInstance().font.width(name);
			int tooltipX = mouseX + 12;
			if (tooltipX + tooltipTextWidth + 4 > screenWidth)
				tooltipX = mouseX - 16 - tooltipTextWidth;
			int tooltipY = mouseY - 12;
			if (tooltipY < 4)
				tooltipY = 4;
			else if (tooltipY + 12 > screenHeight)
				tooltipY = screenHeight - 12;
			
			int x = tooltipX - 4;
			int y = 10 + tooltipY + 5;
			Pair<Aspect, Aspect> combinationPairs = Aspects.COMBINATIONS.inverse().get(aspect);
			if (combinationPairs != null) {
				int color = 0xa0222222;
				// 2px padding horizontally, 2px padding vertically
				guiGraphics.fillGradient(x, y - 2, x + 40, y + 18, color, color);
				x += 2;
				renderAspect(guiGraphics, combinationPairs.getFirst(), x, y);
				x += 20;
				renderAspect(guiGraphics, combinationPairs.getSecond(), x, y);
			}
			poseStack.popPose();
		}
	}
	
	public static void drawAspectStyleTooltip(GuiGraphics guiGraphics, List<Component> text, int mouseX, int mouseY) {
		guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, text, mouseX, mouseY);
	}
	
	public static void renderIcon(GuiGraphics guiGraphics, Icon icon, int x, int y, int itemZLevel) {
		// first, check if its an item
		if (icon.getStack() != null && !icon.getStack().isEmpty()) {
			// this, uhh, doesn't work
			// ItemRenderer adds 50 automatically, so we adjust for it
//			Minecraft.getInstance().getItemRenderer().zLevel = itemZLevel - 50;
			guiGraphics.renderItem(icon.getStack(), x, y);
			guiGraphics.renderItemDecorations(Minecraft.getInstance().font, icon.getStack(), x, y);
		} else {
			// otherwise, check for a texture
			guiGraphics.blit(icon.getResourceLocation(), x, y, 0, 0, 16, 16, 16, 16);
		}
	}
	
	public static void renderVisCore(GuiGraphics guiGraphics, Core core, int x, int y) {
		guiGraphics.blit(core.getGuiTexture(), x, y, 0, 0, 49, 49, 49, 49);
	}
	
	public static void renderVisMeter(GuiGraphics guiGraphics, AspectHandler aspects, int x, int y) {
		int poolOffset = 2;
		int poolSpacing = 6;
		int poolFromEdge = 24;
		// "2": distance to first vis pool
		// "+= 6": distance between vis pools
		// "24": constant distance to vis pool
		Aspect[] vertical = {Aspects.AIR, Aspects.CHAOS, Aspects.EARTH};
		Aspect[] horizontal = {Aspects.FIRE, Aspects.ORDER, Aspects.WATER};
		int offset = poolOffset;
		for (Aspect aspect : vertical) {
			AspectHolder holder = aspects.findFirstHolderContaining(aspect);
			renderVisFill(guiGraphics, holder.getStack(), holder.getCapacity(), true, x + offset, y + poolFromEdge);
			offset += poolSpacing;
		}
		offset = poolOffset;
		for (Aspect aspect : horizontal) {
			AspectHolder holder = aspects.findFirstHolderContaining(aspect);
			renderVisFill(guiGraphics, holder.getStack(), holder.getCapacity(), false, x + poolFromEdge, y + offset);
			offset += poolSpacing;
		}
	}
	
	public static void renderVisFill(GuiGraphics guiGraphics, AspectStack aspStack, float visMax, boolean vertical, int x, int y) {
		int meterShort = 3;
		int meterLen = 16;
		int renderLen = (int)((aspStack.getAmount() * meterLen) / visMax);
		if (renderLen > 0) {
			if (vertical)
				guiGraphics.blit(aspStack.getAspect().getVisMeterTexture(), x, y, 0, 0, meterShort, renderLen, meterLen, meterLen);
			else
				guiGraphics.blit(aspStack.getAspect().getVisMeterTexture(), x, y, 0, 0, renderLen, meterShort, meterLen, meterLen);
		}
	}
	
	public static void renderVisDetailInfo(GuiGraphics guiGraphics, AspectHandler aspects) {
		int topMargin = 0;
		for (AspectHolder holder : aspects.getHolders()) {
			guiGraphics.drawString(Minecraft.getInstance().font,
					I18n.get("aspect." + holder.getStack().getAspect().name().toLowerCase()) + ": " + holder.getStack().getAmount(),
					60, topMargin, java.awt.Color.WHITE.getRGB());
			topMargin += 10;
		}
	}

	public static void drawMultilineString(GuiGraphics guiGraphics, Font font, String text, float x, float y, int color, boolean dropShadow) {
		String[] lines = text.split("\n");
		int lineHeight = font.lineHeight;
		for (int i = 0; i < lines.length; i++) {
			guiGraphics.drawString(font, lines[i], x, y + i * lineHeight, color, dropShadow);
		}
	}
}
