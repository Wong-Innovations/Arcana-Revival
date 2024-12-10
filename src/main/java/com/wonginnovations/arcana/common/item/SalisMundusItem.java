package com.wonginnovations.arcana.common.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class SalisMundusItem extends Item {

    public SalisMundusItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (context.getPlayer() == null) return InteractionResult.FAIL;
        if (!context.getPlayer().mayUseItemAt(context.getClickedPos(), context.getClickedFace(), stack)) {
            return InteractionResult.FAIL;
        } else if (context.getPlayer().isCrouching()) {
            return InteractionResult.PASS;
        } else {
            context.getPlayer().swing(context.getHand());

        }
        return super.onItemUseFirst(stack, context);
    }
}
