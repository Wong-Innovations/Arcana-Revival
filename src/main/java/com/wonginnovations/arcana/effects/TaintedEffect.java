package com.wonginnovations.arcana.effects;

import com.wonginnovations.arcana.entities.ArcanaDamageTypes;
import com.wonginnovations.arcana.entities.TaintedGooWrapper;
import com.wonginnovations.arcana.systems.taint.Taint;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.NotNull;

public class TaintedEffect extends MobEffect {

	public TaintedEffect() {
		super(MobEffectCategory.HARMFUL, 0xa200ff);
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		if (!Taint.isTainted(entity.getType())) {
			entity.hurt(ArcanaDamageTypes.getDamageSource(entity.level(), ArcanaDamageTypes.TAINT), 1.0F + amplifier);
			if ((entity.getHealth() <= (entity.getMaxHealth() / 4f) || entity.getHealth() == 1)
					&& entity.level().getDifficulty() != Difficulty.PEACEFUL) {
				changeEntityToTainted(entity);
			}
		}
	}
	
	private void changeEntityToTainted(LivingEntity entityLiving) {
		if (!(entityLiving instanceof Player) && Taint.getTaintedOfEntity(entityLiving.getType()) != null) {
			LivingEntity l = (LivingEntity)Taint.getTaintedOfEntity(entityLiving.getType()).create(entityLiving.level());
			if (l != null) {
				l.absMoveTo(entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), entityLiving.getYRot(), entityLiving.getXRot());
				if (!l.level().isClientSide) {
					l.level().addFreshEntity(l);
				}
				entityLiving.remove(Entity.RemovalReason.DISCARDED);
			}
		}
	}
	
	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		// Same rate as Regeneration
		int k = 50 >> amplifier;
		if (k > 0) {
			return duration % k == 0;
		} else {
			return true;
		}
	}

	@Override
	public void removeAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
		super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
		((TaintedGooWrapper) pLivingEntity).setGooTicks(0);
	}
}