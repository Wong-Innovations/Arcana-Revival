package arcana.common.world;

import arcana.Arcana;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBiomeModifiers {

    public static final ResourceKey<BiomeModifier> CRYSTAL_OVERWORLD = registerKey("crystal_overworld");

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        HolderGetter<PlacedFeature> placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderSet<Biome> overworld = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);
        Holder<PlacedFeature> crystal = placedFeatures.getOrThrow(ModPlacedFeatures.PLACED_CRYSTAL_KEY);

        context.register(CRYSTAL_OVERWORLD, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                overworld,
                HolderSet.direct(crystal),
                GenerationStep.Decoration.UNDERGROUND_DECORATION));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(Arcana.MODID, name));
    }
}
