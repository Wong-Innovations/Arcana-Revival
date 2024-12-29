package arcana.proxies;

import net.minecraft.world.level.Level;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import arcana.api.ArcanaApi;
import arcana.common.config.ConfigResearch;
import arcana.common.lib.InternalMethodHandler;
import arcana.common.lib.network.PacketHandler;

public class CommonProxy implements IProxy {

    public CommonProxy() {
    }

    @Override
    public void preInit(FMLCommonSetupEvent event) {
        ArcanaApi.internalMethods = new InternalMethodHandler();
        PacketHandler.preInit();
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        ConfigResearch.init();
    }

    @Override
    public void postInit(FMLCommonSetupEvent event) {
        ConfigResearch.postInit();
    }

    @Override
    public Level getClientWorld() {
        return null;
    }
}
