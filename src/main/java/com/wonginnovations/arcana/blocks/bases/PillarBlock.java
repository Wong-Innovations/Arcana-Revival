package com.wonginnovations.arcana.blocks.bases;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class PillarBlock extends Block {
	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty DOWN = BooleanProperty.create("down");
	
	public PillarBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(UP, Boolean.FALSE).setValue(DOWN, Boolean.FALSE));
	}
	
	@Nonnull
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos blockpos = context.getClickedPos();
		Level level = context.getLevel();
		return super.defaultBlockState().setValue(UP, level.getBlockState(blockpos.above()).getBlock() instanceof PillarBlock).setValue(DOWN, level.getBlockState(blockpos.below()).getBlock() instanceof PillarBlock);
	}

//	@Override
//	public boolean isFlammable(BlockState state, LevelAccessor world, BlockPos pos, Direction face) {
//		return false;
//	}
	
	@SuppressWarnings("deprecation")
	@Override
	public @NotNull BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		updatePillars(level, currentPos);
		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}
	
	public void updatePillars(LevelAccessor level, BlockPos pos) {
		if (level.getBlockState(pos.below()).getBlock() instanceof PillarBlock)
			level.setBlock(pos.below(), level.getBlockState(pos.below()).setValue(UP, level.getBlockState(pos).getBlock() instanceof PillarBlock).setValue(DOWN, level.getBlockState(pos.below().below()).getBlock() instanceof PillarBlock), 3);
		if (level.getBlockState(pos.above()).getBlock() instanceof PillarBlock)
			level.setBlock(pos.above(), level.getBlockState(pos.above()).setValue(UP, level.getBlockState(pos.above().above()).getBlock() instanceof PillarBlock).setValue(DOWN, level.getBlockState(pos).getBlock() instanceof PillarBlock), 3);
	}
	
	@Override
	public void wasExploded(Level pLevel, BlockPos pPos, Explosion pExplosion) {
		updatePillars(pLevel, pPos);
		super.wasExploded(pLevel, pPos, pExplosion);
	}
	
	@Override
	public void destroy(LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
		updatePillars(pLevel, pPos);
		super.destroy(pLevel, pPos, pState);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(UP, DOWN);
	}
}
