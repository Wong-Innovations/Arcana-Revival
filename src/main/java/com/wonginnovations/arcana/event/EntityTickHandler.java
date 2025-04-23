package com.wonginnovations.arcana.event;

import com.google.common.collect.Sets;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.capabilities.TaintTrackable;
import com.wonginnovations.arcana.effects.ArcanaEffects;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.systems.spell.casts.DelayedCast;
import com.wonginnovations.arcana.systems.spell.casts.ToggleableCast;
import com.wonginnovations.arcana.systems.taint.Taint;
import com.wonginnovations.arcana.world.AuraView;
import com.wonginnovations.arcana.world.Node;
import com.wonginnovations.arcana.world.ServerAuraView;
import net.minecraft.advancements.Advancement;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.wonginnovations.arcana.ArcanaVariables.arcLoc;

@Mod.EventBusSubscriber
public class EntityTickHandler {

	@SubscribeEvent
	public static void playerSleep(PlayerSleepInBedEvent event) {
		if (!event.getEntity().level().isClientSide) {
			ServerPlayer playerEntity = (ServerPlayer) event.getEntity();
			Advancement interacted = playerEntity.level().getServer().getAdvancements().getAdvancement(arcLoc("interacted_with_magic"));
			Advancement gotArcanum = playerEntity.level().getServer().getAdvancements().getAdvancement(arcLoc("arcanum_accepted"));
			if (playerEntity.getInventory().hasAnyOf(Sets.newHashSet(Items.PAPER,ArcanaItems.SCRIBBLED_NOTES.get())) && playerEntity.getAdvancements().getOrStartProgress(interacted).isDone() && !playerEntity.getAdvancements().getOrStartProgress(gotArcanum).isDone()) {
				playerEntity.getAdvancements().getOrStartProgress(gotArcanum).grantProgress("impossible");
				playerEntity.getInventory().add(new ItemStack(ArcanaItems.SCRIBBLED_NOTES_COMPLETE.get()));
			}
		}
	}

	@SubscribeEvent
	public static void tickPlayer(TickEvent.PlayerTickEvent event) {
		Player player = event.player;
		
		// Give completed scribbled note when player is near node
		if (player instanceof ServerPlayer serverPlayer && event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
            // If the player is near a node,
			AuraView view = new ServerAuraView(serverPlayer.serverLevel());
			Collection<Node> ranged = new ArrayList<>(view.getNodesWithinAABB(player.getBoundingBox().inflate(2)));

			if (!ranged.isEmpty()) {
				// and is holding the scribbled notes item,
				if (player.getInventory().hasAnyMatching(stack -> stack.is(ArcanaItems.SCRIBBLED_NOTES.get()))) {
					// it switches it for a complete version,
					player.getInventory().setItem(getSlotFor(new ItemStack(ArcanaItems.SCRIBBLED_NOTES.get()), player.getInventory()), new ItemStack(ArcanaItems.SCRIBBLED_NOTES_COMPLETE.get()));
					// and gives them a status message.
					Component status = Component.translatable("status.get_complete_note").withStyle(ChatFormatting.ITALIC, ChatFormatting.LIGHT_PURPLE);
					serverPlayer.displayClientMessage(status, false);
					// and grant the advancement.
					MinecraftServer server = player.level().getServer();
					Advancement interacted = server.getAdvancements().getAdvancement(arcLoc("interacted_with_magic"));
					((ServerPlayer) player).getAdvancements().getOrStartProgress(interacted).grantProgress("impossible");
					Advancement gotArcanum = server.getAdvancements().getAdvancement(arcLoc("arcanum_accepted"));
					((ServerPlayer) player).getAdvancements().getOrStartProgress(gotArcanum).grantProgress("impossible");
				} else {
					MinecraftServer server = player.level().getServer();
					Advancement interacted = server.getAdvancements().getAdvancement(arcLoc("interacted_with_magic"));
					if (interacted != null)
						if (!serverPlayer.getAdvancements().getOrStartProgress(interacted).isDone()) {
							// grant the advancement.
							serverPlayer.getAdvancements().getOrStartProgress(interacted).grantProgress("impossible");

							Component status = Component.translatable("status.sleep_paper_get_arcanum").withStyle(ChatFormatting.ITALIC, ChatFormatting.LIGHT_PURPLE);
							serverPlayer.displayClientMessage(status, false);
						}
				}
			}
			
			List<DelayedCast.Impl> spellsScheduledToDeletion = new ArrayList<>();
			DelayedCast.delayedCasts.forEach(delayedCast -> {
				if (delayedCast.ticks >= delayedCast.ticksPassed) {
					delayedCast.spellEvent.accept(0);
					spellsScheduledToDeletion.add(delayedCast);
				} else
					delayedCast.ticksPassed++;
			});
			DelayedCast.delayedCasts.removeAll(spellsScheduledToDeletion);

			ToggleableCast.toggleableCasts.forEach(toggleableCast -> {
				if (toggleableCast.getSecond().ticks >= toggleableCast.getSecond().ticksPassed) {
					toggleableCast.getSecond().spellEvent.accept(0);
					toggleableCast.getSecond().ticksPassed = 0;
				} else
					toggleableCast.getSecond().ticksPassed++;
			});
		}

	}
	
	@SubscribeEvent
	public static void tickEntities(LivingEvent.LivingTickEvent event) {
		LivingEntity living = event.getEntity();
		TaintTrackable trackable = TaintTrackable.getFrom(living);
		if (trackable != null && trackable.isTracking()) {
			if (Taint.isAreaInTaintBiome(living.getOnPos(), living.level())) {
				trackable.setInTaintBiome(true);
				trackable.addTimeInTaintBiome(1);
				if (!Taint.isTainted(living.getType()) && trackable.getTimeInTaintBiome() > ArcanaConfig.TAINT_EFFECT_TIME.get())
					living.addEffect(new MobEffectInstance(ArcanaEffects.TAINTED.get(), 5 * 20, 0, true, true));
			} else {
				trackable.setInTaintBiome(false);
				trackable.setTimeInTaintBiome(0);
				trackable.setTracking(false);
			}
		}
	}
	
	private static int getSlotFor(ItemStack stack, Inventory self) {
		for (int i = 0; i < self.items.size(); ++i)
			if (!self.items.get(i).isEmpty() && stackEqualExact(stack, self.items.get(i)))
				return i;
		
		return -1;
	}
	
	private static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
		return stack1.getItem() == stack2.getItem() && ItemStack.isSameItemSameTags(stack1, stack2);
	}
}