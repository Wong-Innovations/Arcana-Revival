package com.wonginnovations.arcana.network;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.capabilities.Researcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

/**
 * Syncs the player's progress through research. Not to be confused with {@link PkSyncResearch}, which syncs all existing research.
 */
public class PkSyncPlayerResearch{
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	// how about I just change this to use regular serialization methods...
	
	CompoundTag data;
	
	public PkSyncPlayerResearch(CompoundTag nbt) {
		data = nbt;
	}
	
	public static void encode(PkSyncPlayerResearch msg, FriendlyByteBuf buffer) {
		buffer.writeNbt(msg.data);
	}
	
	public static PkSyncPlayerResearch decode(FriendlyByteBuf buffer) {
		return new PkSyncPlayerResearch(buffer.readNbt());
	}
	
	public static void handle(PkSyncPlayerResearch msg, Supplier<NetworkEvent.Context> supplier) {
		// from server to client
		supplier.get().enqueueWork(() -> {
			Researcher researcher = Researcher.getFrom(Arcana.proxy.getPlayerOnClient());
			researcher.deserializeNBT(msg.data);
		});
		supplier.get().setPacketHandled(true);
	}
}