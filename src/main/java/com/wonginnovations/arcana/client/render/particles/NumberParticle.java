package com.wonginnovations.arcana.client.render.particles;

import com.wonginnovations.arcana.Arcana;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
public class NumberParticle extends TextureSheetParticle {
	
	protected NumberParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
		super(level, x, y, z);
		gravity = 0;
		lifetime = 0;
//		particleScale = .04f;
		scale(.04f);
		hasPhysics = false;
		setSprite(sprite);
	}
	
	@Nonnull
	public ParticleRenderType getRenderType() {
		return NodeParticle.PASSTHROUGH_TERRAIN_SHEET;
	}
	
	protected int getLightColor(float partialTick) {
		// fullbright
		return 0xf000f0;
	}
	
	@OnlyIn(Dist.CLIENT)
	@ParametersAreNonnullByDefault
	public static class Factory implements ParticleProvider<NumberParticleData> {
		public Particle createParticle(NumberParticleData data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ResourceLocation countTex = Arcana.arcLoc("font/number_" + data.count);
			TextureAtlasSprite tas = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(countTex);
//			TextureAtlasSprite tas = Minecraft.getInstance().getAtlasSpriteGetter(TextureAtlas.LOCATION_BLOCKS).apply(countTex);
			return new NumberParticle(level, x, y, z, tas);
		}
	}
}
