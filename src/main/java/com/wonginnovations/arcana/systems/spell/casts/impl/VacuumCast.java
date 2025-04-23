package com.wonginnovations.arcana.systems.spell.casts.impl;

import com.wonginnovations.arcana.ArcanaVariables;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.blocks.entities.VacuumBlockEntity;
import com.wonginnovations.arcana.systems.spell.*;
import com.wonginnovations.arcana.systems.spell.casts.Cast;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class VacuumCast extends Cast {

    @Override
    public ResourceLocation getId() {
        return ArcanaVariables.arcLoc("vacuum");
    }
    
    /**
     * Core aspect in spell.
     *
     * @return returns core aspect.
     */
    @Override
    public Aspect getSpellAspect() {
        return Aspects.VACUUM;
    }

    @Override
    public int getSpellDuration() {
        return 1;
    }

    @Override
    public InteractionResult useOnBlock(Player caster, Level level, BlockPos blockTarget) {
        if (caster.level().isClientSide) return InteractionResult.SUCCESS;
        BlockPos.betweenClosed(blockTarget.offset(
                -Mth.floor(getWidth(caster)),
                -Mth.floor(getWidth(caster)),
                -Mth.floor(getWidth(caster))),
                blockTarget.relative(Direction.fromYRot(caster.getYRot()), getDistance(caster)).offset(
                        Mth.floor(getWidth(caster)),
                        Mth.floor(getWidth(caster)),
                        Mth.floor(getWidth(caster)))).forEach(blockPos -> {
            Block blockToReplace = level.getBlockState(blockTarget).getBlock();
            if (blockToReplace != Blocks.AIR && blockToReplace != Blocks.CAVE_AIR) {
                BlockState vaccumBlock = ArcanaBlocks.VACUUM_BLOCK.get().defaultBlockState();
                level.setBlockAndUpdate(blockTarget, vaccumBlock);
                ((VacuumBlockEntity)level.getBlockEntity(blockTarget)).setDuration(getDuration(caster));
                ((VacuumBlockEntity)level.getBlockEntity(blockTarget)).setOriginBlock(blockToReplace.defaultBlockState());
            }
        });
        return InteractionResult.SUCCESS;
    }

    protected int getWidth(Player playerEntity) {
        return SpellValues.getOrDefault(AspectUtils.getAspect(data,"sinModifier"), 1);
    }

    protected int getDistance(Player playerEntity) {
        return SpellValues.getOrDefault(AspectUtils.getAspect(data,"secondModifier"), 16);
    }

    /**
     * Gets Vacuum blocks duration from modifiers
     * @return Vacuum blocks duration
     */
    protected int getDuration(Player playerEntity) {
        return (1+SpellValues.getOrDefault(AspectUtils.getAspect(data,"firstModifier"), 0))*100;
    }

    @Override
    public InteractionResult useOnPlayer(Player playerTarget) {
        //playerTarget.sendStatusMessage(Component.translatable("status.arcana.invalid_spell"), true);
        BlockPos pos = playerTarget.getOnPos().below();
        BlockPos.betweenClosed(pos.offset(
                -Mth.floor(getWidth(playerTarget)),
                -Mth.floor(getWidth(playerTarget)),
                -Mth.floor(getWidth(playerTarget))),
                pos.relative(Direction.fromYRot(playerTarget.getYRot()), getDistance(playerTarget)).offset(
                        Mth.floor(getWidth(playerTarget)),
                        Mth.floor(getWidth(playerTarget)),
                        Mth.floor(getWidth(playerTarget)))).forEach(blockPos -> {
            Block blockToReplace = playerTarget.level().getBlockState(pos).getBlock();
            if (blockToReplace != Blocks.AIR && blockToReplace != Blocks.CAVE_AIR) {
                BlockState vaccumBlock = ArcanaBlocks.VACUUM_BLOCK.get().defaultBlockState();
                playerTarget.level().setBlockAndUpdate(pos, vaccumBlock);
                ((VacuumBlockEntity)playerTarget.level().getBlockEntity(pos)).setDuration(getDuration(playerTarget));
                ((VacuumBlockEntity)playerTarget.level().getBlockEntity(pos)).setOriginBlock(blockToReplace.defaultBlockState());
            }
        });
        return InteractionResult.FAIL;
    }

    @Override
    public InteractionResult useOnEntity(Player caster, Entity entityTarget) {
        //caster.sendStatusMessage(Component.translatable("status.arcana.invalid_spell"), true);
        BlockPos pos = entityTarget.getOnPos().below();
        BlockPos.betweenClosed(pos.offset(
                -Mth.floor(getWidth(caster)),
                -Mth.floor(getWidth(caster)),
                -Mth.floor(getWidth(caster))),
                pos.relative(Direction.fromYRot(caster.getYRot()), getDistance(caster)).offset(
                        Mth.floor(getWidth(caster)),
                        Mth.floor(getWidth(caster)),
                        Mth.floor(getWidth(caster)))).forEach(blockPos -> {
            Block blockToReplace = entityTarget.level().getBlockState(pos).getBlock();
            if (blockToReplace != Blocks.AIR && blockToReplace != Blocks.CAVE_AIR) {
                BlockState vaccumBlock = ArcanaBlocks.VACUUM_BLOCK.get().defaultBlockState();
                entityTarget.level().setBlockAndUpdate(pos, vaccumBlock);
                ((VacuumBlockEntity)entityTarget.level().getBlockEntity(pos)).setDuration(getDuration(caster));
                ((VacuumBlockEntity)entityTarget.level().getBlockEntity(pos)).setOriginBlock(blockToReplace.defaultBlockState());
            }
        });
        return InteractionResult.FAIL;
    }
}