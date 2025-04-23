package com.wonginnovations.arcana.blocks;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.bases.WaterloggableEntityBlock;
import com.wonginnovations.arcana.blocks.entities.AspectBookshelfBlockEntity;
import com.wonginnovations.arcana.items.PhialItem;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.wonginnovations.arcana.ArcanaSounds.playPhialshelfSlideSound;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectBookshelfBlock extends WaterloggableEntityBlock {

	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.FACING;
	public static final BooleanProperty FULL_SIZE = BlockStateProperties.EXTENDED;
	public VoxelShape SHAPE_NORTH = box(0, 0, 8, 16, 16, 16);
	public VoxelShape SHAPE_SOUTH = box(0, 0, 0, 16, 16, 8);
	public VoxelShape SHAPE_EAST = box(0, 0, 0, 8, 16, 16);
	public VoxelShape SHAPE_WEST = box(8, 0, 0, 16, 16, 16);
	public VoxelShape SHAPE_UP = box(0, 8, 0, 16, 16, 16);
	public VoxelShape SHAPE_DOWN = box(0, 0, 0, 16, 8, 16);

	public AspectBookshelfBlock(boolean fullBlock, Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(HORIZONTAL_FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE).setValue(FULL_SIZE, fullBlock));
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity tileentity = level.getBlockEntity(pos);
			if (tileentity instanceof AspectBookshelfBlockEntity) {
				Containers.dropContents(level, pos, (AspectBookshelfBlockEntity)tileentity);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
		super.triggerEvent(state, level, pos, id, param);
		BlockEntity tileentity = level.getBlockEntity(pos);
		return tileentity != null && tileentity.triggerEvent(id, param);
	}

	@Override
	public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
		if (context.getPlayer() != null) {
			if (context.getPlayer().isCrouching() && context.getClickedFace().getOpposite() != Direction.UP) {
				return super.getStateForPlacement(context).setValue(HORIZONTAL_FACING, context.getClickedFace().getOpposite());
			}
		}
		return super.getStateForPlacement(context).setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(HORIZONTAL_FACING, FULL_SIZE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (state.getValue(FULL_SIZE)) {
			return super.getShape(state, level, pos, context);
		} else {
            return switch (state.getValue(HORIZONTAL_FACING)) {
                case SOUTH -> SHAPE_SOUTH;
                case EAST -> SHAPE_EAST;
                case WEST -> SHAPE_WEST;
                case UP -> SHAPE_UP;
                case DOWN -> SHAPE_DOWN;
                default -> SHAPE_NORTH;
            };
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return getShape(state, level, pos, context);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(HORIZONTAL_FACING, rot.rotate(state.getValue(HORIZONTAL_FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(HORIZONTAL_FACING)));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new AspectBookshelfBlockEntity(pos, state, state.getValue(HORIZONTAL_FACING));
	}

//	@Override
//	public boolean hasBlockEntity(BlockState state) {
//		return true;
//	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		BlockEntity te = level.getBlockEntity(pos);
		boolean vert = hitResult.getDirection() == Direction.UP || hitResult.getDirection() == Direction.DOWN;
		if ((vert && hitResult.getDirection() == state.getValue(HORIZONTAL_FACING).getOpposite()) || (!vert && hitResult.getDirection() == state.getValue(HORIZONTAL_FACING))) {
			int widthSlot = -1;
			int heightSlot = switch (state.getValue(HORIZONTAL_FACING)) {
                case NORTH -> {
                    widthSlot = 3 - (int) ((hitResult.getLocation().x - pos.getX()) / .33);
                    yield 3 - (int) ((hitResult.getLocation().y - pos.getY()) / .33);
                }
                case SOUTH -> {
                    widthSlot = 1 + (int) ((hitResult.getLocation().x - pos.getX()) / .33);
                    yield 3 - (int) ((hitResult.getLocation().y - pos.getY()) / .33);
                }
                case EAST -> {
                    widthSlot = 3 - (int) ((hitResult.getLocation().z - pos.getZ()) / .33);
                    yield 3 - (int) ((hitResult.getLocation().y - pos.getY()) / .33);
                }
                case WEST -> {
                    widthSlot = 1 + (int) ((hitResult.getLocation().z - pos.getZ()) / .33);
                    yield 3 - (int) ((hitResult.getLocation().y - pos.getY()) / .33);
                }
                case UP -> {
                    widthSlot = 3 - (int) ((hitResult.getLocation().x - pos.getX()) / .33);
                    yield 1 + (int) ((hitResult.getLocation().z - pos.getZ()) / .33);
                }
                case DOWN -> {
                    widthSlot = 3 - (int) ((hitResult.getLocation().x - pos.getX()) / .33);
                    yield 3 - (int) ((hitResult.getLocation().z - pos.getZ()) / .33);
                }
            };
            if (heightSlot <= 0) {
				heightSlot = 1;
			} else if (heightSlot >= 4) {
				heightSlot = 3;
			}
			if (widthSlot <= 0) {
				widthSlot = 1;
			} else if (widthSlot >= 4) {
				widthSlot = 3;
			}
			int slot = (widthSlot + ((heightSlot - 1) * 3)) - 1;

			if (te instanceof AspectBookshelfBlockEntity abe) {
                if (player.isCrouching()) {
					player.openMenu(abe);
				} else if (player.getItemInHand(hand).getItem() instanceof PhialItem && abe.addPhial(player.getItemInHand(hand), slot)) {
					player.getItemInHand(hand).shrink(1);
					playPhialshelfSlideSound(player, abe.getBlockPos());
				} else {
					ItemStack returned = abe.removePhial(slot);
					if (returned != ItemStack.EMPTY) {
						if (!player.addItem(returned)) {
							ItemEntity itementity = new ItemEntity(level,
									player.getX(),
									player.getY(),
									player.getZ(), returned);
							itementity.setNoPickUpDelay();
							level.addFreshEntity(itementity);
							playPhialshelfSlideSound(player, abe.getBlockPos());
						}
					} else {
						return InteractionResult.PASS;
					}
				}
				return InteractionResult.SUCCESS;
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState block, Level level, BlockPos pos) {
		BlockEntity te = level.getBlockEntity(pos);
		assert te != null;
		return ((AspectBookshelfBlockEntity)te).getRedstoneOut();
	}
}