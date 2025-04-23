package com.wonginnovations.arcana.datagen;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.items.ArcanaItems;
import javafx.scene.shape.Arc;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ArcanaItemTagsProvider extends ItemTagsProvider {

    public static final TagKey<Item> STORAGE_BLOCKS_THAUMIUM = ItemTags.create(Arcana.arcLoc("storages_blocks/thaumium"));
    public static final TagKey<Item> STORAGE_BLOCKS_ARCANIUM = ItemTags.create(Arcana.arcLoc("storage_blocks/arcanium"));
    public static final TagKey<Item> STORAGE_BLOCKS_SILVER = ItemTags.create(new ResourceLocation("forge", "storage_blocks/silver"));
    public static final TagKey<Item> STORAGE_BLOCKS_VOID_METAL = ItemTags.create(Arcana.arcLoc("storage_blocks/void_metal"));

    public static final TagKey<Item> THAUMIUM_INGOT = ItemTags.create(Arcana.arcLoc("ingots/thaumium"));
    public static final TagKey<Item> ARCANIUM_INGOT = ItemTags.create(Arcana.arcLoc("ingots/arcanium"));
    public static final TagKey<Item> SILVER_INGOT = ItemTags.create(new ResourceLocation("forge", "ingots/silver"));
    public static final TagKey<Item> VOID_METAL_INGOT = ItemTags.create(Arcana.arcLoc("ingots/void_metal"));

    public static final TagKey<Item> NETHERITE_NUGGET = ItemTags.create(new ResourceLocation("forge", "nuggets/netherite"));
    public static final TagKey<Item> SILVER_NUGGET = ItemTags.create(new ResourceLocation("forge", "nuggets/silver"));
    public static final TagKey<Item> VOID_METAL_NUGGET = ItemTags.create(Arcana.arcLoc("nuggets/void_metal"));

    public static final TagKey<Item> SILVER_ORE = ItemTags.create(new ResourceLocation("forge", "ores/silver"));
    public static final TagKey<Item> AMBER_ORE = ItemTags.create(Arcana.arcLoc("ores/amber"));

    public static final TagKey<Item> CRYSTALS = ItemTags.create(Arcana.arcLoc("crystals"));
    public static final TagKey<Item> PRIMAL_CRYSTALS = ItemTags.create(Arcana.arcLoc("primal_crystals"));

    public static final TagKey<Item> SCRIBING_TOOLS = ItemTags.create(Arcana.arcLoc("scribing_tools"));

    public static final TagKey<Item> SHULKER_BOXES = ItemTags.create(Arcana.arcLoc("shulker_boxes"));

    public ArcanaItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper fileHelper) {
        super(output, lookupProvider, blockTags, Arcana.MODID, fileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider) {
        tag(ItemTags.LOGS).add(
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DAIR_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DEAD_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.EUCALYPTUS_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.GREATWOOD_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.HAWTHORN_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.SILVERWOOD_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TRYPOPHOBIUS_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.WILLOW_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_DAIR_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_EUCALYPTUS_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_GREATWOOD_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_HAWTHORN_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_WILLOW_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_OAK_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_DARKOAK_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_ACACIA_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_BIRCH_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_JUNGLE_LOG).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_SPRUCE_LOG).get());
        tag(ItemTags.SLABS).add(
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.ARCANE_STONE_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.ARCANE_STONE_BRICKS_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DAIR_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DEAD_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.EUCALYPTUS_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.GREATWOOD_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.HAWTHORN_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.SILVERWOOD_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TRYPOPHOBIUS_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.WILLOW_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DUNGEON_BRICKS_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.CRACKED_DUNGEON_BRICKS_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.MOSSY_DUNGEON_BRICKS_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_ANDESITE_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_DIORITE_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_GRANITE_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_POLISHED_ANDESITE_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_POLISHED_DIORITE_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_POLISHED_GRANITE_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_WILLOW_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_DAIR_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_BIRCH_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_DARKOAK_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_EUCALYPTUS_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_GREATWOOD_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_JUNGLE_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_HAWTHORN_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_OAK_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_SPRUCE_SLAB).get());
        tag(ItemTags.WOODEN_SLABS).add(
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DAIR_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DEAD_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.EUCALYPTUS_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.GREATWOOD_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.HAWTHORN_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.SILVERWOOD_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TRYPOPHOBIUS_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.WILLOW_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_EUCALYPTUS_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_GREATWOOD_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_HAWTHORN_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_WILLOW_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_OAK_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_DARKOAK_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_ACACIA_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_BIRCH_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_JUNGLE_SLAB).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_SPRUCE_SLAB).get());
        tag(ItemTags.PLANKS).add(
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DAIR_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DEAD_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.EUCALYPTUS_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.GREATWOOD_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.HAWTHORN_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.SILVERWOOD_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TRYPOPHOBIUS_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.WILLOW_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_DAIR_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_EUCALYPTUS_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_GREATWOOD_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_HAWTHORN_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_WILLOW_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_OAK_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_DARKOAK_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_ACACIA_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_BIRCH_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_JUNGLE_PLANKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_SPRUCE_PLANKS).get());
        tag(ItemTags.FENCES).add(
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DAIR_FENCE).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DEAD_FENCE).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.EUCALYPTUS_FENCE).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.GREATWOOD_FENCE).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.HAWTHORN_FENCE).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.SILVERWOOD_FENCE).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TRYPOPHOBIUS_FENCE).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.WILLOW_FENCE).get());
        tag(ItemTags.STAIRS).add(
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.ARCANE_STONE_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.ARCANE_STONE_BRICKS_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DAIR_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DEAD_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.EUCALYPTUS_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.GREATWOOD_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.HAWTHORN_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.SILVERWOOD_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TRYPOPHOBIUS_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.WILLOW_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DUNGEON_BRICKS_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.CRACKED_DUNGEON_BRICKS_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.MOSSY_DUNGEON_BRICKS_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_ANDESITE_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_DIORITE_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_GRANITE_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_POLISHED_ANDESITE_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_POLISHED_DIORITE_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_POLISHED_GRANITE_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_WILLOW_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_DAIR_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_BIRCH_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_DARKOAK_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_EUCALYPTUS_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_GREATWOOD_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_JUNGLE_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_HAWTHORN_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_OAK_STAIRS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.TAINTED_SPRUCE_STAIRS).get());
        tag(ItemTags.STONE_BRICKS).add(
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.ARCANE_STONE_BRICKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.DUNGEON_BRICKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.CRACKED_DUNGEON_BRICKS).get(),
                ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.MOSSY_DUNGEON_BRICKS).get());

        tag(THAUMIUM_INGOT).add(ArcanaItems.THAUMIUM_INGOT.get());
        tag(ARCANIUM_INGOT).add(ArcanaItems.ARCANIUM_INGOT.get());
        tag(SILVER_INGOT).add(ArcanaItems.SILVER_INGOT.get());
        tag(VOID_METAL_INGOT).add(ArcanaItems.VOID_METAL_INGOT.get());
        tag(Tags.Items.INGOTS).addTag(THAUMIUM_INGOT);
        tag(Tags.Items.INGOTS).addTag(ARCANIUM_INGOT);
        tag(Tags.Items.INGOTS).addTag(SILVER_INGOT);
        tag(Tags.Items.INGOTS).addTag(VOID_METAL_INGOT);

        tag(NETHERITE_NUGGET).add(ArcanaItems.NETHERITE_NUGGET.get());
        tag(SILVER_NUGGET).add(ArcanaItems.SILVER_NUGGET.get());
        tag(VOID_METAL_NUGGET).add(ArcanaItems.VOID_METAL_NUGGET.get());
        tag(Tags.Items.NUGGETS).addTag(NETHERITE_NUGGET);
        tag(Tags.Items.NUGGETS).addTag(SILVER_NUGGET);
        tag(Tags.Items.NUGGETS).addTag(VOID_METAL_NUGGET);

        tag(Tags.Items.STONE).add(ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.ARCANE_STONE).get());

        tag(SILVER_ORE).add(ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.SILVER_ORE).get());
        tag(AMBER_ORE).add(ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.AMBER_ORE).get());
        tag(Tags.Items.ORES).addTag(SILVER_ORE);
        tag(Tags.Items.ORES).addTag(AMBER_ORE);

        tag(Tags.Items.RODS_WOODEN).add(ArcanaItems.SILVERWOOD_STICK.get());

        tag(STORAGE_BLOCKS_THAUMIUM).add(ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.THAUMIUM_BLOCK).get());
        tag(STORAGE_BLOCKS_ARCANIUM).add(ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.ARCANIUM_BLOCK).get());
        tag(STORAGE_BLOCKS_SILVER).add(ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.SILVER_BLOCK).get());
        tag(STORAGE_BLOCKS_VOID_METAL).add(ArcanaItems.BLOCK_ITEMS.get(ArcanaBlocks.VOID_METAL_BLOCK).get());
        tag(Tags.Items.STORAGE_BLOCKS).addTag(STORAGE_BLOCKS_THAUMIUM);
        tag(Tags.Items.STORAGE_BLOCKS).addTag(STORAGE_BLOCKS_ARCANIUM);
        tag(Tags.Items.STORAGE_BLOCKS).addTag(STORAGE_BLOCKS_SILVER);
        tag(Tags.Items.STORAGE_BLOCKS).addTag(STORAGE_BLOCKS_VOID_METAL);

        tag(SCRIBING_TOOLS).add(ArcanaItems.SCRIBING_TOOLS.get());

        tag(SHULKER_BOXES).add(
                Items.SHULKER_BOX,
                Items.WHITE_SHULKER_BOX,
                Items.ORANGE_SHULKER_BOX,
                Items.MAGENTA_SHULKER_BOX,
                Items.LIGHT_BLUE_SHULKER_BOX,
                Items.YELLOW_SHULKER_BOX,
                Items.LIME_SHULKER_BOX,
                Items.PINK_SHULKER_BOX,
                Items.GRAY_SHULKER_BOX,
                Items.LIGHT_GRAY_SHULKER_BOX,
                Items.CYAN_SHULKER_BOX,
                Items.PURPLE_SHULKER_BOX,
                Items.BLUE_SHULKER_BOX,
                Items.BROWN_SHULKER_BOX,
                Items.GREEN_SHULKER_BOX,
                Items.RED_SHULKER_BOX,
                Items.BLACK_SHULKER_BOX);

        for (Aspect aspect : AspectUtils.primalAspects) {
            tag(PRIMAL_CRYSTALS).add(AspectUtils.aspectCrystalItems.get(Aspects.AIR).get());
        }
        for (RegistryObject<Item> crystal : AspectUtils.aspectCrystalItems.values()) {
            tag(CRYSTALS).add(crystal.get());
        }
    }
}
