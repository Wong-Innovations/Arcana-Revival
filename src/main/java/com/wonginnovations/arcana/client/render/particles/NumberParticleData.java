package com.wonginnovations.arcana.client.render.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NumberParticleData implements ParticleOptions {
	
	public static final Codec<NumberParticleData> CODEC = RecordCodecBuilder.create(o ->
			o.group(Codec.INT.fieldOf("count")
					.forGetter(e -> Integer.valueOf(e.count)))
					.apply(o, (c) -> new NumberParticleData((char)c.intValue())));
	
	@SuppressWarnings("deprecation")
	public static final ParticleOptions.Deserializer<NumberParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
		@Override
		public NumberParticleData fromCommand(ParticleType<NumberParticleData> particleType, StringReader reader) throws CommandSyntaxException{
			reader.expect(' ');
			char c = reader.getRemaining().charAt(0);
			return new NumberParticleData(c);
		}

		@Override
		public NumberParticleData fromNetwork(ParticleType<NumberParticleData> particleType, FriendlyByteBuf buffer) {
			return new NumberParticleData(buffer.readChar());
		}
	};
	
	char count;
	
	public NumberParticleData(char c) {
		this.count = c;
	}

	@Override
	public ParticleType<?> getType() {
		return ArcanaParticles.NUMBER_PARTICLE.get();
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeChar(count);
	}

	@Override
	public String writeToString() {
		return Objects.requireNonNull(ForgeRegistries.PARTICLE_TYPES.getKey(this.getType())).toString() + " " + count;
	}
}