package com.wonginnovations.arcana.worldgen.trees;

import com.wonginnovations.arcana.worldgen.ArcanaConfiguredFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class GreatwoodTreeGrower extends AbstractMegaTreeGrower {

	@Override
	protected @Nullable ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource pRandom) {
		return ArcanaConfiguredFeatures.CONFIGURED_GREATWOOD_TREE;
	}

	@Override
	protected @Nullable ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pHasFlowers) {
		return null;
	}

}
