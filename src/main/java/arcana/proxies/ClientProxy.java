package arcana.proxies;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import arcana.client.ColorHandler;

public class ClientProxy extends CommonProxy {

    public ClientProxy() {
    }

    @Override
    public void init(FMLCommonSetupEvent event) {
        super.init(event);
        ColorHandler.registerColourHandlers();
    }

    @Override
    public Level getClientWorld() {
        return Minecraft.getInstance().level;
    }

}
