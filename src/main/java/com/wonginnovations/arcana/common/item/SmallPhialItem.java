package com.wonginnovations.arcana.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SmallPhialItem extends Item {

    public SmallPhialItem() {
        super(new Properties());
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("aspect")) {
            return Component.translatable(super.getDescriptionId() + ".filled",
                    Component.translatable("aspect.arcana." + stack.getTag().getString("aspect")));
        }
        return super.getName(stack);
    }
}
