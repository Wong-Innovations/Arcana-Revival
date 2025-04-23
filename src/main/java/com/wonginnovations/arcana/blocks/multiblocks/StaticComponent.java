package com.wonginnovations.arcana.blocks.multiblocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

/* You may be wondering why Cores and Components don't share inheritance.
 * Well, even though they represent the same block, Cores resemble Cores
 * and Components resemble Components. To exercise this further, it would
 * be wise to create base classes meant for Cores and Components.
 * I didn't do this because I feared replacing the Waterloggable Block
 * inheritance in Research Table. It was completely arbitrary, trust me.
 */
public interface StaticComponent {

    boolean isCore(BlockPos pos, BlockState state);

    BlockPos getCorePos(BlockPos pos, BlockState state);
}
