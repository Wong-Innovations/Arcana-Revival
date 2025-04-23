package com.wonginnovations.arcana.blocks.multiblocks.research_table;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.blocks.bases.WaterloggableEntityBlock;
import com.wonginnovations.arcana.blocks.multiblocks.StaticComponent;
import com.wonginnovations.arcana.blocks.entities.ResearchTableBlockEntity;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.util.ShapeUtils;
import net.minecraft.core.Vec3i;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.wonginnovations.arcana.blocks.multiblocks.research_table.ResearchTableComponentBlock.COM_OFFSET;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ResearchTableCoreBlock extends WaterloggableEntityBlock implements StaticComponent {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty INK = BooleanProperty.create("ink");

    public ResearchTableCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ResearchTableBlockEntity(pos, state);
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
        builder.add(FACING).add(INK);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockPos offset = pos.offset(ShapeUtils.fromNorth(COM_OFFSET, state.getValue(FACING)));
        if (level.getBlockState(offset).getBlock() == ArcanaBlocks.RESEARCH_TABLE_COMPONENT.get()) {
            level.destroyBlock(offset, false);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    public void onRemove(BlockState state, Level levelIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity tileentity = levelIn.getBlockEntity(pos);
            if (tileentity instanceof ResearchTableBlockEntity) {
                Containers.dropContents(levelIn, pos, (ResearchTableBlockEntity)tileentity);
            }
            super.onRemove(state, levelIn, pos, newState, isMoving);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        Direction facing = state.getValue(FACING);
        Vec3i rotated = ShapeUtils.fromNorth(COM_OFFSET, facing);
        if (level.getBlockState(pos.offset(rotated)).getBlock() != ArcanaBlocks.RESEARCH_TABLE_COMPONENT.get())
            level.destroyBlock(pos.offset(rotated), false);
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        if (!context.getLevel().getBlockState(context.getClickedPos()).canBeReplaced(context))
            return null;
        if (!context.getLevel().getBlockState(context.getClickedPos().offset(ShapeUtils.fromNorth(COM_OFFSET, facing))).canBeReplaced(context))
            return null;
        return this.defaultBlockState().setValue(FACING, facing).setValue(INK, false);
    }

    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            BlockPos comPos = pos.offset(ShapeUtils.fromNorth(COM_OFFSET, facing));
            level.setBlockAndUpdate(comPos,
                    ArcanaBlocks.RESEARCH_TABLE_COMPONENT.get().defaultBlockState()
                            .setValue(ResearchTableComponentBlock.FACING, facing));
            level.updateNeighbourForOutputSignal(comPos, Blocks.AIR);
            state.updateNeighbourShapes(level, comPos, 3);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof ResearchTableBlockEntity rtbe) {
            NetworkHooks.openScreen((ServerPlayer) player, rtbe, buf -> buf.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
        }
        return super.use(state, level, pos, player, hand, rayTraceResult);
    }

    @Override
    public Item asItem() {
        return ArcanaItems.RESEARCH_TABLE_ITEM.get();
    }
}