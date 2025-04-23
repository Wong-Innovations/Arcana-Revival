package com.wonginnovations.arcana.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nonnull;

public class FrozenEffect extends MobEffect {
	
	public FrozenEffect() {
		super(MobEffectCategory.HARMFUL, 0x0055FF);
	}
	
	@Override
	public void applyEffectTick(@Nonnull LivingEntity entity, int amplifier) {
		entity.hurt(entity.damageSources().magic(), 2.0F);
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		int j = 40 >> amplifier;
		if (j > 0) {
			return duration % j == 0;
		} else {
			return true;
		}
	}
}
