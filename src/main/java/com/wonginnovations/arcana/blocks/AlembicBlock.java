package com.wonginnovations.arcana.blocks;

import com.wonginnovations.arcana.blocks.entities.ArcanaBlockEntities;
import com.wonginnovations.arcana.blocks.entities.CrucibleBlockEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.aspects.handlers.AspectHolder;
import com.wonginnovations.arcana.blocks.entities.AlembicBlockEntity;
import com.wonginnovations.arcana.world.AuraView;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AlembicBlock extends BaseEntityBlock {
	
	protected static final VoxelShape SHAPE = Shapes.or(
			box(1, 1, 1, 15, 15, 15),
			box(0, 2, 0, 16, 4, 16),
			box(0, 12, 0, 16, 14, 16),
			box(4, 0, 4, 12, 2, 12),
			box(4, 14, 4, 12, 16, 12)
	).optimize();
	
	public AlembicBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AlembicBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return createTickerHelper(pBlockEntityType, ArcanaBlockEntities.ALEMBIC.get(), AlembicBlockEntity::tick);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
		if (!level.isClientSide) {
			BlockEntity te = level.getBlockEntity(pos);
			if (te instanceof AlembicBlockEntity alembic) {
				alembic.suppressedByRedstone = level.hasNeighborSignal(pos);
				alembic.setChanged();
				level.sendBlockUpdated(pos, state, state, UPDATE_ALL);
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity te = level.getBlockEntity(pos);
		if (te instanceof AlembicBlockEntity alembic)
			if (player.getItemInHand(hand).isEmpty() && player.isCrouching()) {
				// get rid of the content of the alembic
				for (AspectHolder holder : alembic.aspects.getHolders()) {
					AuraView.getSided(level).addFluxAt(pos, (float)(holder.getStack().getAmount() * ArcanaConfig.ASPECT_DUMPING_WASTE.get()));
					holder.drain(holder.getStack().getAmount(), false);
					// TODO: flux particles
				}
				alembic.setChanged();
				level.sendBlockUpdated(pos, state, state, UPDATE_ALL);
			} else {
				if (level.isClientSide())
					return InteractionResult.SUCCESS;
				NetworkHooks.openScreen(((ServerPlayer) player), alembic, pos);
				return InteractionResult.SUCCESS;
			}
		return super.use(state, level, pos, player, hand, hit);
	}
}