package com.wonginnovations.arcana.world;

import com.wonginnovations.arcana.capabilities.AuraChunk;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface AuraView {
	
	Function<Level, AuraView> SIDED_FACTORY = (level) -> level instanceof ClientLevel ? new ClientAuraView((ClientLevel) level) : level instanceof ServerLevel ? new ServerAuraView((ServerLevel) level) : null;
	double HALF_NODE = .7;
	
	Collection<Node> getAllNodes();
	
	Level getLevel();
	
	static AuraView getSided(Level level) {
		return SIDED_FACTORY.apply(level);
	}
	
	// no-op on client
	default void tickTaintLevel() {}
	
	default AuraChunk getAuraChunk(ChunkPos pos) {
		ChunkAccess chunk = getLevel().getChunk(pos.x, pos.z, ChunkStatus.FULL, false);
		if (chunk instanceof LevelChunk)
			return AuraChunk.getFrom((LevelChunk) chunk);
		return null;
	}
	
	default AuraChunk getAuraChunk(BlockPos pos) {
		return getAuraChunk(new ChunkPos(pos));
	}
	
	default Collection<Node> getNodesWithinChunk(ChunkPos pos) {
		AuraChunk nc = getAuraChunk(pos);
		return nc != null ? nc.getNodes() : Collections.emptyList();
	}
	
	default float getFluxWithinChunk(ChunkPos pos) {
		AuraChunk nc = getAuraChunk(pos);
		return nc != null ? nc.getFluxLevel() : -1;
	}
	
	default float getFluxAt(BlockPos pos) {
		return getFluxWithinChunk(new ChunkPos(pos));
	}
	
	/**
	 * Adds taint to a particular chunk. Returns the previous taint level, or -1 if the chunk isn't loaded.
	 *
	 * @param pos
	 * 		The chunk to add taint to.
	 * @return The previous taint level.
	 */
	default float addFluxToChunk(ChunkPos pos, float amount) {
		AuraChunk nc = getAuraChunk(pos);
		if (nc != null) {
			float level = nc.getFluxLevel();
			nc.addFlux(amount);
			return level;
		} else {
			return -1;
		}
	}
	
	default float addFluxAt(BlockPos pos, float amount) {
		return addFluxToChunk(new ChunkPos(pos), amount);
	}
	
	/**
	 * Sets the taint level of a particular chunk. Returns the previous taint level, or -1 if the chunk isn't loaded.
	 *
	 * @param pos
	 * 		The chunk to set the taint of.
	 * @return The previous taint level.
	 */
	default float setFluxOfChunk(ChunkPos pos, float amount) {
		AuraChunk nc = getAuraChunk(pos);
		if (nc != null) {
			float level = nc.getFluxLevel();
			nc.setFlux(amount);
			return level;
		} else {
			return -1;
		}
	}
	
	default float setFluxAt(BlockPos pos, float amount) {
		return setFluxOfChunk(new ChunkPos(pos), amount);
	}
	
	default Collection<Node> getNodesWithinAABB(AABB bounds) {
		// get all related chunks
		// that is, all chunks between minX and maxX, minZ and maxZ
		ChunkPos min = new ChunkPos(new BlockPos((int) bounds.minX, 0, (int) bounds.minZ));
		ChunkPos max = new ChunkPos(new BlockPos((int) bounds.maxX, 0, (int) bounds.maxZ));
		List<ChunkPos> relevant = new ArrayList<>();
		if (!min.equals(max))
			for (int xx = min.x; xx <= max.x; xx++)
				for (int zz = min.z; zz <= max.z; zz++)
					relevant.add(new ChunkPos(xx, zz));
		else
			relevant.add(min);
		//then getNodesWithinAABB for each
		List<Node> list = new ArrayList<>();
		for (ChunkPos pos : relevant)
			if (getAuraChunk(pos) != null)
				list.addAll(getAuraChunk(pos).getNodesWithinAABB(bounds));
		return list;
	}
	
	default Collection<Node> getNodesOfType(NodeType type) {
		return getAllNodes().stream()
				.filter(node -> node.type() == type)
				.collect(Collectors.toList());
	}
	
	default Optional<Node> getNodeByUuid(UUID id) {
		return getAllNodes().stream()
				.filter(node -> node.nodeUniqueId.equals(id))
				.findFirst();
	}
	
	default Collection<Node> getNodesOfTypeWithinAABB(AABB bounds, NodeType type) {
		// get all related chunks
		// that is, all chunks between minX and maxX, minZ and maxZ
		ChunkPos min = new ChunkPos(new BlockPos((int) bounds.minX, 0, (int) bounds.minZ));
		ChunkPos max = new ChunkPos(new BlockPos((int) bounds.maxX, 0, (int) bounds.maxZ));
		List<ChunkPos> relevant = new ArrayList<>();
		if (!min.equals(max))
			for (int xx = min.x; xx <= max.x; xx++)
				for (int zz = min.z; zz <= max.z; zz++)
					relevant.add(new ChunkPos(xx, zz));
		else
			relevant.add(min);
		//then getNodesWithinAABB foreach
		return relevant.stream()
				.map(this::getAuraChunk)
				.map(chunk -> chunk.getNodesWithinAABB(bounds))
				.flatMap(Collection::stream)
				.filter(node -> node.type() == type)
				.collect(Collectors.toList());
	}
	
	default Collection<Node> getNodesExcluding(Node excluded) {
		return getAllNodes().stream()
				.filter(node -> node != excluded)
				.collect(Collectors.toList());
	}
	
	default Collection<Node> getNodesOfTypeExcluding(NodeType type, Node excluded) {
		return getAllNodes().stream()
				.filter(node -> node.type() == type)
				.filter(node -> node != excluded)
				.collect(Collectors.toList());
	}
	
	default Collection<Node> getNodesWithinAABBExcluding(AABB bounds, Node excluded) {
		// get all related chunks
		// that is, all chunks between minX and maxX, minZ and maxZ
		ChunkPos min = new ChunkPos(new BlockPos((int) bounds.minX, 0, (int) bounds.minZ));
		ChunkPos max = new ChunkPos(new BlockPos((int) bounds.maxX, 0, (int) bounds.maxZ));
		List<ChunkPos> relevant = new ArrayList<>();
		if (!min.equals(max))
			for (int xx = min.x; xx <= max.x; xx++)
				for (int zz = min.z; zz <= max.z; zz++)
					relevant.add(new ChunkPos(xx, zz));
		else
			relevant.add(min);
		//then getNodesWithinAABB foreach
		return relevant.stream()
				.map(this::getAuraChunk)
				.map(chunk -> chunk.getNodesWithinAABB(bounds))
				.flatMap(Collection::stream)
				.filter(node -> node != excluded)
				.collect(Collectors.toList());
	}
	
	default Collection<Node> getNodesOfTypeWithinAABBExcluding(AABB bounds, NodeType type, Node excluded) {
		// get all related chunks
		// that is, all chunks between minX and maxX, minZ and maxZ
		ChunkPos min = new ChunkPos(new BlockPos((int) bounds.minX, 0, (int) bounds.minZ));
		ChunkPos max = new ChunkPos(new BlockPos((int) bounds.maxX, 0, (int) bounds.maxZ));
		List<ChunkPos> relevant = new ArrayList<>();
		if (!min.equals(max))
			for (int xx = min.x; xx <= max.x; xx++)
				for (int zz = min.z; zz <= max.z; zz++)
					relevant.add(new ChunkPos(xx, zz));
		else
			relevant.add(min);
		//then getNodesWithinAABB foreach
		return relevant.stream()
				.map(this::getAuraChunk)
				.map(chunk -> chunk.getNodesWithinAABB(bounds))
				.flatMap(Collection::stream)
				.filter(node -> node.type() == type)
				.filter(node -> node != excluded)
				.collect(Collectors.toList());
	}
	
	default boolean addNode(Node node) {
		// get the relevant chunk
		AuraChunk nc = getAuraChunk(new ChunkPos(new BlockPos(node)));
		if (nc != null) {
			nc.addNode(node);
			return true;
		}
		return false;
	}
	
	default boolean removeNode(Node node) {
		// get the relevant chunk
		AuraChunk nc = getAuraChunk(new ChunkPos(new BlockPos(node)));
		if (nc != null && nc.getNodes().contains(node)) {
			nc.removeNode(node);
			return true;
		}
		return false;
	}
	
	default Optional<Node> raycast(Vec3 from, double length, boolean ignoreBlocks, Entity entity) {
		Vec3 to = from.add(entity.getLookAngle().multiply(length, length, length));
		BlockHitResult result = null;
		if (!ignoreBlocks)
			result = getLevel().clip(new ClipContext(from, to, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity));
		AABB box = new AABB(from, to);
		Node ret = null;
		double curDist = length;
		for (Node node : getNodesWithinAABB(box)) {
			AABB nodeBox = new AABB(node.getX() - HALF_NODE, node.getY() - HALF_NODE, node.getZ() - HALF_NODE, node.getX() + HALF_NODE, node.getY() + HALF_NODE, node.getZ() + HALF_NODE);
			Optional<Vec3> optional = nodeBox.clip(from, to);
			if (optional.isPresent()) {
				double dist = from.distanceToSqr(optional.get());
				if (dist < curDist) {
					ret = node;
					curDist = dist;
				}
			}
		}
		if (!ignoreBlocks)
			// Blocked by a block
			if (result.getLocation().distanceToSqr(from) < curDist)
				return Optional.empty();
		return Optional.ofNullable(ret);
	}
	
	default Optional<Node> raycast(Vec3 from, double length, Entity entity) {
		return raycast(from, length, false, entity);
	}
}