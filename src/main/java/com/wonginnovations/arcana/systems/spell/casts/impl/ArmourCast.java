package com.wonginnovations.arcana.systems.spell.casts.impl;

import com.wonginnovations.arcana.ArcanaVariables;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.blocks.entities.WardenedBlockBlockEntity;
import com.wonginnovations.arcana.effects.ArcanaEffects;
import com.wonginnovations.arcana.systems.spell.*;
import com.wonginnovations.arcana.systems.spell.casts.Cast;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ArmourCast extends Cast {

	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("armour");
	}

	@Override
	public Aspect getSpellAspect() {
		return Aspects.ARMOUR;
	}

	@Override
	public int getSpellDuration() {
		return 1;
	}

	public int getWardingDuration() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"firstModifier"), 10);
	}

	public int getAmplifier() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"secondModifier"), 1);
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity targetEntity) {
		if (targetEntity instanceof LivingEntity)
			((LivingEntity)targetEntity).addEffect(new MobEffectInstance(ArcanaEffects.WARDING.get(),getWardingDuration(),getAmplifier(),false,false));
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOnBlock(Player caster, Level level, BlockPos blockTarget) {
		if (level.isClientSide) return InteractionResult.SUCCESS;
		Block previousState = level.getBlockState(blockTarget).getBlock();
		if (previousState != ArcanaBlocks.WARDENED_BLOCK.get()) {
			level.setBlockAndUpdate(blockTarget, ArcanaBlocks.WARDENED_BLOCK.get().defaultBlockState());
			((WardenedBlockBlockEntity) level.getBlockEntity(blockTarget)).setState(Optional.of(previousState.defaultBlockState()));
		} else {
			level.setBlockAndUpdate(blockTarget, ((WardenedBlockBlockEntity) level.getBlockEntity(blockTarget)).getState().orElse(Blocks.AIR.defaultBlockState()));
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget) {
		playerTarget.addEffect(new MobEffectInstance(ArcanaEffects.WARDING.get(),getWardingDuration(),getAmplifier(),false,false));
		return InteractionResult.SUCCESS;
	}
}