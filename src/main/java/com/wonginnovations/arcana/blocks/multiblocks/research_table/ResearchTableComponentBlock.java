package com.wonginnovations.arcana.blocks.multiblocks.research_table;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.blocks.bases.WaterloggableEntityBlock;
import com.wonginnovations.arcana.blocks.multiblocks.StaticComponent;
import com.wonginnovations.arcana.blocks.entities.ResearchTableBlockEntity;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.util.ShapeUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ResearchTableComponentBlock extends WaterloggableEntityBlock implements StaticComponent {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty PAPER = BooleanProperty.create("paper");
	public static final Vec3i COM_OFFSET = new Vec3i(1, 0, 0);
	public static final Vec3i COM_INVERT = new Vec3i(-1, 0, 0);

	public ResearchTableComponentBlock(Properties properties) {
		super(properties);
		registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(PAPER, false));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
//		return new ResearchTableBlockEntity(pos, state);
		return null; // TODO: maybe this should return the entity of the one next to it?
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public BlockState rotate(BlockState state, Rotation direction) {
		return state.setValue(FACING, direction.rotate(state.getValue(FACING)));
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	public boolean isCore(BlockPos pos, BlockState state) {
		return false;
	}

	public BlockPos getCorePos(BlockPos pos, BlockState state) {
		return pos.offset(ShapeUtils.fromNorth(COM_INVERT, state.getValue(FACING)));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING).add(PAPER);
	}

	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		BlockPos corePos = getCorePos(pos, state);
		if (level.getBlockState(corePos).getBlock() == ArcanaBlocks.RESEARCH_TABLE.get()) {
			level.destroyBlock(corePos, false);
		}
		// TODO: loot table that detects harvested by player
		if (!player.isCreative()) {
			dropResources(state, level, pos);
		}
		super.playerWillDestroy(level, pos, state, player);
	}

	public void onRemove(BlockState state, Level levelIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity tileentity = levelIn.getBlockEntity(getCorePos(pos, state));
			if (tileentity instanceof ResearchTableBlockEntity) {
				Containers.dropContents(levelIn, pos, (ResearchTableBlockEntity)tileentity);
			}
			super.onRemove(state, levelIn, pos, newState, isMoving);
		}
	}

	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		BlockPos corePos = getCorePos(pos, state);
		if (level.getBlockState(corePos).getBlock() != ArcanaBlocks.RESEARCH_TABLE.get())
			level.destroyBlock(pos, false);
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult rayTraceResult) {
		if (level.isClientSide)
			return InteractionResult.SUCCESS;
		BlockPos corePos = getCorePos(pos, state);
		BlockEntity te = level.getBlockEntity(corePos);
		if (te instanceof ResearchTableBlockEntity rtbe) {
			NetworkHooks.openScreen((ServerPlayer) player, rtbe, buf -> buf.writeBlockPos(corePos));
			return InteractionResult.SUCCESS;
		}
		return super.use(state, level, pos, player, handIn, rayTraceResult);
	}

	@Override
	public Item asItem() {
		return ArcanaItems.RESEARCH_TABLE_ITEM.get();
	}
}