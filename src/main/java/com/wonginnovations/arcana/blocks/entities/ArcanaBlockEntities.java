package com.wonginnovations.arcana.blocks.entities;

import com.google.common.collect.Sets;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.blocks.pipes.PipeWindowBlockEntity;
import com.wonginnovations.arcana.blocks.pipes.PumpBlockEntity;
import com.wonginnovations.arcana.blocks.pipes.TubeBlockEntity;
import com.wonginnovations.arcana.blocks.pipes.ValveBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("ConstantConditions")
public class ArcanaBlockEntities {
	
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Arcana.MODID);

	public static final RegistryObject<BlockEntityType<JarBlockEntity>> JAR =
			BLOCK_ENTITIES.register("jar", () -> new BlockEntityType<>(JarBlockEntity::new, Sets.newHashSet(ArcanaBlocks.JAR.get(), ArcanaBlocks.SECURE_JAR.get(), ArcanaBlocks.VOID_JAR.get(), ArcanaBlocks.VACUUM_JAR.get(), ArcanaBlocks.PRESSURE_JAR.get()), null));
	public static final RegistryObject<BlockEntityType<AspectBookshelfBlockEntity>> ASPECT_SHELF =
			BLOCK_ENTITIES.register("aspect_shelf", () -> new BlockEntityType<>(AspectBookshelfBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ASPECT_BOOKSHELF.get(), ArcanaBlocks.ASPECT_BOOKSHELF_BLOCK.get()), null));
	public static final RegistryObject<BlockEntityType<ResearchTableBlockEntity>> RESEARCH_TABLE =
			BLOCK_ENTITIES.register("research_table", () -> new BlockEntityType<>(ResearchTableBlockEntity::new, Sets.newHashSet(ArcanaBlocks.RESEARCH_TABLE.get()), null));
	public static final RegistryObject<BlockEntityType<FociForgeBlockEntity>> FOCI_FORGE =
			BLOCK_ENTITIES.register("foci_forge", () -> new BlockEntityType<>(FociForgeBlockEntity::new, Sets.newHashSet(ArcanaBlocks.FOCI_FORGE.get()), null));
	public static final RegistryObject<BlockEntityType<AspectTesterBlockEntity>> ASPECT_TESTER =
			BLOCK_ENTITIES.register("aspect_tester", () -> new BlockEntityType<>(AspectTesterBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ASPECT_TESTER.get()), null));
	public static final RegistryObject<BlockEntityType<TaintScrubberBlockEntity>> TAINT_SCRUBBER =
			BLOCK_ENTITIES.register("taint_scrubber", () -> new BlockEntityType<>(TaintScrubberBlockEntity::new, Sets.newHashSet(ArcanaBlocks.TAINT_SCRUBBER_MK1.get()), null));
	
	public static final RegistryObject<BlockEntityType<TubeBlockEntity>> ASPECT_TUBE =
			BLOCK_ENTITIES.register("essentia_tube", () -> new BlockEntityType<>(TubeBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ASPECT_TUBE.get()), null));
	public static final RegistryObject<BlockEntityType<ValveBlockEntity>> ASPECT_VALVE =
			BLOCK_ENTITIES.register("essentia_valve", () -> new BlockEntityType<>(ValveBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ASPECT_VALVE.get()), null));
	public static final RegistryObject<BlockEntityType<PipeWindowBlockEntity>> ASPECT_WINDOW =
			BLOCK_ENTITIES.register("essentia_window", () -> new BlockEntityType<>(PipeWindowBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ASPECT_WINDOW.get()), null));
	public static final RegistryObject<BlockEntityType<PumpBlockEntity>> ASPECT_PUMP =
			BLOCK_ENTITIES.register("essentia_pump", () -> new BlockEntityType<>(PumpBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ASPECT_PUMP.get()), null));
	
	public static final RegistryObject<BlockEntityType<PedestalBlockEntity>> PEDESTAL =
			BLOCK_ENTITIES.register("pedestal", () -> new BlockEntityType<>(PedestalBlockEntity::new, Sets.newHashSet(ArcanaBlocks.PEDESTAL.get()), null));
	public static final RegistryObject<BlockEntityType<AlembicBlockEntity>> ALEMBIC =
			BLOCK_ENTITIES.register("alembic", () -> new BlockEntityType<>(AlembicBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ALEMBIC.get()), null));
	public static final RegistryObject<BlockEntityType<CrucibleBlockEntity>> CRUCIBLE =
			BLOCK_ENTITIES.register("crucible", () -> new BlockEntityType<>(CrucibleBlockEntity::new, Sets.newHashSet(ArcanaBlocks.CRUCIBLE.get()), null));
	public static final RegistryObject<BlockEntityType<ArcaneCraftingTableBlockEntity>> ARCANE_WORKBENCH =
			BLOCK_ENTITIES.register("arcane_crafting_table", () -> new BlockEntityType<>(ArcaneCraftingTableBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ARCANE_CRAFTING_TABLE.get()), null));
	public static final RegistryObject<BlockEntityType<AspectCrystallizerBlockEntity>> ASPECT_CRYSTALLIZER =
			BLOCK_ENTITIES.register("aspect_crystallizer", () -> new BlockEntityType<>(AspectCrystallizerBlockEntity::new, Sets.newHashSet(ArcanaBlocks.ASPECT_CRYSTALLIZER.get()), null));
	public static final RegistryObject<BlockEntityType<VacuumBlockEntity>> VACUUM =
			BLOCK_ENTITIES.register("vacuum", () -> new BlockEntityType<>(VacuumBlockEntity::new, Sets.newHashSet(ArcanaBlocks.VACUUM_BLOCK.get()), null));
	public static final RegistryObject<BlockEntityType<WardenedBlockBlockEntity>> WARDENED_BLOCK =
			BLOCK_ENTITIES.register("wardened_block", () -> new BlockEntityType<>(WardenedBlockBlockEntity::new, Sets.newHashSet(ArcanaBlocks.WARDENED_BLOCK.get()), null));
}