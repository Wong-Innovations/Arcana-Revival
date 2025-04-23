package com.wonginnovations.arcana.blocks.bases;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

// ripped from PipeBlock because I needed a version that extended EntityBlock
public abstract class SixWayBlock extends BaseEntityBlock {
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), (p_55164_) -> {
        p_55164_.put(Direction.NORTH, NORTH);
        p_55164_.put(Direction.EAST, EAST);
        p_55164_.put(Direction.SOUTH, SOUTH);
        p_55164_.put(Direction.WEST, WEST);
        p_55164_.put(Direction.UP, UP);
        p_55164_.put(Direction.DOWN, DOWN);
    }));
    protected final VoxelShape[] shapeByIndex;

    public SixWayBlock(float pApothem, BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.shapeByIndex = this.makeShapes(pApothem);
    }

    private VoxelShape[] makeShapes(float pApothem) {
        float f = 0.5F - pApothem;
        float f1 = 0.5F + pApothem;
        VoxelShape voxelshape = Block.box(f * 16.0F, f * 16.0F, f * 16.0F, f1 * 16.0F, f1 * 16.0F, f1 * 16.0F);
        VoxelShape[] avoxelshape = new VoxelShape[DIRECTIONS.length];

        for (int i = 0; i < DIRECTIONS.length; ++i) {
            Direction direction = DIRECTIONS[i];
            avoxelshape[i] = Shapes.box(0.5D + Math.min(-pApothem, (double)direction.getStepX() * 0.5D), 0.5D + Math.min(-pApothem, (double)direction.getStepY() * 0.5D), 0.5D + Math.min(-pApothem, (double)direction.getStepZ() * 0.5D), 0.5D + Math.max(pApothem, (double)direction.getStepX() * 0.5D), 0.5D + Math.max(pApothem, (double)direction.getStepY() * 0.5D), 0.5D + Math.max((double)pApothem, (double)direction.getStepZ() * 0.5D));
        }

        VoxelShape[] avoxelshape1 = new VoxelShape[64];

        for (int k = 0; k < 64; ++k) {
            VoxelShape voxelshape1 = voxelshape;

            for (int j = 0; j < DIRECTIONS.length; ++j) {
                if ((k & 1 << j) != 0) {
                    voxelshape1 = Shapes.or(voxelshape1, avoxelshape[j]);
                }
            }

            avoxelshape1[k] = voxelshape1;
        }

        return avoxelshape1;
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState pState, @NotNull BlockGetter pReader, @NotNull BlockPos pPos) {
        return false;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return this.shapeByIndex[this.getAABBIndex(pState)];
    }

    protected int getAABBIndex(BlockState pState) {
        int i = 0;

        for (int j = 0; j < DIRECTIONS.length; ++j) {
            if (pState.getValue(PROPERTY_BY_DIRECTION.get(DIRECTIONS[j]))) {
                i |= 1 << j;
            }
        }

        return i;
    }
}
