package com.wonginnovations.arcana.compat.jei;


import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author BluSunrize - 19.01.2020
 */
public class JEIIngredientStackListBuilder
{
    private final List<List<ItemStack>> list;

    private JEIIngredientStackListBuilder()
    {
        this.list = new ArrayList<>();
    }

    public static JEIIngredientStackListBuilder make(Ingredient... ingredientStacks)
    {
        JEIIngredientStackListBuilder builder = new JEIIngredientStackListBuilder();
        builder.add(ingredientStacks);
        return builder;
    }


    public JEIIngredientStackListBuilder add(Ingredient... ingredientStacks)
    {
        for (Ingredient ingr : ingredientStacks)
            this.list.add(Arrays.asList(ingr.getItems()));
        return this;
    }

    public List<List<ItemStack>> build()
    {
        return this.list;
    }
}