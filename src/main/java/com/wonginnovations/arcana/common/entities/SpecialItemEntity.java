package com.wonginnovations.arcana.common.entities;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SpecialItemEntity extends ItemEntity {

    public SpecialItemEntity(EntityType<SpecialItemEntity> entityType, Level level) {
        super(entityType, level);
    }

    public SpecialItemEntity(Level level, double posX, double posY, double posZ, ItemStack itemStack) {
        this(ModEntityTypes.SPECIAL_ITEM_ENTITY.get(), level);
        this.setPos(posX, posY, posZ);
        this.setNoGravity(true);
        this.setItem(itemStack);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float damage) {
        return !source.is(DamageTypes.EXPLOSION) && super.hurt(source, damage);
    }

}
