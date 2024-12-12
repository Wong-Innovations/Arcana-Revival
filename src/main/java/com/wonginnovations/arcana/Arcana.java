package com.wonginnovations.arcana;

import com.mojang.logging.LogUtils;
import com.wonginnovations.arcana.client.ClientProxy;
import com.wonginnovations.arcana.client.fx.ModParticles;
import com.wonginnovations.arcana.common.CommonProxy;
import com.wonginnovations.arcana.common.ModRecipes;
import com.wonginnovations.arcana.common.ModSounds;
import com.wonginnovations.arcana.common.item.ModCreativeModeTabs;
import com.wonginnovations.arcana.common.item.ModItems;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Arcana.MODID)
public class Arcana
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "arcana";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static CommonProxy proxy;

    public Arcana()
    {
        proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);

        ModParticles.register(modEventBus);

        ModRecipes.initializeDustInteractions();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
//        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
//            event.accept(ModItems.VIS);
//        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            ItemColors aspectColors = event.getItemColors();

            aspectColors.register((stack, tintIndex) -> {
                int color = stack.getOrCreateTag().getInt("color");
                return color != 0 ? color : 0xFFFFFF; // Default to white
            }, ModItems.VIS.get());

            aspectColors.register((ItemStack stack, int tintIndex) -> {
                if (tintIndex == 2) return 0x9DB9CE;
                else if (tintIndex == 1) {
                    int color = stack.getOrCreateTag().getInt("color");
                    return color != 0 ? color : 0x9DB9CE;
                }
                return 0;
            }, ModItems.PHIAL_SMALL.get());

            aspectColors.register((ItemStack stack, int tintIndex) -> {
                if (tintIndex == 2) return 0x9DB9CE;
                else if (tintIndex == 1) {
                    int color = stack.getOrCreateTag().getInt("color");
                    return color != 0 ? color : 0x9DB9CE;
                }
                return 0;
            }, ModItems.PHIAL.get());

            aspectColors.register((ItemStack stack, int tintIndex) -> {
                if (tintIndex == 2) return 0x9DB9CE;
                else if (tintIndex == 1) {
                    int color = stack.getOrCreateTag().getInt("color");
                    return color != 0 ? color : 0x9DB9CE;
                }
                return 0;
            }, ModItems.PHIAL_LARGE.get());
        }
    }
}
