package arcana.api;

import arcana.api.crafting.CrucibleRecipe;
import arcana.api.crafting.IArcaneRecipe;
import arcana.api.crafting.IResearchRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import arcana.api.aspects.AspectEventProxy;
import arcana.api.aspects.AspectList;
import arcana.api.internal.CommonInternals;
import arcana.api.internal.DummyInternalMethodHandler;
import arcana.api.internal.IInternalMethodHandler;

import java.util.HashMap;

public class ArcanaApi {
    public static IInternalMethodHandler internalMethods = new DummyInternalMethodHandler();

    public static void registerResearchLocation(ResourceLocation loc) {
        if (!CommonInternals.jsonLocs.containsKey(loc.toString())) {
            CommonInternals.jsonLocs.put(loc.toString(), loc);
        }
    }

    public static HashMap<ResourceLocation, IResearchRecipe> getCraftingRecipes() {
        return CommonInternals.craftingRecipeCatalog;
    }

    public static HashMap<ResourceLocation, Object> getCraftingRecipesFake() {
        return CommonInternals.craftingRecipeCatalogFake;
    }

    public static void addFakeCraftingRecipe(ResourceLocation registry, Object recipe) {
        getCraftingRecipesFake().put(registry, recipe);
    }

    public static void addCrucibleRecipe(ResourceLocation registry, CrucibleRecipe recipe) {
        getCraftingRecipes().put(registry, recipe);
    }

    public static boolean exists(ItemStack item) {
        ItemStack stack = item.copy();
        stack.setCount(1);
        AspectList tmp = CommonInternals.objectTags.get(stack.serializeNBT().toString());
        if (tmp == null) {
            try {
                stack.setDamageValue(Short.MAX_VALUE);
                tmp = CommonInternals.objectTags.get(stack.serializeNBT().toString());
                if (item.getDamageValue() == Short.MAX_VALUE && tmp == null) {
                    int index = 0;
                    do {
                        stack.setDamageValue(index);
                        tmp = CommonInternals.objectTags.get(stack.serializeNBT().toString());
                        index++;
                    } while (index < 16 && tmp == null);
                }
                if (tmp == null) return false;
            } catch (Exception ignored) {}
        }

        return true;
    }

    public static void registerObjectTag(ItemStack item, AspectList aspects) {
        (new AspectEventProxy()).registerObjectTag(item, aspects);
    }

    @Deprecated
    public static void registerItemTag(TagKey<Item> oreDict, AspectList aspects) {
        (new AspectEventProxy()).registerItemTag(oreDict, aspects);
    }

    @Deprecated
    public static void registerBlockTag(TagKey<Block> oreDict, AspectList aspects) {
        (new AspectEventProxy()).registerBlockTag(oreDict, aspects);
    }
}
