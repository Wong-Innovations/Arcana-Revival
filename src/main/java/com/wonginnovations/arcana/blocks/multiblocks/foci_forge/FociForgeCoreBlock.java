package com.wonginnovations.arcana.blocks.multiblocks.foci_forge;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.blocks.multiblocks.StaticComponent;
import com.wonginnovations.arcana.blocks.entities.FociForgeBlockEntity;
import com.wonginnovations.arcana.items.ArcanaItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.wonginnovations.arcana.blocks.multiblocks.foci_forge.FociForgeComponentBlock.*;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FociForgeCoreBlock extends BaseEntityBlock implements StaticComponent {

	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		VoxelShape shape;
		Direction facing = state.getValue(FACING);
        shape = switch (facing) {
            case EAST -> SHAPE_E;
            case SOUTH -> SHAPE_S;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
		return shape;
	}

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public FociForgeCoreBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new FociForgeBlockEntity(pos, state);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	public boolean isCore(BlockPos pos, BlockState state) {
		return true;
	}

	public BlockPos getCorePos(BlockPos pos, BlockState state) {
		return pos;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		Direction facing = state.getValue(FACING);
		for (FociForgeComponentBlock.Component com : FociForgeComponentBlock.Component.values()) {
			BlockPos offset = pos.offset(com.getOffset(facing));
			if (level.getBlockState(offset).getBlock() == ArcanaBlocks.FOCI_FORGE_COMPONENT.get())
				level.destroyBlock(offset, false);
		}
		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		Direction facing = state.getValue(FACING);
		boolean broke = false;
		for (FociForgeComponentBlock.Component com : FociForgeComponentBlock.Component.values()) {
			if (level.getBlockState(pos.offset(com.getOffset(facing))).getBlock() != ArcanaBlocks.FOCI_FORGE_COMPONENT.get()) {
				broke = true;
				break;
			}
		}
		if (broke) {
			for (FociForgeComponentBlock.Component com : FociForgeComponentBlock.Component.values()) {
				level.destroyBlock(pos.offset(com.getOffset(facing)), false);
			}
		}
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
	}

	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction facing = context.getHorizontalDirection().getOpposite();
		if (!context.getLevel().getBlockState(context.getClickedPos()).canBeReplaced(context))
			return null;
		for (FociForgeComponentBlock.Component com : FociForgeComponentBlock.Component.values())
			if (!context.getLevel().getBlockState(context.getClickedPos().offset(com.getOffset(facing))).canBeReplaced(context))
				return null;
		return this.defaultBlockState().setValue(FACING, facing);
	}

	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!level.isClientSide) {
			Direction facing = state.getValue(FACING);
			for (FociForgeComponentBlock.Component com : FociForgeComponentBlock.Component.values()) {
				BlockPos comPos = pos.offset(com.getOffset(facing));
				level.setBlockAndUpdate(comPos,
						ArcanaBlocks.FOCI_FORGE_COMPONENT.get().defaultBlockState()
								.setValue(FociForgeComponentBlock.FACING, facing)
								.setValue(FociForgeComponentBlock.COMPONENT, com));
			}
			for (FociForgeComponentBlock.Component com : FociForgeComponentBlock.Component.values()) {
				BlockPos comPos = pos.offset(com.getOffset(facing));
				level.updateNeighborsAt(comPos, Blocks.AIR);
				state.updateNeighbourShapes(level, comPos, 3);
			}
		}
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
		if (level.isClientSide)
			return InteractionResult.SUCCESS;
		BlockEntity te = level.getBlockEntity(pos);
		if (te instanceof FociForgeBlockEntity ffbe) {
			NetworkHooks.openScreen((ServerPlayer)player, ffbe, buf -> buf.writeBlockPos(pos));
			return InteractionResult.SUCCESS;
		}
		return super.use(state, level, pos, player, hand, rayTraceResult);
	}

	@Override
	public Item asItem() {
		return ArcanaItems.FOCI_FORGE_ITEM.get();
	}
}
