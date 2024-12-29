package arcana.common.entities;

import arcana.Arcana;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Arcana.MODID);

    public static final RegistryObject<EntityType<EntitySpecialItem>> SPECIAL_ITEM = ENTITY_TYPES.register("special_item",
            () -> EntityType.Builder.<EntitySpecialItem>of(EntitySpecialItem::new, MobCategory.MISC)
            .clientTrackingRange(64).updateInterval(20).setShouldReceiveVelocityUpdates(true).noSummon().build("special_item_entity"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
