package com.wonginnovations.arcana.client.render.particles;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.util.LocalAxis;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectHelixParticle extends TextureSheetParticle {
	
	private float time;
	private final SpriteSet spriteSheet;
	private final Vec3 direction;
	
	protected AspectHelixParticle(ClientLevel level, double x, double y, double z, SpriteSet spriteSheet, AspectHelixParticleData data) {
		super(level, x, y, z);
		this.spriteSheet = spriteSheet;
		setSpriteFromAge(spriteSheet);
		lifetime = data.getLife();
		time = data.getTime();
		direction = data.getDirection();
		hasPhysics = false;
		xd = direction.x * .05;
		yd = direction.y * .05;
		zd = direction.z * .05;
		if (data.getAspect() != null) {
			int color = data.getAspect().getColorRange().get(0);
			int r = (color & 0xff0000) >> 16;
			int g = (color & 0xff00) >> 8;
			int b = color & 0xff;
			rCol = Math.min(r / 127f, 1);
			gCol = Math.min(g / 127f, 1);
			bCol = Math.min(b / 127f, 1);
		}
		float scale = level.random.nextFloat() * 0.3f + 0.7f;
		setSize(0.02F * scale, 0.02F * scale);
//		particleScale *= level.random.nextFloat() * 0.3f + 0.7f;
	}
	
	public void tick() {
		setSpriteFromAge(spriteSheet);
		xo = x;
		yo = y;
		zo = z;
		if (age++ >= lifetime)
			remove();
		else {
			float f = .05f;
			float x1 = f * Mth.cos(time);
			float z1 = f * Mth.sin(time);
			
			// FIXME: this, uhh, doesn't work, I'm working on it
			Vec3 motion = LocalAxis.toAbsolutePos(new Vec3(x1, f, z1), direction, Vec3.ZERO);
			move(motion.x, motion.y, motion.z);
			
			time += .2f;
		}
	}
	
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}
	
	public static class Factory implements ParticleProvider<AspectHelixParticleData> {
		private final SpriteSet spriteSet;
		
		public Factory(SpriteSet sheet) {
			this.spriteSet = sheet;
		}
		
		public Particle createParticle(AspectHelixParticleData data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new AspectHelixParticle(level, x, y, z, spriteSet, data);
		}
	}
}