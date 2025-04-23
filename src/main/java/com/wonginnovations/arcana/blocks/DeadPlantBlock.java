package com.wonginnovations.arcana.blocks;

import com.wonginnovations.arcana.mixin.BushBlockAccessor;
import com.wonginnovations.arcana.systems.taint.Taint;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeadPlantBlock extends DeadBlock implements IPlantable, BonemealableBlock, IForgeShearable {
	public DeadPlantBlock(Block parent) {
		super(parent);
	}

	// Growable methods

	@Nullable
	private BonemealableBlock getGrowable() {
		return parentBlock instanceof BonemealableBlock ? (BonemealableBlock) parentBlock : null;
	}

	public boolean isValidBonemealTarget(@NotNull LevelReader level, @NotNull BlockPos pos, @NotNull BlockState state, boolean isClient) {
		return getGrowable() != null && getGrowable().isValidBonemealTarget(level, pos, state, isClient);
	}

	public boolean isBonemealSuccess(@NotNull Level level, @NotNull RandomSource rand, @NotNull BlockPos pos, @NotNull BlockState state) {
		return getGrowable() != null && getGrowable().isBonemealSuccess(level, rand, pos, state);
	}

	public void performBonemeal(@NotNull ServerLevel level, @NotNull RandomSource rand, @NotNull BlockPos pos, @NotNull BlockState state) {
		if (getGrowable() != null)
			getGrowable().performBonemeal(level, rand, pos, state);
	}

	// Plantable methods

	@Nullable
	private IPlantable getPlantable() {
		return parentBlock instanceof IPlantable ? (IPlantable) parentBlock : null;
	}

	public PlantType getPlantType(BlockGetter level, BlockPos pos) {
		return getPlantable() != null ? getPlantable().getPlantType(level, pos) : PlantType.PLAINS;
	}

	@Override
	public BlockState getPlant(BlockGetter level, BlockPos pos) {
		return getPlantable() != null ? switchBlock(getPlantable().getPlant(level, pos), this) : defaultBlockState();
	}

	// Shearable methods

	@Nullable
	private IForgeShearable getShearable() {
		return parentBlock instanceof IForgeShearable ? (IForgeShearable) parentBlock : null;
	}

	public boolean isShearable(@Nonnull ItemStack item, Level level, BlockPos pos) {
		return getShearable() != null && getShearable().isShearable(item, level, pos);
	}

	@SuppressWarnings("deprecation")
	public boolean canSurvive(@NotNull BlockState state, LevelReader level, BlockPos pos) {
		BlockState bushBaseState = level.getBlockState(pos.below());
		return super.canSurvive(state, level, pos)
				|| (parentBlock instanceof BushBlock && Taint.getPureOfBlock(bushBaseState.getBlock()) != null && ((BushBlockAccessor) parentBlock).invokeMayPlaceOn(switchBlock(bushBaseState, Taint.getPureOfBlock(bushBaseState.getBlock())), level, pos))
				|| (parentBlock instanceof BushBlock && Taint.getLivingOfBlock(bushBaseState.getBlock()) != null && ((BushBlockAccessor) parentBlock).invokeMayPlaceOn(switchBlock(bushBaseState, Taint.getLivingOfBlock(bushBaseState.getBlock())), level, pos));
	}
}
