package com.wonginnovations.arcana.worldgen.trees.features;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wonginnovations.arcana.worldgen.ArcanaFeatures;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.capabilities.AuraChunk;
import com.wonginnovations.arcana.event.WorldTickHandler;
import com.wonginnovations.arcana.world.Node;
import com.wonginnovations.arcana.world.NodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SilverwoodTrunkPlacer extends TrunkPlacer {
	
	public static final Codec<SilverwoodTrunkPlacer> CODEC = RecordCodecBuilder.create((builderInstance) -> trunkPlacerParts(builderInstance).apply(builderInstance, SilverwoodTrunkPlacer::new));

	public SilverwoodTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
		super(baseHeight, heightRandA, heightRandB);
	}

	@Override
	protected TrunkPlacerType<SilverwoodTrunkPlacer> type() {
		return ArcanaFeatures.SILVERWOOD_TRUNK.get();
	}
	
	// more like Generate
	@Override
	public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource rand, int treeHeight, BlockPos pos, TreeConfiguration config) {
		int height = rand.nextInt(2) + rand.nextInt(2) + 12;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if (y >= 1 && y + height + 1 < 255) {
			BlockPos ground = pos.below();
			for (int x1 = -2; x1 <= 2; x1++) {
				for (int z1 = -2; z1 <= 2; z1++) {
					// Manhattan distance from center
					int dist = Math.abs(x1) + Math.abs(z1);
					
					// Place in a 2-wide plus formation
					if (dist <= 2) {
						// 0 distance: normal height
						int logHeight = height;
						if (dist == 1) {
							// plus shape: 70% of height
							logHeight = (int)(height * 0.7) + rand.nextInt(2);
						} else if (dist == 2) {
							// 2-wide plus shape: 1 high plus random
							logHeight = 1 + rand.nextInt(2);
						}
						
						// Place the logs
						generateLogColumn(level, blockSetter, rand, pos.offset(x1, 0, z1), logHeight, config);
					}
				}
			}
			
			// Iterate in a spheroid to place leaves
			for (int x1 = -4; x1 <= 4; x1++) {
				for (int z1 = -4; z1 <= 4; z1++) {
					for (int y1 = -5; y1 <= 5; y1++) {
						double rX = x1 / 4.0;
						double rZ = z1 / 4.0;
						double rY = y1 / 5.0;
						double dist = rX * rX + rZ * rZ + rY * rY;
						
						// Apply randomness to the radius and place leaves
						if (dist <= 0.8 + (rand.nextDouble() * 0.4)) {
							BlockPos local = pos.offset(x1, height - 4 + y1, z1);
							if (TreeFeature.isAirOrLeaves(level, local)) {
//								config.foliagePlacer.createFoliage();
								blockSetter.accept(local, config.foliageProvider.getState(rand, local));
							}
						}
					}
				}
			}
			
			// add pure node at half height
			if (rand.nextInt(100) < ArcanaConfig.SILVERWOOD_NODE_CHANCE.get())
				WorldTickHandler.onTick.add(w -> {
					NodeType type = NodeType.PURE;
					AspectHandler aspects = type.genBattery(pos, w, rand);
					requireNonNull(AuraChunk.getFrom((LevelChunk)w.getChunk(pos))).addNode(new Node(aspects, type, x, Mth.floor(y + height / 2f), z, 0));
				});
			
			return Lists.newArrayList(new FoliagePlacer.FoliageAttachment(pos.above(height - 4), 1, false));
		}
		return Collections.emptyList();
	}
	
	private void generateLogColumn(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, BlockPos start, int height, TreeConfiguration config) {
		for (int y = 0; y < height; y++) {
			BlockPos local = start.above(y);
			if (TreeFeature.validTreePos(level, local)) {
				placeLog(level, blockSetter, random, local, config);
//				world.setBlockState(local, config.trunkProvider.getBlockState(random, local), 3);
			}
		}
	}
}