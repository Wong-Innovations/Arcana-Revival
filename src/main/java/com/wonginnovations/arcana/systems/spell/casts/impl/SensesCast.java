package com.wonginnovations.arcana.systems.spell.casts.impl;

import com.wonginnovations.arcana.ArcanaVariables;
import com.wonginnovations.arcana.util.NotImplementedException;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.systems.spell.casts.Cast;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import static com.wonginnovations.arcana.aspects.Aspects.SENSES;

public class SensesCast extends Cast {
	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("senses");
	}

	@Override
	public InteractionResult useOnBlock(Player caster, Level level, BlockPos blockTarget) {
		throw new NotImplementedException();
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget) {
		throw new NotImplementedException();
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity entityTarget) {
		throw new NotImplementedException();
	}

	@Override
	public Aspect getSpellAspect() {
		return SENSES;
	}

	@Override
	public int getSpellDuration() {
		return 1;
	}
}