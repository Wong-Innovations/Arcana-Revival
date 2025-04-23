package com.wonginnovations.arcana.client.render.particles;

import com.mojang.serialization.Codec;
import com.wonginnovations.arcana.Arcana;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class ArcanaParticles{
	
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Arcana.MODID);
	
	public static final RegistryObject<ParticleType<NodeParticleData>> NODE_PARTICLE = PARTICLE_TYPES.register("node_particle", () -> create(NodeParticleData.DESERIALIZER, __ -> NodeParticleData.CODEC));
	public static final RegistryObject<ParticleType<AspectParticleData>> ASPECT_PARTICLE = PARTICLE_TYPES.register("aspect_particle", () -> create(AspectParticleData.DESERIALIZER, __ -> AspectParticleData.CODEC));
	public static final RegistryObject<ParticleType<AspectHelixParticleData>> ASPECT_HELIX_PARTICLE = PARTICLE_TYPES.register("aspect_helix_particle", () -> create(AspectHelixParticleData.DESERIALIZER, __ -> AspectHelixParticleData.CODEC));
	public static final RegistryObject<ParticleType<NumberParticleData>> NUMBER_PARTICLE = PARTICLE_TYPES.register("number_particle", () -> create(NumberParticleData.DESERIALIZER, __ -> NumberParticleData.CODEC));
	public static final RegistryObject<ParticleType<BlockParticleOption>> HUNGRY_NODE_BLOCK_PARTICLE = PARTICLE_TYPES.register("hungry_node_block_particle", () -> create(BlockParticleOption.DESERIALIZER, BlockParticleOption::codec));
	public static final RegistryObject<ParticleType<BlockParticleOption>> HUNGRY_NODE_DISC_PARTICLE = PARTICLE_TYPES.register("hungry_node_disc_particle", () -> create(BlockParticleOption.DESERIALIZER, BlockParticleOption::codec));
	
	@SuppressWarnings("deprecation")
	private static <T extends ParticleOptions> ParticleType<T> create(BlockParticleOption.Deserializer<T> deserializer, final Function<ParticleType<T>, Codec<T>> codec) {
		return new ParticleType<>(true, deserializer) {
            @Nonnull
            public Codec<T> codec() {
                return codec.apply(this);
            }
        };
	}
}