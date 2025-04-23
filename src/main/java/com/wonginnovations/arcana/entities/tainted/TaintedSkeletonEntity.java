package com.wonginnovations.arcana.entities.tainted;

import com.wonginnovations.arcana.effects.ArcanaPotions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TaintedSkeletonEntity extends Skeleton {
	public TaintedSkeletonEntity(EntityType<? extends Skeleton> type, Level level) {
		super(type, level);
	}

	@Override
	protected void dropCustomDeathLoot(@NotNull DamageSource pSource, int pLooting, boolean pRecentlyHit) {
		super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
		Entity entity = pSource.getEntity();
		if (entity instanceof Creeper creeperentity) {
            if (creeperentity.canDropMobsSkull()) {
				creeperentity.increaseDroppedSkulls();
				this.spawnAtLocation(Items.SKELETON_SKULL);
			}
		}
	}

	@Override
	protected @NotNull AbstractArrow getArrow(@NotNull ItemStack pArrowStack, float pVelocity) {
		return super.getArrow(PotionUtils.setCustomEffects(new ItemStack(Items.TIPPED_ARROW,3), ArcanaPotions.TAINT.getEffects()), pVelocity);
	}
}
