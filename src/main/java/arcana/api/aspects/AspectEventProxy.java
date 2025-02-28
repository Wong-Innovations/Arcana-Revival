package arcana.api.aspects;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import arcana.api.internal.CommonInternals;

public class AspectEventProxy {
    public void registerObjectTag(ItemStack item, AspectList aspects) {
        if (aspects == null) aspects = new AspectList();
        try {
            CommonInternals.objectTags.put(CommonInternals.generateUniqueItemstackId(item), aspects);
        } catch (Exception ignored) {
        }
    }

    public void registerItemTag(TagKey<Item> oreDict, AspectList aspects) {
        if (aspects == null) aspects = new AspectList();

        ITagManager<Item> ores = ForgeRegistries.ITEMS.tags();
        if (ores != null) {
            for (ItemStack ore : ores.getTag(oreDict).stream().map(ItemStack::new).toList()) {
                try {
                    ItemStack oc = ore.copy();
                    oc.setCount(1);
                    registerObjectTag(oc, aspects.copy());
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void registerBlockTag(TagKey<Block> oreDict, AspectList aspects) {
        if (aspects == null) aspects = new AspectList();

        ITagManager<Block> ores = ForgeRegistries.BLOCKS.tags();
        if (ores != null) {
            for (ItemStack ore : ores.getTag(oreDict).stream().map(ItemStack::new).toList()) {
                try {
                    ItemStack oc = ore.copy();
                    oc.setCount(1);
                    registerObjectTag(oc, aspects.copy());
                } catch (Exception ignored) {
                }
            }
        }
    }
}
