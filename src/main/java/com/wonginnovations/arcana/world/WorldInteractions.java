package com.wonginnovations.arcana.world;

import com.wonginnovations.arcana.util.Pair;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public final class WorldInteractions {

	private Level level;

	public static HashMap<Block, Pair<Block,Block>> freezable = new HashMap<>();

	private WorldInteractions(Level level) {
		this.level = level;
	}

	public static WorldInteractions fromWorld(Level level) {
		return new WorldInteractions(level);
	}

	public void freezeBlock(BlockPos position) {
		Block targetedBlock = level.getBlockState(position).getBlock();
		if (freezable.containsKey(targetedBlock)) {
			Pair<Block,Block> replace = freezable.get(targetedBlock);
			level.setBlockAndUpdate(position, replace.getFirst().defaultBlockState());
			if (replace.getSecond() != Blocks.AIR) {
				if (level.getBlockState(position.above()).isAir()) {
					level.setBlockAndUpdate(position.above(), replace.getSecond().defaultBlockState());
				}
			}
		}
	}
}
