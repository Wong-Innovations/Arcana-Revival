package com.wonginnovations.arcana.worldgen;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.worldgen.trees.features.GreatwoodFoliagePlacer;
import com.wonginnovations.arcana.worldgen.trees.features.GreatwoodTrunkPlacer;
import com.wonginnovations.arcana.worldgen.trees.features.SilverwoodFoliagePlacer;
import com.wonginnovations.arcana.worldgen.trees.features.SilverwoodTrunkPlacer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.wonginnovations.arcana.Arcana.MODID;

@Mod.EventBusSubscriber(modid = Arcana.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArcanaFeatures {
	
	public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MODID);
	public static final DeferredRegister<FoliagePlacerType<?>> FOLAIGE_PLACERS = DeferredRegister.create(ForgeRegistries.FOLIAGE_PLACER_TYPES, MODID);
	public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACERS = DeferredRegister.create(Registries.TRUNK_PLACER_TYPE, MODID);
	
	// features have to exist first because forge is stupid and insists on registering biomes first
	public static RegistryObject<Feature<NoneFeatureConfiguration>> NODE = FEATURES.register("node", () -> new NodeFeature(NoneFeatureConfiguration.CODEC));

	public static RegistryObject<TrunkPlacerType<GreatwoodTrunkPlacer>> GREATWOOD_TRUNK = TRUNK_PLACERS.register("greatwood_trunk_placer", () -> new TrunkPlacerType<>(GreatwoodTrunkPlacer.CODEC));
	public static RegistryObject<TrunkPlacerType<SilverwoodTrunkPlacer>> SILVERWOOD_TRUNK = TRUNK_PLACERS.register("silverwood_trunk_placer", () -> new TrunkPlacerType<>(SilverwoodTrunkPlacer.CODEC));
	
	public static RegistryObject<FoliagePlacerType<GreatwoodFoliagePlacer>> GREATWOOD_FOLIAGE = FOLAIGE_PLACERS.register("greatwood_foliage_placer", () -> new FoliagePlacerType<>(GreatwoodFoliagePlacer.CODEC));
	public static RegistryObject<FoliagePlacerType<SilverwoodFoliagePlacer>> SILVERWOOD_FOLIAGE = FOLAIGE_PLACERS.register("silverwood_foliage_placer", () -> new FoliagePlacerType<>(SilverwoodFoliagePlacer.CODEC));

}