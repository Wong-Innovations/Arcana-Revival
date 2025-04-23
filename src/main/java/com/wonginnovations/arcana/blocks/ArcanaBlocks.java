package com.wonginnovations.arcana.blocks;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.ArcanaBlockSetTypes;
import com.wonginnovations.arcana.ArcanaSounds;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.blocks.bases.*;
import com.wonginnovations.arcana.blocks.multiblocks.foci_forge.FociForgeComponentBlock;
import com.wonginnovations.arcana.blocks.multiblocks.foci_forge.FociForgeCoreBlock;
import com.wonginnovations.arcana.blocks.multiblocks.research_table.ResearchTableComponentBlock;
import com.wonginnovations.arcana.blocks.multiblocks.research_table.ResearchTableCoreBlock;
import com.wonginnovations.arcana.blocks.multiblocks.taint_scrubber.BoosterTaintScrubberExtensionBlock;
import com.wonginnovations.arcana.blocks.multiblocks.taint_scrubber.TaintScrubberBlock;
import com.wonginnovations.arcana.blocks.multiblocks.taint_scrubber.TaintScrubberExtensionBlock;
import com.wonginnovations.arcana.blocks.pipes.PipeWindowBlock;
import com.wonginnovations.arcana.blocks.pipes.PumpBlock;
import com.wonginnovations.arcana.blocks.pipes.TubeBlock;
import com.wonginnovations.arcana.blocks.pipes.ValveBlock;
import com.wonginnovations.arcana.blocks.tainted.TaintedSaplingBlock;
import com.wonginnovations.arcana.fluids.ArcanaFluids;
import com.wonginnovations.arcana.fluids.TaintFluid;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.items.CrystalClusterItem;
import com.wonginnovations.arcana.util.annotations.GIM;
import com.wonginnovations.arcana.util.annotations.GLT;
import com.wonginnovations.arcana.worldgen.trees.DummyTree;
import com.wonginnovations.arcana.worldgen.trees.GreatwoodTreeGrower;
import com.wonginnovations.arcana.worldgen.trees.SilverwoodTreeGrower;
import com.wonginnovations.arcana.systems.taint.TaintedGreatwoodTree;
import com.wonginnovations.arcana.systems.taint.TaintedOakTree;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.PressurePlateBlock.Sensitivity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;
import java.util.function.ToIntFunction;

import static com.wonginnovations.arcana.systems.taint.Taint.deadOf;
import static com.wonginnovations.arcana.systems.taint.Taint.taintedOf;
import static com.wonginnovations.arcana.util.annotations.GIM.Type.BLOCK_REF;

@SuppressWarnings("unused")
public class ArcanaBlocks {

	private static ToIntFunction<BlockState> light(int level) {
		return (state) -> level;
	}
	
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Arcana.MODID);

	// General
	// Arcane Stone
	@GLT public static final RegistryObject<Block> ARCANE_STONE = registerBlockItem("arcane_stone", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> ARCANE_STONE_SLAB = registerBlockItem("arcane_stone_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> ARCANE_STONE_STAIRS = registerBlockItem("arcane_stone_stairs", () -> new StairBlock(ARCANE_STONE.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> ARCANE_STONE_PRESSURE_PLATE = registerBlockItem("arcane_stone_pressure_plate", () -> new PressurePlateBlock(Sensitivity.MOBS, BlockBehaviour.Properties.of().strength(.5f).noCollission(), ArcanaBlockSetTypes.ARCANE_STONE));
	@GLT public static final RegistryObject<Block> ARCANE_STONE_WALL = registerBlockItem("arcane_stone_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(ARCANE_STONE.get())));
	
	// Arcane Stone Bricks
	@GLT public static final RegistryObject<Block> ARCANE_STONE_BRICKS = registerBlockItem("arcane_stone_bricks", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> ARCANE_STONE_BRICKS_SLAB = registerBlockItem("arcane_stone_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> ARCANE_STONE_BRICKS_STAIRS = registerBlockItem("arcane_stone_bricks_stairs", () -> new StairBlock(ARCANE_STONE_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> ARCANE_STONE_BRICKS_PRESSURE_PLATE = registerBlockItem("arcane_stone_bricks_pressure_plate", () -> new PressurePlateBlock(Sensitivity.MOBS, BlockBehaviour.Properties.of().strength(.5f).noCollission(), ArcanaBlockSetTypes.ARCANE_STONE_BRICK));
	@GLT public static final RegistryObject<Block> ARCANE_STONE_BRICKS_WALL = registerBlockItem("arcane_stone_bricks_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(ARCANE_STONE_BRICKS.get())));
	
	// Dungeon Bricks
	@GLT public static final RegistryObject<Block> DUNGEON_BRICKS = registerBlockItem("dungeon_bricks", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> DUNGEON_BRICKS_SLAB = registerBlockItem("dungeon_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> DUNGEON_BRICKS_STAIRS = registerBlockItem("dungeon_bricks_stairs", () -> new StairBlock(DUNGEON_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> DUNGEON_BRICKS_PRESSURE_PLATE = registerBlockItem("dungeon_bricks_pressure_plate", () -> new PressurePlateBlock(Sensitivity.MOBS, BlockBehaviour.Properties.of().strength(.5f).noCollission(), ArcanaBlockSetTypes.DUNGEON_BRICK));
	@GLT public static final RegistryObject<Block> DUNGEON_BRICKS_WALL = registerBlockItem("dungeon_bricks_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(DUNGEON_BRICKS.get())));
	
	// Cracked Dungeon Bricks
	@GLT public static final RegistryObject<Block> CRACKED_DUNGEON_BRICKS = registerBlockItem("cracked_dungeon_bricks", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> CRACKED_DUNGEON_BRICKS_SLAB = registerBlockItem("cracked_dungeon_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> CRACKED_DUNGEON_BRICKS_STAIRS = registerBlockItem("cracked_dungeon_bricks_stairs", () -> new StairBlock(CRACKED_DUNGEON_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> CRACKED_DUNGEON_BRICKS_PRESSURE_PLATE = registerBlockItem("cracked_dungeon_bricks_pressure_plate", () -> new PressurePlateBlock(Sensitivity.MOBS, BlockBehaviour.Properties.of().strength(.5f).noCollission(), ArcanaBlockSetTypes.CRACKED_DUNGEON_BRICK));
	@GLT public static final RegistryObject<Block> CRACKED_DUNGEON_BRICKS_WALL = registerBlockItem("cracked_dungeon_bricks_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(CRACKED_DUNGEON_BRICKS.get())));
	
	// Mossy Dungeon Bricks
	@GLT public static final RegistryObject<Block> MOSSY_DUNGEON_BRICKS = registerBlockItem("mossy_dungeon_bricks", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> MOSSY_DUNGEON_BRICKS_SLAB = registerBlockItem("mossy_dungeon_bricks_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> MOSSY_DUNGEON_BRICKS_STAIRS = registerBlockItem("mossy_dungeon_bricks_stairs", () -> new StairBlock(MOSSY_DUNGEON_BRICKS.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> MOSSY_DUNGEON_BRICKS_PRESSURE_PLATE = registerBlockItem("mossy_dungeon_bricks_pressure_plate", () -> new PressurePlateBlock(Sensitivity.MOBS, BlockBehaviour.Properties.of().strength(.5f).noCollission(), ArcanaBlockSetTypes.MOSSY_DUNGEON_BRICK));
	@GLT public static final RegistryObject<Block> MOSSY_DUNGEON_BRICKS_WALL = registerBlockItem("mossy_dungeon_bricks_wall", () -> new WallBlock(BlockBehaviour.Properties.copy(MOSSY_DUNGEON_BRICKS.get())));

	// Alchemical Glass
	public static final RegistryObject<Block> HARDENED_GLASS = registerBlockItem("hardened_glass", () -> new GlassBlock(BlockBehaviour.Properties.of().strength(5, 1200).sound(SoundType.GLASS).noOcclusion()));
	public static final RegistryObject<Block> SMOKEY_GLASS = registerBlockItem("smokey_glass", () -> new SolidVisibleBlock(BlockBehaviour.Properties.of().strength(.3f).sound(SoundType.GLASS)));
	public static final RegistryObject<Block> LUMINIFEROUS_GLASS = registerBlockItem("luminiferous_glass", () -> new GlassBlock(BlockBehaviour.Properties.of().strength(.3f).sound(SoundType.GLASS).noOcclusion().lightLevel(light(15))));

	// Functional // TODO: add block tags harvest level to amber_ore
	public static final RegistryObject<Block> AMBER_ORE = registerBlockItem("amber_ore", () -> new Block(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
	@GLT @GIM(BLOCK_REF) public static final RegistryObject<Block> SILVER_ORE = registerBlockItem("silver_ore", () -> new Block(BlockBehaviour.Properties.of().strength(3.0F, 3.0F)));
	@GLT public static final RegistryObject<Block> INFUSION_ARCANE_STONE = registerBlockItem("infusion_arcane_stone", () -> new Block(BlockBehaviour.Properties.of().strength(2.0F, 6.0F)));
	@GLT public static final RegistryObject<Block> TABLE = registerBlockItem("table", () -> new HorizontalWaterloggableBlock(BlockBehaviour.Properties.of().strength(2).noOcclusion()));
	@GLT public static final RegistryObject<Block> FOCI_FORGE = registerBlockItem("foci_forge", () -> new FociForgeCoreBlock(BlockBehaviour.Properties.of().strength(2).noOcclusion().forceSolidOff()));
	@GLT public static final RegistryObject<Block> FOCI_FORGE_COMPONENT = registerBlockItem("foci_forge_component", () -> new FociForgeComponentBlock(BlockBehaviour.Properties.of().strength(2).noOcclusion().forceSolidOff()));
	@GLT public static final RegistryObject<Block> NITOR = registerBlockItem("nitor", () -> new NitorBlock(BlockBehaviour.Properties.of().strength(0).noCollission().noOcclusion().lightLevel(light(15))));

	public static final RegistryObject<Block> TAINTED_AMBER_ORE = registerBlockItem("tainted_amber_ore", () -> taintedOf(AMBER_ORE.get()));
	public static final RegistryObject<Block> TAINTED_SILVER_ORE = registerBlockItem("tainted_silver_ore", () -> taintedOf(SILVER_ORE.get()));

	// Functional Blocks
	public static final RegistryObject<Block> JAR = registerBlockItem("jar", () -> new JarBlock(BlockBehaviour.Properties.of().sound(ArcanaSounds.JAR).strength(0.25f).forceSolidOn().noOcclusion().dynamicShape(), JarBlock.Type.BASIC));
	public static final RegistryObject<Block> SECURE_JAR = registerBlockItem("secure_jar", () -> new JarBlock(BlockBehaviour.Properties.of().sound(ArcanaSounds.JAR).strength(0.3f).forceSolidOn().noOcclusion().dynamicShape(), JarBlock.Type.SECURED));
	public static final RegistryObject<Block> VOID_JAR = registerBlockItem("void_jar", () -> new JarBlock(BlockBehaviour.Properties.of().sound(ArcanaSounds.JAR).strength(0.3f).forceSolidOn().noOcclusion().dynamicShape(), JarBlock.Type.VOID));
	public static final RegistryObject<Block> VACUUM_JAR = registerBlockItem("vacuum_jar", () -> new JarBlock(BlockBehaviour.Properties.of().sound(ArcanaSounds.JAR).strength(0.3f).forceSolidOn().noOcclusion().dynamicShape(), JarBlock.Type.VACUUM));
	public static final RegistryObject<Block> PRESSURE_JAR = registerBlockItem("pressure_jar", () -> new JarBlock(BlockBehaviour.Properties.of().sound(ArcanaSounds.JAR).strength(0.3f).forceSolidOn().noOcclusion().dynamicShape(), JarBlock.Type.PRESSURE));
	
	public static final RegistryObject<Block> ASPECT_BOOKSHELF = registerBlockItem("aspect_bookshelf", () -> new AspectBookshelfBlock(false, BlockBehaviour.Properties.of().strength(6).sound(SoundType.WOOD).noOcclusion()));
	public static final RegistryObject<Block> ASPECT_BOOKSHELF_BLOCK = registerBlockItem("aspect_bookshelf_block", () -> new AspectBookshelfBlock(true, BlockBehaviour.Properties.of().strength(6).sound(SoundType.WOOD).noOcclusion()));
	@GLT public static final RegistryObject<Block> RESEARCH_TABLE = registerBlockItem("research_table", () -> new ResearchTableCoreBlock(BlockBehaviour.Properties.of().strength(3).noOcclusion()));
	@GLT public static final RegistryObject<Block> RESEARCH_TABLE_COMPONENT = registerBlockItem("research_table_component", () -> new ResearchTableComponentBlock(BlockBehaviour.Properties.of().strength(3).noOcclusion()));
	@GLT public static final RegistryObject<Block> ARCANE_CRAFTING_TABLE = registerBlockItem("arcane_crafting_table", () -> new ArcaneCraftingTableBlock(BlockBehaviour.Properties.of().strength(2).noOcclusion()));
	@GLT public static final RegistryObject<Block> CRUCIBLE = registerBlockItem("crucible", () -> new CrucibleBlock(BlockBehaviour.Properties.of().strength(2).noOcclusion()));
	public static final RegistryObject<Block> ALEMBIC = registerBlockItem("alembic", () -> new AlembicBlock(BlockBehaviour.Properties.of().strength(3).noOcclusion()));
	@GLT public static final RegistryObject<Block> PEDESTAL = registerBlockItem("infusion_pedestal", () -> new PedestalBlock(BlockBehaviour.Properties.of().strength(3).noOcclusion()));
	@GLT public static final RegistryObject<Block> ASPECT_TESTER = registerBlockItem("aspect_tester", () -> new AspectTesterBlock(BlockBehaviour.Properties.of().strength(3).noOcclusion()));
	@GLT public static final RegistryObject<Block> ASPECT_TUBE = registerBlockItem("essentia_tube", () -> new TubeBlock(BlockBehaviour.Properties.of().strength(3).noOcclusion()));
	@GLT public static final RegistryObject<Block> ASPECT_VALVE = registerBlockItem("essentia_valve", () -> new ValveBlock(BlockBehaviour.Properties.of().strength(3).noOcclusion()));
	@GLT public static final RegistryObject<Block> ASPECT_WINDOW = registerBlockItem("essentia_window", () -> new PipeWindowBlock(BlockBehaviour.Properties.of().sound(SoundType.GLASS).strength(3).noOcclusion()));
	@GLT public static final RegistryObject<Block> ASPECT_PUMP = registerBlockItem("essentia_pump", () -> new PumpBlock(BlockBehaviour.Properties.of().sound(SoundType.GLASS).strength(3).noOcclusion()));
	@GLT @GIM(BLOCK_REF) public static final RegistryObject<Block> ASPECT_CRYSTALLIZER = registerBlockItem("aspect_crystallizer", () -> new AspectCrystallizerBlock(BlockBehaviour.Properties.of().strength(3)));
	
	// Taint Scrubber parts
	@GLT public static final RegistryObject<Block> TAINT_SCRUBBER_MK1 = registerBlockItem("taint_scrubber_mk1", () -> new TaintScrubberBlock(BlockBehaviour.Properties.of().strength(3).noOcclusion()));
	@GLT public static final RegistryObject<Block> TAINT_SCRUBBER_MK2 = registerBlockItem("taint_scrubber_mk2", () -> new TaintScrubberExtensionBlock(BlockBehaviour.Properties.of().strength(3).noOcclusion(), TaintScrubberExtensionBlock.Type.SCRUBBER_MK2));
	@GLT public static final RegistryObject<Block> TAINT_BOOSTER = registerBlockItem("taint_booster", () -> new BoosterTaintScrubberExtensionBlock(BlockBehaviour.Properties.of().strength(3).noOcclusion()));
	@GLT public static final RegistryObject<Block> TAINT_SUCKER = registerBlockItem("taint_sucker", () -> new TaintScrubberExtensionBlock(BlockBehaviour.Properties.of().strength(3).noOcclusion(), TaintScrubberExtensionBlock.Type.SUCKER));
	@GLT public static final RegistryObject<Block> TAINT_BORE = registerBlockItem("taint_bore", () -> new TaintScrubberExtensionBlock(BlockBehaviour.Properties.of().strength(3).noOcclusion(), TaintScrubberExtensionBlock.Type.BORE));

	@GLT public static final RegistryObject<Block> SEE_NO_EVIL_STATUE = registerBlockItem("see_no_evil_statue", () -> new StatueBlock(BlockBehaviour.Properties.of().strength(4).noOcclusion()));
	@GLT public static final RegistryObject<Block> HEAR_NO_EVIL_STATUE = registerBlockItem("hear_no_evil_statue", () -> new StatueBlock(BlockBehaviour.Properties.of().strength(4).noOcclusion()));
	@GLT public static final RegistryObject<Block> SPEAK_NO_EVIL_STATUE = registerBlockItem("speak_no_evil_statue", () -> new StatueBlock(BlockBehaviour.Properties.of().strength(4).noOcclusion()));
	
	// Plants
	public static final RegistryObject<Block> MAGIC_MUSHROOM = registerBlockItem("magic_mushroom", () -> new MagicMushroomBlock(BlockBehaviour.Properties.of().noCollission().randomTicks().sound(SoundType.BAMBOO_SAPLING).noOcclusion().lightLevel(light(3))));
	
	// Woods
	// Dair Wood
	public static final RegistryObject<Block> DAIR_LEAVES = registerBlockItem("dair_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.of().strength(0.2F).randomTicks().sound(SoundType.CHERRY_LEAVES).noOcclusion()));
	@GLT public static final RegistryObject<Block> DAIR_LOG = registerBlockItem("dair_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> STRIPPED_DAIR_LOG = registerBlockItem("stripped_dair_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> STRIPPED_DAIR_WOOD = registerBlockItem("stripped_dair_wood", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> DAIR_WOOD = registerBlockItem("dair_wood", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> DAIR_PLANKS = registerBlockItem("dair_planks", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> DAIR_DOOR = registerBlockItem("dair_door", () -> new DoorBlock(BlockBehaviour.Properties.of().strength(3).sound(SoundType.WOOD).noOcclusion(), ArcanaBlockSetTypes.DAIR));
	@GLT public static final RegistryObject<Block> DAIR_TRAPDOOR = registerBlockItem("dair_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.of().strength(3).sound(SoundType.WOOD).noOcclusion(), ArcanaBlockSetTypes.DAIR));
	@GLT public static final RegistryObject<Block> DAIR_PRESSURE_PLATE = registerBlockItem("dair_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, BlockBehaviour.Properties.of().strength(.5f).sound(SoundType.WOOD).noCollission(), ArcanaBlockSetTypes.DAIR));
	// TODO: implement tree
	@GLT public static final RegistryObject<Block> DAIR_SAPLING = registerBlockItem("dair_sapling", () -> new SaplingBlock(new DummyTree(), BlockBehaviour.Properties.of().noCollission().randomTicks().strength(0).sound(SoundType.CHERRY_SAPLING)));
	@GLT public static final RegistryObject<Block> DAIR_SLAB = registerBlockItem("dair_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> DAIR_STAIRS = registerBlockItem("dair_stairs", () -> new StairBlock(DAIR_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> DAIR_BUTTON = registerBlockItem("dair_button", () -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.WOOD), ArcanaBlockSetTypes.DAIR, 30, true));
	@GLT public static final RegistryObject<Block> DAIR_FENCE = registerBlockItem("dair_fence", () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> DAIR_FENCE_GATE = registerBlockItem("dair_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), ArcanaWoodTypes.DAIR));


	public static final RegistryObject<Block> TAINTED_DAIR_LEAVES = registerBlockItem("tainted_dair_leaves", () -> taintedOf(ArcanaBlocks.DAIR_LEAVES.get()));
	@GLT public static final RegistryObject<Block> TAINTED_DAIR_LOG = registerBlockItem("tainted_dair_log", () -> taintedOf(ArcanaBlocks.DAIR_LOG.get()));
	public static final RegistryObject<Block> TAINTED_STRIPPED_DAIR_LOG = registerBlockItem("stripped_tainted_dair_log", () -> taintedOf(ArcanaBlocks.STRIPPED_DAIR_LOG.get()));
	@GLT public static final RegistryObject<Block> TAINTED_STRIPPED_DAIR_WOOD = registerBlockItem("stripped_tainted_dair_wood", () -> taintedOf(ArcanaBlocks.STRIPPED_DAIR_WOOD.get()));
	@GLT public static final RegistryObject<Block> TAINTED_DAIR_WOOD = registerBlockItem("tainted_dair_wood", () -> taintedOf(ArcanaBlocks.DAIR_WOOD.get()));
	@GLT public static final RegistryObject<Block> TAINTED_DAIR_PLANKS = registerBlockItem("tainted_dair_planks", () -> taintedOf(ArcanaBlocks.DAIR_PLANKS.get()));
	@GLT public static final RegistryObject<Block> TAINTED_DAIR_SAPLING = registerBlockItem("tainted_dair_sapling", () -> taintedOf(ArcanaBlocks.DAIR_SAPLING.get()));
	@GLT public static final RegistryObject<Block> TAINTED_DAIR_SLAB = registerBlockItem("tainted_dair_slab", () -> taintedOf(ArcanaBlocks.DAIR_SLAB.get()));
	@GLT public static final RegistryObject<Block> TAINTED_DAIR_STAIRS = registerBlockItem("tainted_dair_stairs", () -> taintedOf(ArcanaBlocks.DAIR_STAIRS.get()));
	
	// Eucalyptus Wood
	public static final RegistryObject<Block> EUCALYPTUS_LEAVES = registerBlockItem("eucalyptus_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.of().strength(0.2F).randomTicks().sound(SoundType.CHERRY_LEAVES).noOcclusion()));
	@GLT public static final RegistryObject<Block> EUCALYPTUS_LOG = registerBlockItem("eucalyptus_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> STRIPPED_EUCALYPTUS_LOG = registerBlockItem("stripped_eucalyptus_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> STRIPPED_EUCALYPTUS_WOOD = registerBlockItem("stripped_eucalyptus_wood", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> EUCALYPTUS_WOOD = registerBlockItem("eucalyptus_wood", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> EUCALYPTUS_PLANKS = registerBlockItem("eucalyptus_planks", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> EUCALYPTUS_DOOR = registerBlockItem("eucalyptus_door", () -> new DoorBlock(BlockBehaviour.Properties.of().strength(3).sound(SoundType.WOOD).noOcclusion(), ArcanaBlockSetTypes.EUCALYPTUS));
	@GLT public static final RegistryObject<Block> EUCALYPTUS_TRAPDOOR = registerBlockItem("eucalyptus_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.of().strength(3).sound(SoundType.WOOD).noOcclusion(), ArcanaBlockSetTypes.EUCALYPTUS));
	@GLT public static final RegistryObject<Block> EUCALYPTUS_PRESSURE_PLATE = registerBlockItem("eucalyptus_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, BlockBehaviour.Properties.of().strength(.5f).sound(SoundType.WOOD).noCollission(), ArcanaBlockSetTypes.EUCALYPTUS));
	// TODO: implement tree
	@GLT public static final RegistryObject<Block> EUCALYPTUS_SAPLING = registerBlockItem("eucalyptus_sapling", () -> new SaplingBlock(new DummyTree(), BlockBehaviour.Properties.of().noCollission().randomTicks().strength(0).sound(SoundType.CHERRY_SAPLING)));
	@GLT public static final RegistryObject<Block> EUCALYPTUS_SLAB = registerBlockItem("eucalyptus_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> EUCALYPTUS_STAIRS = registerBlockItem("eucalyptus_stairs", () -> new StairBlock(EUCALYPTUS_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> EUCALYPTUS_BUTTON = registerBlockItem("eucalyptus_button", () -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.WOOD), ArcanaBlockSetTypes.EUCALYPTUS, 30, true));
	@GLT public static final RegistryObject<Block> EUCALYPTUS_FENCE = registerBlockItem("eucalyptus_fence", () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> EUCALYPTUS_FENCE_GATE = registerBlockItem("eucalyptus_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), ArcanaWoodTypes.EUCALYPTUS));

	public static final RegistryObject<Block> TAINTED_EUCALYPTUS_LEAVES = registerBlockItem("tainted_eucalyptus_leaves", () -> taintedOf(ArcanaBlocks.EUCALYPTUS_LEAVES.get()));
	@GLT public static final RegistryObject<Block> TAINTED_EUCALYPTUS_LOG = registerBlockItem("tainted_eucalyptus_log", () -> taintedOf(ArcanaBlocks.EUCALYPTUS_LOG.get()));
	public static final RegistryObject<Block> TAINTED_STRIPPED_EUCALYPTUS_LOG = registerBlockItem("stripped_tainted_eucalyptus_log", () -> taintedOf(ArcanaBlocks.STRIPPED_EUCALYPTUS_LOG.get()));
	@GLT public static final RegistryObject<Block> TAINTED_STRIPPED_EUCALYPTUS_WOOD = registerBlockItem("stripped_tainted_eucalyptus_wood", () -> taintedOf(ArcanaBlocks.STRIPPED_EUCALYPTUS_WOOD.get()));
	@GLT public static final RegistryObject<Block> TAINTED_EUCALYPTUS_WOOD = registerBlockItem("tainted_eucalyptus_wood", () -> taintedOf(ArcanaBlocks.EUCALYPTUS_WOOD.get()));
	@GLT public static final RegistryObject<Block> TAINTED_EUCALYPTUS_PLANKS = registerBlockItem("tainted_eucalyptus_planks", () -> taintedOf(ArcanaBlocks.EUCALYPTUS_PLANKS.get()));
	@GLT public static final RegistryObject<Block> TAINTED_EUCALYPTUS_SAPLING = registerBlockItem("tainted_eucalyptus_sapling", () -> taintedOf(ArcanaBlocks.EUCALYPTUS_SAPLING.get()));
	@GLT public static final RegistryObject<Block> TAINTED_EUCALYPTUS_SLAB = registerBlockItem("tainted_eucalyptus_slab", () -> taintedOf(ArcanaBlocks.EUCALYPTUS_SLAB.get()));
	@GLT public static final RegistryObject<Block> TAINTED_EUCALYPTUS_STAIRS = registerBlockItem("tainted_eucalyptus_stairs", () -> taintedOf(ArcanaBlocks.EUCALYPTUS_STAIRS.get()));
	
	// Greatwood
	public static final RegistryObject<Block> GREATWOOD_LEAVES = registerBlockItem("greatwood_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.of().strength(0.2F).randomTicks().sound(SoundType.CHERRY_LEAVES).noOcclusion()));
	@GLT public static final RegistryObject<Block> GREATWOOD_LOG = registerBlockItem("greatwood_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> STRIPPED_GREATWOOD_LOG = registerBlockItem("stripped_greatwood_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> STRIPPED_GREATWOOD_WOOD = registerBlockItem("stripped_greatwood_wood", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> GREATWOOD_WOOD = registerBlockItem("greatwood_wood", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> GREATWOOD_PLANKS = registerBlockItem("greatwood_planks", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> GREATWOOD_DOOR = registerBlockItem("greatwood_door", () -> new DoorBlock(BlockBehaviour.Properties.of().strength(3).sound(SoundType.WOOD).noOcclusion(), ArcanaBlockSetTypes.GREATWOOD));
	@GLT public static final RegistryObject<Block> GREATWOOD_TRAPDOOR = registerBlockItem("greatwood_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.of().strength(3).sound(SoundType.WOOD).noOcclusion(), ArcanaBlockSetTypes.GREATWOOD));
	@GLT public static final RegistryObject<Block> GREATWOOD_PRESSURE_PLATE = registerBlockItem("greatwood_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, BlockBehaviour.Properties.of().strength(.5f).sound(SoundType.WOOD).noCollission(), ArcanaBlockSetTypes.GREATWOOD));
	@GLT public static final RegistryObject<Block> GREATWOOD_SAPLING = registerBlockItem("greatwood_sapling", () -> new SaplingBlock(new GreatwoodTreeGrower(), BlockBehaviour.Properties.of().noCollission().randomTicks().strength(0).sound(SoundType.CHERRY_SAPLING)));
	@GLT public static final RegistryObject<Block> GREATWOOD_SLAB = registerBlockItem("greatwood_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> GREATWOOD_STAIRS = registerBlockItem("greatwood_stairs", () -> new StairBlock(GREATWOOD_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> GREATWOOD_BUTTON = registerBlockItem("greatwood_button", () -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.WOOD), ArcanaBlockSetTypes.GREATWOOD, 30, true)); // TODO: buttons should break on piston push
	@GLT public static final RegistryObject<Block> GREATWOOD_FENCE = registerBlockItem("greatwood_fence", () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> GREATWOOD_FENCE_GATE = registerBlockItem("greatwood_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), ArcanaWoodTypes.GREATWOOD));

	public static final RegistryObject<Block> TAINTED_GREATWOOD_LEAVES = registerBlockItem("tainted_greatwood_leaves", () -> taintedOf(ArcanaBlocks.GREATWOOD_LEAVES.get()));
	@GLT public static final RegistryObject<Block> TAINTED_GREATWOOD_LOG = registerBlockItem("tainted_greatwood_log", () -> taintedOf(ArcanaBlocks.GREATWOOD_LOG.get()));
	public static final RegistryObject<Block> TAINTED_STRIPPED_GREATWOOD_LOG = registerBlockItem("stripped_tainted_greatwood_log", () -> taintedOf(ArcanaBlocks.STRIPPED_GREATWOOD_LOG.get()));
	@GLT public static final RegistryObject<Block> TAINTED_STRIPPED_GREATWOOD_WOOD = registerBlockItem("stripped_tainted_greatwood_wood", () -> taintedOf(ArcanaBlocks.STRIPPED_GREATWOOD_WOOD.get()));
	@GLT public static final RegistryObject<Block> TAINTED_GREATWOOD_WOOD = registerBlockItem("tainted_greatwood_wood", () -> taintedOf(ArcanaBlocks.GREATWOOD_WOOD.get()));
	@GLT public static final RegistryObject<Block> TAINTED_GREATWOOD_PLANKS = registerBlockItem("tainted_greatwood_planks", () -> taintedOf(ArcanaBlocks.GREATWOOD_PLANKS.get()));
	@GLT public static final RegistryObject<Block> TAINTED_GREATWOOD_SAPLING = registerBlockItem("tainted_greatwood_sapling", () -> new TaintedSaplingBlock(GREATWOOD_SAPLING.get(), new TaintedGreatwoodTree(), BlockBehaviour.Properties.of().noCollission().randomTicks().strength(0).sound(SoundType.CHERRY_SAPLING)));
	@GLT public static final RegistryObject<Block> TAINTED_GREATWOOD_SLAB = registerBlockItem("tainted_greatwood_slab", () -> taintedOf(ArcanaBlocks.GREATWOOD_SLAB.get()));
	@GLT public static final RegistryObject<Block> TAINTED_GREATWOOD_STAIRS = registerBlockItem("tainted_greatwood_stairs", () -> taintedOf(ArcanaBlocks.GREATWOOD_STAIRS.get()));
	
	// Hawthorn Wood
	public static final RegistryObject<Block> HAWTHORN_LEAVES = registerBlockItem("hawthorn_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.of().strength(0.2F).randomTicks().sound(SoundType.CHERRY_LEAVES).noOcclusion()));
	@GLT public static final RegistryObject<Block> HAWTHORN_LOG = registerBlockItem("hawthorn_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> STRIPPED_HAWTHORN_LOG = registerBlockItem("stripped_hawthorn_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> STRIPPED_HAWTHORN_WOOD = registerBlockItem("stripped_hawthorn_wood", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> HAWTHORN_WOOD = registerBlockItem("hawthorn_wood", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> HAWTHORN_PLANKS = registerBlockItem("hawthorn_planks", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> HAWTHORN_DOOR = registerBlockItem("hawthorn_door", () -> new DoorBlock(BlockBehaviour.Properties.of().strength(3).sound(SoundType.WOOD).noOcclusion(), ArcanaBlockSetTypes.HAWTHORN));
	@GLT public static final RegistryObject<Block> HAWTHORN_TRAPDOOR = registerBlockItem("hawthorn_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.of().strength(3).sound(SoundType.WOOD).noOcclusion(), ArcanaBlockSetTypes.HAWTHORN));
	@GLT public static final RegistryObject<Block> HAWTHORN_PRESSURE_PLATE = registerBlockItem("hawthorn_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, BlockBehaviour.Properties.of().strength(.5f).sound(SoundType.WOOD).noCollission(), ArcanaBlockSetTypes.HAWTHORN));
	// TODO: implement tree
	@GLT public static final RegistryObject<Block> HAWTHORN_SAPLING = registerBlockItem("hawthorn_sapling", () -> new SaplingBlock(new DummyTree(), BlockBehaviour.Properties.of().noCollission().randomTicks().strength(0).sound(SoundType.CHERRY_SAPLING)));
	@GLT public static final RegistryObject<Block> HAWTHORN_SLAB = registerBlockItem("hawthorn_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> HAWTHORN_STAIRS = registerBlockItem("hawthorn_stairs", () -> new StairBlock(HAWTHORN_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> HAWTHORN_BUTTON = registerBlockItem("hawthorn_button", () -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.WOOD), ArcanaBlockSetTypes.HAWTHORN, 30, true));
	@GLT public static final RegistryObject<Block> HAWTHORN_FENCE = registerBlockItem("hawthorn_fence", () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> HAWTHORN_FENCE_GATE = registerBlockItem("hawthorn_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), ArcanaWoodTypes.HAWTHORN));

	public static final RegistryObject<Block> TAINTED_HAWTHORN_LEAVES = registerBlockItem("tainted_hawthorn_leaves", () -> taintedOf(ArcanaBlocks.HAWTHORN_LEAVES.get()));
	@GLT public static final RegistryObject<Block> TAINTED_HAWTHORN_LOG = registerBlockItem("tainted_hawthorn_log", () -> taintedOf(ArcanaBlocks.HAWTHORN_LOG.get()));
	public static final RegistryObject<Block> TAINTED_STRIPPED_HAWTHORN_LOG = registerBlockItem("stripped_tainted_hawthorn_log", () -> taintedOf(ArcanaBlocks.STRIPPED_HAWTHORN_LOG.get()));
	@GLT public static final RegistryObject<Block> TAINTED_STRIPPED_HAWTHORN_WOOD = registerBlockItem("stripped_tainted_hawthorn_wood", () -> taintedOf(ArcanaBlocks.STRIPPED_HAWTHORN_WOOD.get()));
	@GLT public static final RegistryObject<Block> TAINTED_HAWTHORN_WOOD = registerBlockItem("tainted_hawthorn_wood", () -> taintedOf(ArcanaBlocks.HAWTHORN_WOOD.get()));
	@GLT public static final RegistryObject<Block> TAINTED_HAWTHORN_PLANKS = registerBlockItem("tainted_hawthorn_planks", () -> taintedOf(ArcanaBlocks.HAWTHORN_PLANKS.get()));
	@GLT public static final RegistryObject<Block> TAINTED_HAWTHORN_SAPLING = registerBlockItem("tainted_hawthorn_sapling", () -> taintedOf(ArcanaBlocks.HAWTHORN_SAPLING.get()));
	@GLT public static final RegistryObject<Block> TAINTED_HAWTHORN_SLAB = registerBlockItem("tainted_hawthorn_slab", () -> taintedOf(ArcanaBlocks.HAWTHORN_SLAB.get()));
	@GLT public static final RegistryObject<Block> TAINTED_HAWTHORN_STAIRS = registerBlockItem("tainted_hawthorn_stairs", () -> taintedOf(ArcanaBlocks.HAWTHORN_STAIRS.get()));
	
	// Silverwood
	public static final RegistryObject<Block> SILVERWOOD_LEAVES = registerBlockItem("silverwood_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.of().strength(0.2F).randomTicks().sound(SoundType.CHERRY_LEAVES).noOcclusion()));
	@GLT public static final RegistryObject<Block> SILVERWOOD_LOG = registerBlockItem("silverwood_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> STRIPPED_SILVERWOOD_LOG = registerBlockItem("stripped_silverwood_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> STRIPPED_SILVERWOOD_WOOD = registerBlockItem("stripped_silverwood_wood", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> SILVERWOOD_WOOD = registerBlockItem("silverwood_wood", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> SILVERWOOD_PLANKS = registerBlockItem("silverwood_planks", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> SILVERWOOD_DOOR = registerBlockItem("silverwood_door", () -> new DoorBlock(BlockBehaviour.Properties.of().strength(3).sound(SoundType.WOOD).noOcclusion(), ArcanaBlockSetTypes.SILVERWOOD));
	@GLT public static final RegistryObject<Block> SILVERWOOD_TRAPDOOR = registerBlockItem("silverwood_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.of().strength(3).sound(SoundType.WOOD).noOcclusion(), ArcanaBlockSetTypes.SILVERWOOD));
	@GLT public static final RegistryObject<Block> SILVERWOOD_PRESSURE_PLATE = registerBlockItem("silverwood_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, BlockBehaviour.Properties.of().strength(.5f).sound(SoundType.WOOD).noCollission(), ArcanaBlockSetTypes.SILVERWOOD));
	@GLT public static final RegistryObject<Block> SILVERWOOD_SAPLING = registerBlockItem("silverwood_sapling", () -> new SaplingBlock(new SilverwoodTreeGrower(), BlockBehaviour.Properties.of().noCollission().randomTicks().strength(0).sound(SoundType.CHERRY_SAPLING)));
	@GLT public static final RegistryObject<Block> SILVERWOOD_SLAB = registerBlockItem("silverwood_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> SILVERWOOD_STAIRS = registerBlockItem("silverwood_stairs", () -> new StairBlock(SILVERWOOD_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> SILVERWOOD_BUTTON = registerBlockItem("silverwood_button", () -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.WOOD), ArcanaBlockSetTypes.SILVERWOOD, 30, true));
	@GLT public static final RegistryObject<Block> SILVERWOOD_FENCE = registerBlockItem("silverwood_fence", () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> SILVERWOOD_FENCE_GATE = registerBlockItem("silverwood_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), ArcanaWoodTypes.SILVERWOOD));
	
	// Willow Wood
	public static final RegistryObject<Block> WILLOW_LEAVES = registerBlockItem("willow_leaves", () -> new LeavesBlock(BlockBehaviour.Properties.of().strength(0.2F).randomTicks().sound(SoundType.CHERRY_LEAVES).noOcclusion()));
	@GLT public static final RegistryObject<Block> WILLOW_LOG = registerBlockItem("willow_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	public static final RegistryObject<Block> STRIPPED_WILLOW_LOG = registerBlockItem("stripped_willow_log", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> STRIPPED_WILLOW_WOOD = registerBlockItem("stripped_willow_wood", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> WILLOW_WOOD = registerBlockItem("willow_wood", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> WILLOW_PLANKS = registerBlockItem("willow_planks", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> WILLOW_DOOR = registerBlockItem("willow_door", () -> new DoorBlock(BlockBehaviour.Properties.of().strength(3).sound(SoundType.WOOD).noOcclusion(), ArcanaBlockSetTypes.WILLOW));
	@GLT public static final RegistryObject<Block> WILLOW_TRAPDOOR = registerBlockItem("willow_trapdoor", () -> new TrapDoorBlock(BlockBehaviour.Properties.of().strength(3).sound(SoundType.WOOD).noOcclusion(), ArcanaBlockSetTypes.WILLOW));
	@GLT public static final RegistryObject<Block> WILLOW_PRESSURE_PLATE = registerBlockItem("willow_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, BlockBehaviour.Properties.of().strength(.5f).sound(SoundType.WOOD).noCollission(), ArcanaBlockSetTypes.WILLOW));
	// TODO: implement tree
	@GLT public static final RegistryObject<Block> WILLOW_SAPLING = registerBlockItem("willow_sapling", () -> new SaplingBlock(new DummyTree(), BlockBehaviour.Properties.of().noCollission().randomTicks().strength(0).sound(SoundType.CHERRY_SAPLING)));
	@GLT public static final RegistryObject<Block> WILLOW_SLAB = registerBlockItem("willow_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> WILLOW_STAIRS = registerBlockItem("willow_stairs", () -> new StairBlock(WILLOW_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> WILLOW_BUTTON = registerBlockItem("willow_button", () -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.WOOD), ArcanaBlockSetTypes.WILLOW, 30, true));
	@GLT public static final RegistryObject<Block> WILLOW_FENCE = registerBlockItem("willow_fence", () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> WILLOW_FENCE_GATE = registerBlockItem("willow_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), ArcanaWoodTypes.WILLOW));

	public static final RegistryObject<Block> TAINTED_WILLOW_LEAVES = registerBlockItem("tainted_willow_leaves", () -> taintedOf(ArcanaBlocks.WILLOW_LEAVES.get()));
	@GLT public static final RegistryObject<Block> TAINTED_WILLOW_LOG = registerBlockItem("tainted_willow_log", () -> taintedOf(ArcanaBlocks.WILLOW_LOG.get()));
	public static final RegistryObject<Block> TAINTED_STRIPPED_WILLOW_LOG = registerBlockItem("stripped_tainted_willow_log", () -> taintedOf(ArcanaBlocks.STRIPPED_WILLOW_LOG.get()));
	@GLT public static final RegistryObject<Block> TAINTED_STRIPPED_WILLOW_WOOD = registerBlockItem("stripped_tainted_willow_wood", () -> taintedOf(ArcanaBlocks.STRIPPED_WILLOW_WOOD.get()));
	@GLT public static final RegistryObject<Block> TAINTED_WILLOW_WOOD = registerBlockItem("tainted_willow_wood", () -> taintedOf(ArcanaBlocks.WILLOW_WOOD.get()));
	@GLT public static final RegistryObject<Block> TAINTED_WILLOW_PLANKS = registerBlockItem("tainted_willow_planks", () -> taintedOf(ArcanaBlocks.WILLOW_PLANKS.get()));
	@GLT public static final RegistryObject<Block> TAINTED_WILLOW_SAPLING = registerBlockItem("tainted_willow_sapling", () -> taintedOf(ArcanaBlocks.WILLOW_SAPLING.get()));
	@GLT public static final RegistryObject<Block> TAINTED_WILLOW_SLAB = registerBlockItem("tainted_willow_slab", () -> taintedOf(ArcanaBlocks.WILLOW_SLAB.get()));
	@GLT public static final RegistryObject<Block> TAINTED_WILLOW_STAIRS = registerBlockItem("tainted_willow_stairs", () -> taintedOf(ArcanaBlocks.WILLOW_STAIRS.get()));

	// Compressed Resources
	@GLT public static final RegistryObject<Block> ARCANIUM_BLOCK = registerBlockItem("arcanium_block", () -> new Block(BlockBehaviour.Properties.of().strength(6).sound(SoundType.METAL)));
	@GLT public static final RegistryObject<Block> THAUMIUM_BLOCK = registerBlockItem("thaumium_block", () -> new Block(BlockBehaviour.Properties.of().strength(6).sound(SoundType.METAL)));
	@GLT public static final RegistryObject<Block> VOID_METAL_BLOCK = registerBlockItem("void_metal_block", () -> new Block(BlockBehaviour.Properties.of().strength(6).sound(SoundType.METAL)));
	@GLT public static final RegistryObject<Block> SILVER_BLOCK = registerBlockItem("silver_block", () -> new Block(BlockBehaviour.Properties.of().strength(6).sound(SoundType.METAL)));
	
	// Crystal Clusters
	public static final RegistryObject<Block> AIR_CLUSTER = registerCrystalClusterBlock("air_cluster", Aspects.AIR);
	public static final RegistryObject<Block> EARTH_CLUSTER = registerCrystalClusterBlock("earth_cluster", Aspects.EARTH);
	public static final RegistryObject<Block> FIRE_CLUSTER = registerCrystalClusterBlock("fire_cluster", Aspects.FIRE);
	public static final RegistryObject<Block> WATER_CLUSTER = registerCrystalClusterBlock("water_cluster", Aspects.WATER);
	public static final RegistryObject<Block> ORDER_CLUSTER = registerCrystalClusterBlock("order_cluster", Aspects.ORDER);
	public static final RegistryObject<Block> CHAOS_CLUSTER = registerCrystalClusterBlock("chaos_cluster", Aspects.CHAOS);

	//Misc Tainted Blocks
	//public static final RegistryObject<Block> TAINTED_DESTROYED_ORE = registerBlockItem("tainted_destroyed_ore", Taint.taintedOf(Blocks.STONE_BRICKS));
	@GLT public static final RegistryObject<Block> TAINTED_ARCANIUM_BLOCK = registerBlockItem("tainted_arcanium_block", () -> taintedOf(ArcanaBlocks.ARCANIUM_BLOCK.get()));
	@GLT public static final RegistryObject<Block> TAINTED_THAUMIUM_BLOCK = registerBlockItem("tainted_thaumium_block", () -> taintedOf(ArcanaBlocks.THAUMIUM_BLOCK.get()));
	
	// Tainted Blocks
	@GLT public static final RegistryObject<Block> TAINTED_CRUST = registerBlockItem("tainted_crust", () -> taintedOf(Blocks.COBBLESTONE));
//	@GLT public static final RegistryObject<Block> TAINTED_CRUST_SLAB = registerBlockItem("tainted_crust_slab", () -> taintedOf(Blocks.COBBLESTONE_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_GRAVEL = registerBlockItem("tainted_gravel", () -> taintedOf(Blocks.GRAVEL));
	@GLT public static final RegistryObject<Block> TAINTED_SAND = registerBlockItem("tainted_sand", () -> taintedOf(Blocks.SAND));

	@GLT public static final RegistryObject<Block> TAINTED_ANDESITE = registerBlockItem("tainted_andesite", () -> taintedOf(Blocks.ANDESITE));
	@GLT public static final RegistryObject<Block> TAINTED_DIORITE = registerBlockItem("tainted_diorite", () -> taintedOf(Blocks.DIORITE));
	@GLT public static final RegistryObject<Block> TAINTED_GRANITE = registerBlockItem("tainted_granite", () -> taintedOf(Blocks.GRANITE));
	@GLT public static final RegistryObject<Block> TAINTED_ROCK = registerBlockItem("tainted_rock", () -> taintedOf(Blocks.STONE));
	@GLT public static final RegistryObject<Block> TAINTED_ROCK_SLAB = registerBlockItem("tainted_rock_slab", () -> taintedOf(Blocks.STONE_SLAB));

	@GLT public static final RegistryObject<Block> TAINTED_SOIL = registerBlockItem("tainted_soil", () -> taintedOf(Blocks.DIRT));
	public static final RegistryObject<Block> TAINTED_GRASS_BLOCK = registerBlockItem("tainted_grass_block", () -> taintedOf(Blocks.GRASS_BLOCK));
	@GLT public static final RegistryObject<Block> TAINTED_PODZOL = registerBlockItem("tainted_podzol", () -> taintedOf(Blocks.PODZOL));
	@GLT public static final RegistryObject<Block> TAINTED_FARMLAND = registerBlockItem("tainted_farmland", () -> taintedOf(Blocks.FARMLAND));
	@GLT public static final RegistryObject<Block> TAINTED_PATH = registerBlockItem("tainted_path", () -> taintedOf(Blocks.DIRT_PATH));

	@GLT public static final RegistryObject<Block> TAINTED_COAL_BLOCK = registerBlockItem("tainted_coal_block", () -> taintedOf(Blocks.COAL_BLOCK));
	@GLT public static final RegistryObject<Block> TAINTED_EMERALD_BLOCK = registerBlockItem("tainted_emerald_block", () -> taintedOf(Blocks.EMERALD_BLOCK));
	@GLT public static final RegistryObject<Block> TAINTED_DIAMOND_BLOCK = registerBlockItem("tainted_diamond_block", () -> taintedOf(Blocks.DIAMOND_BLOCK));
	@GLT public static final RegistryObject<Block> TAINTED_GOLD_BLOCK = registerBlockItem("tainted_gold_block", () -> taintedOf(Blocks.GOLD_BLOCK));
	@GLT public static final RegistryObject<Block> TAINTED_IRON_BLOCK = registerBlockItem("tainted_iron_block", () -> taintedOf(Blocks.IRON_BLOCK));
	@GLT public static final RegistryObject<Block> TAINTED_LAPIS_BLOCK = registerBlockItem("tainted_lapis_block", () -> taintedOf(Blocks.LAPIS_BLOCK));
	@GLT public static final RegistryObject<Block> TAINTED_REDSTONE_BLOCK = registerBlockItem("tainted_redstone_block", () -> taintedOf(Blocks.REDSTONE_BLOCK));

	public static final RegistryObject<Block> TAINTED_VINE = registerBlockItem("tainted_vine", () -> taintedOf(Blocks.VINE));
	public static final RegistryObject<Block> TAINTED_GRASS = registerBlockItem("tainted_grass", () -> taintedOf(Blocks.GRASS));
	@GLT public static final RegistryObject<Block> TAINTED_FLOWER = registerBlockItem("tainted_flower", () -> taintedOf(
			Blocks.CORNFLOWER,Blocks.DANDELION,Blocks.POPPY,Blocks.BLUE_ORCHID,Blocks.ALLIUM,Blocks.AZURE_BLUET,Blocks.RED_TULIP,Blocks.ORANGE_TULIP,Blocks.WHITE_TULIP,Blocks.PINK_TULIP,Blocks.OXEYE_DAISY,Blocks.LILY_OF_THE_VALLEY
	));
	@GLT public static final RegistryObject<Block> TAINTED_CARVED_PUMPKIN = registerBlockItem("tainted_carved_pumpkin", () -> taintedOf(Blocks.CARVED_PUMPKIN));
	@GLT public static final RegistryObject<Block> TAINTED_JACK_OLANTERN = registerBlockItem("tainted_jack_olantern", () -> taintedOf(Blocks.JACK_O_LANTERN));
	public static final RegistryObject<Block> TAINTED_MELON = registerBlockItem("tainted_melon", () -> taintedOf(Blocks.MELON));
	@GLT public static final RegistryObject<Block> TAINTED_MUSHROOM = registerBlockItem("tainted_mushroom", () -> taintedOf(Blocks.BROWN_MUSHROOM));
	@GLT public static final RegistryObject<Block> TAINTED_PUMPKIN = registerBlockItem("tainted_pumpkin", () -> taintedOf(Blocks.PUMPKIN));

	public static final RegistryObject<Block> TAINTED_COAL_ORE = registerBlockItem("tainted_coal_ore", () -> taintedOf(Blocks.COAL_ORE));
	public static final RegistryObject<Block> TAINTED_IRON_ORE = registerBlockItem("tainted_iron_ore", () -> taintedOf(Blocks.IRON_ORE));
	public static final RegistryObject<Block> TAINTED_GOLD_ORE = registerBlockItem("tainted_gold_ore", () -> taintedOf(Blocks.GOLD_ORE));
	public static final RegistryObject<Block> TAINTED_DIAMOND_ORE = registerBlockItem("tainted_diamond_ore", () -> taintedOf(Blocks.DIAMOND_ORE));
	public static final RegistryObject<Block> TAINTED_LAPIS_ORE = registerBlockItem("tainted_lapis_ore", () -> taintedOf(Blocks.LAPIS_ORE));
	public static final RegistryObject<Block> TAINTED_EMERALD_ORE = registerBlockItem("tainted_emerald_ore", () -> taintedOf(Blocks.EMERALD_ORE));
	public static final RegistryObject<Block> TAINTED_REDSTONE_ORE = registerBlockItem("tainted_redstone_ore", () -> taintedOf(Blocks.REDSTONE_ORE));

	public static final RegistryObject<Block> TAINTED_ACACIA_LEAVES = registerBlockItem("tainted_acacia_leaves", () -> taintedOf(Blocks.ACACIA_LEAVES));
	@GLT public static final RegistryObject<Block> TAINTED_ACACIA_LOG = registerBlockItem("tainted_acacia_log", () -> taintedOf(Blocks.ACACIA_LOG));
	@GLT public static final RegistryObject<Block> TAINTED_ACACIA_PLANKS = registerBlockItem("tainted_acacia_planks", () -> taintedOf(Blocks.ACACIA_PLANKS));
	@GLT public static final RegistryObject<Block> TAINTED_ACACIA_SAPLING = registerBlockItem("tainted_acacia_sapling", () -> taintedOf(Blocks.ACACIA_SAPLING));
	@GLT public static final RegistryObject<Block> TAINTED_ACACIA_SLAB = registerBlockItem("tainted_acacia_slab", () -> taintedOf(Blocks.ACACIA_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_ACACIA_STAIRS = registerBlockItem("tainted_acacia_stairs", () -> taintedOf(Blocks.ACACIA_STAIRS));

	public static final RegistryObject<Block> TAINTED_BIRCH_LEAVES = registerBlockItem("tainted_birch_leaves", () -> taintedOf(Blocks.BIRCH_LEAVES));
	@GLT public static final RegistryObject<Block> TAINTED_BIRCH_LOG = registerBlockItem("tainted_birch_log", () -> taintedOf(Blocks.BIRCH_LOG));
	@GLT public static final RegistryObject<Block> TAINTED_BIRCH_PLANKS = registerBlockItem("tainted_birch_planks", () -> taintedOf(Blocks.BIRCH_PLANKS));
	@GLT public static final RegistryObject<Block> TAINTED_BIRCH_SAPLING = registerBlockItem("tainted_birch_sapling", () -> taintedOf(Blocks.BIRCH_SAPLING));
	@GLT public static final RegistryObject<Block> TAINTED_BIRCH_SLAB = registerBlockItem("tainted_birch_slab", () -> taintedOf(Blocks.BIRCH_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_BIRCH_STAIRS = registerBlockItem("tainted_birch_stairs", () -> taintedOf(Blocks.BIRCH_STAIRS));

	public static final RegistryObject<Block> TAINTED_DARKOAK_LEAVES = registerBlockItem("tainted_darkoak_leaves", () -> taintedOf(Blocks.DARK_OAK_LEAVES));
	@GLT public static final RegistryObject<Block> TAINTED_DARKOAK_LOG = registerBlockItem("tainted_darkoak_log", () -> taintedOf(Blocks.DARK_OAK_LOG));
	@GLT public static final RegistryObject<Block> TAINTED_DARKOAK_PLANKS = registerBlockItem("tainted_darkoak_planks", () -> taintedOf(Blocks.DARK_OAK_PLANKS));
	@GLT public static final RegistryObject<Block> TAINTED_DARKOAK_SAPLING = registerBlockItem("tainted_darkoak_sapling", () -> taintedOf(Blocks.DARK_OAK_SAPLING));
	@GLT public static final RegistryObject<Block> TAINTED_DARKOAK_SLAB = registerBlockItem("tainted_darkoak_slab", () -> taintedOf(Blocks.DARK_OAK_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_DARKOAK_STAIRS = registerBlockItem("tainted_darkoak_stairs", () -> taintedOf(Blocks.DARK_OAK_STAIRS));

	public static final RegistryObject<Block> TAINTED_JUNGLE_LEAVES = registerBlockItem("tainted_jungle_leaves", () -> taintedOf(Blocks.JUNGLE_LEAVES));
	@GLT public static final RegistryObject<Block> TAINTED_JUNGLE_LOG = registerBlockItem("tainted_jungle_log", () -> taintedOf(Blocks.JUNGLE_LOG));
	@GLT public static final RegistryObject<Block> TAINTED_JUNGLE_PLANKS = registerBlockItem("tainted_jungle_planks", () -> taintedOf(Blocks.JUNGLE_PLANKS));
	@GLT public static final RegistryObject<Block> TAINTED_JUNGLE_SAPLING = registerBlockItem("tainted_jungle_sapling", () -> taintedOf(Blocks.JUNGLE_SAPLING));
	@GLT public static final RegistryObject<Block> TAINTED_JUNGLE_SLAB = registerBlockItem("tainted_jungle_slab", () -> taintedOf(Blocks.JUNGLE_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_JUNGLE_STAIRS = registerBlockItem("tainted_jungle_stairs", () -> taintedOf(Blocks.JUNGLE_STAIRS));

	public static final RegistryObject<Block> TAINTED_OAK_LEAVES = registerBlockItem("tainted_oak_leaves", () -> taintedOf(Blocks.OAK_LEAVES));
	@GLT public static final RegistryObject<Block> TAINTED_OAK_LOG = registerBlockItem("tainted_oak_log", () -> taintedOf(Blocks.OAK_LOG));
	@GLT public static final RegistryObject<Block> TAINTED_OAK_PLANKS = registerBlockItem("tainted_oak_planks", () -> taintedOf(Blocks.OAK_PLANKS));
	@GLT public static final RegistryObject<Block> TAINTED_OAK_SAPLING = registerBlockItem("tainted_oak_sapling", () -> new TaintedSaplingBlock(Blocks.OAK_SAPLING, new TaintedOakTree(), BlockBehaviour.Properties.of().noCollission().randomTicks().strength(0).sound(SoundType.CHERRY_SAPLING)));
	@GLT public static final RegistryObject<Block> TAINTED_OAK_SLAB = registerBlockItem("tainted_oak_slab", () -> taintedOf(Blocks.OAK_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_OAK_STAIRS = registerBlockItem("tainted_oak_stairs", () -> taintedOf(Blocks.OAK_STAIRS));

	public static final RegistryObject<Block> TAINTED_SPRUCE_LEAVES = registerBlockItem("tainted_spruce_leaves", () -> taintedOf(Blocks.SPRUCE_LEAVES));
	@GLT public static final RegistryObject<Block> TAINTED_SPRUCE_LOG = registerBlockItem("tainted_spruce_log", () -> taintedOf(Blocks.SPRUCE_LOG));
	@GLT public static final RegistryObject<Block> TAINTED_SPRUCE_PLANKS = registerBlockItem("tainted_spruce_planks", () -> taintedOf(Blocks.SPRUCE_PLANKS));
	@GLT public static final RegistryObject<Block> TAINTED_SPRUCE_SAPLING = registerBlockItem("tainted_spruce_sapling", () -> taintedOf(Blocks.SPRUCE_SAPLING));
	@GLT public static final RegistryObject<Block> TAINTED_SPRUCE_SLAB = registerBlockItem("tainted_spruce_slab", () -> taintedOf(Blocks.SPRUCE_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_SPRUCE_STAIRS = registerBlockItem("tainted_spruce_stairs", () -> taintedOf(Blocks.SPRUCE_STAIRS));

	@GLT public static final RegistryObject<Block> TAINTED_POLISHED_GRANITE = registerBlockItem("tainted_polished_granite", () -> taintedOf(Blocks.POLISHED_GRANITE));
	@GLT public static final RegistryObject<Block> TAINTED_POLISHED_GRANITE_SLAB = registerBlockItem("tainted_polished_granite_slab", () -> taintedOf(Blocks.POLISHED_GRANITE_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_POLISHED_GRANITE_STAIRS = registerBlockItem("tainted_polished_granite_stairs", () -> taintedOf(Blocks.POLISHED_GRANITE_STAIRS));

	@GLT public static final RegistryObject<Block> TAINTED_POLISHED_DIORITE = registerBlockItem("tainted_polished_diorite", () -> taintedOf(Blocks.POLISHED_DIORITE));
	@GLT public static final RegistryObject<Block> TAINTED_POLISHED_DIORITE_SLAB = registerBlockItem("tainted_polished_diorite_slab", () -> taintedOf(Blocks.POLISHED_DIORITE_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_POLISHED_DIORITE_STAIRS = registerBlockItem("tainted_polished_diorite_stairs", () -> taintedOf(Blocks.POLISHED_DIORITE_STAIRS));

	@GLT public static final RegistryObject<Block> TAINTED_POLISHED_ANDESITE = registerBlockItem("tainted_polished_andesite", () -> taintedOf(Blocks.POLISHED_ANDESITE));
	@GLT public static final RegistryObject<Block> TAINTED_POLISHED_ANDESITE_SLAB = registerBlockItem("tainted_polished_andesite_slab", () -> taintedOf(Blocks.POLISHED_ANDESITE_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_POLISHED_ANDESITE_STAIRS = registerBlockItem("tainted_polished_andesite_stairs", () -> taintedOf(Blocks.POLISHED_ANDESITE_STAIRS));

	@GLT public static final RegistryObject<Block> TAINTED_ANDESITE_SLAB = registerBlockItem("tainted_andesite_slab", () -> taintedOf(Blocks.POLISHED_ANDESITE_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_ANDESITE_STAIRS = registerBlockItem("tainted_andesite_stairs", () -> taintedOf(Blocks.POLISHED_ANDESITE_STAIRS));

	@GLT public static final RegistryObject<Block> TAINTED_DIORITE_SLAB = registerBlockItem("tainted_diorite_slab", () -> taintedOf(Blocks.POLISHED_DIORITE_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_DIORITE_STAIRS = registerBlockItem("tainted_diorite_stairs", () -> taintedOf(Blocks.POLISHED_DIORITE_STAIRS));

	@GLT public static final RegistryObject<Block> TAINTED_GRANITE_SLAB = registerBlockItem("tainted_granite_slab", () -> taintedOf(Blocks.POLISHED_GRANITE_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_GRANITE_STAIRS = registerBlockItem("tainted_granite_stairs", () -> taintedOf(Blocks.POLISHED_GRANITE_STAIRS));

	@GLT public static final RegistryObject<Block> TAINTED_BRICKS = registerBlockItem("tainted_bricks", () -> taintedOf(Blocks.BRICKS));
	@GLT public static final RegistryObject<Block> TAINTED_BRICKS_SLAB = registerBlockItem("tainted_bricks_slab", () -> taintedOf(Blocks.BRICK_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_BRICKS_STAIRS = registerBlockItem("tainted_bricks_stairs", () -> taintedOf(Blocks.BRICK_STAIRS));

	@GLT public static final RegistryObject<Block> TAINTED_STONE_BRICKS = registerBlockItem("tainted_stone_bricks", () -> taintedOf(Blocks.STONE_BRICKS));
	@GLT public static final RegistryObject<Block> TAINTED_STONE_BRICKS_SLAB = registerBlockItem("tainted_stone_bricks_slab", () -> taintedOf(Blocks.STONE_BRICK_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_STONE_BRICKS_STAIRS = registerBlockItem("tainted_stone_bricks_stairs", () -> taintedOf(Blocks.STONE_BRICK_STAIRS));

	@GLT public static final RegistryObject<Block> TAINTED_PRISMARINE = registerBlockItem("tainted_prismarine", () -> taintedOf(Blocks.PRISMARINE));
	@GLT public static final RegistryObject<Block> TAINTED_PRISMARINE_SLAB = registerBlockItem("tainted_prismarine_slab", () -> taintedOf(Blocks.PRISMARINE_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_PRISMARINE_STAIRS = registerBlockItem("tainted_prismarine_stairs", () -> taintedOf(Blocks.PRISMARINE_STAIRS));

	@GLT public static final RegistryObject<Block> TAINTED_PRISMARINE_BRICKS = registerBlockItem("tainted_prismarine_bricks", () -> taintedOf(Blocks.PRISMARINE_BRICKS));
	@GLT public static final RegistryObject<Block> TAINTED_PRISMARINE_BRICKS_SLAB = registerBlockItem("tainted_prismarine_bricks_slab", () -> taintedOf(Blocks.PRISMARINE_BRICK_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_PRISMARINE_BRICKS_STAIRS = registerBlockItem("tainted_prismarine_bricks_stairs", () -> taintedOf(Blocks.PRISMARINE_BRICK_STAIRS));

	@GLT public static final RegistryObject<Block> TAINTED_DARK_PRISMARINE = registerBlockItem("tainted_dark_prismarine", () -> taintedOf(Blocks.DARK_PRISMARINE));
	@GLT public static final RegistryObject<Block> TAINTED_DARK_PRISMARINE_SLAB = registerBlockItem("tainted_dark_prismarine_slab", () -> taintedOf(Blocks.DARK_PRISMARINE_SLAB));
	@GLT public static final RegistryObject<Block> TAINTED_DARK_PRISMARINE_STAIRS = registerBlockItem("tainted_dark_prismarine_stairs", () -> taintedOf(Blocks.DARK_PRISMARINE_STAIRS));

	public static final RegistryObject<Block> TAINTED_SNOW_BLOCK = registerBlockItem("tainted_snow_block", () -> taintedOf(Blocks.SNOW_BLOCK));
	public static final RegistryObject<Block> TAINTED_SNOW = registerBlockItem("tainted_snow", () -> taintedOf(Blocks.SNOW));

	// Dead Blocks
	public static final RegistryObject<Block> DEAD_GRASS_BLOCK = registerBlockItem("dead_grass_block", () -> deadOf(Blocks.GRASS_BLOCK));
	public static final RegistryObject<Block> DEAD_GRASS = registerBlockItem("dead_grass", () -> deadOf(Blocks.GRASS));
	@GLT public static final RegistryObject<Block> DEAD_FLOWER = registerBlockItem("dead_flower", () -> deadOf(
			Blocks.CORNFLOWER,Blocks.DANDELION,Blocks.POPPY,Blocks.BLUE_ORCHID,Blocks.ALLIUM,Blocks.AZURE_BLUET,Blocks.RED_TULIP,Blocks.ORANGE_TULIP,Blocks.WHITE_TULIP,Blocks.PINK_TULIP,Blocks.OXEYE_DAISY,Blocks.LILY_OF_THE_VALLEY
	));
	
	// Dead wood
	// Dead wood
	@GLT public static final RegistryObject<Block> DEAD_LOG = registerBlockItem("dead_log", () -> deadOf(
			Blocks.OAK_LOG,Blocks.BIRCH_LOG,Blocks.SPRUCE_LOG,Blocks.JUNGLE_LOG,Blocks.DARK_OAK_LOG,Blocks.ACACIA_LOG
	));
	@GLT public static final RegistryObject<Block> DEAD_PLANKS = registerBlockItem("dead_planks", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> DEAD_PRESSURE_PLATE = registerBlockItem("dead_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, BlockBehaviour.Properties.of().strength(.5f).sound(SoundType.WOOD).noCollission(), ArcanaBlockSetTypes.DEAD));
	@GLT public static final RegistryObject<Block> DEAD_SLAB = registerBlockItem("dead_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> DEAD_STAIRS = registerBlockItem("dead_stairs", () -> new StairBlock(DEAD_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> DEAD_BUTTON = registerBlockItem("dead_button", () -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.WOOD), ArcanaBlockSetTypes.DEAD, 30, true)); // TODO: it would be funny if this breaks after one click
	@GLT public static final RegistryObject<Block> DEAD_FENCE = registerBlockItem("dead_fence", () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> DEAD_FENCE_GATE = registerBlockItem("dead_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), ArcanaWoodTypes.DEAD));

	// Trypophobius Wood
	@GLT
	public static final RegistryObject<Block> TRYPOPHOBIUS_LOG = registerBlockItem("trypophobius_log", () -> deadOf(
			ArcanaBlocks.DAIR_LOG.get(), ArcanaBlocks.EUCALYPTUS_LOG.get(), ArcanaBlocks.GREATWOOD_LOG.get(), ArcanaBlocks.HAWTHORN_LOG.get(), ArcanaBlocks.SILVERWOOD_LOG.get()
	));
	@GLT public static final RegistryObject<Block> TRYPOPHOBIUS_PLANKS = registerBlockItem("trypophobius_planks", () -> new Block(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> TRYPOPHOBIUS_PRESSURE_PLATE = registerBlockItem("trypophobius_pressure_plate", () -> new PressurePlateBlock(Sensitivity.EVERYTHING, BlockBehaviour.Properties.of().strength(.5f).sound(SoundType.WOOD).noCollission(), ArcanaBlockSetTypes.TRYPOPHOBIUS));
	@GLT public static final RegistryObject<Block> TRYPOPHOBIUS_SLAB = registerBlockItem("trypophobius_slab", () -> new SlabBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> TRYPOPHOBIUS_STAIRS = registerBlockItem("trypophobius_stairs", () -> new StairBlock(TRYPOPHOBIUS_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> TRYPOPHOBIUS_BUTTON = registerBlockItem("trypophobius_button", () -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.WOOD), ArcanaBlockSetTypes.TRYPOPHOBIUS, 30, true));
	@GLT public static final RegistryObject<Block> TRYPOPHOBIUS_FENCE = registerBlockItem("trypophobius_fence", () -> new FenceBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD)));
	@GLT public static final RegistryObject<Block> TRYPOPHOBIUS_FENCE_GATE = registerBlockItem("trypophobius_fence_gate", () -> new FenceGateBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), ArcanaWoodTypes.TRYPOPHOBIUS));

	// Spell made blocks
	public static final RegistryObject<Block> VACUUM_BLOCK = BLOCKS.register("vacuum_block", () -> new VacuumBlock(BlockBehaviour.Properties.of().strength(0).noCollission().noOcclusion().lightLevel(light(15))));
	public static final RegistryObject<Block> WARDENED_BLOCK = BLOCKS.register("wardened_block", () -> new WardenedBlock(BlockBehaviour.Properties.of().strength(-1).lightLevel(light(3))));
	public static final RegistryObject<Block> LIGHT_BLOCK = BLOCKS.register("light_block", () -> new Block(BlockBehaviour.Properties.of().strength(0).noCollission().noOcclusion().lightLevel(light(3))));

	// Fluids
	public static RegistryObject<LiquidBlock> TAINT_FLUID_BLOCK = registerFluidBlock("tainted_goo", () -> new TaintFluid(ArcanaFluids.TAINT_FLUID, Block.Properties.of().noCollission().strength(100.0F).noLootTable()));

	public static RegistryObject<LiquidBlock> registerFluidBlock(String name, Supplier<LiquidBlock> block) {
		RegistryObject<LiquidBlock> registeredBlock = BLOCKS.register(name, block);
//		ArcanaItems.ITEMS.register(name + "_bucket", () -> new BucketItem(() -> block.get().getFluid().getSource(), new Item.Properties()));
		return registeredBlock;
	}

	public static RegistryObject<Block> registerCrystalClusterBlock(String name, Aspect aspect) {
		RegistryObject<Block> registeredBlock = BLOCKS.register(name, () -> new CrystalClusterBlock(BlockBehaviour.Properties.of().strength(1.5F).noOcclusion().forceSolidOn().lightLevel(light(5)).sound(SoundType.AMETHYST_CLUSTER).randomTicks(), aspect));
		RegistryObject<Item> registeredItem = ArcanaItems.ITEMS.register(name, () -> new CrystalClusterItem(registeredBlock.get(), new Item.Properties(), 3));
		ArcanaItems.BLOCK_ITEMS.put(registeredBlock, registeredItem);
		ArcanaItems.CRYSTAL_ITEMS.put(aspect, registeredItem);
		return registeredBlock;
	}

	public static RegistryObject<Block> registerBlockItem(String name, Supplier<Block> block) {
		RegistryObject<Block> registeredBlock = BLOCKS.register(name, block);
		RegistryObject<Item> registeredItem = ArcanaItems.ITEMS.register(name, () -> new BlockItem(registeredBlock.get(), new Item.Properties()));
		ArcanaItems.BLOCK_ITEMS.put(registeredBlock, registeredItem);
		return registeredBlock;
	}

	public static void register(IEventBus eventBus) {
		BLOCKS.register(eventBus);
	}
}