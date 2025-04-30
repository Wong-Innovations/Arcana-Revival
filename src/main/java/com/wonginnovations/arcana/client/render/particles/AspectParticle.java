package com.wonginnovations.arcana.client.render.particles;

import com.wonginnovations.arcana.mixin.ParticleEngineAccessor;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectParticle extends TextureSheetParticle {
	
	protected AspectParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite) {
		super(level, x, y, z);
		quadSize = 0.14f;
		gravity = 0;
		lifetime = 0;
//		particleScale = .14f;
		scale(1f);
		hasPhysics = false;
		setSprite(sprite);
	}
	
	public ParticleRenderType getRenderType() {
		return NodeParticle.PASSTHROUGH_TERRAIN_SHEET;
	}
	
	protected int getLightColor(float partialTick) {
		// fullbright
		return 0xf000f0;
	}
	
	@OnlyIn(Dist.CLIENT)
	@ParametersAreNonnullByDefault
	public static class Factory implements ParticleProvider<AspectParticleData> {
		public Particle createParticle(AspectParticleData data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new AspectParticle(level, x, y, z, ((ParticleEngineAccessor) Minecraft.getInstance().particleEngine).getTextureAtlas().getSprite(data.aspectTexture));
		}
	}
}