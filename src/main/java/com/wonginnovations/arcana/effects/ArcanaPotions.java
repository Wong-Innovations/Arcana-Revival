package com.wonginnovations.arcana.effects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

public class ArcanaPotions {
	public static final Potion TAINT = new Potion(new MobEffectInstance(ArcanaEffects.TAINTED.get(),80));
}
