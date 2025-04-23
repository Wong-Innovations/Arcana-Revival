package com.wonginnovations.arcana.systems.taint;

import com.google.common.collect.Lists;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.aspects.VisShareable;
import com.wonginnovations.arcana.blocks.*;
import com.wonginnovations.arcana.blocks.tainted.*;
import com.wonginnovations.arcana.blocks.entities.JarBlockEntity;
import com.wonginnovations.arcana.entities.tainted.*;
import com.wonginnovations.arcana.world.AuraView;
import com.wonginnovations.arcana.world.ServerAuraView;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

import static com.wonginnovations.arcana.blocks.DelegatingBlock.switchBlock;

public class Taint {

	public static final BooleanProperty UNTAINTED = BooleanProperty.create("untainted"); // false by default
	private static final Map<Block, Block> TAINT_MAP = new HashMap<>();
	private static final Map<Block, Block> DEAD_MAP = new HashMap<>();

	/**
	 * We can't access world.rand from {@link #getLivingOfBlock(Block)} or {@link #getPureOfBlock(Block)}
	 * because methods that use it are not provided with a World.
	 * These are only called server-side, so its safe to use.
	 */
	private static final Random RANDOM_LIVING_PICKER = new Random();

	public static void init() {
		addDeadUnstableBlock(
				Blocks.OAK_LEAVES, Blocks.BIRCH_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.ACACIA_LEAVES,
				ArcanaBlocks.DAIR_LEAVES.get(), ArcanaBlocks.EUCALYPTUS_LEAVES.get(), ArcanaBlocks.GREATWOOD_LEAVES.get(), ArcanaBlocks.HAWTHORN_LEAVES.get(), ArcanaBlocks.SILVERWOOD_LEAVES.get()
		);
	}

	@SuppressWarnings("deprecation")
	public static Block taintedOf(Block parent, Block... blocks) {
		Block tainted;
		if (parent instanceof FallingBlock) {
			tainted = new TaintedFallingBlock(parent);
		} else if (parent instanceof VineBlock) {
			tainted = new TaintedVineBlock(parent);
		} else if (parent instanceof SaplingBlock) {
			tainted = new TaintedSaplingBlock(parent);
		} else if (parent instanceof IPlantable || parent instanceof Shearable || parent instanceof BonemealableBlock) {
			tainted = new TaintedPlantBlock(parent);
		} else if (parent instanceof StairBlock) {
			tainted = new TaintedStairsBlock(parent);
		} else if (parent instanceof SlabBlock) {
			tainted = new TaintedSlabBlock(parent);
		} else if (parent instanceof LeavesBlock) {
			tainted = new TaintedLeavesBlock(parent);
		} else if (parent instanceof SnowLayerBlock) {
			tainted = new TaintedSnowLayerBlock(parent);
		} else if (parent instanceof RotatedPillarBlock) {
			tainted = new TaintedRotatedPillarBlock(parent);
		} else {
			tainted = new TaintedBlock(parent);
		}

		//Add children to TaintMapping, NOT parent (see TaintedBlock)!
		for (Block block : blocks) {
			Taint.addTaintMapping(block, tainted);
		}

		return tainted;
	}

	public static void addDeadUnstableBlock(Block... blocks) {
		DEAD_MAP.putAll(Lists.newArrayList(blocks).stream().collect(Collectors.toMap(block -> block, block -> Blocks.AIR)));
	}

	@SuppressWarnings("deprecation")
	public static Block deadOf(Block parent, Block... blocks) {
		Block dead;
		if (parent instanceof IPlantable || parent instanceof Shearable || parent instanceof BonemealableBlock)
			dead = new DeadPlantBlock(parent);
		else if (parent instanceof RotatedPillarBlock)
			dead = new DeadRotatedPillarBlock(parent);
		else
			dead = new DeadBlock(parent);

		//Add children to DeadMapping, NOT parent (see DeadBlock)!
		for (Block block : blocks)
			Taint.addDeadMapping(block, dead);

		return dead;
	}

	/**
	 * Returns the purified version of the input tainted block.
	 * If the input is not a tainted block, the input will be returned.
	 * If there are multiple pure variants (such as for tainted wood), a random one is chosen, which may be different between calls.
	 *
	 * @param block
	 * 		A tainted block.
	 * @return A random pure version of the input tainted block, or the input block if its not tainted.
	 */
	public static Block getPureOfBlock(Block block) {
		List<Block> pures = TAINT_MAP.entrySet().stream()
				.filter(entry -> entry.getValue() == block)
				.map(Map.Entry::getKey)
				.toList();
		return pures.isEmpty() ? block : pures.get(RANDOM_LIVING_PICKER.nextInt(pures.size()));
	}

	/**
	 * Returns the living version of the input dead block.
	 * If the input is not a dead block, the input will be returned.
	 * If there are multiple living variants, a random one is chosen, which may be different between calls.
	 *
	 * @param block
	 * 		A dead block.
	 * @return A random living version of the input tainted block, or the input block if its not dead.
	 */
	public static Block getLivingOfBlock(Block block) {
		List<Block> livings = DEAD_MAP.entrySet().stream()
				.filter(entry -> entry.getValue() == block)
				.map(Map.Entry::getKey)
				.toList();
		return livings.isEmpty() ? block : livings.get(RANDOM_LIVING_PICKER.nextInt(livings.size()));
	}

	public static Block getDeadOfBlock(Block block) {
		return DEAD_MAP.getOrDefault(block, block);
	}

	public static Block getTaintedOfBlock(Block block) {
		return TAINT_MAP.get(block);
	}

	public static void addTaintMapping(Block original, Block tainted) {
		TAINT_MAP.put(original, tainted);
	}

	public static void addDeadMapping(Block original, Block dead) {
		DEAD_MAP.put(original, dead);
	}

	public static void tickTaintedBlock(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		// if we're near a pure node, purify
		if (isBlockProtectedByPureNode(level, pos)) {
			BlockState pureState = switchBlock(state, getPureOfBlock(state.getBlock()));
			level.setBlockAndUpdate(pos, pureState);
			if (pureState.isAir()) {
				int rnd = level.getRandom().nextInt(9) + 4;
				for (int j = 0; j < rnd; j++)
					level.addParticle(
							new BlockParticleOption(ParticleTypes.FALLING_DUST, Blocks.BLACK_CONCRETE_POWDER.defaultBlockState()),
							pos.getX() + 0.5f + ((level.getRandom().nextInt(9) - 4) / 10f), pos.getY() + 0.5f + ((level.getRandom().nextInt(9) - 4) / 10f), pos.getZ() + 0.5f + ((level.getRandom().nextInt(9) - 4) / 10f),
							0.1f, 0.1f, 0.1f
					);
			}
			return;
		}
		// if this is a tainted block that spreads,
		if (state.getBlock() == ArcanaBlocks.TAINT_FLUID_BLOCK.get() || !state.getValue(UNTAINTED)) {
			// and if flux level is greater than 5,
			ServerAuraView auraView = new ServerAuraView(level);
			float at = auraView.getFluxAt(pos);
			if (at > ArcanaConfig.TAINT_SPREAD_MIN_FLUX.get()) {
				// Pick a block within a 4x6x4 area.
				// If this block is air, stop. If this block doesn't have a tainted form, re-roll. If this block is near a pure node, stop.
				// Do this up to 5 times.
				Block tainted = null;
				BlockPos taintingPos = pos;
				int iter = 0;
				while(tainted == null && iter < ArcanaConfig.TAINT_SPREAD_TRIES.get()) {
					int xzSpread = ArcanaConfig.TAINT_SPREAD_XZ.get();
					int ySpread = ArcanaConfig.TAINT_SPREAD_Y.get();
					taintingPos = pos.north(random.nextInt(xzSpread * 2 + 1) - xzSpread).west(random.nextInt(xzSpread * 2 + 1) - xzSpread).above(random.nextInt(ySpread * 2 + 1) - ySpread);
					tainted = level.getBlockState(taintingPos).getBlock();
					if (level.getBlockState(taintingPos).getBlock() == Blocks.AIR || isBlockProtectedByPureNode(level, taintingPos)) {
						tainted = null;
						break;
					}
					tainted = Taint.getTaintedOfBlock(tainted);
					iter++;
				}
				// Replace it with its tainted form if found.
				if (tainted != null) {
					BlockState taintedState = switchBlock(level.getBlockState(taintingPos), tainted).setValue(UNTAINTED, false);
					level.setBlockAndUpdate(taintingPos, taintedState);
					// Reduce flux level
					auraView.addFluxAt(pos, -ArcanaConfig.TAINT_SPREAD_FLUX_COST.get());
					// Schedule a tick
					level.scheduleTick(pos, state.getBlock(), taintTickWait(at));
				}
			}
		}
	}

	public static boolean isBlockProtectedByPureNode(Level level, BlockPos pos) {
		AuraView view = AuraView.SIDED_FACTORY.apply(level);
		int range = ArcanaConfig.PURE_NODE_TAINT_PROTECT_RANGE.get();
		return view.getNodesWithinAABB(new AABB(pos).inflate(range))
				.stream()
				.anyMatch(node ->
						node.type().blocksTaint(level, view, node, pos)
								&& pos.distToCenterSqr(node.getX(), node.getY(), node.getZ()) <= range * range);
	}

	public static int taintTickWait(float taintLevel) {
		// more taint level -> less tick wait
		int base = (int)((1d / taintLevel) * 200);
		return base > 0 ? base : 1;
	}

	public static void tickTaintInContainer(Object sender) {
		if (sender instanceof JarBlockEntity jar) {
            if (!((VisShareable) jar).isSecure()) {
				if (jar.getLevel().random.nextInt(20) == 2)
					jar.vis.getHolder(0).drain(1, false);
				if (jar.getLevel().isClientSide)
					return;
				ServerAuraView auraView = new ServerAuraView((ServerLevel) jar.getLevel());
				if (jar.getLevel().random.nextInt(20) == 2)
					auraView.addFluxAt(jar.getBlockPos(), 1);
			}
		}
	}

	public static boolean isAreaInTaintBiome(BlockPos pos, Level level) {
		// check if they're in a taint biome
		// 7x13x7 cube, centred on the entity
		// at least 20 tainted blocks
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
		int counter = 0;
		for (int x = -3; x < 7; x++)
			for (int y = -6; y < 13; y++)
				for (int z = -3; z < 7; z++) {
					mutable.set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
					try{
						BlockState state = level.getBlockState(mutable);
						if (isTainted(state.getBlock()) && (!state.hasProperty(UNTAINTED) || !state.getValue(UNTAINTED)))
							counter++;
					}catch(ArrayIndexOutOfBoundsException ignored) {
						// ChunkRenderCache throws this when you try to check somewhere "out-of-bounds".
					}
					if (counter >= 20)
						return true;
				}
		return false;
	}

	@SuppressWarnings({"rawtypes"})
	private static final Map<EntityType, EntityType> entityTaintMap = new HashMap<>();

	@SuppressWarnings({"rawtypes"})
	public static EntityType taintedEntityOf(EntityType entity) {
		ResourceLocation entityName = ForgeRegistries.ENTITY_TYPES.getKey(entity);
		if (entityName == null)
			return null;

		String id = new ResourceLocation(Arcana.MODID, "tainted_" + entityName.getPath()).toString();
		float w = entity.getDimensions().width, h = entity.getDimensions().height;

		EntityType<? extends Entity> tainted = entity == EntityType.SQUID
				? EntityType.Builder.of(TaintedSquidEntity::new, MobCategory.MONSTER).sized(w, h).build(id)
				: entity == EntityType.GHAST
				? EntityType.Builder.of(TaintedGhastEntity::new, MobCategory.MONSTER).sized(w, h).build(id)
				: entity == EntityType.CAVE_SPIDER
				? EntityType.Builder.of(TaintedCaveSpiderEntity::new, MobCategory.MONSTER).sized(w, h).build(id)
				: entity == EntityType.SKELETON
				? EntityType.Builder.of(TaintedSkeletonEntity::new, MobCategory.MONSTER).sized(w, h).build(id)
				: entity == EntityType.PANDA
				? EntityType.Builder.of(TaintedPandaEntity::new, MobCategory.MONSTER).sized(w, h).build(id)
				: entity == EntityType.SNOW_GOLEM
				? EntityType.Builder.of(TaintedSnowGolemEntity::new, MobCategory.MONSTER).sized(w, h).build(id)
				: entity == EntityType.RABBIT
				? EntityType.Builder.of(TaintedRabbitEntity::new, MobCategory.MONSTER).sized(w, h).build(id)
				: entity == EntityType.POLAR_BEAR
				? EntityType.Builder.of(TaintedPolarBearEntity::new, MobCategory.MONSTER).sized(w, h).build(id)
				: entity == EntityType.DONKEY
				? EntityType.Builder.of(TaintedDonkeyEntity::new, MobCategory.MONSTER).sized(w, h).build(id)
				: entity == EntityType.FOX
				|| entity == EntityType.WANDERING_TRADER
				|| entity == EntityType.WITCH
				|| entity == EntityType.VILLAGER
				|| entity == EntityType.OCELOT
				|| entity == EntityType.MOOSHROOM
				|| entity == EntityType.CHICKEN
				|| entity == EntityType.BLAZE
				|| entity == EntityType.SPIDER
				|| entity == EntityType.PIG
				|| entity == EntityType.COW
				|| entity == EntityType.ZOMBIE // TODO: this will probably crash. I need a TaintedZombie model separate to appease TaintedZombieModel
				? EntityType.Builder.<Monster>of((type, world) -> new TaintedEntity(type, world, entity), MobCategory.MONSTER).sized(w, h).build(id)
				: EntityType.Builder.of(TaintedSlimeEntity::new, MobCategory.MONSTER).sized(w, h).build(id);

		/*
		* FOX
		* WANDERING_TRADER
		* WITCH
		* VILLAGER
		* OCELOT
		* MOOSHROOM
		* CHICKEN
		* BLAZE
		* SPIDER
		* PIG
		* COW
		* ZOMBIE
		* */

		entityTaintMap.put(entity, tainted);
		return tainted;
	}

	@SuppressWarnings({"rawtypes"})
	public static EntityType getTaintedOfEntity(EntityType entity) {
		return entityTaintMap.get(entity);
	}

	public static boolean isTainted(EntityType<?> entity) {
		return entityTaintMap.containsValue(entity);
	}

	public static boolean isTainted(Block block) {
		return TAINT_MAP.containsValue(block);
	}

	@SuppressWarnings("rawtypes")
	public static Collection<EntityType> getTaintedEntities() {
		return entityTaintMap.values();
	}

	public static AbstractTreeGrower taintedTreeOf(SaplingBlock block) {
		if (block == Blocks.OAK_SAPLING)
			return new TaintedOakTree();
		if (block == Blocks.BIRCH_SAPLING)
			return new TaintedBirchTree();
		if (block == Blocks.SPRUCE_SAPLING)
			return new TaintedSpruceTree();
		if (block == Blocks.JUNGLE_SAPLING)
			return new TaintedJungleTree();
		if (block == Blocks.ACACIA_SAPLING)
			return new TaintedAcaciaTree();
		if (block == Blocks.DARK_OAK_SAPLING)
			return new TaintedDarkOakTree();
		return new TaintedOakTree();
	}
}