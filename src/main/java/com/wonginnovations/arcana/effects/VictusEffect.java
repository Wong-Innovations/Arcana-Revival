package com.wonginnovations.arcana.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class VictusEffect extends MobEffect {
	
	protected VictusEffect() {
		super(MobEffectCategory.BENEFICIAL, 0xCD5CAB);
	}
	
	// regen amount scales with amplifier rather than rate, unlike Regeneration
	
	public void applyEffectTick(LivingEntity target, int amplifier) {
		if (target.getHealth() < target.getMaxHealth())
			target.heal(amplifier + 1);
	}
	
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return duration % 50 == 0;
	}
}