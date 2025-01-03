package arcana.api;

import arcana.api.crafting.NBTIngredient;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.items.ItemGenericEssentiaContainer;
import arcana.common.items.ModItems;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

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

    public static Ingredient getIngredient(Object obj) {
        if (obj instanceof Ingredient)
            return (Ingredient)obj;
        else if (obj instanceof ItemStack)
            return ((ItemStack) obj).hasTag()? new NBTIngredient((ItemStack) obj) : Ingredient.of((ItemStack) obj);
        else if (obj instanceof Item)
            return Ingredient.of((Item) obj);
        else if (obj instanceof Block)
            return Ingredient.of((Block) obj);
        else if (obj instanceof TagKey<?> && ((TagKey<?>) obj).isFor(Registries.ITEM))
            return Ingredient.fromValues(Stream.of(new Ingredient.TagValue((TagKey<Item>) obj)));
        else if (obj instanceof String) {
            TagKey<Item> tk = TagKey.create(Registries.ITEM, new ResourceLocation((String) obj));
            return Ingredient.fromValues(Stream.of(new Ingredient.TagValue(tk)));
        } else {
            throw new IllegalArgumentException("Unable to find corresponding Ingredient for type: [" + obj.getClass() + "]");
        }
    }
}
