package com.wonginnovations.arcana.client.render;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.client.model.WillowEntityModel;
import com.wonginnovations.arcana.entities.SpiritEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WillowSpiritRenderer extends MobRenderer<SpiritEntity, WillowEntityModel<SpiritEntity>> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(Arcana.MODID,
            "textures/entity/willow_spirit.png");

    public WillowSpiritRenderer(EntityRendererProvider.Context context) {
        super(context, new WillowEntityModel<>(), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(SpiritEntity entity) {
        return TEXTURE;
    }
}

