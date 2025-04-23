package com.wonginnovations.arcana.blocks;

import com.wonginnovations.arcana.blocks.bases.WaterloggableBlock;
import com.wonginnovations.arcana.items.CrystalClusterItem;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.handlers.AspectHolder;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.world.AuraView;
import com.wonginnovations.arcana.world.Node;
import com.wonginnovations.arcana.world.ServerAuraView;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CrystalClusterBlock extends WaterloggableBlock {
	
	public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	protected Aspect aspect;
	private final int offset = 3;
	private final int size = 7;
	protected final VoxelShape upAabb = box(offset, 0.0D, offset, 16 - offset, size, 16 - offset);
	protected final VoxelShape downAabb = box(offset, 16 - size, offset, 16 - offset, 16.0D, 16 - offset);
	protected final VoxelShape northAabb = box(offset, offset, 16 - size, 16 - offset, 16 - offset, 16.0D);
	protected final VoxelShape southAabb = box(offset, offset, 0.0D, 16 - offset, 16 - offset, size);
	protected final VoxelShape eastAabb = box(0.0D, offset, offset, size, 16 - offset, 16 - offset);
	protected final VoxelShape westAabb = box(16 - size, offset, offset, 16.0D, 16 - offset, 16 - offset);
	
	public CrystalClusterBlock(Properties properties, Aspect aspect) {
		super(properties);
		this.aspect = aspect;
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, Boolean.FALSE).setValue(AGE, 3).setValue(FACING, Direction.UP));
	}

	@Override
	public void onProjectileHit(Level p_152001_, BlockState p_152002_, BlockHitResult p_152003_, Projectile p_152004_) {
		if (!p_152001_.isClientSide) {
			BlockPos blockpos = p_152003_.getBlockPos();
			p_152001_.playSound(null, blockpos, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 1.0F, 0.5F + p_152001_.random.nextFloat() * 1.2F);
			p_152001_.playSound(null, blockpos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 0.5F + p_152001_.random.nextFloat() * 1.2F);
		}
	}

	@Override
	public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
		// Placement = the block item, which you get with silk touch, so fully grown
		return super.getStateForPlacement(context).setValue(AGE, 3).setValue(FACING, context.getClickedFace());
	}

	@Override
	public InteractionResult use(BlockState state, Level levelIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		ItemStack stack = player.getItemInHand(handIn);
		if (stack.getItem() == Items.WRITABLE_BOOK) {
			stack.setCount(stack.getCount()-1);
			player.getInventory().add(new ItemStack(ArcanaItems.ARCANUM.get()));
		}
		return super.use(state, levelIn, pos, player, handIn, hit);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED, AGE);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		// output comparator signal when fully grown
		return state.getValue(AGE) == 3 ? 15 : 0;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		return facing == state.getValue(FACING).getOpposite() && !this.canSurvive(state, level, currentPos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		Direction direction = state.getValue(FACING);
		BlockPos blockpos = pos.relative(direction.getOpposite());
		return level.getBlockState(blockpos).isFaceSturdy(level, blockpos, direction);
	}

	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}

	public BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}

	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		Direction direction = pState.getValue(FACING);
        return switch (direction) {
            case NORTH -> this.northAabb;
            case SOUTH -> this.southAabb;
            case EAST -> this.eastAabb;
            case WEST -> this.westAabb;
            case DOWN -> this.downAabb;
            default -> this.upAabb;
        };
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		return new ItemStack(ArcanaItems.CRYSTAL_ITEMS.get(aspect).get());
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.randomTick(state, level, pos, random);
		// If we're not fully grown,
		if (state.getValue(AGE) != 3) {
			// Check for any nodes in a 9x9x9 area
			ServerAuraView view = (ServerAuraView)AuraView.SIDED_FACTORY.apply(level);
			Collection<Node> nodes = view.getNodesWithinAABB(new AABB(pos.below(4).south(4).west(4), pos.above(4).north(4).east(4)));
			// For each node in range,
			for (Node node : nodes) {
				// If it has more than 4 of our aspect,
				AspectHolder holder = node.getAspects().findFirstHolderContaining(aspect);
				if (holder != null && holder.getStack().getAmount() > 4) {
					// Take 2-4 of the aspect,
					holder.drain(level.random.nextInt(3) + 2, false);
					// Sync the node,
					view.sendChunkToClients(node);
					// Increment out growth stage,
					level.setBlockAndUpdate(pos, state.setValue(AGE, state.getValue(AGE) + 1));
					// And stop
					break;
				}
			}
		}
	}
}