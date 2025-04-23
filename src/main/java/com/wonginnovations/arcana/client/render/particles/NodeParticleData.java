package com.wonginnovations.arcana.client.render.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.util.Codecs;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NodeParticleData implements ParticleOptions {
	
	public static final Codec<NodeParticleData> CODEC = RecordCodecBuilder.create(o ->
			o.group(Codecs.UUID_CODEC.fieldOf("node")
						.forGetter(e -> e.node),
					ResourceLocation.CODEC.fieldOf("nodeTexture")
						.forGetter(e -> e.nodeTexture))
				.apply(o, NodeParticleData::new));
	
	public static final ParticleOptions.Deserializer<NodeParticleData> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        @Override
        public NodeParticleData fromCommand(ParticleType<NodeParticleData> particleType, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            UUID uuid = UUID.fromString(reader.readStringUntil(' '));
            ResourceLocation rloc = new ResourceLocation(reader.getRemaining());
            return new NodeParticleData(uuid, rloc);
        }

        @Override
        public NodeParticleData fromNetwork(ParticleType<NodeParticleData> particleType, FriendlyByteBuf buffer) {
            return new NodeParticleData(buffer.readUUID(), buffer.readResourceLocation());
        }
    };
	
	UUID node;
	ResourceLocation nodeTexture;
	ParticleType<NodeParticleData> type;
	
	public NodeParticleData(UUID node, ResourceLocation nodeTexture) {
		super();
		this.node = node;
		this.type = ArcanaParticles.NODE_PARTICLE.get();
		this.nodeTexture = nodeTexture;
	}

	@Override
	public ParticleType<?> getType() {
		return type;
	}

	@Override
	public void writeToNetwork(FriendlyByteBuf buffer) {
		buffer.writeUUID(node);
		buffer.writeResourceLocation(nodeTexture);
	}

	@Override
	public String writeToString() {
		return Objects.requireNonNull(ForgeRegistries.PARTICLE_TYPES.getKey(this.getType())) + " " + node.toString() + " " + nodeTexture.toString();
	}
}