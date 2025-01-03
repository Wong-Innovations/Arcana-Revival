package arcana.api.internal;

import arcana.api.crafting.IResearchRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import arcana.api.aspects.AspectList;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class CommonInternals {
    public static HashMap<String, ResourceLocation> jsonLocs = new HashMap<>();
    public static HashMap<ResourceLocation, IResearchRecipe> craftingRecipeCatalog = new HashMap<>();
    public static HashMap<ResourceLocation, Object> craftingRecipeCatalogFake = new HashMap<>();
    public static ConcurrentHashMap<Integer, AspectList> objectTags = new ConcurrentHashMap<>();

    public static IResearchRecipe getCatalogRecipe(ResourceLocation key) {
        return CommonInternals.craftingRecipeCatalog.get(key);
    }

    public static Object getCatalogRecipeFake(ResourceLocation key) {
        return CommonInternals.craftingRecipeCatalogFake.get(key);
    }

    public static int generateUniqueItemstackId(ItemStack stack) {
        ItemStack sc = stack.copy();
        sc.setCount(1);
        String ss = sc.serializeNBT().toString();
        return ss.hashCode();
    }

    public static int generateUniqueItemstackIdStripped(ItemStack stack) {
        ItemStack sc = stack.copy();
        sc.setCount(1);
        sc.setTag(null);
        String ss = sc.serializeNBT().toString();
        return ss.hashCode();
    }
}
