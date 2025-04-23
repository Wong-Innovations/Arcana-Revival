package com.wonginnovations.arcana.blocks.multiblocks.foci_forge;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.blocks.multiblocks.IStaticEnum;
import com.wonginnovations.arcana.blocks.multiblocks.StaticComponent;
import com.wonginnovations.arcana.blocks.entities.FociForgeBlockEntity;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.util.ShapeUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FociForgeComponentBlock extends Block implements StaticComponent {

	public static final VoxelShape SHAPE_N = Shapes.or(
			Shapes.create(0, 0, 0, 32 / 16f, 4 / 16f, 32 / 16f),
			Shapes.create(0, 4 / 16f, 17 / 16f, 32 / 16f, 16 / 16f, 31 / 16f),
			Shapes.create(26 / 16f, 4 / 16f, 1 / 16f, 32 / 16f, 27 / 16f, 31 / 16f),
			Shapes.create(18 / 16f, 16 / 16f, 19 / 16f, 26 / 16f, 30 / 16f, 28 / 16f)
	).optimize();
	public static final VoxelShape SHAPE_E = ShapeUtils.rotate(SHAPE_N, Direction.EAST);
	public static final VoxelShape SHAPE_S = ShapeUtils.rotate(SHAPE_N, Direction.SOUTH);
	public static final VoxelShape SHAPE_W = ShapeUtils.rotate(SHAPE_N, Direction.WEST);

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Component> COMPONENT = EnumProperty.create("ff_com", Component.class);
	
	public FociForgeComponentBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState()
				.setValue(FACING, Direction.NORTH)
				.setValue(COMPONENT, Component.F));
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		VoxelShape shape;
		Direction facing = state.getValue(FACING);
		Vec3i fromCore = state.getValue(COMPONENT).getInvert(facing);
        shape = switch (facing) {
            case EAST -> SHAPE_E;
            case SOUTH -> SHAPE_S;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
		return shape.move(fromCore.getX(), fromCore.getY(), fromCore.getZ());
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
		return RenderShape.INVISIBLE;
	}

	public boolean isCore(BlockPos pos, BlockState state) {
		return false;
	}

	public BlockPos getCorePos(BlockPos pos, BlockState state) {
		return pos.offset(state.getValue(COMPONENT).getInvert(state.getValue(FACING)));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, COMPONENT);
	}

	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
		BlockPos corePos = getCorePos(pos, state);
		if (level.getBlockState(corePos).getBlock() == ArcanaBlocks.FOCI_FORGE.get())
			level.destroyBlock(corePos, false);
		// Components don't naturally spawn drops, for some reason
		if (!player.isCreative())
			dropResources(state, level, pos);
		super.playerWillDestroy(level, pos, state, player);
	}

	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		BlockPos corePos = getCorePos(pos, state);
		if (level.getBlockState(corePos).getBlock() != ArcanaBlocks.FOCI_FORGE.get())
			level.destroyBlock(pos, false);
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
	}

//	public boolean isNormalCube(BlockState state, BlockGetter world, BlockPos pos) {
//		return false;
//	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult rayTraceResult) {
		if (level.isClientSide)
			return InteractionResult.SUCCESS;
		BlockPos corePos = getCorePos(pos, state);
		BlockEntity te = level.getBlockEntity(corePos);
		if (te instanceof FociForgeBlockEntity ffbe) {
			NetworkHooks.openScreen((ServerPlayer) player, ffbe, buf -> buf.writeBlockPos(corePos));
			return InteractionResult.SUCCESS;
		}
		return super.use(state, level, pos, player, handIn, rayTraceResult);
	}

	public enum Component implements IStaticEnum {
		U("u", 0, 1, 0),
		UR("ur", 1, 1, 0),
		R("r", 1, 0, 0),
		F("f",0, 0, 1),
		FU("fu",0, 1, 1),
		FUR("fur", 1, 1, 1),
		FR("fr", 1, 0, 1);

		private final String name;
		private final Vec3i offset;
		private final Vec3i invert;

		Component(String name, int x, int y, int z) {
			this.name = name;
			this.offset = new Vec3i(x, y, z);
			this.invert = new Vec3i(-x, -y, -z);
		}

		public String getName() {
			return name;
		}

		public Vec3i getOffset(Direction direction) {
			return ShapeUtils.fromNorth(this.offset, direction);
		}

		public Vec3i getInvert(Direction direction) {
			return ShapeUtils.fromNorth(this.invert, direction);
		}
		
		public String getSerializedName() {
			return name;
		}
	}

	@Override
	public Item asItem() {
		return ArcanaItems.FOCI_FORGE_ITEM.get();
	}
}