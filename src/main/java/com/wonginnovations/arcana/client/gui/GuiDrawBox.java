package com.wonginnovations.arcana.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiDrawBox {
	public static void drawContinuousTexturedBox(GuiGraphics guiGraphics, ResourceLocation image, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight,
												 int topBorder, int bottomBorder, int leftBorder, int rightBorder)
	{
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

		int fillerWidth = textureWidth - leftBorder - rightBorder;
		int fillerHeight = textureHeight - topBorder - bottomBorder;
		int canvasWidth = width - leftBorder - rightBorder;
		int canvasHeight = height - topBorder - bottomBorder;
		int xPasses = canvasWidth / fillerWidth;
		int remainderWidth = canvasWidth % fillerWidth;
		int yPasses = canvasHeight / fillerHeight;
		int remainderHeight = canvasHeight % fillerHeight;

		// Draw Border
		// Top Left
		guiGraphics.blit(image, x, y, u, v, leftBorder, topBorder);
		// Top Right
		guiGraphics.blit(image, x + leftBorder + canvasWidth, y, u + leftBorder + fillerWidth, v, rightBorder, topBorder);
		// Bottom Left
		guiGraphics.blit(image, x, y + topBorder + canvasHeight, u, v + topBorder + fillerHeight, leftBorder, bottomBorder);
		// Bottom Right
		guiGraphics.blit(image, x + leftBorder + canvasWidth, y + topBorder + canvasHeight, u + leftBorder + fillerWidth, v + topBorder + fillerHeight, rightBorder, bottomBorder);

		for (int i = 0; i < xPasses + (remainderWidth > 0 ? 1 : 0); i++)
		{
			// Top Border
			guiGraphics.blit(image, x + leftBorder + (i * fillerWidth), y, u + leftBorder, v, (i == xPasses ? remainderWidth : fillerWidth), topBorder);
			// Bottom Border
			guiGraphics.blit(image, x + leftBorder + (i * fillerWidth), y + topBorder + canvasHeight, u + leftBorder, v + topBorder + fillerHeight, (i == xPasses ? remainderWidth : fillerWidth), bottomBorder);

			// Throw in some filler for good measure // TODO: Fix lags later
			//for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
			//	guiGraphics.blit(image, x + leftBorder + (i * fillerWidth), y + topBorder + (j * fillerHeight), u + leftBorder, v + topBorder, (i == xPasses ? remainderWidth : fillerWidth), (j == yPasses ? remainderHeight : fillerHeight));
		}

		// Side Borders
		for (int j = 0; j < yPasses + (remainderHeight > 0 ? 1 : 0); j++)
		{
			// Left Border
			guiGraphics.blit(image, x, y + topBorder + (j * fillerHeight), u, v + topBorder, leftBorder, (j == yPasses ? remainderHeight : fillerHeight));
			// Right Border
			guiGraphics.blit(image, x + leftBorder + canvasWidth, y + topBorder + (j * fillerHeight), u + leftBorder + fillerWidth, v + topBorder, rightBorder, (j == yPasses ? remainderHeight : fillerHeight));
		}
	}
}
