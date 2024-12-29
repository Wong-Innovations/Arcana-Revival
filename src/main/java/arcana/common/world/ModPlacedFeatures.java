package arcana.common.world;

import arcana.Arcana;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

public class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> PLACED_CRYSTAL_KEY = ModPlacedFeatures.registerKey("crystal_placed");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        PlacementUtils.register(context, PLACED_CRYSTAL_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.CONFIGURED_CRYSTAL_KEY),
                RarityFilter.onAverageOnceEvery(2),
                InSquarePlacement.spread(),
                BiomeFilter.biome(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.top()));
//              CarvingMaskPlacement.forStep(GenerationStep.Carving.AIR)
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(Arcana.MODID, name));
    }

}
