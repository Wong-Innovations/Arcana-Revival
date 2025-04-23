package com.wonginnovations.arcana.world;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.blocks.DelegatingBlock;
import com.wonginnovations.arcana.blocks.TaintedBlock;
import com.wonginnovations.arcana.capabilities.AuraChunk;
import com.wonginnovations.arcana.capabilities.Researcher;
import com.wonginnovations.arcana.mixin.ChunkMapAccessor;
import com.wonginnovations.arcana.network.Connection;
import com.wonginnovations.arcana.network.PkSyncChunkNodes;
import com.wonginnovations.arcana.network.PkSyncPlayerFlux;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import com.wonginnovations.arcana.systems.taint.Taint;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Streams.stream;

/**
 * A view of the nodes in the world for a particular tick.
 */
public class ServerAuraView implements AuraView {
	
	ServerLevel level;
	
	public ServerAuraView(ServerLevel level) {
		this.level = level;
	}
	
	public Collection<Node> getAllNodes() {
		Collection<Node> allNodes = new ArrayList<>();
		for (ChunkHolder holder : ((ChunkMapAccessor) level.getChunkSource().chunkMap).invokeGetChunks()) { // literally need a mixin wtf?
			LevelChunk chunk = holder.getTickingChunk();
			if (chunk != null) {
				AuraChunk nc = AuraChunk.getFrom(chunk);
				if (nc != null)
					allNodes.addAll(nc.getNodes());
			}
		}
		return allNodes;
	}
	
	// TODO: sendNodeToClients so we send less redundant data
	public void sendChunkToClients(Vec3i pos) {
		sendChunkToClients(new BlockPos(pos));
	}
	
	public void sendChunkToClients(BlockPos pos) {
		sendChunkToClients(new ChunkPos(pos));
	}
	
	public void sendChunkToClients(ChunkPos pos) {
		Connection.INSTANCE.send(PacketDistributor.ALL.noArg(), new PkSyncChunkNodes(pos, getNodesWithinChunk(pos)));
	}
	
	public void sendAllChunksToClients(Collection<? extends Vec3i> pos) {
		// don't send sync packets for the same chunk
		pos.stream()
				.map(BlockPos::new)
				.map(ChunkPos::new)
				.distinct()
				.forEach(this::sendChunkToClients);
	}
	
	@SuppressWarnings({"ConstantConditions", "UnstableApiUsage"})
	public void tickTaintLevel() {
		Map<ChunkPos, AuraChunk> loaded = stream(((ChunkMapAccessor) level.getChunkSource().chunkMap).invokeGetChunks())
				.map(ChunkHolder::getTickingChunk)
				.filter(Objects::nonNull)
				.collect(Collectors.toMap(
						ChunkAccess::getPos,
						AuraChunk::getFrom
				));
		// go through all loaded chunks
		loaded.forEach((pos, chunk) -> {
			// if they have more than 60 flux, place a tainted block and consume 40
			// don't do this *every* tick, to allow for existing tainted areas to spread first
			if (chunk.getFluxLevel() >= ArcanaConfig.TAINT_SPAWN_THRESHOLD.get() && level.getGameTime() % 30 == 0) {
				// pick a completely random block
				BlockPos blockPos = pos.getWorldPosition().above(level.random.nextInt(256)).north(level.random.nextInt(16)).east(level.random.nextInt(16));
				BlockState state = level.getBlockState(blockPos);
				Block block = Taint.getTaintedOfBlock(state.getBlock());
				if (block != null && !Taint.isBlockProtectedByPureNode(level, blockPos)) {
					level.setBlockAndUpdate(blockPos, DelegatingBlock.switchBlock(state, block).setValue(TaintedBlock.UNTAINTED, false));
					chunk.addFlux(-ArcanaConfig.TAINT_SPAWN_COST.get());
				}
			}
			// and for each of their loaded neighbors,
			for (ChunkPos neighbor : neighbors(pos))
				if (loaded.containsKey(neighbor)) {
					// if they have more than 30 more flux
					AuraChunk chunk1 = loaded.get(neighbor);
					if (chunk1.getFluxLevel() - chunk.getFluxLevel() > 60) {
						// move some of the difference
						float diff = (chunk1.getFluxLevel() - chunk.getFluxLevel()) / 20;
						chunk.addFlux(diff);
						chunk1.addFlux(-diff);
					}
				}
		});
		// send an update packet to every player to update their flux meters
		// check if they're eligible for "Taste flux firsthand"
		for (ServerPlayer player : level.getPlayers(player -> true)) {
			Connection.sendTo(new PkSyncPlayerFlux(getFluxAt(player.blockPosition())), player);
			if (getFluxAt(player.blockPosition()) > ArcanaConfig.FLUX_RESEARCH_REQUIREMENT.get())
				Researcher.getFrom(player).completePuzzle(ResearchBooks.puzzles.get(Arcana.arcLoc("flux_build_research")));
		}
	}
	
	private List<ChunkPos> neighbors(ChunkPos pos) {
		return Arrays.asList(
				new ChunkPos(pos.x - 1, pos.z),
				new ChunkPos(pos.x + 1, pos.z),
				new ChunkPos(pos.x, pos.z - 1),
				new ChunkPos(pos.x, pos.z + 1),
				new ChunkPos(pos.x - 1, pos.z - 1),
				new ChunkPos(pos.x + 1, pos.z - 1),
				new ChunkPos(pos.x - 1, pos.z + 1),
				new ChunkPos(pos.x + 1, pos.z + 1)
		);
	}
	
	public Level getLevel() {
		return level;
	}
}