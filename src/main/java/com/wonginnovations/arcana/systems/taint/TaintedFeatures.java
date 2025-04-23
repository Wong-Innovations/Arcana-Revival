package com.wonginnovations.arcana.systems.taint;
/*
import com.google.common.collect.ImmutableList;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import net.minecraft.world.level.block.LogBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.HugeTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliageplacer.AcaciaFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.foliageplacer.SpruceFoliagePlacer;
import net.minecraft.world.gen.treedecorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.treedecorator.BeehiveTreeDecorator;

public class TaintedFeatures {
	// TODO: put this all into ArcanaFeatures with other feature configs and register with registry events
	public static class Config {

		public static final TreeFeatureConfig FANCY_TREE_WITH_MORE_BEEHIVES_CONFIG = (
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_OAK_LOG.get().defaultBlockState().with(LogBlock.AXIS, Direction.Axis.Y)),
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_OAK_LEAVES.get().defaultBlockState()),
						new BlobFoliagePlacer(0, 0))
		).decorators(
				ImmutableList.of(new BeehiveTreeDecorator(0.05F)))
				.setSapling((net.minecraftforge.common.IPlantable) ArcanaBlocks.TAINTED_OAK_SAPLING.get())
				.build();



		public static final TreeFeatureConfig FANCY_TREE_CONFIG = (
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_OAK_LOG.get().defaultBlockState().with(LogBlock.AXIS, Direction.Axis.Y)),
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_OAK_LEAVES.get().defaultBlockState()),
						new BlobFoliagePlacer(0, 0)))
				.setSapling((net.minecraftforge.common.IPlantable) ArcanaBlocks.TAINTED_OAK_SAPLING.get())
				.build();



		public static final TreeFeatureConfig OAK_TREE_WITH_MORE_BEEHIVES_CONFIG = (
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_OAK_LOG.get().defaultBlockState().with(LogBlock.AXIS, Direction.Axis.Y)),
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_OAK_LEAVES.get().defaultBlockState()),
						new BlobFoliagePlacer(2, 0))
		).baseHeight(4).heightRandA(2).foliageHeight(3).ignoreVines()
				.decorators(ImmutableList.of(new BeehiveTreeDecorator(0.05F)))
				.setSapling((net.minecraftforge.common.IPlantable) ArcanaBlocks.TAINTED_OAK_SAPLING.get())
				.build();



		public static final TreeFeatureConfig OAK_TREE_CONFIG = (
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_OAK_LOG.get().defaultBlockState().with(LogBlock.AXIS, Direction.Axis.Y)),
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_OAK_LEAVES.get().defaultBlockState()),
						new BlobFoliagePlacer(2, 0))
		).baseHeight(4).heightRandA(2).foliageHeight(3).ignoreVines()
				.setSapling((net.minecraftforge.common.IPlantable) ArcanaBlocks.TAINTED_OAK_SAPLING.get())
				.build();



		public static final TreeFeatureConfig BIRCH_TREE_WITH_MORE_BEEHIVES_CONFIG = (
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_BIRCH_LOG.get().defaultBlockState().with(LogBlock.AXIS, Direction.Axis.Y)),
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_BIRCH_LEAVES.get().defaultBlockState()),
						new BlobFoliagePlacer(2, 0))
		).baseHeight(5).heightRandA(2).foliageHeight(3).ignoreVines()
				.decorators(ImmutableList.of(new BeehiveTreeDecorator(0.05F)))
				.setSapling((net.minecraftforge.common.IPlantable) ArcanaBlocks.TAINTED_BIRCH_SAPLING.get())
				.build();



		public static final TreeFeatureConfig BIRCH_TREE_CONFIG = (
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_BIRCH_LOG.get().defaultBlockState().with(LogBlock.AXIS, Direction.Axis.Y)),
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_BIRCH_LEAVES.get().defaultBlockState()),
						new BlobFoliagePlacer(2, 0))
		).baseHeight(5).heightRandA(2).foliageHeight(3).ignoreVines()
				.setSapling((net.minecraftforge.common.IPlantable) ArcanaBlocks.TAINTED_BIRCH_SAPLING.get())
				.build();



		public static final TreeFeatureConfig SPRUCE_TREE_CONFIG = (
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_SPRUCE_LOG.get().defaultBlockState().with(LogBlock.AXIS, Direction.Axis.Y)),
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_SPRUCE_LEAVES.get().defaultBlockState()),
						new SpruceFoliagePlacer(2, 1))
		).baseHeight(6).heightRandA(3).trunkHeight(1).trunkHeightRandom(1).trunkTopOffsetRandom(2).ignoreVines()
				.setSapling((net.minecraftforge.common.IPlantable)ArcanaBlocks.TAINTED_SPRUCE_SAPLING.get())
				.build();



		public static final HugeTreeFeatureConfig MEGA_SPRUCE_TREE_CONFIG = (
				new HugeTreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_SPRUCE_LOG.get().defaultBlockState().with(LogBlock.AXIS, Direction.Axis.Y)),
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_SPRUCE_LEAVES.get().defaultBlockState())
				)
		).baseHeight(13)
				.heightInterval(15)
				.crownHeight(13)
				.decorators(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_PODZOL.get().defaultBlockState()))))
				.setSapling((net.minecraftforge.common.IPlantable) ArcanaBlocks.TAINTED_SPRUCE_SAPLING.get())
				.build();



		public static final HugeTreeFeatureConfig MEGA_PINE_TREE_CONFIG = (
				new HugeTreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_SPRUCE_LOG.get().defaultBlockState().with(LogBlock.AXIS, Direction.Axis.Y)),
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_SPRUCE_LEAVES.get().defaultBlockState())
				)
		).baseHeight(13).heightInterval(15).crownHeight(3)
				.decorators(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_PODZOL.get().defaultBlockState()))))
				.setSapling((net.minecraftforge.common.IPlantable) ArcanaBlocks.TAINTED_SPRUCE_SAPLING.get())
				.build();



		public static final TreeFeatureConfig JUNGLE_SAPLING_TREE_CONFIG = (
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_JUNGLE_LOG.get().defaultBlockState().with(LogBlock.AXIS, Direction.Axis.Y)),
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_JUNGLE_LEAVES.get().defaultBlockState()),
						new BlobFoliagePlacer(2, 0))
		).baseHeight(4).heightRandA(8).foliageHeight(3).ignoreVines()
				.setSapling((net.minecraftforge.common.IPlantable)ArcanaBlocks.TAINTED_JUNGLE_SAPLING.get())
				.build();



		public static final HugeTreeFeatureConfig MEGA_JUNGLE_TREE_CONFIG = (
				new HugeTreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_JUNGLE_LOG.get().defaultBlockState().with(LogBlock.AXIS, Direction.Axis.Y)),
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_JUNGLE_LEAVES.get().defaultBlockState())
				)
		).baseHeight(10).heightInterval(20)
				.decorators(ImmutableList.of(new TaintedTrunkVineTreeDecorator(), new TaintedLeaveVineTreeDecorator()))
				.setSapling((net.minecraftforge.common.IPlantable) ArcanaBlocks.TAINTED_JUNGLE_SAPLING.get())
				.build();



		public static final TreeFeatureConfig ACACIA_TREE_CONFIG = (
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_ACACIA_LOG.get().defaultBlockState().with(LogBlock.AXIS, Direction.Axis.Y)),
						new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_ACACIA_LEAVES.get().defaultBlockState()),
						new AcaciaFoliagePlacer(2, 0))
		).baseHeight(3).heightRandA(2).heightRandB(2).trunkHeight(0).ignoreVines()
				.setSapling((net.minecraftforge.common.IPlantable)ArcanaBlocks.TAINTED_ACACIA_SAPLING.get()).build();

		public static final HugeTreeFeatureConfig DARK_OAK_TREE_CONFIG = (new HugeTreeFeatureConfig.Builder(new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_DARKOAK_LOG.get().defaultBlockState().with(LogBlock.AXIS, Direction.Axis.Y)), new SimpleBlockStateProvider(ArcanaBlocks.TAINTED_DARKOAK_LEAVES.get().defaultBlockState()))).baseHeight(6).setSapling((net.minecraftforge.common.IPlantable) ArcanaBlocks.TAINTED_DARKOAK_SAPLING.get()).build();
	}
}*/