package com.wonginnovations.arcana.common.entities;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SpecialItemEntity extends ItemEntity {

    public SpecialItemEntity(Level level, double posX, double posY, double posZ, ItemStack itemStack) {
        super(level, posX, posY, posZ, itemStack, Math.random() * 0.20000000298023224 - 0.10000000149011612, 0.20000000298023224, Math.random() * 0.20000000298023224 - 0.10000000149011612);
    }

    @Override
    public void tick() {
        if (this.tickCount > 1) {
            Vec3 movement = this.getDeltaMovement();
            if (movement.y > 0) {
                movement.multiply(0, 0.8999999761581421, 0);
            }

            movement.add(0, 0.03999999910593033, 0);
            this.setDeltaMovement(movement);
            super.tick();
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float damage) {
        return !source.is(DamageTypes.EXPLOSION) && super.hurt(source, damage);
    }

}
