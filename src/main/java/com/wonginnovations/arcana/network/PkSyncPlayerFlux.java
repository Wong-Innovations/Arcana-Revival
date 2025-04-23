package com.wonginnovations.arcana.network;


import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.capabilities.Researcher;
import com.wonginnovations.arcana.client.ClientAuraHandler;
import com.wonginnovations.arcana.client.ClientUtils;
import com.wonginnovations.arcana.systems.research.Puzzle;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PkSyncPlayerFlux{
	
	float flux;
	
	public PkSyncPlayerFlux(float flux) {
		this.flux = flux;
	}
	
	public static void encode(PkSyncPlayerFlux msg, FriendlyByteBuf buffer) {
		buffer.writeFloat(msg.flux);
	}
	
	public static PkSyncPlayerFlux decode(FriendlyByteBuf buffer) {
		return new PkSyncPlayerFlux(buffer.readFloat());
	}
	
	public static void handle(PkSyncPlayerFlux msg, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> {
			// I'm on client.
			// There's a separate ClientAuraHandler per player.
			ClientAuraHandler.currentFlux = msg.flux;
			// And check if there's enough flux around for research
			Puzzle puzzle = ResearchBooks.puzzles.get(Arcana.arcLoc("flux_build_research"));
			Researcher researcher = Researcher.getFrom(Arcana.proxy.getPlayerOnClient());
			if (msg.flux > ArcanaConfig.FLUX_RESEARCH_REQUIREMENT.get() && !researcher.isPuzzleCompleted(puzzle)) {
				researcher.completePuzzle(puzzle);
				ClientUtils.displayPuzzleToast(ResearchBooks.getEntry(Arcana.arcLoc("flux")));
			}
		});
		supplier.get().setPacketHandled(true);
	}
}