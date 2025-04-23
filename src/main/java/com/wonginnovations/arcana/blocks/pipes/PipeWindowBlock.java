package com.wonginnovations.arcana.blocks.pipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PipeWindowBlock extends TubeBlock {
	
	public PipeWindowBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PipeWindowBlockEntity(pos, state);
	}
	
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState block, Level level, BlockPos pos) {
		BlockEntity te = level.getBlockEntity(pos);
		if (te instanceof PipeWindowBlockEntity window) {
            int elapsed = (int)(te.getLevel().getGameTime() - window.getLastTransferTime());
			return elapsed > 12 ? 0 : 15;
		}
		return 0;
	}
}