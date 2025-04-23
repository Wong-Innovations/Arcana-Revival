package com.wonginnovations.arcana.worldgen.trees.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.worldgen.ArcanaFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GreatwoodFoliagePlacer extends FoliagePlacer {

	public static final Codec<GreatwoodFoliagePlacer> CODEC = RecordCodecBuilder.create(pineFoliagePlacerInstance
			-> foliagePlacerParts(pineFoliagePlacerInstance).and(Codec.intRange(0, 24).fieldOf("height")
			.forGetter(fp -> fp.height)).apply(pineFoliagePlacerInstance, GreatwoodFoliagePlacer::new));
	
	protected final int height;
	
	public GreatwoodFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
		super(radius, offset);
		this.height = height;
	}
	
	protected FoliagePlacerType<?> type() {
		return ArcanaFeatures.GREATWOOD_FOLIAGE.get();
	}
	
	// generate
	protected void createFoliage(LevelSimulatedReader level, FoliagePlacer.FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config, int maxHeight, FoliagePlacer.FoliageAttachment foliageAttachment, int foliageHeight, int foliageRadius, int offset) {
		BlockPos node = foliageAttachment.pos();
		// Iterate in a spheroid to place leaves
		for (int x1 = -3; x1 <= 3; x1++) {
			for (int z1 = -3; z1 <= 3; z1++) {
				for (int y1 = -2; y1 <= 2; y1++) {
					double rX = x1 / 3.0;
					double rZ = z1 / 3.0;
					double rY = y1 / 2.0;
					// Scale the distance to customize the blob shape
					rX *= 1.1;
					rZ *= 1.1;
					rY *= 0.95;
					double dist = rX * rX + rZ * rZ + rY * rY;
					
					// Apply randomness to the radius and place leaves
					if (dist <= 1 + (random.nextDouble() * 0.3)) {
						BlockPos local = node.offset(x1, y1, z1);
						if (level.isStateAtPosition(local, BlockBehaviour.BlockStateBase::isAir)) {
							foliageSetter.set(local, config.foliageProvider.getState(random, local));
						}
					}
				}
			}
		}
	}
	
	// height
	public int foliageHeight(RandomSource rand, int trunkHeight, TreeConfiguration config) {
		return 3;
	}
	
	protected boolean shouldSkipLocation(RandomSource p_225547_, int p_225548_, int p_225549_, int p_225550_, int p_225551_, boolean p_225552_) {
		return false;
	}
}
