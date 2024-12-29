package arcana.common.lib.crafting;

import arcana.Arcana;
import arcana.common.config.ModRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import arcana.common.items.ModItems;
import arcana.common.items.consumables.ItemPhial;
import org.jetbrains.annotations.NotNull;

public class RecipeScribingTools extends CustomRecipe {

    public RecipeScribingTools(ResourceLocation pId) {
        super(pId, CraftingBookCategory.MISC);
    }

    @Override
    public boolean matches(CraftingContainer pContainer, @NotNull Level pLevel) {
        boolean feather = false;
        boolean phial = false;
        boolean dye = false;
        for (int i = 0; i < pContainer.getContainerSize(); ++i) {
            if (!pContainer.getItem(i).isEmpty()) {
                ItemStack itemstack = pContainer.getItem(i);

                if (itemstack.isEmpty()) {
                    return false;
                }

                if (itemstack.getItem() == Items.FEATHER && feather) {
                    return false;
                }
                if (itemstack.getItem() == Items.FEATHER && !feather) {
                    feather = true;
                } else {
                    if (itemstack.getTags().anyMatch(t -> t == Tags.Items.DYES_BLACK) && dye) {
                        return false;
                    }
                    if (itemstack.getTags().anyMatch(t -> t == Tags.Items.DYES_BLACK) && !dye) {
                        dye = true;
                    } else {
                        if (itemstack.getItem() != ModItems.phial.get()) {
                            return false;
                        }
                        ItemPhial item = (ItemPhial) itemstack.getItem();
                        if (item.getAspects(itemstack) == null) {
                            phial = true;
                        }
                    }
                }
            }
        }

        return feather && phial && dye;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer pContainer, @NotNull RegistryAccess pRegistryAccess) {
        return new ItemStack(ModItems.scribingTools.get());
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 3;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return ModRecipes.SCRIBING_TOOLS_SERIALIZER.get();
    }
}
