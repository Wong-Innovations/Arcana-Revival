package com.wonginnovations.arcana.items;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.Aspect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CrystalItem extends Item {
    
    public final Aspect aspect;
    
    public CrystalItem(Properties properties, Aspect aspect) {
        super(properties);
        this.aspect = aspect;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("item.arcana.crystal", Component.translatable("aspect." + aspect.name()));
    }
}