package com.wonginnovations.arcana.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.handlers.AspectHolder;
import com.wonginnovations.arcana.containers.AlembicMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.wonginnovations.arcana.Arcana.arcLoc;
import static com.wonginnovations.arcana.client.gui.UiUtil.*;

@ParametersAreNonnullByDefault
public class AlembicScreen extends AbstractContainerScreen<AlembicMenu> {
	
	private static final ResourceLocation BG = arcLoc("textures/gui/container/alembic.png");
	
	public AlembicScreen(AlembicMenu container, Inventory inv, Component title) {
		super(container, inv, title);
		imageWidth = 176;
		imageHeight = 222;
	}
	
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int x, int y) {
		renderBackground(guiGraphics);
		guiGraphics.blit(BG, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		
		// draw cell contents
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(leftPos, topPos, 0);
		List<AspectHolder> holders = menu.te.aspects.getHolders();
		for (int i = 0, size = holders.size(); i < size; i++) {
			AspectHolder holder = holders.get(i);
			AspectStack stack = holder.getStack();
			if (!stack.isEmpty()) {
				ClientUiUtil.renderAspect(guiGraphics, stack.getAspect(), 52 + 24 * i, 12);
				int color = stack.getAspect().getColorRange().get(3);
				int dispHeight = (int)(58 * (stack.getAmount() / holder.getCapacity()));
				RenderSystem.setShaderColor(red(color) / 255f, green(color) / 255f, blue(color) / 255f, 1);
				guiGraphics.blit(BG, 53 + 24 * i, 60 + (58 - dispHeight), 196, 58 - dispHeight, 14, dispHeight);
				RenderSystem.setShaderColor(1, 1, 1, 1);
			}
		}
		if (menu.te.burnTicks > 0) {
			int fuelHeight = (int)(52 * (menu.te.burnTicks / (float)menu.te.maxBurnTicks));
			guiGraphics.blit(BG, 12, 44 + (52 - fuelHeight), 176, 52 - fuelHeight, 20, fuelHeight);
		}
		guiGraphics.pose().popPose();
	}
	
	// don't label things
	protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {}
	
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		renderTooltip(guiGraphics, mouseX, mouseY);
		List<AspectHolder> holders = menu.te.aspects.getHolders();
		for (int i = 0, size = holders.size(); i < size; i++) {
			AspectHolder holder = holders.get(i);
			AspectStack stack = holder.getStack();
			if (!stack.isEmpty()) {
				int x = leftPos + 52 + 24 * i, y = topPos + 12;
				if ((mouseX >= x) && (mouseX < (x + 16)) && (mouseY >= y) && (mouseY < (y + 16))) {
					ClientUiUtil.drawAspectTooltip(guiGraphics, stack.getAspect(), "", mouseX, mouseY, width, height);
					break;
				}
			}
		}
	}
}