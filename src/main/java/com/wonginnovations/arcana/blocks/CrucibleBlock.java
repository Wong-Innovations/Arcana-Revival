package com.wonginnovations.arcana.blocks;

import com.wonginnovations.arcana.blocks.entities.ArcanaBlockEntities;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.entities.CrucibleBlockEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CrucibleBlock extends BaseEntityBlock {
	
	public static final VoxelShape INSIDE = box(2.0D, 4.0D, 2.0D, 14.0D, 15.0D, 14.0D);
	protected static final VoxelShape SHAPE = Shapes.join(box(0, 0, 0, 16, 15, 16), Shapes.or(box(0.0D, 0.0D, 3.0D, 16.0D, 3.0D, 13.0D), box(3.0D, 0.0D, 0.0D, 13.0D, 3.0D, 16.0D), box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE), BooleanOp.ONLY_FIRST);
	
	public static final BooleanProperty FULL = BooleanProperty.create("full");
	
	public CrucibleBlock(Properties properties) {
		super(properties);
		registerDefaultState(defaultBlockState().setValue(FULL, false));
	}
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FULL);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
		return RenderShape.MODEL;
	}

	// TODO: maybe getShape needs to subtract INSIDE for this?
//	public VoxelShape getRaytraceShape(BlockState state, IBlockReader levelIn, BlockPos pos) {
//		return INSIDE;
//	}
	
//	public boolean allowsMovement(BlockState state, IBlockReader levelIn, BlockPos pos, PathType type) {
//		return false;
//	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
		return createTickerHelper(pBlockEntityType, ArcanaBlockEntities.CRUCIBLE.get(), CrucibleBlockEntity::tick);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult rayTrace) {
		ItemStack itemstack = player.getItemInHand(handIn);
		if (itemstack.isEmpty()) {
			if (player.isCrouching()) {
				if (state.getValue(FULL)) {
					if (!level.isClientSide) {
						level.setBlock(pos, state.setValue(FULL, false), UPDATE_CLIENTS);
						level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
					}
					((CrucibleBlockEntity)level.getBlockEntity(pos)).empty();
				}
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		} else {
			Item item = itemstack.getItem();
			if (item == Items.WATER_BUCKET) {
				if (!state.getValue(FULL) && !level.isClientSide) {
					if (!player.isCreative())
						player.setItemInHand(handIn, new ItemStack(Items.BUCKET));
					player.awardStat(Stats.FILL_CAULDRON);
					level.setBlock(pos, state.setValue(FULL, true), UPDATE_CLIENTS);
					level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
				return InteractionResult.SUCCESS;
			} else if (item == Items.BUCKET) {
				if (state.getValue(FULL) && !level.isClientSide && ((CrucibleBlockEntity)level.getBlockEntity(pos)).getAspectStackMap().isEmpty()) {
					if (!player.isCreative()) {
						itemstack.shrink(1);
						if (itemstack.isEmpty())
							player.setItemInHand(handIn, new ItemStack(Items.WATER_BUCKET));
						else if (!player.getInventory().add(new ItemStack(Items.WATER_BUCKET)))
							player.drop(new ItemStack(Items.WATER_BUCKET), false);
					}
					player.awardStat(Stats.USE_CAULDRON);
					level.setBlock(pos, state.setValue(FULL, false), UPDATE_CLIENTS);
					level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
				}
				return InteractionResult.SUCCESS;
			}
		}
		return super.use(state, level, pos, player, handIn, rayTrace);
	}
	
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		BlockEntity entity = level.getBlockEntity(pos);
		if (entity instanceof CrucibleBlockEntity)
			((CrucibleBlockEntity)entity).empty();
		super.onRemove(state, level, pos, newState, isMoving);
	}
	
	public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
		// if boiling, show bubbles
		if (((CrucibleBlockEntity)level.getBlockEntity(pos)).isBoiling()) {
			// we boiling
			double x = pos.getX();
			double y = pos.getY();
			double z = pos.getZ();
			// bubble column particles remove themselves quickly, we might want our own thing
			level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, x + .125 + rand.nextFloat() * .75f, y + .8125f, z + .125 + rand.nextFloat() * .75f, 0.0D, 0.04D, 0.0D);
			level.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, x + .125 + rand.nextFloat() * .75f, y + .8125f, z + .125 + rand.nextFloat() * .75f, 0.0D, 0.04D, 0.0D);
		}
	}

	public void handlePrecipitation(BlockState state, Level level, BlockPos pos, Biome.Precipitation precipitation) {
		if (precipitation == Biome.Precipitation.RAIN && !state.getValue(FULL)) {
			level.setBlock(pos, state.setValue(FULL, true), UPDATE_CLIENTS);
		}
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new CrucibleBlockEntity(pos, state);
	}
}