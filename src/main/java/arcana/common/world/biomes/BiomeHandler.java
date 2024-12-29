package arcana.common.world.biomes;

import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.Tags;
import arcana.api.aspects.Aspect;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BiomeHandler {
    public static HashMap<TagKey<Biome>, BiomeMetadata> biomeInfo = new HashMap<>();

    public static void registerBiomeInfo(TagKey<Biome> type, BiomeMetadata biomeMetadata) {
        BiomeHandler.biomeInfo.put(type, biomeMetadata);
    }

    public static float getBiomeAuraModifier(Holder<Biome> biome) {
        try {
            Set<TagKey<Biome>> types = biome.tags().collect(Collectors.toSet());
            float average = 0.0f;
            int count = 0;
            for (TagKey<Biome> type : types) {
                average += BiomeHandler.biomeInfo.get(type).auraLevel;
                ++count;
            }
            return average / count;
        } catch (Exception ex) {
            return 0.5f;
        }
    }

    public static Aspect getRandomBiomeTag(Holder<Biome> biome, RandomSource random) {
        try {
            Set<TagKey<Biome>> types = biome.tags().collect(Collectors.toSet());
            TagKey<Biome> type = (TagKey<Biome>) (biome.tags().toArray()[random.nextInt(types.size())]);
            return BiomeHandler.biomeInfo.get(type).tag;
        } catch (Exception ex) {
            return null;
        }
    }

    public static void registerBiomes() {
        registerBiomeInfo(Tags.Biomes.IS_WATER, new BiomeMetadata(0.33f, Aspect.WATER, false, 0.0f));
        registerBiomeInfo(BiomeTags.IS_OCEAN, new BiomeMetadata(0.33f, Aspect.WATER, false, 0.0f));
        registerBiomeInfo(BiomeTags.IS_RIVER, new BiomeMetadata(0.4f, Aspect.WATER, false, 0.0f));
        registerBiomeInfo(Tags.Biomes.IS_WET, new BiomeMetadata(0.4f, Aspect.WATER, false, 0.0f));
        registerBiomeInfo(Tags.Biomes.IS_LUSH, new BiomeMetadata(0.5f, Aspect.WATER, true, 0.5f));
        registerBiomeInfo(Tags.Biomes.IS_HOT, new BiomeMetadata(0.33f, Aspect.FIRE, false, 0.0f));
        registerBiomeInfo(Tags.Biomes.IS_DRY, new BiomeMetadata(0.25f, Aspect.FIRE, false, 0.0f));
        registerBiomeInfo(BiomeTags.IS_NETHER, new BiomeMetadata(0.125f, Aspect.FIRE, false, 0.0f));
        registerBiomeInfo(BiomeTags.IS_BADLANDS, new BiomeMetadata(0.33f, Aspect.FIRE, false, 0.0f));
        registerBiomeInfo(Tags.Biomes.IS_SPOOKY, new BiomeMetadata(0.5f, Aspect.FIRE, false, 0.0f));
        registerBiomeInfo(Tags.Biomes.IS_DENSE, new BiomeMetadata(0.4f, Aspect.ORDER, false, 0.0f));
        registerBiomeInfo(Tags.Biomes.IS_SNOWY, new BiomeMetadata(0.25f, Aspect.ORDER, false, 0.0f));
        registerBiomeInfo(Tags.Biomes.IS_COLD, new BiomeMetadata(0.25f, Aspect.ORDER, false, 0.0f));
        registerBiomeInfo(Tags.Biomes.IS_MUSHROOM, new BiomeMetadata(0.75f, Aspect.ORDER, false, 0.0f));
        registerBiomeInfo(Tags.Biomes.IS_MAGICAL, new BiomeMetadata(0.75f, Aspect.ORDER, true, 1.0f));
        registerBiomeInfo(Tags.Biomes.IS_CONIFEROUS, new BiomeMetadata(0.33f, Aspect.EARTH, true, 0.2f));
        registerBiomeInfo(BiomeTags.IS_FOREST, new BiomeMetadata(0.5f, Aspect.EARTH, true, 1.0f));
        registerBiomeInfo(Tags.Biomes.IS_SANDY, new BiomeMetadata(0.25f, Aspect.EARTH, false, 0.0f));
        registerBiomeInfo(BiomeTags.IS_BEACH, new BiomeMetadata(0.3f, Aspect.EARTH, false, 0.0f));
        registerBiomeInfo(BiomeTags.IS_JUNGLE, new BiomeMetadata(0.6f, Aspect.EARTH, false, 0.0f));
        registerBiomeInfo(BiomeTags.IS_SAVANNA, new BiomeMetadata(0.25f, Aspect.AIR, true, 0.2f));
        registerBiomeInfo(Tags.Biomes.IS_MOUNTAIN, new BiomeMetadata(0.3f, Aspect.AIR, false, 0.0f));
//        registerBiomeInfo(Tags.Biomes.IS_HILLS, new BiomeMetadata(0.33f, Aspect.AIR, false, 0.0f));
        registerBiomeInfo(Tags.Biomes.IS_PLAINS, new BiomeMetadata(0.3f, Aspect.AIR, true, 0.2f));
//        registerBiomeInfo(Tags.Biomes.IS_THE_END, 0.125f, Aspect.AIR, false, 0.0f);
        registerBiomeInfo(Tags.Biomes.IS_DRY, new BiomeMetadata(0.125f, Aspect.ENTROPY, false, 0.0f));
        registerBiomeInfo(Tags.Biomes.IS_SPARSE, new BiomeMetadata(0.2f, Aspect.ENTROPY, false, 0.0f));
        registerBiomeInfo(Tags.Biomes.IS_SWAMP, new BiomeMetadata(0.5f, Aspect.ENTROPY, true, 0.2f));
        registerBiomeInfo(Tags.Biomes.IS_WASTELAND, new BiomeMetadata(0.125f, Aspect.ENTROPY, false, 0.0f));
        registerBiomeInfo(Tags.Biomes.IS_DEAD, new BiomeMetadata(0.1f, Aspect.ENTROPY, false, 0.0f));
    }

    public static class BiomeMetadata {
        public final float auraLevel;
        public final Aspect tag;
        public final boolean greatwood;
        public final float greatwoodchance;

        public BiomeMetadata(float pAuraLevel, Aspect pTag, boolean pGreatwood, float pGreatwoodChance) {
            this.auraLevel = pAuraLevel;
            this.tag = pTag;
            this.greatwood = pGreatwood;
            this.greatwoodchance = pGreatwoodChance;
        }
    }
}
