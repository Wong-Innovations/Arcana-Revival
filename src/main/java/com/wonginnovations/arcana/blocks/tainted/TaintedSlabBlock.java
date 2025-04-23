package com.wonginnovations.arcana.blocks.tainted;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.capabilities.TaintTrackable;
import com.wonginnovations.arcana.systems.taint.Taint;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SlabBlock;
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
public class TaintedSlabBlock extends SlabBlock {
	public static final BooleanProperty UNTAINTED = Taint.UNTAINTED;
	
	public TaintedSlabBlock(Block parent) {
		super(Block.Properties.copy(parent));
		Taint.addTaintMapping(parent, this);
	}
	
	public boolean ticksRandomly(BlockState state) {
		return true;
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(UNTAINTED);
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState placement = super.getStateForPlacement(context);
		return placement != null ? placement.setValue(UNTAINTED, true) : null;
	}
	
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.tick(state, level, pos, random);
		Taint.tickTaintedBlock(state, level, pos, random);
	}
	
	@SuppressWarnings("deprecation")
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		super.entityInside(state, level, pos, entity);
		startTracking(entity);
	}
	
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
