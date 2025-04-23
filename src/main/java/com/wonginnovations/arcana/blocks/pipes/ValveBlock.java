package com.wonginnovations.arcana.blocks.pipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ValveBlock extends TubeBlock {

	public ValveBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ValveBlockEntity(pos, state);
	}

	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult raytrace) {
		BlockEntity te = level.getBlockEntity(pos);
		if (te instanceof ValveBlockEntity valve) {
			valve.setEnabledAndNotify(!valve.enabledByHand());
			return InteractionResult.SUCCESS;
		}
		return super.use(state, level, pos, player, hand, raytrace);
	}

	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
		if (!level.isClientSide()) {
			BlockEntity te = level.getBlockEntity(pos);
			if (te instanceof ValveBlockEntity valve) {
                valve.setSuppressedByRedstone(level.hasNeighborSignal(pos));
				valve.setChanged();
				level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, Level level, BlockPos pos, Random rand) {
		BlockEntity te = level.getBlockEntity(pos);
		if (te instanceof ValveBlockEntity && ((ValveBlockEntity)te).isSuppressedByRedstone() && rand.nextFloat() < 0.25F)
			addParticles(level, pos);
	}

	private static void addParticles(Level level, BlockPos pos) {
		double x = (double)pos.getX() + .5;
		double y = (double)pos.getY() + 1;
		double z = (double)pos.getZ() + .5;
		level.addParticle(DustParticleOptions.REDSTONE, x, y, z, 0.0D, 0.0D, 0.0D);
	}

	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction side) {
		return true;
	}
}