package com.wonginnovations.arcana.blocks.multiblocks.taint_scrubber;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ITaintScrubberExtension {
	
	/**
	 * Returns true if an extension is in a valid position.
	 *
	 * @param level
	 * 		Level
	 * @param pos
	 * 		Position of extension
	 * @return If an extension is in a correct position.
	 */
	boolean isValidConnection(Level level, BlockPos pos);
	
	/**
	 * Called by a taint scrubber that this extension is connected to.
	 *
	 * @param level
	 * 		Level
	 * @param pos
	 * 		Position of extension
	 */
	void sendUpdate(Level level, BlockPos pos);
	
	/**
	 * Runs extension action.
	 *
	 * @param level
	 * 		Level
	 * @param pos
	 * 		Position of extension
	 */
	void run(Level level, BlockPos pos, CompoundTag compound);
	
	CompoundTag getShareableData(CompoundTag compound);
}