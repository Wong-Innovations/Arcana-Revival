package arcana.api.crafting;

import net.minecraft.world.item.crafting.CraftingRecipe;
import arcana.api.aspects.AspectList;

public interface IArcaneRecipe extends CraftingRecipe
{
    int getVis();
    String getResearch();
    AspectList getCrystals();
}