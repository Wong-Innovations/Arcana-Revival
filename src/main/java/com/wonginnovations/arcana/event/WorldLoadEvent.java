package com.wonginnovations.arcana.event;

import com.mojang.brigadier.CommandDispatcher;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.aspects.ItemAspectRegistry;
import com.wonginnovations.arcana.commands.FillAspectCommand;
import com.wonginnovations.arcana.commands.NodeCommand;
import com.wonginnovations.arcana.commands.ResearchCommand;
import com.wonginnovations.arcana.commands.TaintCommand;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.network.Connection;
import com.wonginnovations.arcana.network.PkSyncResearch;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import com.wonginnovations.arcana.systems.research.ResearchLoader;
import com.wonginnovations.arcana.capabilities.Researcher;
import com.wonginnovations.arcana.world.NodeType;
import com.wonginnovations.arcana.world.WorldInteractionsRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import net.minecraftforge.server.ServerLifecycleHooks;

import static com.wonginnovations.arcana.ArcanaVariables.arcLoc;

/**
 * Class for handling any events that occur upon world load
 *
 * @author Atlas
 */
@EventBusSubscriber
public class WorldLoadEvent {

	@SubscribeEvent
	public static void onClientLoad(ClientPlayerNetworkEvent.LoggingIn event) {
		for (NodeType nt : NodeType.TYPES.values()) {
			nt.spawned.clear();
		}
	}
	
	@SubscribeEvent
	public static void onWorldLoad(PlayerEvent.PlayerLoggedInEvent event) {
		// It's definitely an ServerPlayer.
		Connection.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()), new PkSyncResearch(ResearchBooks.books, ResearchBooks.puzzles));
		Researcher researcher = Researcher.getFrom(event.getEntity());
		Connection.sendSyncPlayerResearch(researcher, (ServerPlayer) event.getEntity());
		
		// If the player should get a one-time scribbled notes,
		if (ArcanaConfig.SPAWN_WITH_NOTES.get()) {
			ServerPlayer player = (ServerPlayer) event.getEntity();
			MinecraftServer server = player.level().getServer();
			if (server != null) {
				Advancement hasNote = server.getAdvancements().getAdvancement(arcLoc("obtained_note"));
				// and they haven't already got them this way,
				if (hasNote != null)
					if (!player.getAdvancements().getOrStartProgress(hasNote).isDone()) {
						// give them the notes,
						player.addItem(new ItemStack(ArcanaItems.SCRIBBLED_NOTES.get()));
						// and grant the advancement, so they never get it again.
						player.getAdvancements().getOrStartProgress(hasNote).grantProgress("impossible");
					}
			}
		}
	}
	
	@SubscribeEvent
	public static void serverAboutToStart(AddReloadListenerEvent event) {
		//IReloadableResourceManager manager = (IReloadableResourceManager)event.getServer().getDataPackRegistries().getResourceManager();
		event.addListener(Arcana.researchManager = new ResearchLoader());
		event.addListener(Arcana.itemAspectRegistry = new ItemAspectRegistry(event.getServerResources().getRecipeManager(), event.getRegistryAccess()));
		event.addListener(Arcana.levelInteractionsRegistry = new WorldInteractionsRegistry());
	}

	@OnlyIn(Dist.DEDICATED_SERVER)
	@SubscribeEvent
	public static void onTagsUpdatedServer(TagsUpdatedEvent event) {
		Arcana.LOGGER.info("TagsUpdatedEvent received!");
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		if (server != null) {
			ItemAspectRegistry.generateUndefinedAspects(server.getRecipeManager(), event.getRegistryAccess());
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void onTagsUpdatedClient(TagsUpdatedEvent event) {
		Arcana.LOGGER.info("TagsUpdatedEvent received!");
		if (Minecraft.getInstance().getConnection() != null) {
			ItemAspectRegistry.generateUndefinedAspects(Minecraft.getInstance().getConnection().getRecipeManager(), event.getRegistryAccess());
		} else if (Minecraft.getInstance().level != null) {
			ItemAspectRegistry.generateUndefinedAspects(Minecraft.getInstance().level.getRecipeManager(), event.getRegistryAccess());
		}
	}

//	@SubscribeEvent
//	public static void onRecipesUpdated(RecipesUpdatedEvent event) {
//	}
	
	@SubscribeEvent
	public static void serverStarting(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		ResearchCommand.register(dispatcher);
		FillAspectCommand.register(dispatcher);
		NodeCommand.register(dispatcher);
		TaintCommand.register(dispatcher);
	}
}