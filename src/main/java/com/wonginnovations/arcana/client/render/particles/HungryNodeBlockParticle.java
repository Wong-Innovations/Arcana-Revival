package com.wonginnovations.arcana.client.render.particles;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class HungryNodeBlockParticle extends TextureSheetParticle {
	
	private final BlockState sourceState;
	private BlockPos sourcePos;
	private final float u;
	private final float v;
	
	protected HungryNodeBlockParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, BlockState state) {
		super(level, x, y, z, xSpeed, ySpeed, zSpeed);
		sourceState = state;
		xd = xd * 0.01f + xSpeed;
		yd = yd * 0.01f + ySpeed;
		zd = zd * 0.01f + zSpeed;
		x += (level.random.nextFloat() - level.random.nextFloat()) * .05f;
		y += (level.random.nextFloat() - level.random.nextFloat()) * .05f;
		z += (level.random.nextFloat() - level.random.nextFloat()) * .05f;
		rCol = .6f;
		gCol = .6f;
		bCol = .6f;
//		particleScale /= 2;
		scale(0.5f);
		u = level.random.nextFloat() * 3;
		v = level.random.nextFloat() * 3;
		lifetime = 20;
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
		else
			move(xd, yd, zd);
	}
	
	public HungryNodeBlockParticle setBlockPos(BlockPos pos) {
		updateSprite(pos);
		sourcePos = pos;
		if (sourceState.getBlock() != Blocks.GRASS_BLOCK)
			multiplyColor(pos);
		return this;
	}
	
	public HungryNodeBlockParticle init() {
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
	
	private HungryNodeBlockParticle updateSprite(BlockPos pos) {
		if (pos != null)
			setSprite(Minecraft.getInstance().getModelManager().getBlockModelShaper().getTexture(sourceState, level, pos));
		return this;
	}
	
	public static class Factory implements ParticleProvider<BlockParticleOption> {
		public Particle createParticle(BlockParticleOption type, @Nonnull ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			BlockState blockstate = type.getState();
			return !blockstate.isAir() && blockstate.getBlock() != Blocks.MOVING_PISTON ? (new HungryNodeBlockParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, blockstate)).init().updateSprite(type.getPos()) : null;
		}
	}
}