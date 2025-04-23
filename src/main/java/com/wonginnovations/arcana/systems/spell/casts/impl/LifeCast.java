package com.wonginnovations.arcana.systems.spell.casts.impl;

import com.wonginnovations.arcana.ArcanaVariables;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.effects.ArcanaEffects;
import com.wonginnovations.arcana.systems.spell.*;
import com.wonginnovations.arcana.systems.spell.casts.Cast;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class LifeCast extends Cast {

	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("life");
	}
	
	@Override
	public Aspect getSpellAspect() {
		return Aspects.LIFE;
	}

	@Override
	public int getSpellDuration() {
		return 1;
	}

	public int getVictusDuration() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"firstModifier"), 10);
	}

	public int getAmplifier() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"secondModifier"), 1);
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity targetEntity) {
		if (targetEntity instanceof LivingEntity)
			((LivingEntity)targetEntity).addEffect(new MobEffectInstance(ArcanaEffects.VICTUS.get(),getVictusDuration(),getAmplifier(),false,false));
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOnBlock(Player caster, Level level, BlockPos blockTarget) {

		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget) {
		playerTarget.addEffect(new MobEffectInstance(ArcanaEffects.VICTUS.get(),getVictusDuration(),getAmplifier(),false,false));
		return InteractionResult.SUCCESS;
	}
}