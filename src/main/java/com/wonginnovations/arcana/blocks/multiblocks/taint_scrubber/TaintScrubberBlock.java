package com.wonginnovations.arcana.blocks.multiblocks.taint_scrubber;

import com.wonginnovations.arcana.aspects.VisShareable;
import com.wonginnovations.arcana.aspects.handlers.AspectBattery;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.blocks.entities.TaintScrubberBlockEntity;
import com.wonginnovations.arcana.systems.taint.Taint;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Random;

import static com.wonginnovations.arcana.blocks.DelegatingBlock.switchBlock;

public class TaintScrubberBlock extends BaseEntityBlock implements ITaintScrubberExtension {
	
	public static final BooleanProperty SUPPORTED = BooleanProperty.create("supported"); // false by default
	public static Random RANDOM = new Random();
	
	public TaintScrubberBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
		return RenderShape.MODEL;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new TaintScrubberBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
		return (lvl, pos, st, blockEntity) -> {
			if (blockEntity instanceof TaintScrubberBlockEntity) {
				((TaintScrubberBlockEntity) blockEntity).tick(lvl, pos, st);
			}
		};
	}
	
	public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		return state != null ? state.setValue(SUPPORTED, false) : null;
	}
	
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SUPPORTED);
	}
	
	@Override
	public boolean isValidConnection(Level level, BlockPos pos) {
		return true;
	}
	
	@Override
	public void sendUpdate(Level level, BlockPos pos) {}
	
	@Override
	public void run(Level level, BlockPos pos, CompoundTag compound) {
		// TODO: don't use NBT for this, just have methods for getting range and speed from extensions directly
		// pick the highest range, and add speeds.
		int rh = compound.getInt("h_range");
		int rv = compound.getInt("v_range");
		for (int i = 0; i < compound.getInt("speed") + (rv / 32) + 1; i++) {
			// Pick a block within a rh x rh x rv area.
			// If this block is air, stop. If this block doesn't have a tainted form, re-roll.
			// Do this up to 8 times.
			Block dead = null;
			BlockPos taintingPos = pos;
			int iter = 0;
			while(dead == null && iter < 8) {
				// TODO: don't try to pick blocks below or above the height limit, for the Bore's sake.
				// TODO: separate up/down ranges would also be useful, also for the bore, so its not terrible on the surface.
				taintingPos = pos.north(RANDOM.nextInt(rh + 1) - (rh / 2)).west(RANDOM.nextInt(rh + 1) - (rh / 2)).above(RANDOM.nextInt(rv + 1) - (rv / 2));
				BlockState deadstate = level.getBlockState(taintingPos);
				dead = deadstate.getBlock();
				if (deadstate.isAir()) {
					dead = null;
					break;
				}
				// Drain open/unsealed jars
				// TODO: replace with essentia input.
				if (deadstate.hasBlockEntity()) {
					BlockEntity te = level.getBlockEntity(taintingPos);
					if (te instanceof VisShareable shareable) {
                        if (shareable.isVisShareable() && !shareable.isSecure()) {
							AspectBattery vis = (AspectBattery)AspectHandler.getFrom(te);
							if (vis != null) {
								if (vis.countHolders() != 0)
									vis.getHolder(RANDOM.nextInt(vis.countHolders())).drain(8, false);
							}
						}
						break;
					}
				}
				dead = Taint.getDeadOfBlock(Taint.getPureOfBlock(dead));
				// todo: what the heck?
				if (compound.getBoolean("silk_touch"))
					dead = Taint.getPureOfBlock(dead);
				iter++;
			}
			// Replace it with its dead form if found.
			if (dead != null && !level.isClientSide()) {
				BlockState deadState = switchBlock(level.getBlockState(taintingPos), dead);
				level.setBlockAndUpdate(taintingPos, deadState);
				if (deadState.isAir()) {
					int rnd = RANDOM.nextInt(9) + 4;
					for (int j = 0; j < rnd; j++) {
						level.addParticle(
								new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.BLACK_CONCRETE_POWDER.defaultBlockState()),
								taintingPos.getX() + 0.5f + ((RANDOM.nextInt(9) - 4) / 10f), taintingPos.getY() + 0.5f + ((RANDOM.nextInt(9) - 4) / 10f), taintingPos.getZ() + 0.5f + ((RANDOM.nextInt(9) - 4) / 10f),
								0.1f, 0.1f, 0.1f
						); // Ash Particle if block is destroyed
					}
				}
			}
		}
	}
	
	@Override
	public CompoundTag getShareableData(CompoundTag compound) {
		return compound;
	}
}