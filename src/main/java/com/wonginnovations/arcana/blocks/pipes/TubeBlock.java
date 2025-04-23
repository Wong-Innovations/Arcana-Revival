package com.wonginnovations.arcana.blocks.pipes;

import com.wonginnovations.arcana.blocks.bases.SixWayBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.handlers.AspectHandlerCapability;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TubeBlock extends SixWayBlock {
	
	public TubeBlock(Properties properties) {
		super(.1875f, properties);
		registerDefaultState(defaultBlockState()
				.setValue(DOWN, Boolean.FALSE)
				.setValue(UP, Boolean.FALSE)
				.setValue(NORTH, Boolean.FALSE)
				.setValue(EAST, Boolean.FALSE)
				.setValue(SOUTH, Boolean.FALSE)
				.setValue(WEST, Boolean.FALSE));
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
		return RenderShape.MODEL;
	}
	
	private boolean isVisHolder(LevelAccessor level, BlockPos pos) {
		Block block = level.getBlockState(pos).getBlock();
		BlockEntity tile = level.getBlockEntity(pos);
		return (tile != null && tile.getCapability(AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY).isPresent()) || block instanceof TubeBlock;
	}
	
	// Blockstate stuff

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.makeConnections(context.getLevel(), context.getClickedPos());
	}
	
	public BlockState makeConnections(Level level, BlockPos pos) {
		return this.defaultBlockState()
				.setValue(DOWN, isVisHolder(level, pos.below()))
				.setValue(UP, isVisHolder(level, pos.above()))
				.setValue(NORTH, isVisHolder(level, pos.north()))
				.setValue(EAST, isVisHolder(level, pos.east()))
				.setValue(SOUTH, isVisHolder(level, pos.south()))
				.setValue(WEST, isVisHolder(level, pos.west()));
	}
	
	/**
	 * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
	 * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
	 * returns its solidified counterpart.
	 * Note that this method should ideally consider only the specific face passed in.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		boolean flag = isVisHolder(level, facingPos);
		return state.setValue(PROPERTY_BY_DIRECTION.get(facing), flag);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
		return (lvl, pos, st, blockEntity) -> {
			if (blockEntity instanceof TubeBlockEntity) {
				((TubeBlockEntity) blockEntity).tick(lvl, pos, st);
			}
		};
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

//	@Override
//	public boolean allowsMovement(BlockState state, Level levelIn, BlockPos pos, PathComputationType type) {
//		return false;
//	}

//	@Override
//	public boolean hasBlockEntity() {
//		return true;
//	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TubeBlockEntity(pos, state);
	}

}