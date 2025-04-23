package com.wonginnovations.arcana.event;

import com.wonginnovations.arcana.Arcana;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.wonginnovations.arcana.Arcana.arcLoc;

/**
 * Event Handler for ModelBakeEvent
 *
 * @author Merijn
 */
@Mod.EventBusSubscriber(modid = Arcana.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModelRegistrar {
	
	@SubscribeEvent
	public static void registerModels(ModelEvent.RegisterAdditional event) {
		// TODO: variants
		event.register(arcLoc("item/wands/caps/wand"));
		event.register(arcLoc("item/wands/variants/wand"));

		event.register(arcLoc("item/phial"));
		event.register(arcLoc("item/arcanum"));
		event.register(arcLoc("item/cheaters_arcanum"));
		event.register(arcLoc("item/illustrious_grimoire"));
		event.register(arcLoc("item/tainted_codex"));
		event.register(arcLoc("item/crimson_rites"));
	}

}