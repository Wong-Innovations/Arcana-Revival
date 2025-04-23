package com.wonginnovations.arcana.network;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import com.wonginnovations.arcana.systems.research.ResearchEntry;
import com.wonginnovations.arcana.capabilities.Researcher;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class PkModifyResearch{
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	Diff diff;
	ResourceLocation key;
	
	public PkModifyResearch(Diff diff, ResourceLocation key) {
		this.diff = diff;
		this.key = key;
	}
	
	public static void encode(PkModifyResearch msg, FriendlyByteBuf buffer) {
		buffer.writeEnum(msg.diff);
		buffer.writeResourceLocation(msg.key);
	}
	
	public static PkModifyResearch decode(FriendlyByteBuf buffer) {
		return new PkModifyResearch(buffer.readEnum(Diff.class), buffer.readResourceLocation());
	}
	
	public static void handle(PkModifyResearch msg, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> {
			Player pe = Arcana.proxy.getPlayerOnClient();
			Researcher researcher = Researcher.getFrom(pe);
			ResearchEntry entry = ResearchBooks.streamEntries().filter(e -> e.key().equals(msg.key)).findFirst().orElseGet(() -> {
				LOGGER.error("An error occurred modifying player research progress on client: invalid research entry.");
				return null;
			});
			if (entry != null)
				if (msg.diff == Diff.complete)
					researcher.completeEntry(entry);
				else if (msg.diff == Diff.advance)
					researcher.advanceEntry(entry);
				else
					researcher.resetEntry(entry);
		});
		supplier.get().setPacketHandled(true);
	}
	
	public enum Diff{
		complete, advance, reset
	}
}