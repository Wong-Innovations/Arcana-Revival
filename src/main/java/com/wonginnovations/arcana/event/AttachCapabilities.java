package com.wonginnovations.arcana.event;

import com.wonginnovations.arcana.capabilities.*;
import com.wonginnovations.arcana.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AttachCapabilities {
	
	@SubscribeEvent
	public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
		// Add TaintTrackable capability to all living entities.
		if (event.getObject() instanceof LivingEntity) {
			TaintTrackableCapability.Provider cap = new TaintTrackableCapability.Provider();
			event.addCapability(TaintTrackableCapability.KEY, cap);
		}

		// Add Researcher capability to players.
		if (event.getObject() instanceof Player player) {
			ResearcherCapability.Provider cap = new ResearcherCapability.Provider(player);
			event.addCapability(ResearcherCapability.KEY, cap);
		}
	}
	
	@SubscribeEvent
	public static void attachChunkCapabilities(AttachCapabilitiesEvent<LevelChunk> event) {
		AuraChunkCapability.Provider cap = new AuraChunkCapability.Provider();
		event.addCapability(AuraChunkCapability.KEY, cap);
	}
	
	@SuppressWarnings("ConstantConditions")
	@SubscribeEvent
	public static void playerClone(PlayerEvent.Clone event) {
		Researcher.getFrom(event.getEntity()).deserializeNBT(Researcher.getFrom(event.getOriginal()).serializeNBT());
		TaintTrackable.getFrom(event.getEntity()).deserializeNBT(TaintTrackable.getFrom(event.getOriginal()).serializeNBT());
		
		// research gets desynced here
		// so we send a sync packet
		// yes its always a ServerPlayer
		// we also need to delay by a tick so the new player can actually get the capability
		// TODO: maybe Researcher.getFrom(event.getOriginal())
		WorldTickHandler.onTick.add(world -> Connection.sendSyncPlayerResearch(Researcher.getFrom(event.getEntity()), (ServerPlayer) event.getEntity()));
	}

}