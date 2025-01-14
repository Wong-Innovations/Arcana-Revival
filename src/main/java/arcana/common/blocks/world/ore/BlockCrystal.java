package arcana.common.blocks.world.ore;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import arcana.api.ArcanaApiHelper;
import arcana.api.aspects.Aspect;
import arcana.common.lib.ModSounds;
import arcana.common.lib.utils.BlockUtils;

import java.util.ArrayList;
import java.util.List;

//TODO Spread crystal
//TODO Crystal growth
//TODO Fix lightmaps
//TODO Fix overlay coordinates for block destruction progress (destroy_stage_* textures)
public class BlockCrystal extends Block {
    public static final IntegerProperty SIZE = IntegerProperty.create("size", 0, 3);
    public static final IntegerProperty GENERATION = IntegerProperty.create("gen", 1, 4);
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public Aspect aspect;

    public BlockCrystal(Aspect aspect) {
        super(Properties.of(/* Material.GLASS */)
                .strength(0.25f)
                .sound(ModSounds.CRYSTAL)
                .randomTicks()
                .noCollission());

        this.aspect = aspect;

        this.registerDefaultState(this.defaultBlockState().setValue(BlockCrystal.SIZE, 0).setValue(BlockCrystal.GENERATION, 1));
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState pState, LootParams.@NotNull Builder pParams) {
        List<ItemStack> ret = new ArrayList<>();
        for (int count = this.getGrowth(pState) + 1, i = 0; i < count; ++i) {
            ret.add(ArcanaApiHelper.makeCrystal(this.aspect));
        }
        return ret;
    }

    // updateTick

    // spreadCrystal

    @Override
    public boolean canSurvive(@NotNull BlockState pState, @NotNull LevelReader pLevel, @NotNull BlockPos pPos) {
        return BlockUtils.isBlockTouching(pLevel, pPos, BlockTags.STONE_ORE_REPLACEABLES, true);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState pState, @NotNull Direction pDirection, @NotNull BlockState pNeighborState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pNeighborPos) {
        if (!BlockUtils.isBlockTouching(pLevel, pCurrentPos, BlockTags.STONE_ORE_REPLACEABLES, true)) {
            return Blocks.AIR.defaultBlockState();
        }
        return getExtendedState(pState, pLevel, pCurrentPos);
    }

    private boolean drawAt(LevelAccessor worldIn, BlockPos pos, Direction side) {
        BlockState fbs = worldIn.getBlockState(pos);
        return fbs.is(BlockTags.STONE_ORE_REPLACEABLES) && fbs.isFaceSturdy(worldIn, pos, side.getOpposite());
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        int c = 0;
        if (pState.getValue(BlockCrystal.UP)) {
            ++c;
        }
        if (pState.getValue(BlockCrystal.DOWN)) {
            ++c;
        }
        if (pState.getValue(BlockCrystal.EAST)) {
            ++c;
        }
        if (pState.getValue(BlockCrystal.WEST)) {
            ++c;
        }
        if (pState.getValue(BlockCrystal.SOUTH)) {
            ++c;
        }
        if (pState.getValue(BlockCrystal.NORTH)) {
            ++c;
        }
        if (c > 1) {
            return Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        }
        if (pState.getValue(BlockCrystal.UP)) {
            return Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
        }
        if (pState.getValue(BlockCrystal.DOWN)) {
            return Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
        }
        if (pState.getValue(BlockCrystal.EAST)) {
            return Block.box(8.0, 0.0, 0.0, 16.0, 16.0, 16.0);
        }
        if (pState.getValue(BlockCrystal.WEST)) {
            return Block.box(0.0, 0.0, 0.0, 8.0, 16.0, 16.0);
        }
        if (pState.getValue(BlockCrystal.SOUTH)) {
            return Block.box(0.0, 0.0, 8.0, 16.0, 16.0, 16.0);
        }
        if (pState.getValue(BlockCrystal.NORTH)) {
            return Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 8.0);
        }

        return Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return 1;
    }

    // getPackedLightmapCoords

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(SIZE, GENERATION, NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    public BlockState getExtendedState(BlockState state, LevelAccessor world, BlockPos pos) {
        return state.setValue(UP, this.drawAt(world, pos.above(), Direction.UP))
                .setValue(DOWN, this.drawAt(world, pos.below(), Direction.DOWN))
                .setValue(NORTH, this.drawAt(world, pos.north(), Direction.NORTH))
                .setValue(EAST, this.drawAt(world, pos.east(), Direction.NORTH))
                .setValue(SOUTH, this.drawAt(world, pos.south(), Direction.SOUTH))
                .setValue(WEST, this.drawAt(world, pos.west(), Direction.WEST));
    }

    public BlockState getStateFromMeta(int meta) {
        return this.defaultBlockState().setValue(BlockCrystal.SIZE, (meta & 0x3)).setValue(BlockCrystal.GENERATION, (1 + (meta >> 2 & 0x3)));
    }

    public int getMetaFromState(BlockState state) {
        int i = 0;
        i |= state.getValue(BlockCrystal.SIZE);
        i |= state.getValue(BlockCrystal.GENERATION) - 1 << 2;
        return i;
    }

    public int getGrowth(BlockState state) {
        return this.getMetaFromState(state) & 0x3;
    }

    public int getGeneration(BlockState state) {
        return 1 + (this.getMetaFromState(state) >> 2);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return getExtendedState(this.defaultBlockState(), pContext.getLevel(), pContext.getClickedPos());
    }
}
