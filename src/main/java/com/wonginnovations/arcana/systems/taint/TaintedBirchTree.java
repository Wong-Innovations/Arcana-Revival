package com.wonginnovations.arcana.systems.taint;

import com.wonginnovations.arcana.worldgen.ArcanaConfiguredFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TaintedBirchTree extends AbstractTreeGrower {
	
	@Nullable
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource rand, boolean largeHive) {
		return ArcanaConfiguredFeatures.CONFIGURED_TAINTED_BIRCH_TREE;
	}
}