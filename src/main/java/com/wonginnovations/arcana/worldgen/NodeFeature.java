package com.wonginnovations.arcana.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.blocks.CrystalClusterBlock;
import com.wonginnovations.arcana.capabilities.AuraChunk;
import com.wonginnovations.arcana.event.WorldTickHandler;
import com.wonginnovations.arcana.world.Node;
import com.wonginnovations.arcana.world.NodeType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static com.wonginnovations.arcana.world.NodeType.DEFAULT;
import static com.wonginnovations.arcana.world.NodeType.SPECIAL_TYPES;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NodeFeature extends Feature<NoneFeatureConfiguration> {
	
	private static final Map<Aspect, Supplier<Block>> CRYSTAL_CLUSTERS_FROM_ASPECTS = new HashMap<>();

	static {
		CRYSTAL_CLUSTERS_FROM_ASPECTS.put(Aspects.AIR, ArcanaBlocks.AIR_CLUSTER);
		CRYSTAL_CLUSTERS_FROM_ASPECTS.put(Aspects.EARTH, ArcanaBlocks.EARTH_CLUSTER);
		CRYSTAL_CLUSTERS_FROM_ASPECTS.put(Aspects.FIRE, ArcanaBlocks.FIRE_CLUSTER);
		CRYSTAL_CLUSTERS_FROM_ASPECTS.put(Aspects.WATER, ArcanaBlocks.WATER_CLUSTER);
		CRYSTAL_CLUSTERS_FROM_ASPECTS.put(Aspects.ORDER, ArcanaBlocks.ORDER_CLUSTER);
		CRYSTAL_CLUSTERS_FROM_ASPECTS.put(Aspects.CHAOS, ArcanaBlocks.CHAOS_CLUSTER);
		
	}
	
	public NodeFeature(Codec<NoneFeatureConfiguration> codec) {
		super(codec);
	}
	
	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
		BlockPos pos = context.origin();
		RandomSource rand = context.random();
		//requireNonNull(NodeChunk.getFrom((Chunk)world.getChunk(pos))).addNode(new Node(NORMAL.genNodeAspects(pos, world, rand), NORMAL, pos.getX(), pos.getY(), pos.getZ()));
		// its a chunkprimer, not a chunk, with no capability data attached
		// add it on the next tick.
		BlockPos newPos = pos.above(5 + rand.nextInt(2));
		NodeType type = rand.nextInt(100) < ArcanaConfig.SPECIAL_NODE_CHANCE.get() ? new ArrayList<>(SPECIAL_TYPES).get(rand.nextInt(SPECIAL_TYPES.size())) : DEFAULT;
		if (rand.nextInt(100) < ArcanaConfig.NODE_CHANCE.get()) {
			WorldTickHandler.onTick.add(newWorld -> {
				AspectHandler aspects = type.genBattery(newPos, newWorld, rand);
				requireNonNull(AuraChunk.getFrom((LevelChunk) newWorld.getChunk(newPos))).addNode(new Node(aspects, type, newPos.getX(), newPos.getY(), newPos.getZ(), 0));
				// Add some crystal clusters around here too
				int successes = 0;
				BlockPos.MutableBlockPos pointer = pos.mutable();
				for (int i = 0; i < 40 && successes < (rand.nextInt(5) + 6); i++) {
					// Pick a random block from the ground
					pointer.set(pos).move(rand.nextInt(7) - rand.nextInt(7), rand.nextInt(5) - rand.nextInt(5), rand.nextInt(7) - rand.nextInt(7));
					if (newWorld.getBlockState(pointer).isAir() || newWorld.getBlockState(pointer).canBeReplaced()) {
						// If it has at least one open side,
						for (Direction value : Direction.values()) {
							BlockState state = newWorld.getBlockState(pointer.offset(value.getNormal()));
							boolean replace = false;
							if (state.isSolidRender(newWorld, pointer.offset(value.getNormal())) || (replace = state.getBlock() == Blocks.SNOW)) {
								if (replace)
									pointer.move(value);
								// Place a crystal,
								Aspect aspect = aspects.getHolder(rand.nextInt(aspects.countHolders())).getStack().getAspect();
								System.out.println(aspect);
								newWorld.setBlockAndUpdate(pointer, CRYSTAL_CLUSTERS_FROM_ASPECTS.get(aspect).get().defaultBlockState().setValue(CrystalClusterBlock.FACING, value.getOpposite()).setValue(CrystalClusterBlock.AGE, 3).setValue(CrystalClusterBlock.WATERLOGGED, newWorld.getBlockState(pointer).getFluidState().is(FluidTags.WATER)));
								// Increment successes
								successes++;
							}
							break;
						}
					}
				}
			});
		}
		return true;
	}
}