package com.wonginnovations.arcana.client.research.impls;

import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.client.gui.ClientUiUtil;
import com.wonginnovations.arcana.client.gui.ResearchEntryScreen;
import com.wonginnovations.arcana.client.research.EntrySectionRenderer;
import com.wonginnovations.arcana.systems.research.ResearchBook;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import com.wonginnovations.arcana.systems.research.impls.AspectCombosSection;
import com.wonginnovations.arcana.util.Pair;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static com.wonginnovations.arcana.client.gui.ResearchEntryScreen.*;

public class AspectCombosSectionRenderer implements EntrySectionRenderer<AspectCombosSection>{
	
	protected ResourceLocation textures = null;
	
	public void render(GuiGraphics guiGraphics, AspectCombosSection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		ResearchBook book = ResearchBooks.getEntry(section.getEntry()).category().book();
		// don't make a new rloc every frame
		if (textures == null || !textures.getNamespace().equals(book.getKey().getNamespace()))
			textures = new ResourceLocation(book.getKey().getNamespace(), "textures/gui/research/" + book.getPrefix() + ResearchEntryScreen.OVERLAY_SUFFIX);
		
		int x = (right ? PAGE_X + RIGHT_X_OFFSET : PAGE_X) + (screenWidth - 256) / 2 + 4;
		int y = PAGE_Y + (screenHeight - 181) / 2 + HEIGHT_OFFSET + 10;
		
		List<Pair<Aspect, Aspect>> list = Aspects.COMBOS_AS_LIST;
		for (int i = pageIndex * 5, size = list.size(); i < size && i < (pageIndex + 1) * 5; i++) {
			Pair<Aspect, Aspect> pair = list.get(i);
			int dispIndex = i - pageIndex * 5;
			ClientUiUtil.renderAspect(guiGraphics, pair.getFirst(), x, y + 30 * dispIndex);
			ClientUiUtil.renderAspect(guiGraphics, pair.getSecond(), x + 40, y + 30 * dispIndex);
			ClientUiUtil.renderAspect(guiGraphics, Aspects.COMBINATIONS.get(pair), x + 80, y + 30 * dispIndex);
			guiGraphics.blit(textures, x + 20, y + 30 * dispIndex, 105, 161, 12, 13);
			guiGraphics.blit(textures, x + 60, y + 30 * dispIndex, 118, 161, 12, 13);
		}
	}
	
	public void renderAfter(GuiGraphics guiGraphics, AspectCombosSection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		int x = (right ? PAGE_X + RIGHT_X_OFFSET : PAGE_X) + (screenWidth - 256) / 2 + 4;
		int y = PAGE_Y + (screenHeight - 181) / 2 + HEIGHT_OFFSET + 10;
		
		List<Pair<Aspect, Aspect>> list = Aspects.COMBOS_AS_LIST;
		for (int i = pageIndex * 5, size = list.size(); i < size && i < (pageIndex + 1) * 5; i++) {
			Pair<Aspect, Aspect> pair = list.get(i);
			int dispIndex = i - pageIndex * 5;
			if (mouseX >= x && mouseX < x + 16 && mouseY >= y + 30 * dispIndex && mouseY < y + 30 * dispIndex + 16) {
				ClientUiUtil.drawAspectTooltip(guiGraphics, pair.getFirst(), "", mouseX, mouseY, screenWidth, screenHeight);
				break;
			}
			if (mouseX >= x + 40 && mouseX < x + 40 + 16 && mouseY >= y + 30 * dispIndex && mouseY < y + 30 * dispIndex + 16) {
				ClientUiUtil.drawAspectTooltip(guiGraphics, pair.getSecond(), "", mouseX, mouseY, screenWidth, screenHeight);
				break;
			}
			if (mouseX >= x + 80 && mouseX < x + 80 + 16 && mouseY >= y + 30 * dispIndex && mouseY < y + 30 * dispIndex + 16) {
				ClientUiUtil.drawAspectTooltip(guiGraphics, Aspects.COMBINATIONS.get(pair), "", mouseX, mouseY, screenWidth, screenHeight);
				break;
			}
		}
	}
	
	public int span(AspectCombosSection section, Player player) {
		// how many aspects fit on a page? lets say 3 for now
		return (int)Math.ceil(Aspects.COMBINATIONS.size() / 5f);
	}
}