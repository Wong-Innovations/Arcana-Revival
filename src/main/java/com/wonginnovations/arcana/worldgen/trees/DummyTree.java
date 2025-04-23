package com.wonginnovations.arcana.worldgen.trees;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class DummyTree extends AbstractTreeGrower {

	@Override
	protected @Nullable ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(@NotNull RandomSource pRandom, boolean pHasFlowers) {
		return null;
	}

}