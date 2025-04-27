package com.wonginnovations.arcana.world;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.aspects.ItemAspectRegistry;
import com.wonginnovations.arcana.aspects.handlers.*;
import com.wonginnovations.arcana.client.render.particles.ArcanaParticles;
import com.wonginnovations.arcana.client.render.particles.NodeParticleData;
import com.wonginnovations.arcana.items.settings.GogglePriority;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.wonginnovations.arcana.Arcana.arcLoc;
import static com.wonginnovations.arcana.aspects.AspectUtils.primalAspects;

// Although IDEA complains about class loading deadlock, this only occurs under specific conditions.
// Handles type-specific things, such as behaviour and vis generation rates.
@SuppressWarnings("StaticInitializerReferencesSubClass")
public abstract class NodeType {
	
	// A diet registry, used for serialization and deserialization.
	public static final BiMap<ResourceLocation, NodeType> TYPES = HashBiMap.create(7);
	public static final Set<NodeType> SPECIAL_TYPES = new HashSet<>(5);
	
	public static final NodeType
			NORMAL = new NormalNodeType(),
			BRIGHT = new BrightNodeType(),
			PALE = new PaleNodeType(),
			ELDRITCH = new EldritchNodeType(),
			HUNGRY = new HungryNodeType(),
			PURE = new PureNodeType(),
			TAINTED = new TaintedNodeType();
	
	public static final NodeType DEFAULT = NORMAL;
	
	public static void init() {
		TYPES.put(arcLoc("normal"), NORMAL);
		TYPES.put(arcLoc("bright"), BRIGHT);
		TYPES.put(arcLoc("pale"), PALE);
		TYPES.put(arcLoc("eldritch"), ELDRITCH);
		TYPES.put(arcLoc("hungry"), HUNGRY);
		TYPES.put(arcLoc("pure"), PURE);
		TYPES.put(arcLoc("tainted"), TAINTED);
		
		SPECIAL_TYPES.add(BRIGHT);
		SPECIAL_TYPES.add(PALE);
		SPECIAL_TYPES.add(ELDRITCH);
		SPECIAL_TYPES.add(HUNGRY);
		SPECIAL_TYPES.add(PURE);
	}
	
	public void tick(Level level, AuraView nodes, Node node) {
		// Display the node
		if (level.isClientSide()) {
			GogglePriority priority = GogglePriority.getClientGogglePriority();
			if (priority == GogglePriority.SHOW_NODE || priority == GogglePriority.SHOW_ASPECTS)
				level.addParticle(new NodeParticleData(node.nodeUniqueId(), node.type().texture(level, nodes, node)), node.getX(), node.getY(), node.getZ(), 0, 0, 0);
		}
		
		// Regenerate aspects over time
		// Time: default is 20 * 60, bright has 20 * 45, pale has 20 * 75
		// Cap: default is 23, bright has 47, pale has 11
		// Power: default is 5, bright has 7, pale has 4 (all +/- 1)
		// These are random numbers I pulled out of the void, please adjust as is reasonable
		
		if (node.timeUntilRecharge <= 0) {
			node.timeUntilRecharge = rechargeTime(level, nodes, node);
			// are we below our cap? if so, charge
			if (node.getAspects().getHolders().stream().mapToDouble(h -> h.getStack().getAmount()).sum() < rechargeCap(level, nodes, node)) {
				int toCharge = rechargePower(level, nodes, node) + level.getRandom().nextInt(3) - 1;
				// pick a random aspect and give 1-3 aspect to it until we're done
				while(toCharge > 0) {
					AspectHolder holder = node.getAspects().getHolders().get(level.getRandom().nextInt(node.getAspects().getHolders().size()));
					int giving = Math.min(level.getRandom().nextInt(3) + 1, toCharge);
					holder.insert(new AspectStack(holder.getStack().getAspect(), giving), false);
					toCharge -= giving;
				}
			}
		} else
			node.timeUntilRecharge--;
	}
	
	public abstract ResourceLocation texture(Level level, AuraView nodes, Node node);
	public abstract Collection<ResourceLocation> textures();
	
	public int rechargeTime(Level level, AuraView nodes, Node node) {
		return 20 * 60;
	}
	
	public int rechargeCap(Level level, AuraView nodes, Node node) {
		return 23;
	}
	
	public int rechargePower(Level level, AuraView nodes, Node node) {
		return 5;
	}
	
	public boolean blocksTaint(Level level, AuraView nodes, Node node, BlockPos pos) {
		return false;
	}
	
	public String toString() {
		return TYPES.containsValue(this) ? TYPES.inverse().get(this).toString() : super.toString();
	}
	
	public AspectHandler genBattery(BlockPos location, Level level, RandomSource random) {
		AspectBattery battery = new AspectBattery(/*6, -1*/);
		// 2-4 random primal aspects
		int primalCount = 2 + random.nextInt(3);
		for (int i = 0; i < primalCount; i++) {
			// the same primal can be added multiple times
			Aspect aspect = primalAspects[random.nextInt(primalAspects.length)];
			// 10-27
			int amount = 10 + random.nextInt(18);
			AspectHolder holder = battery.findFirstHolderContaining(aspect);
			if (holder != null)
				holder.insert(new AspectStack(aspect, amount), false);
			else {
				AspectCell cell = new AspectCell();
				cell.setCapacity(-1);
				cell.insert(new AspectStack(aspect, amount), false);
				cell.setWhitelist(Collections.singletonList(aspect));
				battery.getHolders().add(cell);
			}
		}
		// if the node is underwater, add 5-10 aqua
		if (level.getFluidState(location).is(FluidTags.WATER)) {
			Aspect aspect = Aspects.WATER;
			int amount = 5 + random.nextInt(6);
			AspectHolder holder = battery.findFirstHolderContaining(aspect);
			if (holder != null)
				holder.insert(new AspectStack(aspect, amount), false);
			else {
				AspectCell cell = new AspectCell();
				cell.insert(new AspectStack(aspect, amount), false);
				cell.setCapacity(-1);
				cell.setWhitelist(Collections.singletonList(aspect));
				battery.getHolders().add(cell);
			}
		}
		return battery;
	}
	
	public static class NormalNodeType extends NodeType {
		
		public ResourceLocation texture(Level level, AuraView nodes, Node node) {
			return arcLoc("nodes/normal_node");
//			return arcLoc("nodes/normal_node_" + (level.getGameTime() / 2) % textures().size());
		}
		
		public Collection<ResourceLocation> textures() {
			return List.of(
					arcLoc("particle/nodes/normal_node")
			);
		}
	}
	
	public static class BrightNodeType extends NodeType {
		
		public ResourceLocation texture(Level level, AuraView nodes, Node node) {
			return arcLoc("particle/nodes/bright_node");
		}
		
		public Collection<ResourceLocation> textures() {
			return Collections.singleton(arcLoc("particle/nodes/brightest_node"));
		}
		
		// Add 50% to all aspects
		public AspectHandler genBattery(BlockPos location, Level level, RandomSource random) {
			AspectHandler handler = super.genBattery(location, level, random);
			for (AspectHolder holder : handler.getHolders())
				holder.insert(holder.getStack().getAmount() / 2, false);
			return handler;
		}
		
		public int rechargeTime(Level level, AuraView nodes, Node node) {
			return 20 * 45;
		}
		
		public int rechargeCap(Level level, AuraView nodes, Node node) {
			return 47;
		}
		
		public int rechargePower(Level level, AuraView nodes, Node node) {
			return 7;
		}
	}
	
	public static class PaleNodeType extends NodeType{
		
		public ResourceLocation texture(Level level, AuraView nodes, Node node) {
			return arcLoc("particle/nodes/fading_node");
		}
		
		public Collection<ResourceLocation> textures() {
			return Collections.singleton(arcLoc("particle/nodes/fading_node"));
		}
		
		// Remove 30% from all aspects
		public AspectHandler genBattery(BlockPos location, Level level, RandomSource random) {
			AspectHandler handler = super.genBattery(location, level, random);
			for (AspectHolder holder : handler.getHolders())
				holder.drain(holder.getStack().getAmount() * .3f, false);
			return handler;
		}
		
		public int rechargeTime(Level level, AuraView nodes, Node node) {
			return 20 * 75;
		}
		
		public int rechargeCap(Level level, AuraView nodes, Node node) {
			return 11;
		}
		
		public int rechargePower(Level level, AuraView nodes, Node node) {
			return 4;
		}
	}
	
	public static class EldritchNodeType extends NodeType{
		
		public ResourceLocation texture(Level level, AuraView nodes, Node node) {
			return arcLoc("particle/nodes/eldritch_node");
		}
		
		public Collection<ResourceLocation> textures() {
			return Collections.singleton(arcLoc("particle/nodes/eldritch_node"));
		}
	}
	
	@SuppressWarnings("deprecation")
	public static class HungryNodeType extends NodeType {
		
		public ResourceLocation texture(Level level, AuraView nodes, Node node) {
			return arcLoc("particle/nodes/hungry_node");
		}
		
		public Collection<ResourceLocation> textures() {
			return Collections.singleton(arcLoc("particle/nodes/hungry_node"));
		}
		
		public void tick(Level level, AuraView nodes, Node node) {
			super.tick(level, nodes, node);
			// check all blocks in range
			int range = (int)(0.7f * Mth.sqrt((float) node.aspects.getHolders().stream().mapToDouble(h -> h.getStack().getAmount()).sum())) + 1;
			BlockPos nodePos = new BlockPos(node);
			BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
			for (int x = -range; x < range; x++)
				for (int y = -range; y < range; y++)
					for (int z = -range; z < range; z++) {
						cursor.set(nodePos).move(x, y, z);
						// if they have air on at least one side
						BlockState state = level.getBlockState(cursor);
						if (!state.isAir() && !(state.getBlock() instanceof IFluidBlock || state.getFluidState().getType() instanceof FlowingFluid) && (cursor.distToCenterSqr(node.getX(), node.getY(), node.getZ()) < range * range) && (cursor.distToCenterSqr(node.getX(), node.getY(), node.getZ()) < 2 || (emptySpace(level.getBlockState(cursor.above())) || emptySpace(level.getBlockState(cursor.below())) || emptySpace(level.getBlockState(cursor.north())) || emptySpace(level.getBlockState(cursor.south())) || emptySpace(level.getBlockState(cursor.east())) || emptySpace(level.getBlockState(cursor.west()))))) {
							// have particles moving from the block to the node
							float xR = level.getRandom().nextFloat(), yR = level.getRandom().nextFloat(), zR = level.getRandom().nextFloat();
							if (level.getRandom().nextBoolean())
								level.addParticle(new BlockParticleOption(ArcanaParticles.HUNGRY_NODE_BLOCK_PARTICLE.get(), state).setPos(cursor), cursor.getX() + xR, cursor.getY() + yR, cursor.getZ() + zR, -(x /*- node.getX() % 1*/ + xR - 1) / 20f, -(y /*- node.getY() % 1*/ + yR) / 20f, -(z /*- node.getZ() % 1*/ + zR) / 20f);
							// TODO: minimum time to break (instead of pure random)
							if (!level.isClientSide()) {
								float hardness = state.getDestroySpeed(level, cursor);
								if (hardness != -1 && level.getRandom().nextInt((int)(hardness * 300) + 1) == 0) {
									// note down that the block has been broken
									CompoundTag blocks = node.getData().getCompound("blocks");
									node.getData().put("blocks", blocks);
									String key = Objects.requireNonNull(state.getBlock().builtInRegistryHolder().key().location()).toString();
									blocks.putInt(key, blocks.getInt(key) + 1);
									// gain a fraction of that block's aspects
									if (ArcanaConfig.HUNGRY_NODE_ASPECT_CARRY_FRACTION.get() > 0) {
										if (level instanceof ServerLevel)
											for (ItemStack drop : Block.getDrops(state, (ServerLevel) level, cursor, level.getBlockEntity(cursor)))
												for (AspectStack stack : ItemAspectRegistry.get(drop)) {
													int toInsert = (int)Math.max(1, stack.getAmount() * ArcanaConfig.HUNGRY_NODE_ASPECT_CARRY_FRACTION.get());
													VisUtils.moveAspects(stack.getAspect(), toInsert, node.aspects, -1);
												}
									}
									// send changes to client
									if (nodes instanceof ServerAuraView)
										((ServerAuraView)nodes).sendChunkToClients(node);
									// destroy the block
									level.destroyBlock(cursor, false);
								}
							}
						}
					}
			// make disc particles
			// disc radius = 1/3 * pull radius
			CompoundTag blocks = node.getData().getCompound("blocks");
			if (!blocks.getAllKeys().isEmpty()) {
				float discRad = (float)(range * (1 / 3f) + level.getRandom().nextGaussian() / 5f);
				float xPos = (float)(node.getX());
				float zPos = node.getZ() - discRad;
				// TODO: weighted selection
				BlockState state = Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blocks.getAllKeys().toArray(new String[0])[level.getRandom().nextInt(blocks.getAllKeys().size())]))).defaultBlockState();
				level.addParticle(new BlockParticleOption(ArcanaParticles.HUNGRY_NODE_DISC_PARTICLE.get(), state).setPos(nodePos), xPos, node.getY(), zPos, discRad / 6f, 0, discRad / 6f);
			}
		}
		
		private static boolean emptySpace(BlockState state) {
			return state.isAir() || state.getBlock() instanceof IFluidBlock || state.getFluidState().getType() instanceof FlowingFluid;
		}
	}

	public static class PureNodeType extends NodeType {

		public ResourceLocation texture(Level level, AuraView nodes, Node node) {
			return arcLoc("particle/nodes/pure_node");
		}

		public Collection<ResourceLocation> textures() {
			return Collections.singleton(arcLoc("particle/nodes/pure_node"));
		}
		
		public boolean blocksTaint(Level level, AuraView nodes, Node node, BlockPos pos) {
			return true;
		}
	}
	
	public static class TaintedNodeType extends NodeType {
		
		public ResourceLocation texture(Level level, AuraView nodes, Node node) {
			return arcLoc("particle/nodes/tainted_node");
		}
		
		public Collection<ResourceLocation> textures() {
			return Collections.singleton(arcLoc("particle/nodes/tainted_node"));
		}
		
		public AspectHandler genBattery(BlockPos location, Level level, RandomSource random) {
			AspectBattery handler = (AspectBattery)super.genBattery(location, level, random);
			// Add 5-15 taint
			// This is only accessible using /arcana-nodes
			AspectCell cell = new AspectCell();
			Aspect aspect = Aspects.TAINT;
			cell.insert(new AspectStack(aspect, 5 + random.nextInt(11)), false);
			cell.setWhitelist(Collections.singletonList(aspect));
			handler.getHolders().add(cell);
			return handler;
		}
	}
}