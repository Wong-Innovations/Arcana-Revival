package arcana.api.crafting;

import net.minecraft.world.item.crafting.CraftingRecipe;
import arcana.api.aspects.AspectList;
import org.jetbrains.annotations.NotNull;

public interface IArcaneRecipe extends CraftingRecipe, IResearchRecipe
{
    int getVis();
    String getResearch();
    @NotNull String getGroup();
    AspectList getCrystals();
}