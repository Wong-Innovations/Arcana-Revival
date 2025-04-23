package com.wonginnovations.arcana.entities;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import static com.wonginnovations.arcana.Arcana.arcLoc;

public class ArcanaDamageTypes {

    public static final ResourceKey<DamageType> TAINT = create("taint");

    public static ResourceKey<DamageType> create(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, arcLoc(name));
    }

    public static DamageSource getDamageSource(Level level, ResourceKey<DamageType> type) {
        return getDamageSource(level, type, null);
    }

    public static DamageSource getDamageSource(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker) {
        return getDamageSource(level, type, attacker, attacker);
    }

    public static DamageSource getDamageSource(Level level, ResourceKey<DamageType> type, @Nullable Entity attacker, @Nullable Entity causer) {
        Holder<DamageType> holder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type);
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(type), attacker, causer);
    }

    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(TAINT,new DamageType("arcana.taint", 0.0F));
    }

}
