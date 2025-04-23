package com.wonginnovations.arcana.client.render;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.client.model.KoalaEntityModel;
import com.wonginnovations.arcana.entities.KoalaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class KoalaEntityRenderer extends MobRenderer<KoalaEntity, KoalaEntityModel<KoalaEntity>> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(Arcana.MODID,
            "textures/entity/koala.png");

    public KoalaEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new KoalaEntityModel<>(), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(KoalaEntity entity) {
        return TEXTURE;
    }
}
