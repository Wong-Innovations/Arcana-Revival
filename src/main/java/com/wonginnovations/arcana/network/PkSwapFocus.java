package com.wonginnovations.arcana.network;

import com.wonginnovations.arcana.items.MagicDeviceItem;
import com.wonginnovations.arcana.items.attachment.FocusItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PkSwapFocus {
	
	InteractionHand wandHand;
	int newFocusIndex;
	
	public PkSwapFocus(InteractionHand wandHand, int newFocusIndex) {
		this.wandHand = wandHand;
		this.newFocusIndex = newFocusIndex;
	}
	
	public static void encode(PkSwapFocus msg, FriendlyByteBuf buffer) {
		buffer.writeEnum(msg.wandHand);
		buffer.writeVarInt(msg.newFocusIndex);
	}
	
	public static PkSwapFocus decode(FriendlyByteBuf buffer) {
		return new PkSwapFocus(buffer.readEnum(InteractionHand.class), buffer.readVarInt());
	}
	
	public static void handle(PkSwapFocus msg, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> {
			ServerPlayer spe = supplier.get().getSender();
			ItemStack wandStack = spe.getItemInHand(msg.wandHand);
			// Give player the old focus
			if (msg.newFocusIndex >= 0) {
				// Set the wand focus of the wand
				List<ItemStack> foci = getAllFociStacks(spe);
				MagicDeviceItem.getFocusStack(wandStack).ifPresent(spe.getInventory()::add);
				ItemStack focus = foci.get(msg.newFocusIndex);
				MagicDeviceItem.setFocusFromStack(wandStack, focus);
				// Remove the stack from the inventory
				spe.getInventory().clearOrCountMatchingItems(stack -> stack == focus, 1, spe.inventoryMenu.getCraftSlots());
			} else {
				MagicDeviceItem.getFocusStack(wandStack).ifPresent(spe.getInventory()::add);
				MagicDeviceItem.setFocusFromStack(wandStack, ItemStack.EMPTY);
			}
		});
		supplier.get().setPacketHandled(true);
	}
	
	private static List<ItemStack> getAllFociStacks(ServerPlayer spe) {
		//TODO: focus pouch?
		List<ItemStack> foci = new ArrayList<>();
		for (int i = 0; i < spe.getInventory().getContainerSize(); i++) {
			ItemStack stack = spe.getInventory().getItem(i);
			if (stack.getItem() instanceof FocusItem)
				foci.add(stack);
		}
		return foci;
	}
}