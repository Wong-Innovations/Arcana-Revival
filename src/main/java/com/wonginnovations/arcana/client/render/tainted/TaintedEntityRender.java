package com.wonginnovations.arcana.client.render.tainted;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.Arcana;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TaintedEntityRender<T extends Mob, M extends EntityModel<T>> extends MobRenderer<T, EntityModel<T>> {

	public TaintedEntityRender(EntityRendererProvider.Context context, M model) {
		super(context, model, 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return new ResourceLocation(Arcana.MODID,
				"textures/entity/"+ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).getPath()+".png");
	}
}