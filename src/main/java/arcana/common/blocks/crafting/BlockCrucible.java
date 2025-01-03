package arcana.common.blocks.crafting;

import arcana.common.blockentities.crafting.BlockEntityCrucible;
import arcana.common.entities.EntitySpecialItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BlockCrucible extends BaseEntityBlock {
    private int delay = 0;
    protected static final AABB AABB_LEGS = new AABB(0.0, 0.0, 0.0, 1.0, 0.3125, 1.0);
    protected static final AABB AABB_WALL_NORTH = new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 0.125);
    protected static final AABB AABB_WALL_SOUTH = new AABB(0.0, 0.0, 0.875, 1.0, 1.0, 1.0);
    protected static final AABB AABB_WALL_EAST = new AABB(0.875, 0.0, 0.0, 1.0, 1.0, 1.0);
    protected static final AABB AABB_WALL_WEST = new AABB(0.0, 0.0, 0.0, 0.125, 1.0, 1.0);

    public BlockCrucible() {
        super(Properties.of().sound(SoundType.METAL).noOcclusion());
    }

    @Override
    public void stepOn(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        if (!level.isClientSide()) {
            BlockEntityCrucible tile = (BlockEntityCrucible)level.getBlockEntity(pos);
            if (tile != null && entity instanceof ItemEntity && !(entity instanceof EntitySpecialItem) && tile.heat > 150 && tile.tank.getFluidAmount() > 0) {
                tile.attemptSmelt((ItemEntity) entity);
            } else {
                ++this.delay;
                if (this.delay < 10) {
                    return;
                }

                this.delay = 0;
                if (entity instanceof LivingEntity && tile != null && tile.heat > 150 && tile.tank.getFluidAmount() > 0) {
                    entity.hurt(level.damageSources().inFire(), 1.0F);
                    level.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.4F, 2.0F + level.random.nextFloat() * 0.4F);
                }
            }
        }

        super.stepOn(level, pos, state, entity);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.create(AABB_LEGS), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.create(AABB_WALL_WEST), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.create(AABB_WALL_NORTH), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.create(AABB_WALL_EAST), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.create(AABB_WALL_SOUTH), BooleanOp.OR);
        return shape;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void destroy(LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te != null && te instanceof BlockEntityCrucible) {
            ((BlockEntityCrucible)te).spillRemnants();
        }

        super.destroy(level, pos, state);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        } else {
            Optional<FluidStack> fs = FluidUtil.getFluidContained(player.getItemInHand(hand));
            BlockEntity te;
            BlockEntityCrucible tile;
            if (fs.isPresent()) {
                if (fs.get().containsFluid(new FluidStack(Fluids.WATER, 1000))) {
                    te = level.getBlockEntity(pos);
                    if (te != null && te instanceof BlockEntityCrucible) {
                        tile = (BlockEntityCrucible)te;
                        if (tile.tank.getFluidAmount() < tile.tank.getCapacity()) {
                            if (FluidUtil.interactWithFluidHandler(player, hand, tile.tank)) {
                                player.inventoryMenu.broadcastChanges();
                                te.setChanged();
                                level.markAndNotifyBlock(pos, level.getChunkAt(pos), state, state, 3, 512);
                                level.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.33F, 1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.3F);
                            }

                            return InteractionResult.SUCCESS;
                        }
                    }

                    return super.use(state, level, pos, player, hand, hit);
                }
            }

            if (!player.isCrouching() && hit.getDirection() == Direction.UP /* && !(player.getItemInHand(hand).getItem() instanceof ICaster) */) {
                te = level.getBlockEntity(pos);
                if (te != null && te instanceof BlockEntityCrucible) {
                    tile = (BlockEntityCrucible)te;
                    ItemStack ti = player.getItemInHand(hand).copy();
                    ti.setCount(1);
                    if (tile.heat > 150 && tile.tank.getFluidAmount() > 0 && tile.attemptSmelt(ti, player) == null) {
                        player.getInventory().removeItem(player.getInventory().selected, 1);
                        return InteractionResult.SUCCESS;
                    }
                }
            } else if (player.getItemInHand(hand).isEmpty() && player.isCrouching()) {
                te = level.getBlockEntity(pos);
                if (te != null && te instanceof BlockEntityCrucible) {
                    tile = (BlockEntityCrucible)te;
                    tile.spillRemnants();
                    return InteractionResult.SUCCESS;
                }
            }

            return super.use(state, level, pos, player, hand, hit);
        }
    }

    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    public int getComparatorInputOverride(BlockState state, Level level, BlockPos pos) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te != null && te instanceof BlockEntityCrucible) {
            float var10000 = (float)((BlockEntityCrucible)te).aspects.visSize();
            ((BlockEntityCrucible)te).getClass();
            float r = var10000 / 500.0F;
            return Mth.floor(r * 14.0F) + (((BlockEntityCrucible)te).aspects.visSize() > 0 ? 1 : 0);
        } else {
            return 0;
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return (lvl, pos, st, blockEntity) -> {
            if (blockEntity instanceof BlockEntityCrucible) {
                ((BlockEntityCrucible) blockEntity).tick(lvl, pos, st);
            }
        };
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, @NotNull Level w, @NotNull BlockPos pos, RandomSource r) {
        if (r.nextInt(10) == 0) {
            BlockEntity te = w.getBlockEntity(pos);
            if (te != null && te instanceof BlockEntityCrucible && ((BlockEntityCrucible)te).tank.getFluidAmount() > 0 && ((BlockEntityCrucible)te).heat > 150) {
                w.playSound(Minecraft.getInstance().player, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.1F + r.nextFloat() * 0.1F, 1.2F + r.nextFloat() * 0.2F);
            }
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new BlockEntityCrucible(pPos, pState);
    }
}
