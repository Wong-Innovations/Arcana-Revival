package com.wonginnovations.arcana.client.fx;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.client.fx.particles.SparkleParticle;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Arcana.MODID);

    public static final RegistryObject<SparkleParticle.Type> SPARKLE = PARTICLE_TYPES.register("sparkle", () -> new SparkleParticle.Type(true, SparkleParticle.Options.DESERIALIZER));
    public static final RegistryObject<SimpleParticleType> SPECIAL_SPARKLE = PARTICLE_TYPES.register("special_sparkle", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }

}
