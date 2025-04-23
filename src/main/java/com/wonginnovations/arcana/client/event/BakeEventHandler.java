package com.wonginnovations.arcana.client.event;

import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.client.model.baked.WardenedBlockBakedModel;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class BakeEventHandler {

	private static Logger LOGGER = LogManager.getLogger();

	@SubscribeEvent
	public static void onModelBake(ModelEvent.ModifyBakingResult event) {
		// Find the existing mappings for CamouflageBakedModel - they will have been added automatically because
		// of our blockstates file for the BlockCamouflage.
		// Replace the mapping with our CamouflageBakedModel.
		// we only have one BlockState variant but I've shown code that loops through all of them, in case you have more than one.
		for (BlockState blockState : ArcanaBlocks.WARDENED_BLOCK.get().getStateDefinition().getPossibleStates()) {
			BakedModel existingModel = event.getModels().get(BlockModelShaper.stateToModelLocation(blockState));
			if (existingModel == null) {
				LOGGER.warn("Did not find the expected vanilla baked model(s) for blockCamouflage in registry");
			} else if (existingModel instanceof WardenedBlockBakedModel) {
				LOGGER.warn("Tried to replace CamouflagedBakedModel twice");
			} else {
				WardenedBlockBakedModel customModel = new WardenedBlockBakedModel(existingModel);
				event.getModels().put(BlockModelShaper.stateToModelLocation(blockState), customModel);
			}
		}
	}
}