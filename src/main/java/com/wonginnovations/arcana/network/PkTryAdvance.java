package com.wonginnovations.arcana.network;

import com.wonginnovations.arcana.systems.research.ResearchBooks;
import com.wonginnovations.arcana.systems.research.ResearchEntry;
import com.wonginnovations.arcana.capabilities.Researcher;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class PkTryAdvance{
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	ResourceLocation key;
	
	public PkTryAdvance(ResourceLocation key) {
		this.key = key;
	}
	
	public static void encode(PkTryAdvance msg, FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(msg.key);
	}
	
	public static PkTryAdvance decode(FriendlyByteBuf buffer) {
		return new PkTryAdvance(buffer.readResourceLocation());
	}
	
	public static void handle(PkTryAdvance msg, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> {
			ServerPlayer sender = supplier.get().getSender();
			Researcher researcher = Researcher.getFrom(sender);
			ResearchEntry entry = ResearchBooks.streamEntries().filter(e -> e.key().equals(msg.key)).findFirst().orElseGet(() -> {
				LOGGER.error("An error occurred trying to advance research progress on server: invalid research entry.");
				return null;
			});
			if (entry != null)
				if (Researcher.canAdvanceEntry(researcher, entry)) {
					Researcher.takeRequirementsAndAdvanceEntry(researcher, entry);
					Connection.sendModifyResearch(PkModifyResearch.Diff.advance, msg.key, sender);
				}
		});
		supplier.get().setPacketHandled(true);
	}
}