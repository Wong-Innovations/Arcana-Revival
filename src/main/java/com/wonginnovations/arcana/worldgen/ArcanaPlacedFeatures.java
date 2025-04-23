package com.wonginnovations.arcana.worldgen;

import com.wonginnovations.arcana.Arcana;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.NoiseThresholdCountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.List;

public class ArcanaPlacedFeatures {

    public static final ResourceKey<PlacedFeature> PLACED_MAGICAL_FOREST_BONUS_TREES = registerKey("magical_forest_bonus_trees");
    public static final ResourceKey<PlacedFeature> PLACED_MAGICAL_FOREST_GIANT_MUSHROOMS = registerKey("magical_forest_giant_mushrooms");
    public static final ResourceKey<PlacedFeature> PLACED_MAGIC_MUSHROOM_PATCH = registerKey("magic_mushroom_patch");

    public static final ResourceKey<PlacedFeature> PLACED_NODE = registerKey("node");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        PlacementUtils.register(context, PLACED_MAGICAL_FOREST_BONUS_TREES, configuredFeatures.getOrThrow(ArcanaConfiguredFeatures.CONFIGURED_MAGICAL_FOREST_BONUS_TREES),
                HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES),
                NoiseThresholdCountPlacement.of(0.28F, 0, 1));

        PlacementUtils.register(context, PLACED_MAGICAL_FOREST_GIANT_MUSHROOMS, configuredFeatures.getOrThrow(ArcanaConfiguredFeatures.CONFIGURED_MAGICAL_FOREST_GIANT_MUSHROOMS),
                HeightmapPlacement.onHeightmap(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES),
                NoiseThresholdCountPlacement.of(0.25F, 0, 1));

        PlacementUtils.register(context, PLACED_MAGIC_MUSHROOM_PATCH, configuredFeatures.getOrThrow(ArcanaConfiguredFeatures.CONFIGURED_MAGIC_MUSHROOM_PATCH),
                PlacementUtils.HEIGHTMAP);


        PlacementUtils.register(context, PLACED_NODE, configuredFeatures.getOrThrow(ArcanaConfiguredFeatures.CONFIGURED_NODE),
                PlacementUtils.HEIGHTMAP_TOP_SOLID);
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Arcana.arcLoc(name));
    }

}
