package arcana;

import arcana.client.gui.ModMenuTypes;
import arcana.common.blockentities.ModBlockEntities;
import arcana.common.blocks.ModBlocks;
import arcana.common.config.ModRecipes;
import arcana.common.items.ModItems;
import arcana.common.lib.ModCreativeModeTabs;
import arcana.common.lib.ModSounds;
import arcana.common.entities.ModEntityTypes;
import arcana.common.world.ModFeatures;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import arcana.common.config.ModConfig;
import arcana.proxies.ClientProxy;
import arcana.proxies.IProxy;
import arcana.proxies.ServerProxy;

@Mod(Arcana.MODID)
public class Arcana {
    public static final String MODID = "arcana";
    public static IProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static final Logger log = LogManager.getLogger(MODID.toUpperCase());

    public Arcana() {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();
        ModConfig.register(modLoadingContext);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModFeatures.register(modEventBus);
//        ModConfiguredFeatures.register(modEventBus);
//        ModPlacedFeatures.register(modEventBus);

        modEventBus.addListener(Arcana.proxy::preInit);
        modEventBus.addListener(Arcana.proxy::init);
        modEventBus.addListener(Arcana.proxy::postInit);
    }
}
