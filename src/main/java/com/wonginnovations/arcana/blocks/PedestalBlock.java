package com.wonginnovations.arcana.blocks;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.bases.WaterloggableEntityBlock;
import com.wonginnovations.arcana.blocks.entities.PedestalBlockEntity;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PedestalBlock extends WaterloggableEntityBlock {

	protected static final VoxelShape SHAPE = Shapes.or(box(1, 0, 1, 15, 4, 15), box(6, 0, 6, 10, 16, 10), box(3, 12, 3, 13, 16, 13)).optimize();

	public PedestalBlock(Properties properties) {
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
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PedestalBlockEntity(pos, state);
	}

	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTrace) {
		ItemStack itemstack = player.getItemInHand(hand);
		PedestalBlockEntity te = (PedestalBlockEntity) level.getBlockEntity(pos);

		if (te.getItem() == ItemStack.EMPTY) {
			if (!itemstack.isEmpty()) {
				te.setItem(itemstack.split(1));
				te.setChanged();
				return InteractionResult.SUCCESS;
			}
		} else {
			ItemStack pedestalItem = te.getItem();
			if (!pedestalItem.isEmpty() && !player.addItem(pedestalItem)) {
				ItemEntity itementity = new ItemEntity(level,
						player.getX(),
						player.getY(),
						player.getZ(), pedestalItem);
				itementity.setNoPickUpDelay();
				level.addFreshEntity(itementity);
			}
			te.setItem(ItemStack.EMPTY);
			te.setChanged();
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity te = level.getBlockEntity(pos);
			if (te instanceof PedestalBlockEntity)
				Containers.dropItemStack(level, te.getBlockPos().getX(), te.getBlockPos().getY(), te.getBlockPos().getZ(), ((PedestalBlockEntity)te).getItem());
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}
}