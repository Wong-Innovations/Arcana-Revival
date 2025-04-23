package com.wonginnovations.arcana.blocks.tainted;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.systems.taint.Taint;
import com.wonginnovations.arcana.capabilities.TaintTrackable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TaintedFallingBlock extends FallingBlock {
	
	public static final BooleanProperty UNTAINTED = Taint.UNTAINTED;
	
	public TaintedFallingBlock(Block parent) {
		super(Properties.copy(parent));
		Taint.addTaintMapping(parent, this);
	}
	
	// Thankfully, no falling block has block properties so we don't have to do delegation stuff.
	// If we want to make a tainted anvil, we need to reimplement stuff, sadly.

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

	@SuppressWarnings("deprecation")
	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.randomTick(state, level, pos, random);
		Taint.tickTaintedBlock(state, level, pos, random);
	}

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
}