package com.wonginnovations.arcana.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.RecordItem;

import java.util.function.Supplier;

public class ArcanaMusicDiscItem extends RecordItem {
    public ArcanaMusicDiscItem(int comparator, Supplier<SoundEvent> sound, Properties builder, int lengthInTicks) {
        super(comparator, sound, builder, lengthInTicks);
    }
}
