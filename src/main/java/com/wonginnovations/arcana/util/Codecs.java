package com.wonginnovations.arcana.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.Aspects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public final class Codecs{
	
	private Codecs() {}
	
	public static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);
	public static final Codec<Aspect> ASPECT_CODEC = ResourceLocation.CODEC.xmap(Aspects.ASPECTS::get, Aspects.ASPECT_IDS::get);
	
	public static final Codec<Vec3> VECTOR_3D_CODEC = RecordCodecBuilder.create(o ->
			o.group(Codec.DOUBLE.fieldOf("x")
							.forGetter(e -> e.x),
					Codec.DOUBLE.fieldOf("y")
							.forGetter(e -> e.y),
					Codec.DOUBLE.fieldOf("z")
							.forGetter(e -> e.z))
					.apply(o, Vec3::new));
}