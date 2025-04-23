package com.wonginnovations.arcana.entities.tainted;

import com.wonginnovations.arcana.effects.ArcanaEffects;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TaintedCaveSpiderEntity extends CaveSpider {
	public TaintedCaveSpiderEntity(EntityType<? extends CaveSpider> type, Level levelIn) {
		super(type, levelIn);
	}

	public static AttributeSupplier.Builder createTaintedCaveSpider() {
		return Spider.createAttributes().add(Attributes.MAX_HEALTH, 18.0D);
	}

	public boolean doHurtTarget(@NotNull Entity entityIn) {
		if (super.doHurtTarget(entityIn)) {
			if (entityIn instanceof LivingEntity) {
				int i = 0;
				if (this.level().getDifficulty() == Difficulty.NORMAL) {
					i = 7;
				} else if (this.level().getDifficulty() == Difficulty.HARD) {
					i = 15;
				}

				if (i > 0) {
					((LivingEntity)entityIn).addEffect(new MobEffectInstance(ArcanaEffects.TAINTED.get(), i * 20, 1));
				}
			}

			return true;
		} else {
			return false;
		}
	}
}
