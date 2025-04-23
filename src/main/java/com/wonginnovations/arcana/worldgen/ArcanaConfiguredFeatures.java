package com.wonginnovations.arcana.worldgen;

import com.google.common.collect.ImmutableList;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.worldgen.trees.features.GreatwoodFoliagePlacer;
import com.wonginnovations.arcana.worldgen.trees.features.GreatwoodTrunkPlacer;
import com.wonginnovations.arcana.worldgen.trees.features.SilverwoodFoliagePlacer;
import com.wonginnovations.arcana.worldgen.trees.features.SilverwoodTrunkPlacer;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class ArcanaConfiguredFeatures {

    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_GREATWOOD_TREE = registerKey("greatwood_tree");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_SILVERWOOD_TREE = registerKey("silverwood_tree");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_TAINTED_GREATWOOD_TREE = registerKey("tainted_greatwood_tree");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_TAINTED_OAK_TREE = registerKey("tainted_oak_tree");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_TAINTED_DARK_OAK_TREE = registerKey("tainted_dark_oak_tree");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_TAINTED_BIRCH_TREE = registerKey("tainted_birch_tree");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_TAINTED_ACACIA_TREE = registerKey("tainted_acacia_tree");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_TAINTED_SPRUCE_TREE = registerKey("tainted_spruce_tree");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_TAINTED_PINE_TREE = registerKey("tainted_pine_tree");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_TAINTED_FANCY_OAK_TREE = registerKey("tainted_fancy_oak_tree");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_TAINTED_JUNGLE_TREE = registerKey("tainted_jungle_tree");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_TAINTED_MEGA_JUNGLE_TREE = registerKey("tainted_mega_jungle_tree");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_TAINTED_MEGA_SPRUCE_TREE = registerKey("tainted_mega_spruce_tree");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_TAINTED_MEGA_PINE_TREE = registerKey("tainted_mega_pine_tree");

    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_MAGICAL_FOREST_BONUS_TREES = registerKey("magical_forest_bonus_trees");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_MAGICAL_FOREST_GIANT_MUSHROOMS = registerKey("magical_forest_giant_mushrooms");
    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_MAGIC_MUSHROOM_PATCH = registerKey("magic_mushroom_patch");

    public static ResourceKey<ConfiguredFeature<?, ?>> CONFIGURED_NODE = registerKey("node");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        FeatureUtils.register(context, CONFIGURED_GREATWOOD_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ArcanaBlocks.GREATWOOD_LOG.get().defaultBlockState()),
                new GreatwoodTrunkPlacer(4, 2, 0),
                BlockStateProvider.simple(ArcanaBlocks.GREATWOOD_LEAVES.get().defaultBlockState()),
                new GreatwoodFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 15),
                new TwoLayersFeatureSize(1, 0, 1))
                .ignoreVines()
                .build());

        FeatureUtils.register(context, CONFIGURED_SILVERWOOD_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ArcanaBlocks.SILVERWOOD_LOG.get().defaultBlockState()),
                new SilverwoodTrunkPlacer(4, 2, 0),
                BlockStateProvider.simple(ArcanaBlocks.SILVERWOOD_LEAVES.get().defaultBlockState()),
                new SilverwoodFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 15),
                new TwoLayersFeatureSize(1, 0, 1))
                .ignoreVines()
                .build());

//        FeatureUtils.register(context, CONFIGURED_TAINTED_GREATWOOD_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
//                BlockStateProvider.simple(ArcanaBlocks.TAINTED_GREATWOOD_LOG.get().defaultBlockState()),
//                new GreatwoodTrunkPlacer(4, 2, 0),
//                BlockStateProvider.simple(ArcanaBlocks.TAINTED_GREATWOOD_LEAVES.get().defaultBlockState()),
//                new GreatwoodFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 15),
//                new TwoLayersFeatureSize(1, 0, 1))
//                .ignoreVines()
//                .build());

        FeatureUtils.register(context, CONFIGURED_TAINTED_OAK_TREE, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ArcanaBlocks.TAINTED_OAK_LOG.get().defaultBlockState()),
                new StraightTrunkPlacer(4, 2, 0),
                BlockStateProvider.simple(ArcanaBlocks.TAINTED_OAK_LEAVES.get().defaultBlockState()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(0), 3),
                new TwoLayersFeatureSize(1, 0, 1))
                .ignoreVines()
                .build());


        FeatureUtils.register(context, CONFIGURED_MAGICAL_FOREST_BONUS_TREES, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(ImmutableList.of(
                        new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configuredFeatures.getOrThrow(CONFIGURED_SILVERWOOD_TREE)), 2 / 7f),
                        new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configuredFeatures.getOrThrow(CONFIGURED_GREATWOOD_TREE)), 5 / 7f)),
                placedFeatures.getOrThrow(ArcanaPlacedFeatures.PLACED_MAGICAL_FOREST_BONUS_TREES)));// GREATWOOD TREE

        FeatureUtils.register(context, CONFIGURED_MAGICAL_FOREST_GIANT_MUSHROOMS, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(ImmutableList.of(
                        new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configuredFeatures.getOrThrow(TreeFeatures.HUGE_BROWN_MUSHROOM)), 0.5f),
                        new WeightedPlacedFeature(PlacementUtils.inlinePlaced(configuredFeatures.getOrThrow(TreeFeatures.HUGE_RED_MUSHROOM)), 0.5f)),
                placedFeatures.getOrThrow(ArcanaPlacedFeatures.PLACED_MAGICAL_FOREST_GIANT_MUSHROOMS))); // RED MUSHROOOM

        FeatureUtils.register(context, CONFIGURED_MAGIC_MUSHROOM_PATCH, Feature.RANDOM_PATCH, new RandomPatchConfiguration(
                64,
                6,
                2,
                PlacementUtils.onlyWhenEmpty(
                        Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(BlockStateProvider.simple(ArcanaBlocks.MAGIC_MUSHROOM.get())))));


        FeatureUtils.register(context, CONFIGURED_NODE, ArcanaFeatures.NODE.get(), new NoneFeatureConfiguration());
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Arcana.arcLoc(name));
    }
    
}
