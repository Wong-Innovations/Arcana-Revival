package com.wonginnovations.arcana.client.render.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectParticleData implements ParticleOptions {
	
	public static final Codec<AspectParticleData> CODEC = RecordCodecBuilder.create(o ->
			o.group(ResourceLocation.CODEC.fieldOf("aspectTexture")
							.forGetter(e -> e.aspectTexture))
					.apply(o, AspectParticleData::new));
	
	public static final ParticleOptions.Deserializer<AspectParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public AspectParticleData fromCommand(ParticleType<AspectParticleData> particleType, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            ResourceLocation rloc = new ResourceLocation(reader.getRemaining());
            return new AspectParticleData(rloc);
        }

        @Override
        public AspectParticleData fromNetwork(ParticleType<AspectParticleData> particleType, FriendlyByteBuf buffer) {
            return new AspectParticleData(buffer.readResourceLocation());
        }
    };

	ResourceLocation aspectTexture;
	ParticleType<AspectParticleData> type;

	public AspectParticleData(ResourceLocation aspectTexture) {
		this.type = ArcanaParticles.ASPECT_PARTICLE.get();
		this.aspectTexture = aspectTexture;
	}

	@Override
	public ParticleType<?> getType() {
		return type;
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(aspectTexture);
	}

	@Override
	public String writeToString() {
		return Objects.requireNonNull(ForgeRegistries.PARTICLE_TYPES.getKey(this.getType())) + " " + aspectTexture.toString();
	}
}
