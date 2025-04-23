package com.wonginnovations.arcana.datagen;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

import static com.wonginnovations.arcana.ArcanaVariables.arcLoc;
import static com.wonginnovations.arcana.blocks.ArcanaBlocks.*;
import static com.wonginnovations.arcana.datagen.ArcanaDataGenerators.*;

public class BlockstateGenerator extends BlockStateProvider {

	ExistingFileHelper efh;
	
	public BlockstateGenerator(PackOutput output, ExistingFileHelper exFileHelper) {
		super(output, Arcana.MODID, exFileHelper);
		efh = exFileHelper;
	}
	
	protected void registerStatesAndModels() {
		ArcanaDataGenerators.LIVING_WOODS.forEach((name, texture) -> {
			if (ForgeRegistries.BLOCKS.getValue(arcLoc("stripped_" + name + "_wood")) != Blocks.AIR) {
			simpleBlock(ForgeRegistries.BLOCKS.getValue(arcLoc("stripped_" + name + "_wood")));
			simpleBlock(ForgeRegistries.BLOCKS.getValue(arcLoc(name+"_wood")));
			logBlock((RotatedPillarBlock) ForgeRegistries.BLOCKS.getValue(arcLoc("stripped_" + name + "_log")));
			}
		});
		
		fenceBlock((FenceBlock) DAIR_FENCE.get(), DAIR);
		fenceBlock((FenceBlock) DEAD_FENCE.get(), DEAD);
		fenceBlock((FenceBlock) EUCALYPTUS_FENCE.get(), EUCALYPTUS);
		fenceBlock((FenceBlock) HAWTHORN_FENCE.get(), HAWTHORN);
		fenceBlock((FenceBlock) GREATWOOD_FENCE.get(), GREATWOOD);
		fenceBlock((FenceBlock) SILVERWOOD_FENCE.get(), SILVERWOOD);
		fenceBlock((FenceBlock) TRYPOPHOBIUS_FENCE.get(), TRYPOPHOBIUS);
		fenceBlock((FenceBlock) WILLOW_FENCE.get(), WILLOW);
		
		fenceGateBlock((FenceGateBlock) DAIR_FENCE_GATE.get(), DAIR);
		fenceGateBlock((FenceGateBlock) DEAD_FENCE_GATE.get(), DEAD);
		fenceGateBlock((FenceGateBlock) EUCALYPTUS_FENCE_GATE.get(), EUCALYPTUS);
		fenceGateBlock((FenceGateBlock) HAWTHORN_FENCE_GATE.get(), HAWTHORN);
		fenceGateBlock((FenceGateBlock) GREATWOOD_FENCE_GATE.get(), GREATWOOD);
		fenceGateBlock((FenceGateBlock) SILVERWOOD_FENCE_GATE.get(), SILVERWOOD);
		fenceGateBlock((FenceGateBlock) TRYPOPHOBIUS_FENCE_GATE.get(), TRYPOPHOBIUS);
		fenceGateBlock((FenceGateBlock) WILLOW_FENCE_GATE.get(), WILLOW);
		
		simpleBlock(ArcanaBlocks.ARCANE_STONE.get());
		simpleBlock(ArcanaBlocks.ARCANE_STONE_BRICKS.get());
		simpleBlock(ArcanaBlocks.DUNGEON_BRICKS.get());
		simpleBlock(ArcanaBlocks.CRACKED_DUNGEON_BRICKS.get());
		simpleBlock(ArcanaBlocks.MOSSY_DUNGEON_BRICKS.get());
		
		simpleBlock(ArcanaBlocks.TAINTED_GRANITE.get());
		simpleBlock(ArcanaBlocks.TAINTED_DIORITE.get());
		simpleBlock(ArcanaBlocks.TAINTED_ANDESITE.get());
		
		slabBlock((SlabBlock) ARCANE_STONE_SLAB.get(), ArcanaDataGenerators.ARCANE_STONE, ArcanaDataGenerators.ARCANE_STONE);
		slabBlock((SlabBlock) ARCANE_STONE_BRICKS_SLAB.get(), ArcanaDataGenerators.ARCANE_STONE_BRICKS, ArcanaDataGenerators.ARCANE_STONE_BRICKS);
		slabBlock((SlabBlock) DUNGEON_BRICKS_SLAB.get(), ArcanaDataGenerators.DUNGEON_BRICKS, ArcanaDataGenerators.DUNGEON_BRICKS);
		slabBlock((SlabBlock) CRACKED_DUNGEON_BRICKS_SLAB.get(), ArcanaDataGenerators.CRACKED_DUNGEON_BRICKS, ArcanaDataGenerators.CRACKED_DUNGEON_BRICKS);
		slabBlock((SlabBlock) MOSSY_DUNGEON_BRICKS_SLAB.get(), ArcanaDataGenerators.MOSSY_DUNGEON_BRICKS, ArcanaDataGenerators.MOSSY_DUNGEON_BRICKS);
		
		stairsBlock((StairBlock) ARCANE_STONE_STAIRS.get(), ArcanaDataGenerators.ARCANE_STONE);
		stairsBlock((StairBlock) ARCANE_STONE_BRICKS_STAIRS.get(), ArcanaDataGenerators.ARCANE_STONE_BRICKS);
		stairsBlock((StairBlock) DUNGEON_BRICKS_STAIRS.get(), ArcanaDataGenerators.DUNGEON_BRICKS);
		stairsBlock((StairBlock) CRACKED_DUNGEON_BRICKS_STAIRS.get(), ArcanaDataGenerators.CRACKED_DUNGEON_BRICKS);
		stairsBlock((StairBlock) MOSSY_DUNGEON_BRICKS_STAIRS.get(), ArcanaDataGenerators.MOSSY_DUNGEON_BRICKS);
		
		// pressure plate blockstates are done manually
		
		wallBlock((WallBlock) ARCANE_STONE_WALL.get(), ArcanaDataGenerators.ARCANE_STONE);
		wallBlock((WallBlock) ARCANE_STONE_BRICKS_WALL.get(), ArcanaDataGenerators.ARCANE_STONE_BRICKS);
		wallBlock((WallBlock) DUNGEON_BRICKS_WALL.get(), ArcanaDataGenerators.DUNGEON_BRICKS);
		wallBlock((WallBlock) CRACKED_DUNGEON_BRICKS_WALL.get(), ArcanaDataGenerators.CRACKED_DUNGEON_BRICKS);
		wallBlock((WallBlock) MOSSY_DUNGEON_BRICKS_WALL.get(), ArcanaDataGenerators.MOSSY_DUNGEON_BRICKS);
		
		simpleBlock(SILVER_BLOCK.get());
		simpleBlock(SILVER_ORE.get());
		simpleBlock(VOID_METAL_BLOCK.get());
	}
	
	@Nonnull
	public String getName() {
		return "Arcana Blockstates";
	}
}