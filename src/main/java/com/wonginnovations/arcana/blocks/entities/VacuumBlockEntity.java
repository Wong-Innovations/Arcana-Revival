package com.wonginnovations.arcana.blocks.entities;

import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class VacuumBlockEntity extends BlockEntity {

    int existTime = 0;
    int duration = Short.MAX_VALUE;
    private BlockState originBlock = null;

    public VacuumBlockEntity(BlockPos pos, BlockState state) {
        super(ArcanaBlockEntities.VACUUM.get(), pos, state);
    }

    public boolean shouldRenderFace(Direction direction) {
        BlockState block = level.getBlockState(getBlockPos().relative(direction, -1));
        return block.getBlock() != Blocks.AIR
                && block.getBlock() != Blocks.CAVE_AIR
                && block.getBlock() != Blocks.VOID_AIR
                && block.getBlock() != ArcanaBlocks.VACUUM_BLOCK.get();
    }

    @Override
    public void load(@NotNull CompoundTag compoundNBT) {
        super.load(compoundNBT);
        duration = compoundNBT.getInt("Duration");
        existTime = compoundNBT.getInt("ExistTime");
        originBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(compoundNBT.getString("Block"))).defaultBlockState();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compoundNBT) {
        super.saveAdditional(compoundNBT);
        compoundNBT.putInt("Duration",duration);
        compoundNBT.putInt("ExistTime",existTime);
        compoundNBT.putString("Block", ForgeRegistries.BLOCKS.getResourceKey(originBlock.getBlock()).get().location().toString());
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setOriginBlock(BlockState originBlock) {
        this.originBlock = originBlock;
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        existTime++;
        if (existTime >= duration) {
            level.setBlockAndUpdate(pos, Objects.requireNonNullElseGet(originBlock, Blocks.AIR::defaultBlockState));
        }
    }
}
