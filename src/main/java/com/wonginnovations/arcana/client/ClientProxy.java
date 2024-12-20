package com.wonginnovations.arcana.client;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.client.renderers.entity.RenderSpecialItem;
import com.wonginnovations.arcana.common.CommonProxy;
import com.wonginnovations.arcana.common.entities.ModEntityTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Arcana.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        super();
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.SPECIAL_ITEM_ENTITY.get(), RenderSpecialItem::new);
    }

}
