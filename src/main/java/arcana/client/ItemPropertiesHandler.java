package arcana.client;

import arcana.Arcana;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import arcana.api.items.ItemGenericEssentiaContainer;
import arcana.common.items.ModItems;

public class ItemPropertiesHandler {
    public static void registerItemProperties() {
        ItemProperties.register(ModItems.phial.get(), new ResourceLocation(Arcana.MODID, "filled"), (stack, level, entity, seed) -> {
            ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer) stack.getItem();
            return item.getAspects(stack) != null && !item.getAspects(stack).aspects.isEmpty() ? 1.0f : 0.0f;
        });
    }
}
