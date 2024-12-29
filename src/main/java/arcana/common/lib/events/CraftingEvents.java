package arcana.common.lib.events;

import arcana.Arcana;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import arcana.common.lib.research.ResearchManager;

@Mod.EventBusSubscriber(modid = Arcana.MODID)
public class CraftingEvents {
    @SubscribeEvent
    public static void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity() != null && !event.getEntity().level().isClientSide) {
            int stackHash = ResearchManager.createItemStackHash(event.getCrafting().copy());
            if (ResearchManager.craftingReferences.contains(stackHash)) {
                ResearchManager.completeResearch(event.getEntity(), "[#]" + stackHash);
            } else {
                stackHash = ResearchManager.createItemStackHash(new ItemStack(event.getCrafting().getItem(), event.getCrafting().getCount()));
                if (ResearchManager.craftingReferences.contains(stackHash)) {
                    ResearchManager.completeResearch(event.getEntity(), "[#]" + stackHash);
                }
            }
        }
    }
}
