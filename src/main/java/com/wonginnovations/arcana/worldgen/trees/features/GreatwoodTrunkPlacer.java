package com.wonginnovations.arcana.worldgen.trees.features;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wonginnovations.arcana.worldgen.ArcanaFeatures;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GreatwoodTrunkPlacer extends TrunkPlacer {
	
	public static final Codec<GreatwoodTrunkPlacer> CODEC = RecordCodecBuilder.create((builderInstance) -> trunkPlacerParts(builderInstance).apply(builderInstance, GreatwoodTrunkPlacer::new));
	//public static final TrunkPlacerType<GreatwoodTrunkPlacer> GREATWOOD_PLACER = Registry.register(BuiltInRegistries.TRUNK_PLACER_TYPE, Arcana.arcLoc("greatwood_placer"), new TrunkPlacerType<>(CODEC));

	public GreatwoodTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
		super(baseHeight, heightRandA, heightRandB);
	}

	@Override
	protected TrunkPlacerType<GreatwoodTrunkPlacer> type() {
		return ArcanaFeatures.GREATWOOD_TRUNK.get();
	}

	@Override
	public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource rand, int treeHeight, BlockPos pos, TreeConfiguration config) {
		// todo: customisation options?
		int height = rand.nextInt(3) + rand.nextInt(3) + /*treeHeight*/ 18;
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		if (y >= 1 && y + height + 1 < 255) {
			BlockPos ground = pos.below();
			/*if (!isSoil(world, ground, config.getSapling()))
				return false;
			else if (!isSpaceClearForHeight(world, pos, height))
				return false;
			else {*/
			setDirtAt(level, blockSetter, rand, ground, config);
			setDirtAt(level, blockSetter, rand, ground.east(), config);
			setDirtAt(level, blockSetter, rand, ground.south(), config);
			setDirtAt(level, blockSetter, rand, ground.south().east(), config);
			int top = y + height - 1;
			Set<BlockPos> leafNodes = new HashSet<>();
			// roots
			for (int x1 = -1; x1 <= 2; x1++) {
				for (int z1 = -1; z1 <= 2; z1++) {
					// Skip root placement if we're in the trunk
					if ((x1 == 0 || x1 == 1) && (z1 == 0 || z1 == 1)) {
						continue;
					}
					
					// Get the root height by nesting random calls to make it biased towards 0
					int rootHeight = rand.nextInt(rand.nextInt(4) + 1);
					
					if (isRootOnEdge(x1) && isRootOnEdge(z1)) {
						rootHeight--; // Reduce on corners
					}
					
					if (rootHeight > 0) {
						BlockPos groundPos = new BlockPos(x + x1, y - 1, z + z1);
						
						if (TreeFeature.isAirOrLeaves(level, groundPos)) {
//							level.setBlockState(groundPos, Blocks.DIRT.defaultBlockState(), 3);
							setDirtAt(level, blockSetter, rand, groundPos, config);
						}
						
						// Place roots
						for (int curHeight = 0; curHeight < rootHeight; curHeight++) {
							int curY = y + curHeight;
							BlockPos curPos = new BlockPos(x + x1, curY, z + z1);
							
							if (TreeFeature.validTreePos(level, curPos)) {
								placeLog(level, blockSetter, rand, curPos, config);
							}
						}
					}
				}
			}
			
			// main trunk
			for (int curHeight = 0; curHeight < height; ++curHeight) {
				int curY = y + curHeight;
				BlockPos curPos = new BlockPos(x, curY, z);
				if (TreeFeature.validTreePos(level, curPos)) {
					placeLog(level, blockSetter, rand, curPos, config);
					placeLog(level, blockSetter, rand, curPos.east(), config);
					placeLog(level, blockSetter, rand, curPos.south(), config);
					placeLog(level, blockSetter, rand, curPos.east().south(), config);
				}
				
				// Branches
				if (curHeight > 6 && curHeight < height - 3) {
					int branchCount = 1 + (curHeight / 8);
					double offset = Math.PI * 2 * rand.nextDouble();
					
					// Make fewer branches at the bottom, but more at the top
					for (int i = 0; i < branchCount; i++) {
						double angle = (((double)i / branchCount) * (Math.PI * 2)) + offset + (rand.nextDouble() * 0.2);
						int length = rand.nextInt(2) + (6 - branchCount) + curHeight / 10;
						// Choose a starting location on the trunk
						BlockPos start = chooseStart(curPos, rand);
						
						for (int j = 0; j <= length; j++) {
							// Traverse through the branch
							BlockPos local = start.offset(Mth.floor(Math.cos(angle) * j), Mth.floor(j / 2.0), Mth.floor(Math.sin(angle) * j));
							
							// Place logs if it's air
							if (TreeFeature.isAirOrLeaves(level, local)) {
								placeLog(level, blockSetter, rand, local, config);
							}
							
							// If we're at the end, mark this position for generating leaves
							if (j == length) {
								leafNodes.add(local);
							}
						}
					}
					
				}
				
				// Add leaves to the top of the trunk
				if (curHeight == height - 1) {
					leafNodes.add(curPos);
					leafNodes.add(curPos.east());
					leafNodes.add(curPos.south());
					leafNodes.add(curPos.east().south());
				}
			}
			List<FoliagePlacer.FoliageAttachment> list = Lists.newArrayList();
			for (BlockPos node : leafNodes)
				// position, ???, ???
				list.add(new FoliagePlacer.FoliageAttachment(node, 1, false));
			return list;
		}
		return Collections.emptyList();
	}

	private boolean isRootOnEdge(int axis) {
		return axis == -1 || axis == 2;
	}
	
	private static BlockPos chooseStart(BlockPos start, RandomSource random) {
        return switch (random.nextInt(4)) {
            case 1 -> start.east();
            case 2 -> start.south();
            case 3 -> start.east().south();
            default -> start;
        };
	}
	
	private boolean isSpaceClearForHeight(LevelSimulatedReader level, BlockPos pos, int height) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
		
		for (int i = 0; i <= height + 1; ++i) {
			int r = 1;
			if (i == 0)
				r = 0;
			
			if (i >= height - 1)
				r = 2;
			
			for (int xr = -r; xr <= r; ++xr)
				for (int zr = -r; zr <= r; ++zr)
					if (!TreeFeature.validTreePos(level, cursor.set(x + xr, y + i, z + zr)))
						return false;
		}
		
		return true;
	}
}