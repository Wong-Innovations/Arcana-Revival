package arcana.api;

import net.minecraft.world.item.ItemStack;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.items.ItemGenericEssentiaContainer;
import arcana.common.items.ModItems;

public class ArcanaApiHelper {
    public static ItemStack makeCrystal(Aspect aspect, int stackSize) {
        if (aspect == null) {
            return null;
        }
        ItemStack is = new ItemStack(ModItems.crystalEssence.get(), stackSize);
        ((ItemGenericEssentiaContainer) ModItems.crystalEssence.get()).setAspects(is, new AspectList().add(aspect, 1));
        return is;
    }

    public static ItemStack makeCrystal(Aspect aspect) {
        return makeCrystal(aspect, 1);
    }
}
