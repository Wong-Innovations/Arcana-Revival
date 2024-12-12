package com.wonginnovations.arcana.common;

import com.wonginnovations.arcana.api.crafting.IDustInteraction;
import com.wonginnovations.arcana.common.crafting.SimpleDustInteraction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class ModRecipes {

    public static void initializeDustInteractions() {
        IDustInteraction.registerDustInteraction(new SimpleDustInteraction(Blocks.BOOKSHELF, new ItemStack(Items.DIAMOND), null));
    }
}
