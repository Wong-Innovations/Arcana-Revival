package com.wonginnovations.arcana.blocks.bases;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

@SuppressWarnings("deprecation")
public class HorizontalWaterloggableBlock extends WaterloggableBlock {
	
	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public HorizontalWaterloggableBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, Boolean.FALSE).setValue(HORIZONTAL_FACING, Direction.NORTH));
	}
	
	@Override
	public @NotNull BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(HORIZONTAL_FACING, context.getHorizontalDirection());
	}

	@Override
	protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(HORIZONTAL_FACING);
	}
	
	@Nonnull
	public BlockState rotate(@Nonnull BlockState state, @Nonnull Rotation rot) {
		return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
	}
	
	@Nonnull
	public BlockState mirror(@Nonnull BlockState state, @Nonnull Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(HORIZONTAL_FACING)));
	}
}
