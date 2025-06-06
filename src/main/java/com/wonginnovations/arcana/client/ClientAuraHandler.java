package com.wonginnovations.arcana.client;

import com.wonginnovations.arcana.network.Connection;
import com.wonginnovations.arcana.network.PkRequestNodeSync;
import com.wonginnovations.arcana.world.ClientAuraView;
import com.wonginnovations.arcana.world.AuraView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientAuraHandler {
	
	public static final Collection<ChunkPos> CLIENT_LOADED_CHUNKS = new ConcurrentSkipListSet<>(Comparator.<ChunkPos>comparingInt(value -> value.x).thenComparingInt(value -> value.z));
	public static final Collection<Runnable> UNTIL_PLAYER_JOIN = new CopyOnWriteArrayList<>();
	
	// Keep track of loaded chunks on client using events.
	// Don't keep track of aura at arbitrary positions - the server will update us on the aura at our position.
	
	public static float currentFlux = 0;
	
	@SubscribeEvent
	public static void chunkLoadOnClient(ChunkEvent.Load event) {
		// on client
		// send PkRequestClientSync
		UNTIL_PLAYER_JOIN.add(() -> Connection.sendToServer(new PkRequestNodeSync(event.getChunk().getPos())));
		CLIENT_LOADED_CHUNKS.add(event.getChunk().getPos());
	}
	
	@SubscribeEvent
	public static void chunkUnloadOnClient(ChunkEvent.Unload event) {
		CLIENT_LOADED_CHUNKS.remove(event.getChunk().getPos());
	}
	
	// And tick nodes.
	
	@SubscribeEvent
	public static void tickEndClient(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			ClientLevel level = Minecraft.getInstance().level;
			
			if (level != null) {
				AuraView view = new ClientAuraView(level);
				view.getAllNodes().forEach(node -> node.type().tick(level, view, node));
			}
			
			if (!UNTIL_PLAYER_JOIN.isEmpty() && Minecraft.getInstance().player != null) {
				List<Runnable> temp = new ArrayList<>(ClientAuraHandler.UNTIL_PLAYER_JOIN);
				temp.forEach(Runnable::run);
				ClientAuraHandler.UNTIL_PLAYER_JOIN.removeAll(temp);
			}
		}
	}
}
