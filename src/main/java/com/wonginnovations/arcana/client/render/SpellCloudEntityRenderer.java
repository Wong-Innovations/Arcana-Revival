package com.wonginnovations.arcana.client.render;

import com.wonginnovations.arcana.entities.SpellCloudEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class SpellCloudEntityRenderer extends EntityRenderer<SpellCloudEntity>{
	public SpellCloudEntityRenderer(EntityRendererProvider.Context context) {
		super(context);
	}
	
	/**
	 * Returns the location of an entity's texture.
	 */
	@SuppressWarnings("deprecation")
	@Nonnull
	public ResourceLocation getTextureLocation(@Nonnull SpellCloudEntity entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}