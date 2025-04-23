package com.wonginnovations.arcana.client.render.particles;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class HungryNodeDiscParticle extends TextureSheetParticle {
	
	private final BlockState sourceState;
	private BlockPos sourcePos;
	private final float u;
	private final float v;
	
	protected HungryNodeDiscParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, BlockState state) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
		sourceState = state;
		xd = xd * 0.01f + xSpeed;
		yd = yd * 0.01f + ySpeed;
		zd = zd * 0.01f + zSpeed;
		this.x += (level.random.nextFloat() - level.random.nextFloat()) * .05f;
		this.y += (level.random.nextGaussian()) * .2f;
		this.z += (level.random.nextFloat() - level.random.nextFloat()) * .05f;
		rCol = .6f;
		gCol = .6f;
		bCol = .6f;
//		particleScale /= 2;
		scale(0.5f);
		u = level.random.nextFloat() * 3;
		v = level.random.nextFloat() * 3;
		lifetime = 120;
	}
	
	@Nonnull
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.TERRAIN_SHEET;
	}
	
	public void move(double x, double y, double z) {
		setBoundingBox(getBoundingBox().move(x, y, z));
		setLocationFromBoundingbox();
	}
	
	public void tick() {
		xo = x;
		yo = y;
		zo = z;
		if (age++ >= lifetime)
			remove();
		else {
			float time = age / 6f;
			// TODO: allow off-axis rings
			move(Mth.cos(time) * xd, yd, Mth.sin(time) * zd);
		}
	}
	
	public HungryNodeDiscParticle setBlockPos(BlockPos pos) {
		updateSprite(pos);
		sourcePos = pos;
		if (sourceState.getBlock() != Blocks.GRASS_BLOCK)
			multiplyColor(pos);
		return this;
	}
	
	public HungryNodeDiscParticle init() {
		sourcePos = BlockPos.containing(x, y, z);
		Block block = sourceState.getBlock();
		if (block != Blocks.GRASS_BLOCK)
			multiplyColor(sourcePos);
		return this;
	}
	
	protected void multiplyColor(@Nullable BlockPos pos) {
		int i = Minecraft.getInstance().getBlockColors().getColor(sourceState, level, pos, 0);
		rCol *= (float)(i >> 16 & 255) / 255f;
		gCol *= (float)(i >> 8 & 255) / 255f;
		bCol *= (float)(i & 255) / 255f;
	}
	
	protected float getU0() {
		return sprite.getU((u + 1) / 4f * 16f);
	}
	
	protected float getU1() {
		return sprite.getU(u / 4f * 16f);
	}
	
	protected float getV0() {
		return sprite.getV(v / 4f * 16f);
	}
	
	protected float getV1() {
		return sprite.getV((v + 1) / 4f * 16f);
	}
	
	private HungryNodeDiscParticle updateSprite(BlockPos pos) {
		if (pos != null)
			setSprite(Minecraft.getInstance().getModelManager().getBlockModelShaper().getTexture(sourceState, level, pos));
		return this;
	}
	
	public static class Factory implements ParticleProvider<BlockParticleOption> {
		public Particle createParticle(BlockParticleOption options, @Nonnull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			BlockState blockstate = options.getState();
			return !blockstate.isAir() && blockstate.getBlock() != Blocks.MOVING_PISTON ? (new HungryNodeDiscParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, blockstate)).init().updateSprite(options.getPos()) : null;
		}
	}
}