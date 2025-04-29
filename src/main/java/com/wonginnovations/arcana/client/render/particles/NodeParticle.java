package com.wonginnovations.arcana.client.render.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.wonginnovations.arcana.items.settings.GogglePriority;
import com.wonginnovations.arcana.mixin.ParticleEngineAccessor;
import com.wonginnovations.arcana.world.NodeType;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.world.ClientAuraView;
import com.wonginnovations.arcana.world.Node;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.HashMap;
import java.util.Map;

import static com.wonginnovations.arcana.client.gui.UiUtil.*;

@OnlyIn(Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NodeParticle extends TextureSheetParticle {

	private Node node;
	protected static final int time = 40;

	protected NodeParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite, @Nullable Node node) {
		super(level, x, y, z);
		quadSize = 0.1f;
		this.node = node;
		gravity = 0;
		lifetime = 0;
//		particleScale = .7f;
		scale(3f);
		hasPhysics = false;
		setSprite(sprite);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return PASSTHROUGH_TERRAIN_SHEET;
	}

	@Override
	public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
		if (node != null && time > 0 && node.getAspects().countHolders() > 0) {
			// get current and next aspect
			//TextureAtlasSprite tex = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(node.type().texture(level, new ClientAuraView(level), node));
			try {
				Aspect current = node.getAspects().getHolder(((int)(level.getGameTime() + pPartialTicks) / time) % node.getAspects().countHolders()).getStack().getAspect();
				Aspect next = node.getAspects().getHolder(((int)(level.getGameTime() + pPartialTicks) / time + 1) % node.getAspects().countHolders()).getStack().getAspect();
				// get progress between them
				float progress = (((int)level.getGameTime() + pPartialTicks) / (float)time) % 1;
				// set color to blended
				int blended = blend(0xffffff, blend(next.getColorRange().get(3), current.getColorRange().get(3), progress), .3f);
				rCol = red(blended) / 255f;
				gCol = green(blended) / 255f;
				bCol = blue(blended) / 255f;
			} catch (ArithmeticException arithmeticException) {
				Arcana.LOGGER.error("{} at: [{}@{}@{}]", arithmeticException, x, y, z);
			}
		}
		super.render(pBuffer, pRenderInfo, pPartialTicks);
	}

	@Override
	protected int getLightColor(float partialTick) {
		// fullbright
		return 0xf000f0;
	}

	// TODO: appears behind clouds?
	public static final ParticleRenderType PASSTHROUGH_TERRAIN_SHEET = new ParticleRenderType() {
		@Override
		public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.depthMask(false);
			RenderSystem.disableDepthTest();
			RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
			bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
		}

		@Override
		public void end(Tesselator tesselator) {
			tesselator.end();
			RenderSystem.enableDepthTest();
		}

		public String toString() {
			return "arcana:PASSTHROUGH_TERRAIN_SHEET";
		}
	};

	@OnlyIn(Dist.CLIENT)
	@ParametersAreNonnullByDefault
	public static class Factory implements ParticleProvider<NodeParticleData> {
		@Override
		public Particle createParticle(NodeParticleData data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new NodeParticle(level, x, y, z, ((ParticleEngineAccessor) Minecraft.getInstance().particleEngine).getTextureAtlas().getSprite(data.nodeTexture), new ClientAuraView(level).getNodeByUuid(data.node).orElse(null));
		}
	}
}