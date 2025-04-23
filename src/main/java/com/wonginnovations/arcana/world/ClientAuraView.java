package com.wonginnovations.arcana.world;

import com.wonginnovations.arcana.capabilities.AuraChunk;
import com.wonginnovations.arcana.client.ClientAuraHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A view of the nodes in the world for a particular tick.
 */
@OnlyIn(Dist.CLIENT)
public class ClientAuraView implements AuraView {
	
	ClientLevel level;
	
	public ClientAuraView(ClientLevel level) {
		this.level = level;
	}
	
	public Collection<Node> getAllNodes() {
		Collection<Node> allNodes = new ArrayList<>();
		for (ChunkPos chunkPos : ClientAuraHandler.CLIENT_LOADED_CHUNKS) {
			ChunkAccess chunk = level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, false);
			if (chunk instanceof LevelChunk) { // also a nonnull check
				AuraChunk nc = AuraChunk.getFrom((LevelChunk) chunk);
				if (nc != null)
					allNodes.addAll(nc.getNodes());
			}
		}
		return allNodes;
	}
	
	public Level getLevel() {
		return level;
	}
}