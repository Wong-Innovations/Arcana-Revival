package com.wonginnovations.arcana.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.blocks.entities.JarBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JarBlockEntityRender implements BlockEntityRenderer<JarBlockEntity> {
	public static final ResourceLocation JAR_CONTENT_TOP = new ResourceLocation(Arcana.MODID, "block/models/parts/fluid_top");
	public static final ResourceLocation JAR_CONTENT_SIDE = new ResourceLocation(Arcana.MODID, "block/models/parts/fluid_side");
	public static final ResourceLocation JAR_CONTENT_BOTTOM = new ResourceLocation(Arcana.MODID, "block/models/parts/fluid_bottom");
	public static final ResourceLocation JAR_LABEL = new ResourceLocation(Arcana.MODID, "block/models/parts/jar_label");

	public JarBlockEntityRender(BlockEntityRendererProvider.Context context) {
	}
	
	private void add(VertexConsumer buffer, PoseStack stack, Color color, float x, float y, float z, float u, float v, int lightmap) {
		buffer.vertex(stack.last().pose(), x, y, z)
				.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f)
				.uv(u, v)
				.uv2(lightmap)
				.normal(1, 0, 0)
				.endVertex();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(JarBlockEntity tileEntity, float partialTicks, PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

		TextureAtlasSprite spriteTop = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(JAR_CONTENT_TOP);
		TextureAtlasSprite spriteSide = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(JAR_CONTENT_SIDE);
		TextureAtlasSprite spriteBottom = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(JAR_CONTENT_BOTTOM);
		TextureAtlasSprite label = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(JAR_LABEL);
		TextureAtlasSprite aspect = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(tileEntity.getPaperAspectLocation());
		VertexConsumer builder = bufferSource.getBuffer(RenderType.translucent());
		VertexConsumer label_builder = bufferSource.getBuffer(RenderType.cutout());
		
		// 0-100
		float visAmount = ArcanaConfig.NO_JAR_ANIMATION.get() ? tileEntity.vis.getHolder(0).getStack().getAmount() : (float) tileEntity.getClientVis(partialTicks);
		Color aspectColor = tileEntity.getAspectColor();
		Direction labelSide = tileEntity.getLabelSide();
		
		float visScaled = visAmount / 90;
		float visBase = -.3f;
		float visHeight = visScaled + visBase;

		float scale = 0.4f;

		// label
		if (labelSide != null) {
			poseStack.pushPose();

			Quaternionf q;
			float xt, zt;
			float xta, yta = -0.68f, zta;

			switch (labelSide) {
				case NORTH:
					q = Axis.YP.rotationDegrees(-90);
					xt = 0.45f;
					zt = -1.75f;
					xta = -0.02f;
					yta = -0.90f;
					zta = 0.1f;
					break;
				case SOUTH:
					q = Axis.YP.rotationDegrees(90);
					xt = -2.05f;
					zt = 0.845f;
					xta = -0.02f;
					yta = -0.90f;
					zta = 0.1f;
					break;
				case WEST:
					q = new Quaternionf();
					xt = 0.45f;
					zt = 0.85f;
					xta = -0.02f;
					yta = -0.90f;
					zta = 0.1f;
					break;
				case EAST:
					q = Axis.YP.rotationDegrees(180);
					xt = -2.05f;
					zt = -1.75f;
					xta = -0.02f;
					yta = -0.90f;
					zta = 0.1f;
					break;
				default:
					q = new Quaternionf();
					xt = 0;
					zt = 0;
					xta = 0;
					zta = 0;
					break;
			}

			poseStack.scale(scale, scale, scale);
			poseStack.mulPose(q);
			poseStack.translate(xt, .5f, zt);
			
			q = Axis.XP.rotationDegrees(tileEntity.label.renderRotation);
			poseStack.mulPose(q);
			poseStack.translate(0,0,-(tileEntity.label.renderRotation/200f));

			add(label_builder, poseStack, Color.WHITE, 0, 1, 0, label.getU0(), label.getV1(), packedLight);
			add(label_builder, poseStack, Color.WHITE, 0, 0, 0, label.getU1(), label.getV1(), packedLight);
			add(label_builder, poseStack, Color.WHITE, 0, 0, 1, label.getU1(), label.getV0(), packedLight);
			add(label_builder, poseStack, Color.WHITE, 0, 1, 1, label.getU0(), label.getV0(), packedLight);

//			q = new Quaternionf(180,180,0,true); // TODO: there has to be a way to do this using Axis and only applying once
			poseStack.translate(0, 1, 0);
			poseStack.mulPose(Axis.XP.rotationDegrees(180));
			poseStack.mulPose(Axis.YP.rotationDegrees(180));
			add(label_builder, poseStack, Color.WHITE, 0, 1, 0, label.getU0(), label.getV1(), packedLight);
			add(label_builder, poseStack, Color.WHITE, 0, 0, 0, label.getU1(), label.getV1(), packedLight);
			add(label_builder, poseStack, Color.WHITE, 0, 0, 1, label.getU1(), label.getV0(), packedLight);
			add(label_builder, poseStack, Color.WHITE, 0, 1, 1, label.getU0(), label.getV0(), packedLight);
			poseStack.mulPose(Axis.XP.rotationDegrees(180));
			poseStack.mulPose(Axis.YP.rotationDegrees(180));
			poseStack.translate(0, -1, 0);

			q = Axis.XP.rotationDegrees(-90);
			poseStack.mulPose(q);
			poseStack.translate(xta,yta,zta);

			scale = 0.8f;
			poseStack.scale(scale, scale, scale);
			poseStack.translate(-.002, -.002, -.002);
			add(label_builder, poseStack, Color.WHITE, 0, 1, 0, aspect.getU0(), aspect.getV1(), packedLight);
			add(label_builder, poseStack, Color.WHITE, 0, 0, 0, aspect.getU1(), aspect.getV1(), packedLight);
			add(label_builder, poseStack, Color.WHITE, 0, 0, 1, aspect.getU1(), aspect.getV0(), packedLight);
			add(label_builder, poseStack, Color.WHITE, 0, 1, 1, aspect.getU0(), aspect.getV0(), packedLight);

			poseStack.popPose();
		}

		scale = 0.5f;
		if (visScaled > 0) {
			poseStack.pushPose();
			poseStack.scale(scale, scale, scale);
			poseStack.translate(.5, .5, .5);

			Color c = new Color(aspectColor.getRGB()-0x80000000);
			
			// top
			add(builder, poseStack, c, 0, visHeight, 1, spriteTop.getU0(), spriteTop.getV1(), packedLight);
			add(builder, poseStack, c, 1, visHeight, 1, spriteTop.getU1(), spriteTop.getV1(), packedLight);
			add(builder, poseStack, c, 1, visHeight, 0, spriteTop.getU1(), spriteTop.getV0(), packedLight);
			add(builder, poseStack, c, 0, visHeight, 0, spriteTop.getU0(), spriteTop.getV0(), packedLight);
			
			// bottom
			add(builder, poseStack, c, 1, visBase, 1, spriteBottom.getU1(), spriteBottom.getV1(), packedLight);
			add(builder, poseStack, c, 0, visBase, 1, spriteBottom.getU0(), spriteBottom.getV1(), packedLight);
			add(builder, poseStack, c, 0, visBase, 0, spriteBottom.getU0(), spriteBottom.getV0(), packedLight);
			add(builder, poseStack, c, 1, visBase, 0, spriteBottom.getU1(), spriteBottom.getV0(), packedLight);
			
			// east (+X) face
			add(builder, poseStack, c, 1, visHeight, 0, spriteSide.getU0(), spriteSide.getV0(), packedLight);
			add(builder, poseStack, c, 1, visHeight, 1, spriteSide.getU1(), spriteSide.getV0(), packedLight);
			add(builder, poseStack, c, 1, visBase, 1, spriteSide.getU1(), spriteSide.getV1(), packedLight);
			add(builder, poseStack, c, 1, visBase, 0, spriteSide.getU0(), spriteSide.getV1(), packedLight);
			
			// west (-X) face
			add(builder, poseStack, c, 0, visHeight, 0, spriteSide.getU0(), spriteSide.getV0(), packedLight);
			add(builder, poseStack, c, 0, visBase, 0, spriteSide.getU0(), spriteSide.getV1(), packedLight);
			add(builder, poseStack, c, 0, visBase, 1, spriteSide.getU1(), spriteSide.getV1(), packedLight);
			add(builder, poseStack, c, 0, visHeight, 1, spriteSide.getU1(), spriteSide.getV0(), packedLight);
			
			// north (-Z) face
			add(builder, poseStack, c, 1, visBase, 0, spriteSide.getU1(), spriteSide.getV1(), packedLight);
			add(builder, poseStack, c, 0, visBase, 0, spriteSide.getU0(), spriteSide.getV1(), packedLight);
			add(builder, poseStack, c, 0, visHeight, 0, spriteSide.getU0(), spriteSide.getV0(), packedLight);
			add(builder, poseStack, c, 1, visHeight, 0, spriteSide.getU1(), spriteSide.getV0(), packedLight);
			
			// north (+Z) face
			add(builder, poseStack, c, 0, visBase, 1, spriteSide.getU0(), spriteSide.getV1(), packedLight);
			add(builder, poseStack, c, 1, visBase, 1, spriteSide.getU1(), spriteSide.getV1(), packedLight);
			add(builder, poseStack, c, 1, visHeight, 1, spriteSide.getU1(), spriteSide.getV0(), packedLight);
			add(builder, poseStack, c, 0, visHeight, 1, spriteSide.getU0(), spriteSide.getV0(), packedLight);

			poseStack.popPose();
		}
	}
}