package com.wonginnovations.arcana.blocks;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.entities.AspectCrystallizerBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectCrystallizerBlock extends BaseEntityBlock {
	
	public AspectCrystallizerBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AspectCrystallizerBlockEntity(pos, state);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
		if (level.isClientSide)
			return InteractionResult.SUCCESS;
		BlockEntity te = level.getBlockEntity(pos);
		if (te instanceof AspectCrystallizerBlockEntity acbe) {
			NetworkHooks.openScreen((ServerPlayer)player, acbe, buf -> buf.writeBlockPos(pos));
			return InteractionResult.SUCCESS;
		}
		return super.use(state, level, pos, player, hand, rayTraceResult);
	}
	
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity tileentity = level.getBlockEntity(pos);
			if (tileentity instanceof Container container) {
				Containers.dropContents(level, pos, container);
				level.updateNeighbourForOutputSignal(pos, this);
			}
			
			super.onRemove(state, level, pos, newState, moving);
		}
	}
}