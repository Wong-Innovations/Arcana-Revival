package com.wonginnovations.arcana.network;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PkWriteSpellToFoci {

	ItemStack focus;
	CompoundTag focusNBT;

	public PkWriteSpellToFoci(ItemStack focus, CompoundTag focusNBT) {
		this.focus = focus;
		this.focusNBT = focusNBT;
	}

	public static void encode(PkWriteSpellToFoci msg, FriendlyByteBuf buffer) {
		buffer.writeNbt(msg.focusNBT);
		buffer.writeItemStack(msg.focus, true);
	}

	public static PkWriteSpellToFoci decode(FriendlyByteBuf buffer) {
		return new PkWriteSpellToFoci(buffer.readItem(), buffer.readNbt());
	}

	public static void handle(PkWriteSpellToFoci msg, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> {
			msg.focus.getOrCreateTag().put("spell", msg.focusNBT);
		});
		supplier.get().setPacketHandled(true);
	}
}
