package com.wonginnovations.arcana.datagen;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.entities.ArcanaDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class DamageTypeTagGenerator extends TagsProvider<DamageType> {

    public DamageTypeTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper efh) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, Arcana.MODID, efh);
    }

    protected void addTags(@NotNull HolderLookup.Provider provider) {
        this.tag(DamageTypeTags.BYPASSES_ARMOR).add(ArcanaDamageTypes.TAINT);
        this.tag(DamageTypeTags.BYPASSES_SHIELD).add(ArcanaDamageTypes.TAINT);
    }

}
