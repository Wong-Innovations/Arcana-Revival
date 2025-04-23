package com.wonginnovations.arcana.client.render;

import com.wonginnovations.arcana.entities.TaintBottleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class TaintBottleEntityRenderer extends SpriteRenderer<TaintBottleEntity>{
	
	public TaintBottleEntityRenderer(EntityRendererProvider.Context context, ItemRenderer itemRenderer, float scale, boolean fullbright) {
		super(context, itemRenderer, scale, fullbright);
	}
	
	public TaintBottleEntityRenderer(EntityRendererProvider.Context context) {
		this(context, Minecraft.getInstance().getItemRenderer(), 1, true);
	}
}