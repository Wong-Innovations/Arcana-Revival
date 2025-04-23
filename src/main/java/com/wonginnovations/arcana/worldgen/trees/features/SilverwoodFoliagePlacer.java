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
public class SilverwoodFoliagePlacer extends FoliagePlacer {

	public static final Codec<SilverwoodFoliagePlacer> CODEC = RecordCodecBuilder.create(pineFoliagePlacerInstance
			-> foliagePlacerParts(pineFoliagePlacerInstance).and(Codec.intRange(0, 16).fieldOf("height")
			.forGetter(fp -> fp.height)).apply(pineFoliagePlacerInstance, SilverwoodFoliagePlacer::new));
	
	protected final int height;
	
	public SilverwoodFoliagePlacer(IntProvider radius, IntProvider offset, int height) {
		super(radius, offset);
		this.height = height;
	}
	
	protected FoliagePlacerType<?> type() {
		return ArcanaFeatures.SILVERWOOD_FOLIAGE.get();
	}
	
	// generate
	protected void createFoliage(LevelSimulatedReader level, FoliagePlacer.FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config, int maxHeight, FoliagePlacer.FoliageAttachment foliageAttachment, int foliageHeight, int foliageRadius, int offset) {
		BlockPos pos = foliageAttachment.pos();
		// Iterate in a spheroid to place leaves
		for (int x1 = -4; x1 <= 4; x1++) {
			for (int z1 = -4; z1 <= 4; z1++) {
				for (int y1 = -5; y1 <= 5; y1++) {
					double rX = x1 / 4.0;
					double rZ = z1 / 4.0;
					double rY = y1 / 5.0;
					double dist = rX * rX + rZ * rZ + rY * rY;
					
					// Apply randomness to the radius and place leaves
					if (dist <= 0.8 + (random.nextDouble() * 0.4)) {
						BlockPos local = pos.offset(x1, y1, z1);
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
		return 0;
	}
	
	protected boolean shouldSkipLocation(RandomSource p_225547_, int p_225548_, int p_225549_, int p_225550_, int p_225551_, boolean p_225552_) {
		return false;
	}
}
