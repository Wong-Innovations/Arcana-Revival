package com.wonginnovations.arcana.blocks;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.bases.WaterloggableEntityBlock;
import com.wonginnovations.arcana.blocks.entities.ArcaneCraftingTableBlockEntity;
import net.minecraft.world.*;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
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
public class ArcaneCraftingTableBlock extends WaterloggableEntityBlock {
	public ArcaneCraftingTableBlock(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ArcaneCraftingTableBlockEntity(pos, state);
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult rayTraceResult) {
		if (level.isClientSide)
			return InteractionResult.SUCCESS;
		BlockEntity te = level.getBlockEntity(pos);
		if (te instanceof ArcaneCraftingTableBlockEntity) {
			NetworkHooks.openScreen(((ServerPlayer) player), (ArcaneCraftingTableBlockEntity) te, pos);
			return InteractionResult.SUCCESS;
		}
		return super.use(state, level, pos, player, handIn, rayTraceResult);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity tileentity = level.getBlockEntity(pos);
			if (tileentity instanceof Container) {
				Containers.dropContents(level, pos, (Container) tileentity);
				level.updateNeighbourForOutputSignal(pos, this);
			}
			
			super.onRemove(state, level, pos, newState, moving);
		}
	}
}