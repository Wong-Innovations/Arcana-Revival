package com.wonginnovations.arcana.client.render;

import com.wonginnovations.arcana.entities.SpellEggEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class SpellEggEntityRenderer extends SpriteRenderer<SpellEggEntity> {
	
	public SpellEggEntityRenderer(EntityRendererProvider.Context context, ItemRenderer itemRenderer, float scale, boolean fullbright) {
		super(context, itemRenderer, scale, fullbright);
	}
	
	public SpellEggEntityRenderer(EntityRendererProvider.Context context) {
		this(context, Minecraft.getInstance().getItemRenderer(), 1, true);
	}
}