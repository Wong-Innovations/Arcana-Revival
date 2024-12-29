package arcana.common.world;

import arcana.Arcana;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, Arcana.MODID);

    public static final RegistryObject<CrystalFeature> CRYSTAL_FEATURE = FEATURES.register("crystal", CrystalFeature::new);

    public static void register(IEventBus bus) {
        FEATURES.register(bus);
    }
}
