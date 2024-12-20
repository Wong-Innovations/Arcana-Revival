package com.wonginnovations.arcana.common.entities;

import com.wonginnovations.arcana.Arcana;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Arcana.MODID);

    public static final RegistryObject<EntityType<SpecialItemEntity>> SPECIAL_ITEM_ENTITY = ENTITY_TYPES.register("special_item_entity",
            () -> EntityType.Builder.<SpecialItemEntity>of(SpecialItemEntity::new, MobCategory.MISC)
                    .clientTrackingRange(64).updateInterval(20).setShouldReceiveVelocityUpdates(true).noSummon().build("special_item_entity"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
