package com.wonginnovations.arcana.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.BiomeManager;

import static com.wonginnovations.arcana.Arcana.arcLoc;
import static net.minecraft.data.worldgen.BiomeDefaultFeatures.*;

public class ArcanaBiomes {
	
	public static final ResourceKey<Biome> MAGICAL_FOREST = ResourceKey.create(Registries.BIOME, arcLoc("magical_forest"));

	public static void boostrap(BootstapContext<Biome> context) {
		context.register(MAGICAL_FOREST, makeMagicalForestBiome(context));
	}
	
	private static Biome makeMagicalForestBiome(BootstapContext<Biome> context) {
		BiomeGenerationSettings.Builder settings = new BiomeGenerationSettings.Builder(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER)) /*.withSurfaceBuilder(ConfiguredSurfaceBuilders.GRASS)*/;
		globalOverworldGeneration(settings);
		addForestFlowers(settings);
		addDefaultOres(settings);
		addDefaultSoftDisks(settings);
		addBirchTrees(settings);
		addDefaultFlowers(settings);
		addForestGrass(settings);
		addDefaultMushrooms(settings);
		addDefaultExtraVegetation(settings);
		settings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ArcanaPlacedFeatures.PLACED_MAGICAL_FOREST_BONUS_TREES);
		settings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ArcanaPlacedFeatures.PLACED_MAGICAL_FOREST_GIANT_MUSHROOMS);
		settings.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ArcanaPlacedFeatures.PLACED_MAGIC_MUSHROOM_PATCH);
		
		MobSpawnSettings.Builder mobSpawnBuilder = new MobSpawnSettings.Builder()
				.addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(EntityType.WOLF, 5, 4, 4));
//				.isValidSpawnBiomeForPlayer();
		farmAnimals(mobSpawnBuilder);
		commonSpawns(mobSpawnBuilder);
		
		Biome biome = new Biome.BiomeBuilder()
				.hasPrecipitation(true)
//				.depth(.2f)
//				.scale(.3f)
				.temperature(.6f)
				.downfall(.6f)
				.specialEffects(new BiomeSpecialEffects.Builder()
						.grassColorOverride(0x7ff3ac)
						.waterColor(0x3f76e4)
						.waterFogColor(0x50533)
						.fogColor(0xc0d8ff)
						.skyColor(getSkyColorWithTemperatureModifier(.7f))
						.ambientMoodSound(AmbientMoodSettings.LEGACY_CAVE_SETTINGS)
						.backgroundMusic(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_FOREST)).build())
				.mobSpawnSettings(mobSpawnBuilder.build())
				.generationSettings(settings.build())
				.build();

//		RegistryKey<Biome> key = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, MAGICAL_FOREST.getId());
		BiomeManager.addBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(MAGICAL_FOREST, 5));
//		BiomeDictionary.addTypes(key, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.MAGICAL);
		
		return biome;
	}

	private static void globalOverworldGeneration(BiomeGenerationSettings.Builder settings) {
		addDefaultCarversAndLakes(settings);
		addDefaultCrystalFormations(settings);
		addDefaultMonsterRoom(settings);
		addDefaultUndergroundVariety(settings);
		addDefaultSprings(settings);
		addSurfaceFreezing(settings);
	}
	
	private static int getSkyColorWithTemperatureModifier(float temperature) {
		float temp = temperature / 3;
		temp = Mth.clamp(temp, -1, 1);
		return Mth.hsvToRgb(0.62222224F - temp * .05f, .5f + temp * .1f, 1);
	}

}