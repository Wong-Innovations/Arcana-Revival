package com.wonginnovations.arcana.systems.taint;

import com.wonginnovations.arcana.worldgen.ArcanaConfiguredFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractMegaTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TaintedSpruceTree extends AbstractMegaTreeGrower {

	@Override
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredMegaFeature(RandomSource rand) {
		return ArcanaConfiguredFeatures.CONFIGURED_TAINTED_MEGA_SPRUCE_TREE;
	}

	@Override
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean largeHive) {
		return ArcanaConfiguredFeatures.CONFIGURED_TAINTED_SPRUCE_TREE;
	}
}