package com.wonginnovations.arcana.network;

import com.wonginnovations.arcana.capabilities.AuraChunk;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PkRequestNodeSync{
	
	ChunkPos chunk;
	
	public PkRequestNodeSync(net.minecraft.world.level.ChunkPos chunk) {
		this.chunk = chunk;
	}
	
	public static void encode(PkRequestNodeSync msg, FriendlyByteBuf buffer) {
		buffer.writeInt(msg.chunk.x);
		buffer.writeInt(msg.chunk.z);
	}
	
	public static PkRequestNodeSync decode(FriendlyByteBuf buffer) {
		int x = buffer.readInt();
		int z = buffer.readInt();
		return new PkRequestNodeSync(new ChunkPos(x, z));
	}
	
	public static void handle(PkRequestNodeSync msg, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> {
			// I'm on server
			// Get nodes at chunk
			LevelChunk chunk = (LevelChunk) supplier.get().getSender().level().getChunk(msg.chunk.x, msg.chunk.z, ChunkStatus.FULL, false);
			if (chunk != null) {
				AuraChunk nc = AuraChunk.getFrom(chunk);
				// Send a PkSyncChunkNodes
				if (nc != null)
					Connection.sendTo(new PkSyncChunkNodes(msg.chunk, nc.getNodes()), supplier.get().getSender());
			}
		});
		supplier.get().setPacketHandled(true);
	}
}