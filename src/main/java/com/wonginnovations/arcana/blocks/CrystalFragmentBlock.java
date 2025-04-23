package com.wonginnovations.arcana.blocks;

import com.wonginnovations.arcana.blocks.bases.SixWayBlock;
import com.wonginnovations.arcana.blocks.bases.WaterloggableBlock;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.Aspect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CrystalFragmentBlock extends WaterloggableBlock {
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty EAST = SixWayBlock.EAST;
    public static final BooleanProperty WEST = SixWayBlock.WEST;
    public static final BooleanProperty NORTH = SixWayBlock.NORTH;
    public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    public static final BooleanProperty DOWN = SixWayBlock.DOWN;
    
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = SixWayBlock.PROPERTY_BY_DIRECTION;
    
    public static final VoxelShape DOWN_AABB = box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    public static final VoxelShape EAST_AABB = box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    public static final VoxelShape WEST_AABB = box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    public static final VoxelShape SOUTH_AABB = box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    public static final VoxelShape NORTH_AABB = box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    public static final VoxelShape UP_AABB = box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    private Aspect aspect;

    public CrystalFragmentBlock(Properties properties, Aspect aspect) {
        super(properties);
        this.aspect = aspect;
        registerDefaultState(this.defaultBlockState()
                .setValue(WATERLOGGED, Boolean.FALSE)
                .setValue(UP, Boolean.FALSE)
                .setValue(NORTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE)
                .setValue(SOUTH, Boolean.FALSE)
                .setValue(WEST, Boolean.FALSE)
                .setValue(DOWN, Boolean.FALSE));
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        for (Direction dir : Direction.values()) {
            Level level = context.getLevel();
            BlockPos pos = context.getClickedPos();
            BlockState attachState = level.getBlockState(pos);
            state = state.setValue(PROPERTY_BY_DIRECTION.get(dir), attachState.isFaceSturdy(level, pos, dir));
        }
        return state;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, NORTH, EAST, SOUTH, WEST, DOWN, WATERLOGGED);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        for (Direction dir : Direction.values()) {
            state = state.setValue(PROPERTY_BY_DIRECTION.get(dir), state.isFaceSturdy(level, currentPos, dir));
        }

        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (state.isFaceSturdy(level, pos, dir)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        VoxelShape voxelshape = Shapes.empty();
        if (state.getValue(UP)) {
            voxelshape = Shapes.or(voxelshape, UP_AABB);
        }
        if (state.getValue(EAST)) {
            voxelshape = Shapes.or(voxelshape, EAST_AABB);
        }
        if (state.getValue(WEST)) {
            voxelshape = Shapes.or(voxelshape, WEST_AABB);
        }
        if (state.getValue(NORTH)) {
            voxelshape = Shapes.or(voxelshape, NORTH_AABB);
        }
        if (state.getValue(SOUTH)) {
            voxelshape = Shapes.or(voxelshape, SOUTH_AABB);
        }
        if (state.getValue(DOWN)) {
            voxelshape = Shapes.or(voxelshape, DOWN_AABB);
        }

        return voxelshape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter levelIn, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean someAttached = false;
        for (Direction dir : Direction.values()) {
            boolean attached = state.isFaceSturdy(level, pos, dir);
            state = state.setValue(PROPERTY_BY_DIRECTION.get(dir), attached);
            if (attached) {
                someAttached = true;
            }
        }
        if (!someAttached) {
            dropResources(state, level, pos);
            level.removeBlock(pos, false);
        } else {
            level.setBlockAndUpdate(pos, state);
            super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        }
    }
}
