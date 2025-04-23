package com.wonginnovations.arcana.systems.spell.casts.impl;

import com.wonginnovations.arcana.ArcanaVariables;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.effects.ArcanaEffects;
import com.wonginnovations.arcana.systems.spell.SpellValues;
import com.wonginnovations.arcana.systems.spell.casts.Cast;
import com.wonginnovations.arcana.world.WorldInteractions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import static com.wonginnovations.arcana.aspects.Aspects.ICE;

public class IceCast extends Cast {
	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("ice");
	}

	@Override
	public InteractionResult useOnBlock(Player caster, Level level, BlockPos blockTarget) {
		WorldInteractions.fromWorld(level).freezeBlock(blockTarget);
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget) {
		playerTarget.addEffect(new MobEffectInstance(ArcanaEffects.FROZEN.get(),getFrozenDuration(),getAmplifier(),false,false));
		playerTarget.setSecondsOnFire(-80);
		return InteractionResult.SUCCESS;
	}

	private int getAmplifier() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"secondModifier"),0);
	}

	private int getFrozenDuration() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"firstModifier"),3) * 20;
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity entityTarget) {
		if (entityTarget instanceof LivingEntity target) {
			target.addEffect(new MobEffectInstance(ArcanaEffects.FROZEN.get(), getFrozenDuration(), getAmplifier(), false, false));
			target.setSecondsOnFire(-80);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public Aspect getSpellAspect() {
		return ICE;
	}

	@Override
	public int getSpellDuration() {
		return 1;
	}
}