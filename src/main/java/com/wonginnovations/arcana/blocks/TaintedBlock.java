package com.wonginnovations.arcana.blocks;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.ArcanaSounds;
import com.wonginnovations.arcana.capabilities.TaintTrackable;
import com.wonginnovations.arcana.systems.taint.Taint;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.IPlantable;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.world.level.block.FarmBlock.MOISTURE;
import static net.minecraft.world.level.block.SnowyDirtBlock.SNOWY;
import static net.minecraftforge.common.ForgeHooks.onFarmlandTrample;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TaintedBlock extends DelegatingBlock {
	
	public static final BooleanProperty UNTAINTED = Taint.UNTAINTED;
	
	@Deprecated() // Use Taint#taintedOf instead
	public TaintedBlock(Block block) {
		super(block, ArcanaSounds.TAINT);
		Taint.addTaintMapping(block, this);
	}

	@Override
	public MutableComponent getName() {
		return Component.translatable("arcana.status.tainted", super.getName());
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(UNTAINTED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState placement = super.getStateForPlacement(context);
		return placement != null ? placement.setValue(UNTAINTED, true) : null;
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		// Tainted Farmland yet again
		boolean continueTick = true;
		if (parentBlock == Blocks.FARMLAND) {
			if (!state.canSurvive(level, pos)) {
				level.setBlockAndUpdate(pos, pushEntitiesUp(level.getBlockState(pos), ArcanaBlocks.TAINTED_SOIL.get().defaultBlockState().setValue(UNTAINTED, state.getValue(UNTAINTED)), level, pos));
				continueTick = false;
			} else if (!hasWater(level, pos) && !level.isRainingAt(pos.above()))
				if (state.getValue(MOISTURE) == 0)
					if (!hasCrops(level, pos)) {
						level.setBlockAndUpdate(pos, pushEntitiesUp(level.getBlockState(pos), ArcanaBlocks.TAINTED_SOIL.get().defaultBlockState().setValue(UNTAINTED, state.getValue(UNTAINTED)), level, pos));
						continueTick = false;
					}
		}
		// Tainted grass path decays into tainted soil
		if (parentBlock == Blocks.DIRT_PATH) {
			if (!state.canSurvive(level, pos))
				level.setBlockAndUpdate(pos, pushEntitiesUp(level.getBlockState(pos), ArcanaBlocks.TAINTED_SOIL.get().defaultBlockState().setValue(UNTAINTED, state.getValue(UNTAINTED)), level, pos));
			continueTick = false;
		}
		// And tainted grass decays into tainted soil, and spreads.
		// Should also cover mycelium.
		if (parentBlock instanceof SpreadingSnowyDirtBlock) {
			if (!isLocationUncovered(state, level, pos)) {
				if (!level.isAreaLoaded(pos, 3))
					return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
				level.setBlockAndUpdate(pos, ArcanaBlocks.TAINTED_SOIL.get().defaultBlockState().setValue(UNTAINTED, state.getValue(UNTAINTED)));
			} else if (level.getLightEmission(pos.above()) >= 9) {
				BlockState blockstate = defaultBlockState();
				for (int i = 0; i < 4; ++i) {
					BlockPos blockpos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
					if (level.getBlockState(blockpos).getBlock() == ArcanaBlocks.TAINTED_SOIL.get() && isLocationValidForGrass(blockstate, level, blockpos))
						level.setBlockAndUpdate(blockpos, blockstate.setValue(SNOWY, level.getBlockState(blockpos.above()).getBlock() == Blocks.SNOW).setValue(UNTAINTED, state.getValue(UNTAINTED)));
				}
			}
			continueTick = false;
		}
		if (continueTick)
			super.randomTick(state, level, pos, random);
		Taint.tickTaintedBlock(state, level, pos, random);
	}
	
	// Tainted Cactus and Sugar Cane
	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction facing, IPlantable plantable) {
		// BlockState plant = plantable.getPlant(world, pos.offset(facing));
		return super.canSustainPlant(state, world, pos, facing, plantable);
				/*|| ((parentBlock == Blocks.GRASS_BLOCK || parentBlock == Blocks.DIRT || parentBlock == Blocks.COARSE_DIRT || parentBlock == Blocks.PODZOL || parentBlock == Blocks.FARMLAND)
						&& plantable instanceof BushBlock)
				|| (parentBlock == Blocks.CACTUS
						&& (plant.getBlock() == Blocks.CACTUS *//*|| plant.getBlock() == ArcanaBlocks.TAINTED_CACTUS*//*))
				|| (parentBlock == Blocks.SUGAR_CANE
						&& (plant.getBlock() == Blocks.SUGAR_CANE *//*|| plant.getBlock() == ArcanaBlocks.TAINTED_SUGAR_CANE*//*));*/
	}
	
	// Make farmland turn to tainted soil
	@Override
	public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
		if (parentBlock == Blocks.FARMLAND) {
			// Forge: Move logic to Entity#canTrample
			if (!level.isClientSide && onFarmlandTrample(level, pos, Blocks.DIRT.defaultBlockState(), fallDistance, entity))
				level.setBlockAndUpdate(pos, pushEntitiesUp(level.getBlockState(pos), ArcanaBlocks.TAINTED_SOIL.get().defaultBlockState(), level, pos));
			entity.causeFallDamage(fallDistance, 1.0F, level.damageSources().fall());
		} else
			super.fallOn(level, state, pos, entity, fallDistance);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		super.stepOn(level, pos, state, entity);
		startTracking(entity);
	}

	private void startTracking(Entity entity) {
		if (entity instanceof LivingEntity) {
			// Start tracking taint biome for entity
			TaintTrackable trackable = TaintTrackable.getFrom((LivingEntity)entity);
			if (trackable != null)
				trackable.setTracking(true);
		}
	}
	
	// Private stuff in FarmlandBlock
	// TODO: AT this
	private boolean hasCrops(BlockGetter level, BlockPos pos) {
		BlockState state = level.getBlockState(pos.above());
		return state.getBlock() instanceof IPlantable && canSustainPlant(state, level, pos, Direction.UP, (IPlantable)state.getBlock());
	}
	
	private static boolean hasWater(LevelReader level, BlockPos pos) {
		for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4)))
			if (level.getFluidState(blockpos).getTags().anyMatch(FluidTags.WATER::equals))
				return true;
		return FarmlandWaterManager.hasBlockWaterTicket(level, pos);
	}
	
	// Private stuff in SpreadableSnowyDirtBlock
	private static boolean isLocationUncovered(BlockState state, BlockGetter level, BlockPos pos) {
		BlockPos blockpos = pos.above();
		BlockState blockstate = level.getBlockState(blockpos);
		if (blockstate.getBlock() == Blocks.SNOW && blockstate.getValue(SnowLayerBlock.LAYERS) == 1)
			return true;
		else {
			int i = LightEngine.getLightBlockInto(level, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(level, blockpos));
			return i < level.getMaxLightLevel();
		}
	}
	
	private static boolean isLocationValidForGrass(BlockState state, BlockGetter world, BlockPos pos) {
		BlockPos blockpos = pos.above();
		return isLocationUncovered(state, world, pos) && world.getFluidState(blockpos).getTags().noneMatch(FluidTags.WATER::equals);
	}
}