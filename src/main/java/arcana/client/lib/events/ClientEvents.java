package arcana.client.lib.events;

import arcana.Arcana;
import arcana.client.gui.GuiArcaneWorkbench;
import arcana.client.gui.GuiResearchTable;
import arcana.client.gui.ModMenuTypes;
import arcana.client.renderers.entity.RenderSpecialItem;
import arcana.common.entities.ModEntityTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Arcana.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.SPECIAL_ITEM.get(), RenderSpecialItem::new);
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.ARCANE_WORKBENCH_MENU.get(), GuiArcaneWorkbench::new);
            MenuScreens.register(ModMenuTypes.RESEARCH_TABLE_MENU.get(), GuiResearchTable::new);
        });
    }
}