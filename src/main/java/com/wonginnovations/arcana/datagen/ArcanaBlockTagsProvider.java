package com.wonginnovations.arcana.datagen;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.items.ArcanaItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ArcanaBlockTagsProvider extends BlockTagsProvider {

    public static final TagKey<Block> STORAGE_BLOCKS_THAUMIUM = BlockTags.create(Arcana.arcLoc("storage_blocks/thaumium"));
    public static final TagKey<Block> STORAGE_BLOCKS_ARCANIUM = BlockTags.create(Arcana.arcLoc("storage_blocks/arcanium"));
    public static final TagKey<Block> STORAGE_BLOCKS_SILVER = BlockTags.create(new ResourceLocation("forge", "storage_blocks/silver"));
    public static final TagKey<Block> STORAGE_BLOCKS_VOID_METAL = BlockTags.create(Arcana.arcLoc("storage_blocks/void_metal"));

    public static final TagKey<Block> SILVER_ORE = BlockTags.create(new ResourceLocation("forge", "ores/silver"));
    public static final TagKey<Block> AMBER_ORE = BlockTags.create(Arcana.arcLoc("ores/amber"));

    public ArcanaBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
        super(output, lookupProvider, Arcana.MODID, fileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        tag(BlockTags.CLIMBABLE).add(ArcanaBlocks.TAINTED_VINE.get());
        tag(BlockTags.LOGS).add(
                ArcanaBlocks.DAIR_LOG.get(),
                ArcanaBlocks.DEAD_LOG.get(),
                ArcanaBlocks.EUCALYPTUS_LOG.get(),
                ArcanaBlocks.GREATWOOD_LOG.get(),
                ArcanaBlocks.HAWTHORN_LOG.get(),
                ArcanaBlocks.SILVERWOOD_LOG.get(),
                ArcanaBlocks.TRYPOPHOBIUS_LOG.get(),
                ArcanaBlocks.WILLOW_LOG.get(),
                ArcanaBlocks.TAINTED_DAIR_LOG.get(),
                ArcanaBlocks.TAINTED_EUCALYPTUS_LOG.get(),
                ArcanaBlocks.TAINTED_GREATWOOD_LOG.get(),
                ArcanaBlocks.TAINTED_HAWTHORN_LOG.get(),
                ArcanaBlocks.TAINTED_WILLOW_LOG.get(),
                ArcanaBlocks.TAINTED_OAK_LOG.get(),
                ArcanaBlocks.TAINTED_DARKOAK_LOG.get(),
                ArcanaBlocks.TAINTED_ACACIA_LOG.get(),
                ArcanaBlocks.TAINTED_BIRCH_LOG.get(),
                ArcanaBlocks.TAINTED_JUNGLE_LOG.get(),
                ArcanaBlocks.TAINTED_SPRUCE_LOG.get());
        tag(BlockTags.SLABS).add(
                ArcanaBlocks.ARCANE_STONE_SLAB.get(),
                ArcanaBlocks.ARCANE_STONE_BRICKS_SLAB.get(),
                ArcanaBlocks.DAIR_SLAB.get(),
                ArcanaBlocks.DEAD_SLAB.get(),
                ArcanaBlocks.EUCALYPTUS_SLAB.get(),
                ArcanaBlocks.GREATWOOD_SLAB.get(),
                ArcanaBlocks.HAWTHORN_SLAB.get(),
                ArcanaBlocks.SILVERWOOD_SLAB.get(),
                ArcanaBlocks.TRYPOPHOBIUS_SLAB.get(),
                ArcanaBlocks.WILLOW_SLAB.get(),
                ArcanaBlocks.DUNGEON_BRICKS_SLAB.get(),
                ArcanaBlocks.CRACKED_DUNGEON_BRICKS_SLAB.get(),
                ArcanaBlocks.MOSSY_DUNGEON_BRICKS_SLAB.get(),
                ArcanaBlocks.TAINTED_ANDESITE_SLAB.get(),
                ArcanaBlocks.TAINTED_DIORITE_SLAB.get(),
                ArcanaBlocks.TAINTED_GRANITE_SLAB.get(),
                ArcanaBlocks.TAINTED_POLISHED_ANDESITE_SLAB.get(),
                ArcanaBlocks.TAINTED_POLISHED_DIORITE_SLAB.get(),
                ArcanaBlocks.TAINTED_POLISHED_GRANITE_SLAB.get(),
                ArcanaBlocks.TAINTED_WILLOW_SLAB.get(),
                ArcanaBlocks.TAINTED_DAIR_SLAB.get(),
                ArcanaBlocks.TAINTED_BIRCH_SLAB.get(),
                ArcanaBlocks.TAINTED_DARKOAK_SLAB.get(),
                ArcanaBlocks.TAINTED_EUCALYPTUS_SLAB.get(),
                ArcanaBlocks.TAINTED_GREATWOOD_SLAB.get(),
                ArcanaBlocks.TAINTED_JUNGLE_SLAB.get(),
                ArcanaBlocks.TAINTED_HAWTHORN_SLAB.get(),
                ArcanaBlocks.TAINTED_OAK_SLAB.get(),
                ArcanaBlocks.TAINTED_SPRUCE_SLAB.get() /*,
                ArcanaBlocks.TAINTED_CRUST_SLAB.get()*/);
        tag(BlockTags.WOODEN_SLABS).add(
                ArcanaBlocks.DAIR_SLAB.get(),
                ArcanaBlocks.DEAD_SLAB.get(),
                ArcanaBlocks.EUCALYPTUS_SLAB.get(),
                ArcanaBlocks.GREATWOOD_SLAB.get(),
                ArcanaBlocks.HAWTHORN_SLAB.get(),
                ArcanaBlocks.SILVERWOOD_SLAB.get(),
                ArcanaBlocks.TRYPOPHOBIUS_SLAB.get(),
                ArcanaBlocks.WILLOW_SLAB.get(),
                ArcanaBlocks.TAINTED_EUCALYPTUS_SLAB.get(),
                ArcanaBlocks.TAINTED_GREATWOOD_SLAB.get(),
                ArcanaBlocks.TAINTED_HAWTHORN_SLAB.get(),
                ArcanaBlocks.TAINTED_WILLOW_SLAB.get(),
                ArcanaBlocks.TAINTED_OAK_SLAB.get(),
                ArcanaBlocks.TAINTED_DARKOAK_SLAB.get(),
                ArcanaBlocks.TAINTED_ACACIA_SLAB.get(),
                ArcanaBlocks.TAINTED_BIRCH_SLAB.get(),
                ArcanaBlocks.TAINTED_JUNGLE_SLAB.get(),
                ArcanaBlocks.TAINTED_SPRUCE_SLAB.get());
        tag(BlockTags.PLANKS).add(
                ArcanaBlocks.DAIR_PLANKS.get(),
                ArcanaBlocks.DEAD_PLANKS.get(),
                ArcanaBlocks.EUCALYPTUS_PLANKS.get(),
                ArcanaBlocks.GREATWOOD_PLANKS.get(),
                ArcanaBlocks.HAWTHORN_PLANKS.get(),
                ArcanaBlocks.SILVERWOOD_PLANKS.get(),
                ArcanaBlocks.TRYPOPHOBIUS_PLANKS.get(),
                ArcanaBlocks.WILLOW_PLANKS.get(),
                ArcanaBlocks.TAINTED_DAIR_PLANKS.get(),
                ArcanaBlocks.TAINTED_EUCALYPTUS_PLANKS.get(),
                ArcanaBlocks.TAINTED_GREATWOOD_PLANKS.get(),
                ArcanaBlocks.TAINTED_HAWTHORN_PLANKS.get(),
                ArcanaBlocks.TAINTED_WILLOW_PLANKS.get(),
                ArcanaBlocks.TAINTED_OAK_PLANKS.get(),
                ArcanaBlocks.TAINTED_DARKOAK_PLANKS.get(),
                ArcanaBlocks.TAINTED_ACACIA_PLANKS.get(),
                ArcanaBlocks.TAINTED_BIRCH_PLANKS.get(),
                ArcanaBlocks.TAINTED_JUNGLE_PLANKS.get(),
                ArcanaBlocks.TAINTED_SPRUCE_PLANKS.get());
        tag(BlockTags.LEAVES).add(
                ArcanaBlocks.DAIR_LEAVES.get(),
                ArcanaBlocks.EUCALYPTUS_LEAVES.get(),
                ArcanaBlocks.GREATWOOD_LEAVES.get(),
                ArcanaBlocks.HAWTHORN_LEAVES.get(),
                ArcanaBlocks.SILVERWOOD_LEAVES.get(),
                ArcanaBlocks.WILLOW_LEAVES.get(),
                ArcanaBlocks.TAINTED_DAIR_LEAVES.get(),
                ArcanaBlocks.TAINTED_EUCALYPTUS_LEAVES.get(),
                ArcanaBlocks.TAINTED_GREATWOOD_LEAVES.get(),
                ArcanaBlocks.TAINTED_HAWTHORN_LEAVES.get(),
                ArcanaBlocks.TAINTED_WILLOW_LEAVES.get(),
                ArcanaBlocks.TAINTED_OAK_LEAVES.get(),
                ArcanaBlocks.TAINTED_DARKOAK_LEAVES.get(),
                ArcanaBlocks.TAINTED_ACACIA_LEAVES.get(),
                ArcanaBlocks.TAINTED_BIRCH_LEAVES.get(),
                ArcanaBlocks.TAINTED_JUNGLE_LEAVES.get(),
                ArcanaBlocks.TAINTED_SPRUCE_LEAVES.get());
        tag(BlockTags.FENCES).add(
                ArcanaBlocks.DAIR_FENCE.get(),
                ArcanaBlocks.DEAD_FENCE.get(),
                ArcanaBlocks.EUCALYPTUS_FENCE.get(),
                ArcanaBlocks.GREATWOOD_FENCE.get(),
                ArcanaBlocks.HAWTHORN_FENCE.get(),
                ArcanaBlocks.SILVERWOOD_FENCE.get(),
                ArcanaBlocks.TRYPOPHOBIUS_FENCE.get(),
                ArcanaBlocks.WILLOW_FENCE.get());
        tag(BlockTags.STAIRS).add(
                ArcanaBlocks.ARCANE_STONE_STAIRS.get(),
                ArcanaBlocks.ARCANE_STONE_BRICKS_STAIRS.get(),
                ArcanaBlocks.DAIR_STAIRS.get(),
                ArcanaBlocks.DEAD_STAIRS.get(),
                ArcanaBlocks.EUCALYPTUS_STAIRS.get(),
                ArcanaBlocks.GREATWOOD_STAIRS.get(),
                ArcanaBlocks.HAWTHORN_STAIRS.get(),
                ArcanaBlocks.SILVERWOOD_STAIRS.get(),
                ArcanaBlocks.TRYPOPHOBIUS_STAIRS.get(),
                ArcanaBlocks.WILLOW_STAIRS.get(),
                ArcanaBlocks.DUNGEON_BRICKS_STAIRS.get(),
                ArcanaBlocks.CRACKED_DUNGEON_BRICKS_STAIRS.get(),
                ArcanaBlocks.MOSSY_DUNGEON_BRICKS_STAIRS.get(),
                ArcanaBlocks.TAINTED_ANDESITE_STAIRS.get(),
                ArcanaBlocks.TAINTED_DIORITE_STAIRS.get(),
                ArcanaBlocks.TAINTED_GRANITE_STAIRS.get(),
                ArcanaBlocks.TAINTED_POLISHED_ANDESITE_STAIRS.get(),
                ArcanaBlocks.TAINTED_POLISHED_DIORITE_STAIRS.get(),
                ArcanaBlocks.TAINTED_POLISHED_GRANITE_STAIRS.get(),
                ArcanaBlocks.TAINTED_WILLOW_STAIRS.get(),
                ArcanaBlocks.TAINTED_DAIR_STAIRS.get(),
                ArcanaBlocks.TAINTED_BIRCH_STAIRS.get(),
                ArcanaBlocks.TAINTED_DARKOAK_STAIRS.get(),
                ArcanaBlocks.TAINTED_EUCALYPTUS_STAIRS.get(),
                ArcanaBlocks.TAINTED_GREATWOOD_STAIRS.get(),
                ArcanaBlocks.TAINTED_JUNGLE_STAIRS.get(),
                ArcanaBlocks.TAINTED_HAWTHORN_STAIRS.get(),
                ArcanaBlocks.TAINTED_OAK_STAIRS.get(),
                ArcanaBlocks.TAINTED_SPRUCE_STAIRS.get());
        tag(BlockTags.STONE_BRICKS).add(
                ArcanaBlocks.ARCANE_STONE_BRICKS.get(),
                ArcanaBlocks.DUNGEON_BRICKS.get(),
                ArcanaBlocks.CRACKED_DUNGEON_BRICKS.get(),
                ArcanaBlocks.MOSSY_DUNGEON_BRICKS.get());
        tag(BlockTags.WALLS).add(
                ArcanaBlocks.ARCANE_STONE_WALL.get(),
                ArcanaBlocks.ARCANE_STONE_BRICKS_WALL.get(),
                ArcanaBlocks.DUNGEON_BRICKS_WALL.get(),
                ArcanaBlocks.CRACKED_DUNGEON_BRICKS_WALL.get(),
                ArcanaBlocks.MOSSY_DUNGEON_BRICKS_WALL.get());

        tag(Tags.Blocks.STONE).add(ArcanaBlocks.ARCANE_STONE.get());

        tag(SILVER_ORE).add(ArcanaBlocks.SILVER_ORE.get());
        tag(AMBER_ORE).add(ArcanaBlocks.AMBER_ORE.get());
        tag(Tags.Blocks.ORES).addTag(SILVER_ORE);
        tag(Tags.Blocks.ORES).addTag(AMBER_ORE);

        tag(STORAGE_BLOCKS_THAUMIUM).add(ArcanaBlocks.THAUMIUM_BLOCK.get());
        tag(STORAGE_BLOCKS_ARCANIUM).add(ArcanaBlocks.ARCANIUM_BLOCK.get());
        tag(STORAGE_BLOCKS_SILVER).add(ArcanaBlocks.SILVER_BLOCK.get());
        tag(STORAGE_BLOCKS_VOID_METAL).add(ArcanaBlocks.VOID_METAL_BLOCK.get());
        tag(Tags.Blocks.STORAGE_BLOCKS).addTag(STORAGE_BLOCKS_THAUMIUM);
        tag(Tags.Blocks.STORAGE_BLOCKS).addTag(STORAGE_BLOCKS_ARCANIUM);
        tag(Tags.Blocks.STORAGE_BLOCKS).addTag(STORAGE_BLOCKS_SILVER);
        tag(Tags.Blocks.STORAGE_BLOCKS).addTag(STORAGE_BLOCKS_VOID_METAL);
    }
}
