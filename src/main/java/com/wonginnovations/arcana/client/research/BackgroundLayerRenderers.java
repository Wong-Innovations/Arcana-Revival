package com.wonginnovations.arcana.client.research;

import com.wonginnovations.arcana.systems.research.BackgroundLayer;
import com.wonginnovations.arcana.systems.research.impls.ImageLayer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

import static com.wonginnovations.arcana.client.gui.ResearchBookScreen.MAX_PAN;

public class BackgroundLayerRenderers {
	
	public static final Map<ResourceLocation, BackgroundLayerRenderer<?>> RENDERERS = new HashMap<>();
	
	public static void init() {
		RENDERERS.put(ImageLayer.TYPE, (ImageLayer layer, GuiGraphics guiGraphics, int x, int y, int width, int height, float xPan, float yPan, float parallax, float xOff, float yOff, float zoom) -> {
			float parallax1 = parallax / layer.speed();
			if (layer.vanishZoom() == -1 || layer.vanishZoom() > zoom)
				guiGraphics.blit(layer.image, x, y, (-xPan + MAX_PAN) / parallax1 + xOff, (yPan + MAX_PAN) / parallax1 + yOff, width, height, 512, 512);
		});
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void render(BackgroundLayer layer, GuiGraphics guiGraphics, int x, int y, int width, int height, float xPan, float yPan, float parallax, float xOff, float yOff, float zoom) {
		BackgroundLayerRenderer renderer = RENDERERS.get(layer.type());
		renderer.render(layer, guiGraphics, x, y, width, height, xPan, yPan, parallax, xOff, yOff, zoom);
	}
	
	@FunctionalInterface
	public interface BackgroundLayerRenderer<T extends BackgroundLayer> {
		
		void render(T layer, GuiGraphics guiGraphics, int x, int y, int width, int height, float xPan, float yPan, float parallax, float xOff, float yOff, float zoom);
	}
}