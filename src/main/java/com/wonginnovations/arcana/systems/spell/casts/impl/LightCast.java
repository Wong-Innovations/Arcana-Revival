package com.wonginnovations.arcana.systems.spell.casts.impl;

import com.wonginnovations.arcana.ArcanaVariables;
import com.wonginnovations.arcana.util.NotImplementedException;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.systems.spell.casts.Cast;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import static com.wonginnovations.arcana.aspects.Aspects.LIGHT;

public class LightCast extends Cast {
	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("light");
	}

	@Override
	public InteractionResult useOnBlock(Player caster, Level level, BlockPos blockTarget) {
		if (level.getBlockState(blockTarget.above()).getBlock() == ArcanaBlocks.LIGHT_BLOCK.get()) {
			return InteractionResult.SUCCESS;
		}
		if (level.getBlockState(blockTarget.above()).isAir()) {
			level.setBlockAndUpdate(blockTarget.above(), ArcanaBlocks.LIGHT_BLOCK.get().defaultBlockState());
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget) {
		return placeLight(playerTarget);
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity entityTarget) {
		return placeLight(entityTarget);
	}

	public InteractionResult placeLight(Entity entityTarget) {
		if (entityTarget.level().getBlockState(entityTarget.blockPosition().above()).getBlock() == ArcanaBlocks.LIGHT_BLOCK.get()) {
			return InteractionResult.SUCCESS;
		}
		if (entityTarget.level().getBlockState(entityTarget.blockPosition().above()).isAir()) {
			entityTarget.level().setBlockAndUpdate(entityTarget.blockPosition().above(), ArcanaBlocks.LIGHT_BLOCK.get().defaultBlockState());
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	@Override
	public Aspect getSpellAspect() {
		return LIGHT;
	}

	@Override
	public int getSpellDuration() {
		return 1;
	}
}