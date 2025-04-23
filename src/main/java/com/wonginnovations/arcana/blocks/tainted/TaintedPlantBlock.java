package com.wonginnovations.arcana.blocks.tainted;

import com.wonginnovations.arcana.mixin.BushBlockAccessor;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.TaintedBlock;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TaintedPlantBlock extends TaintedBlock implements IPlantable, BonemealableBlock, IForgeShearable {
	
	public TaintedPlantBlock(Block block) {
		super(block);
	}
	
	// Growable methods
	
	@Nullable
	private BonemealableBlock getGrowable() {
		return parentBlock instanceof BonemealableBlock ? (BonemealableBlock) parentBlock : null;
	}
	
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
		return getGrowable() != null && getGrowable().isValidBonemealTarget(level, pos, state, isClient);
	}
	
	public boolean isBonemealSuccess(Level level, RandomSource rand, BlockPos pos, BlockState state) {
		return getGrowable() != null && getGrowable().isBonemealSuccess(level, rand, pos, state);
	}
	
	public void performBonemeal(ServerLevel level, RandomSource rand, BlockPos pos, BlockState state) {
		if (getGrowable() != null)
			getGrowable().performBonemeal(level, rand, pos, state);
	}
	
	// Plantable methods
	
	@Nullable
	private IPlantable getPlantable() {
		return parentBlock instanceof IPlantable ? (IPlantable)parentBlock : null;
	}
	
	public PlantType getPlantType(BlockGetter level, BlockPos pos) {
		return getPlantable() != null ? getPlantable().getPlantType(level, pos) : PlantType.PLAINS;
	}
	
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
	
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockState bushBaseState = level.getBlockState(pos.below());
		return super.canSurvive(state, level, pos)
				|| (parentBlock instanceof BushBlock && Taint.getPureOfBlock(bushBaseState.getBlock()) != null && ((BushBlockAccessor) parentBlock).invokeMayPlaceOn(switchBlock(bushBaseState, Taint.getPureOfBlock(bushBaseState.getBlock())), level, pos))
				|| (parentBlock instanceof BushBlock && Taint.getLivingOfBlock(bushBaseState.getBlock()) != null && ((BushBlockAccessor) parentBlock).invokeMayPlaceOn(switchBlock(bushBaseState, Taint.getLivingOfBlock(bushBaseState.getBlock())), level, pos));
	}
}