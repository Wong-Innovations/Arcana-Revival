package com.wonginnovations.arcana.event;

import com.wonginnovations.arcana.entities.ArcanaEntities;
import com.wonginnovations.arcana.entities.KoalaEntity;
import com.wonginnovations.arcana.entities.SpiritEntity;
import com.wonginnovations.arcana.entities.tainted.TaintedEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = "arcana")
public class GlobalEntity {
	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		ArcanaEntities.T_ENTITY_TYPES.getEntries().forEach(regobj -> {
			if (regobj.isPresent()) {
				event.put((EntityType<? extends LivingEntity>)regobj.get(), TaintedEntity.createLivingAttributes().build());
			}
		});
		event.put(ArcanaEntities.KOALA_ENTITY.get(), KoalaEntity.createLivingAttributes().build());
		event.put(ArcanaEntities.DAIR_SPIRIT.get(), SpiritEntity.createLivingAttributes().build());
		event.put(ArcanaEntities.WILLOW_SPIRIT.get(), SpiritEntity.createLivingAttributes().build());
	}
}