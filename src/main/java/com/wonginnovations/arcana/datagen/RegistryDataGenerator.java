package com.wonginnovations.arcana.datagen;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.entities.ArcanaDamageTypes;
import com.wonginnovations.arcana.worldgen.ArcanaBiomeModifiers;
import com.wonginnovations.arcana.worldgen.ArcanaBiomes;
import com.wonginnovations.arcana.worldgen.ArcanaConfiguredFeatures;
import com.wonginnovations.arcana.worldgen.ArcanaPlacedFeatures;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class RegistryDataGenerator extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.DAMAGE_TYPE, ArcanaDamageTypes::bootstrap)
            .add(Registries.CONFIGURED_FEATURE, ArcanaConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ArcanaPlacedFeatures::bootstrap)
            .add(Registries.BIOME, ArcanaBiomes::boostrap)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ArcanaBiomeModifiers::bootstrap);

    public RegistryDataGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider, BUILDER, Set.of("minecraft", Arcana.MODID));
    }

}
