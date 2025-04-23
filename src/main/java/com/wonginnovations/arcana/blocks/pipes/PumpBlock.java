package com.wonginnovations.arcana.blocks.pipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PumpBlock extends TubeBlock{
	
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	
	public PumpBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(NORTH, Boolean.FALSE)
				.setValue(EAST, Boolean.FALSE)
				.setValue(SOUTH, Boolean.FALSE)
				.setValue(WEST, Boolean.FALSE)
				.setValue(UP, Boolean.FALSE)
				.setValue(DOWN, Boolean.FALSE)
				.setValue(FACING, Direction.UP));
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PumpBlockEntity(pos, state, state.getValue(FACING));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context)
				.setValue(FACING, context.getClickedFace());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, FACING);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
		if (level.isClientSide)
			return InteractionResult.SUCCESS;
		BlockEntity te = level.getBlockEntity(pos);
		if (te instanceof PumpBlockEntity pbe) {
			NetworkHooks.openScreen((ServerPlayer) player, pbe, pos);
			return InteractionResult.SUCCESS;
		}
		return super.use(state, level, pos, player, hand, rayTraceResult);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
		if (!level.isClientSide()) {
			BlockEntity te = level.getBlockEntity(pos);
			if (te instanceof PumpBlockEntity alembic) {
                alembic.suppressedByRedstone = level.hasNeighborSignal(pos);
				alembic.setChanged();
				level.sendBlockUpdated(pos, state, state, UPDATE_ALL);
			}
		}
	}
}