package com.wonginnovations.arcana.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Invoker;

@Pseudo
@Mixin(Biome.class)
public interface BiomeAccessor {

    @Invoker("getHeightAdjustedTemperature")
    float getTemperatureAt(BlockPos pPos);

}
