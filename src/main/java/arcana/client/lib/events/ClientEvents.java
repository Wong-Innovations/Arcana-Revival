package arcana.client.lib.events;

import arcana.Arcana;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Arcana.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

//    @SubscribeEvent
//    public static void textureStitchEventPre(TextureStitchEvent.Post event) {
//        if (event.getAtlas().location() == TextureAtlas.LOCATION_BLOCKS) {
//            event.addSprite(new ResourceLocation(Arcana.MODID, "research/quill"));
//        }
//    }
}
