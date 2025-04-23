package com.wonginnovations.arcana.client.research.impls;

import com.wonginnovations.arcana.client.research.EntrySectionRenderer;
import com.wonginnovations.arcana.systems.research.impls.ImageSection;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

import static com.wonginnovations.arcana.client.gui.ResearchEntryScreen.*;

public class ImageSectionRenderer implements EntrySectionRenderer<ImageSection>{
	
	public void render(GuiGraphics guiGraphics, ImageSection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		guiGraphics.blit(section.getImage(), (right ? PAGE_X + RIGHT_X_OFFSET : PAGE_X) + (screenWidth - 256) / 2, PAGE_Y + (screenHeight - 181) / 2 + HEIGHT_OFFSET, 0, 0, PAGE_WIDTH, PAGE_HEIGHT);
	}
	
	public void renderAfter(GuiGraphics guiGraphics, ImageSection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player) {
		// no-op
		// maybe allow specifying tooltips in the future
	}
	
	public int span(ImageSection section, Player player) {
		return 1;
	}
}