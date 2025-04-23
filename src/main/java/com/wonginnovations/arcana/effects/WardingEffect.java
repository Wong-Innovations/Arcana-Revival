package com.wonginnovations.arcana.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class WardingEffect extends MobEffect {
	
	public WardingEffect() {
		super(MobEffectCategory.BENEFICIAL, 0x3255FF);
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
