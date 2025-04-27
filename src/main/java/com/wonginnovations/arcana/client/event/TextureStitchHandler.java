package com.wonginnovations.arcana.client.event;

import com.wonginnovations.arcana.ArcanaVariables;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.client.render.entities.AspectBookshelfBlockEntityRenderer;
import com.wonginnovations.arcana.client.render.entities.AspectValveBlockEntityRenderer;
import com.wonginnovations.arcana.client.render.entities.JarBlockEntityRender;
import com.wonginnovations.arcana.items.attachment.Cap;
import com.wonginnovations.arcana.items.attachment.Core;
import com.wonginnovations.arcana.items.attachment.Focus;
import com.wonginnovations.arcana.world.NodeType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class TextureStitchHandler {

//	public static void beforeTextureStitch(RegisterSpriteSourceEvent event) {
//		event.addSpriteSource((atlas, registry) -> {
//			if (atlas.equals(TextureAtlas.LOCATION_PARTICLES)) {
//				registry.add(new ResourceLocation("yourmod", "particle/your_node"));
//			}
//		});
//	}
	
	@SuppressWarnings("deprecation")
	public static void onTextureStitch(@Nonnull TextureStitchEvent.Post event) {
		if (event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
//			event.getAtlas().getSprite(JarBlockEntityRender.JAR_CONTENT_SIDE);
//			event.getAtlas().getSprite(JarBlockEntityRender.JAR_CONTENT_TOP);
//			event.getAtlas().getSprite(JarBlockEntityRender.JAR_CONTENT_BOTTOM);
//			event.getAtlas().getSprite(JarBlockEntityRender.JAR_LABEL);
//			event.getAtlas().getSprite(AspectBookshelfBlockEntityRenderer.PHIAL_LID);
//			event.getAtlas().getSprite(AspectBookshelfBlockEntityRenderer.PHIAL_BODY);
//			event.getAtlas().getSprite(AspectBookshelfBlockEntityRenderer.PHIAL_BASE);
//			event.getAtlas().getSprite(AspectBookshelfBlockEntityRenderer.PHIAL_TOP);
//			event.getAtlas().getSprite(AspectBookshelfBlockEntityRenderer.PHIAL_SIDE);
//			event.getAtlas().getSprite(AspectBookshelfBlockEntityRenderer.PHIAL_BOTTOM);
//			event.getAtlas().getSprite(AspectBookshelfBlockEntityRenderer.PHIAL_CAP);
			event.getAtlas().getSprite(ArcanaVariables.arcLoc("font/number_0"));
			event.getAtlas().getSprite(ArcanaVariables.arcLoc("font/number_1"));
			event.getAtlas().getSprite(ArcanaVariables.arcLoc("font/number_2"));
			event.getAtlas().getSprite(ArcanaVariables.arcLoc("font/number_3"));
			event.getAtlas().getSprite(ArcanaVariables.arcLoc("font/number_4"));
			event.getAtlas().getSprite(ArcanaVariables.arcLoc("font/number_5"));
			event.getAtlas().getSprite(ArcanaVariables.arcLoc("font/number_6"));
			event.getAtlas().getSprite(ArcanaVariables.arcLoc("font/number_7"));
			event.getAtlas().getSprite(ArcanaVariables.arcLoc("font/number_8"));
			event.getAtlas().getSprite(ArcanaVariables.arcLoc("font/number_9"));
			event.getAtlas().getSprite(ArcanaVariables.arcLoc("models/items/thaumonomicon_model"));
			event.getAtlas().getSprite(AspectValveBlockEntityRenderer.GEAR_TEX);

//			for (Aspect aspect : Aspects.getAll()) {
//				event.getAtlas().getSprite(ArcanaVariables.arcLoc("aspect/paper/paper_"+aspect.name()));
//			}
			
			// add all of the wand related textures
			for (Cap cap : Cap.CAPS.values())
				event.getAtlas().getSprite(cap.getTextureLocation());
			for (Core core : Core.CORES.values())
				event.getAtlas().getSprite(core.getTextureLocation());
			for (Focus focus : Focus.FOCI)
				for (ResourceLocation location : focus.getAllModelLocations())
					event.getAtlas().getSprite(new ResourceLocation(location.getNamespace(), "models/wands/foci/" + location.getPath()));

		} else if (event.getAtlas().location().equals(TextureAtlas.LOCATION_PARTICLES)) {
			for (NodeType value : NodeType.TYPES.values()) {
				for (ResourceLocation rl : value.textures()) {
					event.getAtlas().getSprite(rl);
				}
			}
		}
	}
}