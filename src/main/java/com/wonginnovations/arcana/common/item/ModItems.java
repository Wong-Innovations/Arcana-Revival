package com.wonginnovations.arcana.common.item;

import com.wonginnovations.arcana.Arcana;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Arcana.MODID);

    public static final RegistryObject<Item> VIS = ITEMS.register("vis", VisCrystalItem::new);
    public static final RegistryObject<Item> PHIAL_SMALL = ITEMS.register("small_phial", SmallPhialItem::new);
    public static final RegistryObject<Item> PHIAL = ITEMS.register("phial", PhialItem::new);
    public static final RegistryObject<Item> PHIAL_LARGE = ITEMS.register("large_phial", LargePhialItem::new);
    public static final RegistryObject<Item> SALIS_MUNDUS = ITEMS.register("salis_mundus", SalisMundusItem::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
