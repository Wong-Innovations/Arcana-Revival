package arcana.common.items;

import net.minecraft.world.item.Item;
import arcana.api.aspects.IEssentiaContainerItem;
import arcana.api.items.ItemGenericEssentiaContainer;

public class ItemEssentiaContainer extends ItemGenericEssentiaContainer implements IEssentiaContainerItem {
    public ItemEssentiaContainer(int base) {
        super(new Item.Properties()/*.tab( ConfigItems.TABTC )*/, base);
    }
}
