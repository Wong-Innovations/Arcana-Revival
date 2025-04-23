package com.wonginnovations.arcana.worldgen;

import com.wonginnovations.arcana.Arcana;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.tags.BiomeTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.wonginnovations.arcana.worldgen.ArcanaBiomes.MAGICAL_FOREST;

public class ArcanaBiomeTagsProvider extends BiomeTagsProvider {

    public ArcanaBiomeTagsProvider(PackOutput pOutput, CompletableFuture<Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, Arcana.MODID, existingFileHelper);
    }

    protected void addTags(@NotNull Provider provider) {
        this.tag(BiomeTags.IS_FOREST).add(MAGICAL_FOREST);
    }

}
