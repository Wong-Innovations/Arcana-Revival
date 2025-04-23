package com.wonginnovations.arcana.init;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.items.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ArcanaCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Arcana.MODID);

    public static final RegistryObject<CreativeModeTab> GENERAL = CREATIVE_MODE_TABS.register("arcana_general",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ArcanaBlocks.ARCANE_STONE.get()))
                    .title(Component.translatable("creativetab.arcana.general"))
                    .withSearchBar()
                    .displayItems((parameters, output) -> {
                        // Items
                        output.accept(ArcanaItems.MUSIC_DISC_MAIN.get());
                        output.accept(ArcanaItems.MUSIC_DISC_GREEN_SLEEVES.get());

                        output.accept(ArcanaItems.ARCANIUM_SWORD.get());
                        output.accept(ArcanaItems.ARCANIUM_SHOVEL.get());
                        output.accept(ArcanaItems.ARCANIUM_PICKAXE.get());
                        output.accept(ArcanaItems.ARCANIUM_AXE.get());
                        output.accept(ArcanaItems.ARCANIUM_HOE.get());

                        output.accept(ArcanaItems.ARCANIUM_HELMET.get());
                        output.accept(ArcanaItems.ARCANIUM_CHESTPLATE.get());
                        output.accept(ArcanaItems.ARCANIUM_LEGGINGS.get());
                        output.accept(ArcanaItems.ARCANIUM_BOOTS.get());

                        output.accept(ArcanaItems.SILVER_SWORD.get());
                        output.accept(ArcanaItems.SILVER_SHOVEL.get());
                        output.accept(ArcanaItems.SILVER_PICKAXE.get());
                        output.accept(ArcanaItems.SILVER_AXE.get());
                        output.accept(ArcanaItems.SILVER_HOE.get());

                        output.accept(ArcanaItems.VOID_METAL_SWORD.get());
                        output.accept(ArcanaItems.VOID_METAL_SHOVEL.get());
                        output.accept(ArcanaItems.VOID_METAL_PICKAXE.get());
                        output.accept(ArcanaItems.VOID_METAL_AXE.get());
                        output.accept(ArcanaItems.VOID_METAL_HOE.get());

                        output.accept(ArcanaItems.VOID_METAL_HELMET.get());
                        output.accept(ArcanaItems.VOID_METAL_CHESTPLATE.get());
                        output.accept(ArcanaItems.VOID_METAL_LEGGINGS.get());
                        output.accept(ArcanaItems.VOID_METAL_BOOTS.get());

                        output.accept(ArcanaItems.RESEARCH_NOTE_COMPLETE.get());
                        output.accept(ArcanaItems.RESEARCH_NOTE.get());
                        output.accept(ArcanaItems.SCRIBING_TOOLS.get());
                        output.accept(ArcanaItems.VIS_MANIPULATION_TOOLS.get());
                        output.accept(ArcanaItems.PHIAL.get());
                        output.accept(ArcanaItems.SCRIBBLED_NOTES_COMPLETE.get());
                        output.accept(ArcanaItems.SCRIBBLED_NOTES.get());
                        output.accept(ArcanaItems.TAINT_IN_A_BOTTLE.get());
                        output.accept(ArcanaItems.FLUX_METER.get());

                        output.accept(ArcanaItems.ARCANUM.get());
                        output.accept(ArcanaItems.CHEATERS_ARCANUM.get());
                        output.accept(ArcanaItems.GRIMOIRE.get());
                        output.accept(ArcanaItems.CODEX.get());
                        output.accept(ArcanaItems.RITES.get());

                        output.accept(ArcanaItems.LABEL.get());

                        output.accept(ArcanaItems.THAUMIUM_INGOT.get());
                        output.accept(ArcanaItems.ARCANIUM_INGOT.get());
                        output.accept(ArcanaItems.VOID_METAL_INGOT.get());
                        output.accept(ArcanaItems.VOID_METAL_NUGGET.get());
                        output.accept(ArcanaItems.SILVER_INGOT.get());
                        output.accept(ArcanaItems.NETHERITE_NUGGET.get());
                        output.accept(ArcanaItems.SILVER_NUGGET.get());
                        output.accept(ArcanaItems.THAUMIUM_NUGGET.get());

                        output.accept(ArcanaItems.SILVERWOOD_STICK.get());
                        output.accept(ArcanaItems.AMBER.get());
                        output.accept(ArcanaItems.ELDRITCH_CLOTH.get());

                        output.accept(ArcanaItems.PURIFIED_IRON.get());
                        output.accept(ArcanaItems.PURIFIED_GOLD.get());
                        output.accept(ArcanaItems.PURIFIED_SILVER.get());
                        output.accept(ArcanaItems.ALTERED_IRON.get());

                        output.accept(ArcanaItems.GOGGLES_OF_REVEALING.get());

                        output.accept(ArcanaItems.AIR_CRYSTAL_SEED.get());
                        output.accept(ArcanaItems.EARTH_CRYSTAL_SEED.get());
                        output.accept(ArcanaItems.FIRE_CRYSTAL_SEED.get());
                        output.accept(ArcanaItems.WATER_CRYSTAL_SEED.get());
                        output.accept(ArcanaItems.ORDER_CRYSTAL_SEED.get());
                        output.accept(ArcanaItems.CHAOS_CRYSTAL_SEED.get());

                        output.accept(ArcanaItems.IRON_CAP.get());
                        output.accept(ArcanaItems.GOLD_CAP.get());
                        output.accept(ArcanaItems.COPPER_CAP.get());
                        output.accept(ArcanaItems.SILVER_CAP.get());
                        output.accept(ArcanaItems.THAUMIUM_CAP.get());
                        output.accept(ArcanaItems.VOID_CAP.get());
                        output.accept(ArcanaItems.NETHERITE_CAP.get());
                        output.accept(ArcanaItems.PRISMARINE_CAP.get());
                        output.accept(ArcanaItems.BAMBOO_CAP.get());
                        output.accept(ArcanaItems.AMBER_CAP.get());
                        output.accept(ArcanaItems.QUARTZ_CAP.get());
                        output.accept(ArcanaItems.LEATHER_CAP.get());
                        output.accept(ArcanaItems.HONEY_CAP.get());
                        output.accept(ArcanaItems.SHULKER_CAP.get());
                        output.accept(ArcanaItems.MECHANICAL_CAP.get());
                        output.accept(ArcanaItems.ELDRITCH_CLOTH_CAP.get());
                        output.accept(ArcanaItems.CLAY_CAP.get());

                        output.accept(ArcanaItems.DEFAULT_FOCUS.get());
                        output.accept(ArcanaItems.FOCUS_PARTS.get());

                        output.accept(ArcanaItems.GREATWOOD_WAND_CORE.get());
                        output.accept(ArcanaItems.TAINTED_WAND_CORE.get());
                        output.accept(ArcanaItems.DAIR_WAND_CORE.get());
                        output.accept(ArcanaItems.HAWTHORN_WAND_CORE.get());
                        output.accept(ArcanaItems.SILVERWOOD_WAND_CORE.get());
                        output.accept(ArcanaItems.ARCANIUM_WAND_CORE.get());
                        output.accept(ArcanaItems.BLAZE_WAND_CORE.get());
                        output.accept(ArcanaItems.ENDROD_WAND_CORE.get());
                        output.accept(ArcanaItems.BONE_WAND_CORE.get());
                        output.accept(ArcanaItems.ICE_WAND_CORE.get());
                        output.accept(ArcanaItems.ARCANE_STONE_WAND_CORE.get());
                        output.accept(ArcanaItems.OBSIDIAN_WAND_CORE.get());
                        output.accept(ArcanaItems.SUGAR_CANE_WAND_CORE.get());
                        output.accept(ArcanaItems.MECHANICAL_WAND_CORE.get());
                        output.accept(ArcanaItems.ELDRITCH_WAND_CORE.get());
                        output.accept(ArcanaItems.CLAY_WAND_CORE.get());

                        output.accept(WandItem.withCapAndCore("arcana:iron_cap", "arcana:wood_wand"));
                        output.accept(WandItem.withCapAndCore("arcana:silver_cap", "arcana:dair_wand"));
                        output.accept(WandItem.withCapAndCore("arcana:gold_cap", "arcana:greatwood_wand"));
                        output.accept(WandItem.withCapAndCore("arcana:thaumium_cap", "arcana:silverwood_wand"));
                        output.accept(WandItem.withCapAndCore("arcana:void_cap", "arcana:arcanium_wand"));

                        output.accept(ScepterItem.withCapAndCore("arcana:iron_cap", "arcana:wood_wand"));
                        output.accept(ScepterItem.withCapAndCore("arcana:silver_cap", "arcana:dair_wand"));
                        output.accept(ScepterItem.withCapAndCore("arcana:gold_cap", "arcana:greatwood_wand"));
                        output.accept(ScepterItem.withCapAndCore("arcana:thaumium_cap", "arcana:silverwood_wand"));
                        output.accept(ScepterItem.withCapAndCore("arcana:void_cap", "arcana:arcanium_wand"));

                        output.accept(StaffItem.withCapAndCore("arcana:iron_cap", "arcana:wood_wand"));
                        output.accept(StaffItem.withCapAndCore("arcana:silver_cap", "arcana:dair_wand"));
                        output.accept(StaffItem.withCapAndCore("arcana:gold_cap", "arcana:greatwood_wand"));
                        output.accept(StaffItem.withCapAndCore("arcana:thaumium_cap", "arcana:silverwood_wand"));
                        output.accept(StaffItem.withCapAndCore("arcana:void_cap", "arcana:arcanium_wand"));

                        output.accept(ArcanaItems.GAUNTLET.get());

                        for (RegistryObject<Item> item : AspectUtils.aspectCrystalItems.values()) {
                            output.accept(item.get());
                        }

                        for (Aspect aspect : Aspects.getWithoutEmpty()) {
                            output.accept(((PhialItem) ArcanaItems.PHIAL.get()).withAspect(aspect));
                        }

                        // TODO: Ensure FOCI forge and research table show

                        output.accept(ArcanaItems.RAW_DOG_MEAT.get());
                        output.accept(ArcanaItems.RAW_CAT_MEAT.get());
                        output.accept(ArcanaItems.COOKED_DOG_MEAT.get());
                        output.accept(ArcanaItems.COOKED_CAT_MEAT.get());

                        output.accept(ArcanaItems.ARCANIUM_FILTER.get());
                        output.accept(ArcanaItems.SILVERWOOD_FILTER.get());
                        output.accept(ArcanaItems.GREATWOOD_FILTER.get());
                        output.accept(ArcanaItems.HAWTHORN_FILTER.get());
                        output.accept(ArcanaItems.DAIR_FILTER.get());
                        output.accept(ArcanaItems.WILLOW_FILTER.get());
                        output.accept(ArcanaItems.EUCALYPTUS_FILTER.get());

                        // Blocks
                        output.accept(ArcanaBlocks.ARCANE_STONE.get());
                        output.accept(ArcanaBlocks.ARCANE_STONE_SLAB.get());
                        output.accept(ArcanaBlocks.ARCANE_STONE_STAIRS.get());
                        output.accept(ArcanaBlocks.ARCANE_STONE_PRESSURE_PLATE.get());
                        output.accept(ArcanaBlocks.ARCANE_STONE_WALL.get());

                        output.accept(ArcanaBlocks.ARCANE_STONE_BRICKS.get());
                        output.accept(ArcanaBlocks.ARCANE_STONE_BRICKS_SLAB.get());
                        output.accept(ArcanaBlocks.ARCANE_STONE_BRICKS_STAIRS.get());
                        output.accept(ArcanaBlocks.ARCANE_STONE_BRICKS_PRESSURE_PLATE.get());
                        output.accept(ArcanaBlocks.ARCANE_STONE_BRICKS_WALL.get());

                        output.accept(ArcanaBlocks.DUNGEON_BRICKS.get());
                        output.accept(ArcanaBlocks.DUNGEON_BRICKS_SLAB.get());
                        output.accept(ArcanaBlocks.DUNGEON_BRICKS_STAIRS.get());
                        output.accept(ArcanaBlocks.DUNGEON_BRICKS_PRESSURE_PLATE.get());
                        output.accept(ArcanaBlocks.DUNGEON_BRICKS_WALL.get());

                        output.accept(ArcanaBlocks.CRACKED_DUNGEON_BRICKS.get());
                        output.accept(ArcanaBlocks.CRACKED_DUNGEON_BRICKS_SLAB.get());
                        output.accept(ArcanaBlocks.CRACKED_DUNGEON_BRICKS_STAIRS.get());
                        output.accept(ArcanaBlocks.CRACKED_DUNGEON_BRICKS_PRESSURE_PLATE.get());
                        output.accept(ArcanaBlocks.CRACKED_DUNGEON_BRICKS_WALL.get());

                        output.accept(ArcanaBlocks.MOSSY_DUNGEON_BRICKS.get());
                        output.accept(ArcanaBlocks.MOSSY_DUNGEON_BRICKS_SLAB.get());
                        output.accept(ArcanaBlocks.MOSSY_DUNGEON_BRICKS_STAIRS.get());
                        output.accept(ArcanaBlocks.MOSSY_DUNGEON_BRICKS_PRESSURE_PLATE.get());
                        output.accept(ArcanaBlocks.MOSSY_DUNGEON_BRICKS_WALL.get());

                        output.accept(ArcanaBlocks.HARDENED_GLASS.get());
                        output.accept(ArcanaBlocks.SMOKEY_GLASS.get());
                        output.accept(ArcanaBlocks.LUMINIFEROUS_GLASS.get());

                        output.accept(ArcanaBlocks.AMBER_ORE.get());
                        output.accept(ArcanaBlocks.SILVER_ORE.get());

                        output.accept(ArcanaBlocks.INFUSION_ARCANE_STONE.get());
                        output.accept(ArcanaBlocks.TABLE.get());
                        output.accept(ArcanaBlocks.FOCI_FORGE.get());
                        output.accept(ArcanaBlocks.NITOR.get());

                        output.accept(ArcanaBlocks.JAR.get());
                        output.accept(ArcanaBlocks.SECURE_JAR.get());
                        output.accept(ArcanaBlocks.VOID_JAR.get());
                        output.accept(ArcanaBlocks.VACUUM_JAR.get());
                        output.accept(ArcanaBlocks.PRESSURE_JAR.get());

                        output.accept(ArcanaBlocks.ASPECT_BOOKSHELF.get());
                        output.accept(ArcanaBlocks.ASPECT_BOOKSHELF_BLOCK.get());
                        output.accept(ArcanaBlocks.RESEARCH_TABLE.get());
                        output.accept(ArcanaBlocks.ARCANE_CRAFTING_TABLE.get());
                        output.accept(ArcanaBlocks.CRUCIBLE.get());
                        output.accept(ArcanaBlocks.ALEMBIC.get());
                        output.accept(ArcanaBlocks.PEDESTAL.get());
                        output.accept(ArcanaBlocks.ASPECT_TESTER.get());
                        output.accept(ArcanaBlocks.ASPECT_TUBE.get());
                        output.accept(ArcanaBlocks.ASPECT_VALVE.get());
                        output.accept(ArcanaBlocks.ASPECT_WINDOW.get());
                        output.accept(ArcanaBlocks.ASPECT_PUMP.get());
                        output.accept(ArcanaBlocks.ASPECT_CRYSTALLIZER.get());

                        output.accept(ArcanaBlocks.TAINT_SCRUBBER_MK1.get());
                        output.accept(ArcanaBlocks.TAINT_SCRUBBER_MK2.get());
                        output.accept(ArcanaBlocks.TAINT_BOOSTER.get());
                        output.accept(ArcanaBlocks.TAINT_SUCKER.get());
                        output.accept(ArcanaBlocks.TAINT_BORE.get());

                        output.accept(ArcanaBlocks.SEE_NO_EVIL_STATUE.get());
                        output.accept(ArcanaBlocks.HEAR_NO_EVIL_STATUE.get());
                        output.accept(ArcanaBlocks.SPEAK_NO_EVIL_STATUE.get());

                        output.accept(ArcanaBlocks.MAGIC_MUSHROOM.get());

                        output.accept(ArcanaBlocks.DAIR_LEAVES.get());
                        output.accept(ArcanaBlocks.DAIR_LOG.get());
                        output.accept(ArcanaBlocks.STRIPPED_DAIR_LOG.get());
                        output.accept(ArcanaBlocks.STRIPPED_DAIR_WOOD.get());
                        output.accept(ArcanaBlocks.DAIR_WOOD.get());
                        output.accept(ArcanaBlocks.DAIR_PLANKS.get());
                        output.accept(ArcanaBlocks.DAIR_DOOR.get());
                        output.accept(ArcanaBlocks.DAIR_TRAPDOOR.get());
                        output.accept(ArcanaBlocks.DAIR_PRESSURE_PLATE.get());
                        output.accept(ArcanaBlocks.DAIR_SAPLING.get());
                        output.accept(ArcanaBlocks.DAIR_SLAB.get());
                        output.accept(ArcanaBlocks.DAIR_STAIRS.get());
                        output.accept(ArcanaBlocks.DAIR_BUTTON.get());
                        output.accept(ArcanaBlocks.DAIR_FENCE.get());
                        output.accept(ArcanaBlocks.DAIR_FENCE_GATE.get());

                        output.accept(ArcanaBlocks.EUCALYPTUS_LEAVES.get());
                        output.accept(ArcanaBlocks.EUCALYPTUS_LOG.get());
                        output.accept(ArcanaBlocks.STRIPPED_EUCALYPTUS_LOG.get());
                        output.accept(ArcanaBlocks.STRIPPED_EUCALYPTUS_WOOD.get());
                        output.accept(ArcanaBlocks.EUCALYPTUS_WOOD.get());
                        output.accept(ArcanaBlocks.EUCALYPTUS_PLANKS.get());
                        output.accept(ArcanaBlocks.EUCALYPTUS_DOOR.get());
                        output.accept(ArcanaBlocks.EUCALYPTUS_TRAPDOOR.get());
                        output.accept(ArcanaBlocks.EUCALYPTUS_PRESSURE_PLATE.get());
                        output.accept(ArcanaBlocks.EUCALYPTUS_SAPLING.get());
                        output.accept(ArcanaBlocks.EUCALYPTUS_SLAB.get());
                        output.accept(ArcanaBlocks.EUCALYPTUS_STAIRS.get());
                        output.accept(ArcanaBlocks.EUCALYPTUS_BUTTON.get());
                        output.accept(ArcanaBlocks.EUCALYPTUS_FENCE.get());
                        output.accept(ArcanaBlocks.EUCALYPTUS_FENCE_GATE.get());

                        output.accept(ArcanaBlocks.GREATWOOD_LEAVES.get());
                        output.accept(ArcanaBlocks.GREATWOOD_LOG.get());
                        output.accept(ArcanaBlocks.STRIPPED_GREATWOOD_LOG.get());
                        output.accept(ArcanaBlocks.STRIPPED_GREATWOOD_WOOD.get());
                        output.accept(ArcanaBlocks.GREATWOOD_WOOD.get());
                        output.accept(ArcanaBlocks.GREATWOOD_PLANKS.get());
                        output.accept(ArcanaBlocks.GREATWOOD_DOOR.get());
                        output.accept(ArcanaBlocks.GREATWOOD_TRAPDOOR.get());
                        output.accept(ArcanaBlocks.GREATWOOD_PRESSURE_PLATE.get());
                        output.accept(ArcanaBlocks.GREATWOOD_SAPLING.get());
                        output.accept(ArcanaBlocks.GREATWOOD_SLAB.get());
                        output.accept(ArcanaBlocks.GREATWOOD_STAIRS.get());
                        output.accept(ArcanaBlocks.GREATWOOD_BUTTON.get());
                        output.accept(ArcanaBlocks.GREATWOOD_FENCE.get());
                        output.accept(ArcanaBlocks.GREATWOOD_FENCE_GATE.get());

                        output.accept(ArcanaBlocks.HAWTHORN_LEAVES.get());
                        output.accept(ArcanaBlocks.HAWTHORN_LOG.get());
                        output.accept(ArcanaBlocks.STRIPPED_HAWTHORN_LOG.get());
                        output.accept(ArcanaBlocks.STRIPPED_HAWTHORN_WOOD.get());
                        output.accept(ArcanaBlocks.HAWTHORN_WOOD.get());
                        output.accept(ArcanaBlocks.HAWTHORN_PLANKS.get());
                        output.accept(ArcanaBlocks.HAWTHORN_DOOR.get());
                        output.accept(ArcanaBlocks.HAWTHORN_TRAPDOOR.get());
                        output.accept(ArcanaBlocks.HAWTHORN_PRESSURE_PLATE.get());
                        output.accept(ArcanaBlocks.HAWTHORN_SAPLING.get());
                        output.accept(ArcanaBlocks.HAWTHORN_SLAB.get());
                        output.accept(ArcanaBlocks.HAWTHORN_STAIRS.get());
                        output.accept(ArcanaBlocks.HAWTHORN_BUTTON.get());
                        output.accept(ArcanaBlocks.HAWTHORN_FENCE.get());
                        output.accept(ArcanaBlocks.HAWTHORN_FENCE_GATE.get());

                        output.accept(ArcanaBlocks.SILVERWOOD_LEAVES.get());
                        output.accept(ArcanaBlocks.SILVERWOOD_LOG.get());
                        output.accept(ArcanaBlocks.STRIPPED_SILVERWOOD_LOG.get());
                        output.accept(ArcanaBlocks.STRIPPED_SILVERWOOD_WOOD.get());
                        output.accept(ArcanaBlocks.SILVERWOOD_WOOD.get());
                        output.accept(ArcanaBlocks.SILVERWOOD_PLANKS.get());
                        output.accept(ArcanaBlocks.SILVERWOOD_DOOR.get());
                        output.accept(ArcanaBlocks.SILVERWOOD_TRAPDOOR.get());
                        output.accept(ArcanaBlocks.SILVERWOOD_PRESSURE_PLATE.get());
                        output.accept(ArcanaBlocks.SILVERWOOD_SAPLING.get());
                        output.accept(ArcanaBlocks.SILVERWOOD_SLAB.get());
                        output.accept(ArcanaBlocks.SILVERWOOD_STAIRS.get());
                        output.accept(ArcanaBlocks.SILVERWOOD_BUTTON.get());
                        output.accept(ArcanaBlocks.SILVERWOOD_FENCE.get());
                        output.accept(ArcanaBlocks.SILVERWOOD_FENCE_GATE.get());

                        output.accept(ArcanaBlocks.WILLOW_LEAVES.get());
                        output.accept(ArcanaBlocks.WILLOW_LOG.get());
                        output.accept(ArcanaBlocks.STRIPPED_WILLOW_LOG.get());
                        output.accept(ArcanaBlocks.STRIPPED_WILLOW_WOOD.get());
                        output.accept(ArcanaBlocks.WILLOW_WOOD.get());
                        output.accept(ArcanaBlocks.WILLOW_PLANKS.get());
                        output.accept(ArcanaBlocks.WILLOW_DOOR.get());
                        output.accept(ArcanaBlocks.WILLOW_TRAPDOOR.get());
                        output.accept(ArcanaBlocks.WILLOW_PRESSURE_PLATE.get());
                        output.accept(ArcanaBlocks.WILLOW_SAPLING.get());
                        output.accept(ArcanaBlocks.WILLOW_SLAB.get());
                        output.accept(ArcanaBlocks.WILLOW_STAIRS.get());
                        output.accept(ArcanaBlocks.WILLOW_BUTTON.get());
                        output.accept(ArcanaBlocks.WILLOW_FENCE.get());
                        output.accept(ArcanaBlocks.WILLOW_FENCE_GATE.get());

                        output.accept(ArcanaBlocks.ARCANIUM_BLOCK.get());
                        output.accept(ArcanaBlocks.THAUMIUM_BLOCK.get());
                        output.accept(ArcanaBlocks.VOID_METAL_BLOCK.get());
                        output.accept(ArcanaBlocks.SILVER_BLOCK.get());

                        for (RegistryObject<Item> item : ArcanaItems.CRYSTAL_ITEMS.values()) {
                            output.accept(item.get());
                        }

                        output.accept(ArcanaBlocks.DEAD_GRASS_BLOCK.get());
                        output.accept(ArcanaBlocks.DEAD_GRASS.get());
                        output.accept(ArcanaBlocks.DEAD_FLOWER.get());

                        output.accept(ArcanaBlocks.DEAD_LOG.get());
                        output.accept(ArcanaBlocks.DEAD_PLANKS.get());
                        output.accept(ArcanaBlocks.DEAD_PRESSURE_PLATE.get());
                        output.accept(ArcanaBlocks.DEAD_SLAB.get());
                        output.accept(ArcanaBlocks.DEAD_STAIRS.get());
                        output.accept(ArcanaBlocks.DEAD_BUTTON.get());
                        output.accept(ArcanaBlocks.DEAD_FENCE.get());
                        output.accept(ArcanaBlocks.DEAD_FENCE_GATE.get());

                        output.accept(ArcanaBlocks.TRYPOPHOBIUS_LOG.get());
                        output.accept(ArcanaBlocks.TRYPOPHOBIUS_PLANKS.get());
                        output.accept(ArcanaBlocks.TRYPOPHOBIUS_PRESSURE_PLATE.get());
                        output.accept(ArcanaBlocks.TRYPOPHOBIUS_SLAB.get());
                        output.accept(ArcanaBlocks.TRYPOPHOBIUS_STAIRS.get());
                        output.accept(ArcanaBlocks.TRYPOPHOBIUS_BUTTON.get());
                        output.accept(ArcanaBlocks.TRYPOPHOBIUS_FENCE.get());
                        output.accept(ArcanaBlocks.TRYPOPHOBIUS_FENCE_GATE.get());
                    }).build());

    public static final RegistryObject<CreativeModeTab> TAINTED_ITEMS = CREATIVE_MODE_TABS.register("arcana_taint_items",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ArcanaItems.RAW_TAINTED_COD.get()))
                    .title(Component.translatable("creativetab.arcana.taint_items"))
                    .displayItems((parameters, output) -> {
                        output.accept(ArcanaItems.TAINTED_MELON_SLICE.get());
                        output.accept(ArcanaItems.TAINTED_POTATO.get());
                        output.accept(ArcanaItems.BAKED_TAINTED_POTATO.get());
                        output.accept(ArcanaItems.TAINTED_BEETROOT.get());
                        output.accept(ArcanaItems.TAINTED_CARROT.get());
                        output.accept(ArcanaItems.TAINTED_BERRIES.get());

                        output.accept(ArcanaItems.RAW_TAINTED_COD.get());
                        output.accept(ArcanaItems.COOKED_TAINTED_COD.get());
                        output.accept(ArcanaItems.RAW_TAINTED_SALMON.get());
                        output.accept(ArcanaItems.COOKED_TAINTED_SALMON.get());
                        output.accept(ArcanaItems.TAINTED_TROPICAL_FISH.get());
                        output.accept(ArcanaItems.TAINTED_PUFFERFISH.get());
                        output.accept(ArcanaItems.TAINTED_SUGAR_CANE.get());
                        output.accept(ArcanaItems.TAINTED_SNOWBALL.get());

                        output.accept(ArcanaItems.TAINT_FLUID_BUCKET.get());
                    }).build());

    public static final RegistryObject<CreativeModeTab> TAINTED_BLOCKS = CREATIVE_MODE_TABS.register("arcana_taint_blocks",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ArcanaBlocks.TAINTED_GRASS_BLOCK.get()))
                    .title(Component.translatable("creativetab.arcana.taint_blocks"))
                    .displayItems((parameters, output) -> {
                        output.accept(ArcanaBlocks.TAINTED_AMBER_ORE.get());
                        output.accept(ArcanaBlocks.TAINTED_SILVER_ORE.get());

                        output.accept(ArcanaBlocks.TAINTED_DAIR_LEAVES.get());
                        output.accept(ArcanaBlocks.TAINTED_DAIR_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_STRIPPED_DAIR_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_STRIPPED_DAIR_WOOD.get());
                        output.accept(ArcanaBlocks.TAINTED_DAIR_WOOD.get());
                        output.accept(ArcanaBlocks.TAINTED_DAIR_PLANKS.get());
                        output.accept(ArcanaBlocks.TAINTED_DAIR_SAPLING.get());
                        output.accept(ArcanaBlocks.TAINTED_DAIR_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_DAIR_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_EUCALYPTUS_LEAVES.get());
                        output.accept(ArcanaBlocks.TAINTED_EUCALYPTUS_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_STRIPPED_EUCALYPTUS_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_STRIPPED_EUCALYPTUS_WOOD.get());
                        output.accept(ArcanaBlocks.TAINTED_EUCALYPTUS_WOOD.get());
                        output.accept(ArcanaBlocks.TAINTED_EUCALYPTUS_PLANKS.get());
                        output.accept(ArcanaBlocks.TAINTED_EUCALYPTUS_SAPLING.get());
                        output.accept(ArcanaBlocks.TAINTED_EUCALYPTUS_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_EUCALYPTUS_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_GREATWOOD_LEAVES.get());
                        output.accept(ArcanaBlocks.TAINTED_GREATWOOD_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_STRIPPED_GREATWOOD_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_STRIPPED_GREATWOOD_WOOD.get());
                        output.accept(ArcanaBlocks.TAINTED_GREATWOOD_WOOD.get());
                        output.accept(ArcanaBlocks.TAINTED_GREATWOOD_PLANKS.get());
                        output.accept(ArcanaBlocks.TAINTED_GREATWOOD_SAPLING.get());
                        output.accept(ArcanaBlocks.TAINTED_GREATWOOD_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_GREATWOOD_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_HAWTHORN_LEAVES.get());
                        output.accept(ArcanaBlocks.TAINTED_HAWTHORN_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_STRIPPED_HAWTHORN_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_STRIPPED_HAWTHORN_WOOD.get());
                        output.accept(ArcanaBlocks.TAINTED_HAWTHORN_WOOD.get());
                        output.accept(ArcanaBlocks.TAINTED_HAWTHORN_PLANKS.get());
                        output.accept(ArcanaBlocks.TAINTED_HAWTHORN_SAPLING.get());
                        output.accept(ArcanaBlocks.TAINTED_HAWTHORN_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_HAWTHORN_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_WILLOW_LEAVES.get());
                        output.accept(ArcanaBlocks.TAINTED_WILLOW_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_STRIPPED_WILLOW_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_STRIPPED_WILLOW_WOOD.get());
                        output.accept(ArcanaBlocks.TAINTED_WILLOW_WOOD.get());
                        output.accept(ArcanaBlocks.TAINTED_WILLOW_PLANKS.get());
                        output.accept(ArcanaBlocks.TAINTED_WILLOW_SAPLING.get());
                        output.accept(ArcanaBlocks.TAINTED_WILLOW_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_WILLOW_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_ARCANIUM_BLOCK.get());
                        output.accept(ArcanaBlocks.TAINTED_THAUMIUM_BLOCK.get());

                        output.accept(ArcanaBlocks.TAINTED_CRUST.get());
//                        output.accept(ArcanaBlocks.TAINTED_CRUST_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_GRAVEL.get());
                        output.accept(ArcanaBlocks.TAINTED_SAND.get());

                        output.accept(ArcanaBlocks.TAINTED_ANDESITE.get());
                        output.accept(ArcanaBlocks.TAINTED_DIORITE.get());
                        output.accept(ArcanaBlocks.TAINTED_GRANITE.get());
                        output.accept(ArcanaBlocks.TAINTED_ROCK.get());
                        output.accept(ArcanaBlocks.TAINTED_ROCK_SLAB.get());

                        output.accept(ArcanaBlocks.TAINTED_SOIL.get());
                        output.accept(ArcanaBlocks.TAINTED_GRASS_BLOCK.get());
                        output.accept(ArcanaBlocks.TAINTED_PODZOL.get());
                        output.accept(ArcanaBlocks.TAINTED_FARMLAND.get());
                        output.accept(ArcanaBlocks.TAINTED_PATH.get());

                        output.accept(ArcanaBlocks.TAINTED_COAL_BLOCK.get());
                        output.accept(ArcanaBlocks.TAINTED_EMERALD_BLOCK.get());
                        output.accept(ArcanaBlocks.TAINTED_DIAMOND_BLOCK.get());
                        output.accept(ArcanaBlocks.TAINTED_GOLD_BLOCK.get());
                        output.accept(ArcanaBlocks.TAINTED_IRON_BLOCK.get());
                        output.accept(ArcanaBlocks.TAINTED_LAPIS_BLOCK.get());
                        output.accept(ArcanaBlocks.TAINTED_REDSTONE_BLOCK.get());

                        output.accept(ArcanaBlocks.TAINTED_VINE.get());
                        output.accept(ArcanaBlocks.TAINTED_GRASS.get());
                        output.accept(ArcanaBlocks.TAINTED_FLOWER.get());

                        output.accept(ArcanaBlocks.TAINTED_CARVED_PUMPKIN.get());
                        output.accept(ArcanaBlocks.TAINTED_JACK_OLANTERN.get());
                        output.accept(ArcanaBlocks.TAINTED_MELON.get());
                        output.accept(ArcanaBlocks.TAINTED_MUSHROOM.get());
                        output.accept(ArcanaBlocks.TAINTED_PUMPKIN.get());

                        output.accept(ArcanaBlocks.TAINTED_COAL_ORE.get());
                        output.accept(ArcanaBlocks.TAINTED_IRON_ORE.get());
                        output.accept(ArcanaBlocks.TAINTED_GOLD_ORE.get());
                        output.accept(ArcanaBlocks.TAINTED_DIAMOND_ORE.get());
                        output.accept(ArcanaBlocks.TAINTED_LAPIS_ORE.get());
                        output.accept(ArcanaBlocks.TAINTED_EMERALD_ORE.get());
                        output.accept(ArcanaBlocks.TAINTED_REDSTONE_ORE.get());

                        output.accept(ArcanaBlocks.TAINTED_ACACIA_LEAVES.get());
                        output.accept(ArcanaBlocks.TAINTED_ACACIA_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_ACACIA_PLANKS.get());
                        output.accept(ArcanaBlocks.TAINTED_ACACIA_SAPLING.get());
                        output.accept(ArcanaBlocks.TAINTED_ACACIA_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_ACACIA_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_BIRCH_LEAVES.get());
                        output.accept(ArcanaBlocks.TAINTED_BIRCH_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_BIRCH_PLANKS.get());
                        output.accept(ArcanaBlocks.TAINTED_BIRCH_SAPLING.get());
                        output.accept(ArcanaBlocks.TAINTED_BIRCH_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_BIRCH_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_DARKOAK_LEAVES.get());
                        output.accept(ArcanaBlocks.TAINTED_DARKOAK_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_DARKOAK_PLANKS.get());
                        output.accept(ArcanaBlocks.TAINTED_DARKOAK_SAPLING.get());
                        output.accept(ArcanaBlocks.TAINTED_DARKOAK_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_DARKOAK_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_JUNGLE_LEAVES.get());
                        output.accept(ArcanaBlocks.TAINTED_JUNGLE_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_JUNGLE_PLANKS.get());
                        output.accept(ArcanaBlocks.TAINTED_JUNGLE_SAPLING.get());
                        output.accept(ArcanaBlocks.TAINTED_JUNGLE_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_JUNGLE_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_OAK_LEAVES.get());
                        output.accept(ArcanaBlocks.TAINTED_OAK_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_OAK_PLANKS.get());
                        output.accept(ArcanaBlocks.TAINTED_OAK_SAPLING.get());
                        output.accept(ArcanaBlocks.TAINTED_OAK_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_OAK_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_SPRUCE_LEAVES.get());
                        output.accept(ArcanaBlocks.TAINTED_SPRUCE_LOG.get());
                        output.accept(ArcanaBlocks.TAINTED_SPRUCE_PLANKS.get());
                        output.accept(ArcanaBlocks.TAINTED_SPRUCE_SAPLING.get());
                        output.accept(ArcanaBlocks.TAINTED_SPRUCE_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_SPRUCE_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_POLISHED_GRANITE.get());
                        output.accept(ArcanaBlocks.TAINTED_POLISHED_GRANITE_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_POLISHED_GRANITE_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_POLISHED_DIORITE.get());
                        output.accept(ArcanaBlocks.TAINTED_POLISHED_DIORITE_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_POLISHED_DIORITE_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_POLISHED_ANDESITE.get());
                        output.accept(ArcanaBlocks.TAINTED_POLISHED_ANDESITE_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_POLISHED_ANDESITE_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_ANDESITE_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_ANDESITE_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_DIORITE_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_DIORITE_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_GRANITE_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_GRANITE_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_BRICKS.get());
                        output.accept(ArcanaBlocks.TAINTED_BRICKS_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_BRICKS_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_STONE_BRICKS.get());
                        output.accept(ArcanaBlocks.TAINTED_STONE_BRICKS_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_STONE_BRICKS_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_PRISMARINE.get());
                        output.accept(ArcanaBlocks.TAINTED_PRISMARINE_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_PRISMARINE_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_PRISMARINE_BRICKS.get());
                        output.accept(ArcanaBlocks.TAINTED_PRISMARINE_BRICKS_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_PRISMARINE_BRICKS_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_DARK_PRISMARINE.get());
                        output.accept(ArcanaBlocks.TAINTED_DARK_PRISMARINE_SLAB.get());
                        output.accept(ArcanaBlocks.TAINTED_DARK_PRISMARINE_STAIRS.get());

                        output.accept(ArcanaBlocks.TAINTED_SNOW_BLOCK.get());
                        output.accept(ArcanaBlocks.TAINTED_SNOW.get());

                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }

}
