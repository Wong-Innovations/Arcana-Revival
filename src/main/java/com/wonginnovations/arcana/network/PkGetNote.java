package com.wonginnovations.arcana.network;

import com.wonginnovations.arcana.items.ArcanaItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PkGetNote{
	
	String entryName;
	ResourceLocation researchLocation;
	
	public PkGetNote(ResourceLocation id, String entryName) {
		this.researchLocation = id;
		this.entryName = entryName;
	}
	
	public static void encode(PkGetNote msg, FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(msg.researchLocation);
		buffer.writeUtf(msg.entryName);
	}
	
	public static PkGetNote decode(FriendlyByteBuf buffer) {
		return new PkGetNote(buffer.readResourceLocation(), buffer.readUtf());
	}
	
	public static void handle(PkGetNote msg, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> {
			ServerPlayer epm = supplier.get().getSender();
			if (epm.getInventory().countItem(ArcanaItems.SCRIBING_TOOLS.get()) > 0 && epm.getInventory().countItem(Items.PAPER) > 0) {
				// if I can give research note
				ItemStack in = new ItemStack(ArcanaItems.RESEARCH_NOTE.get(), 1);
				CompoundTag nbt = new CompoundTag();
				nbt.putString("puzzle", msg.researchLocation.toString());
				nbt.putString("research", msg.entryName);
				in.setTag(nbt);
				if (epm.getInventory().add(in)) {
					// give it -- done
					// damage ink
					for (int i = 0; i < epm.getInventory().getContainerSize(); i++) {
						ItemStack stack = epm.getInventory().getItem(i);
						if (stack.getItem() == ArcanaItems.SCRIBING_TOOLS.get()) {
							stack.hurtAndBreak(1, epm, ignored -> {});
							break;
						}
					}
					// remove paper -- done
					
					epm.getInventory().clearOrCountMatchingItems(e -> e.getItem() == Items.PAPER, 1, epm.inventoryMenu.getCraftSlots());
				}
				// tell client?
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
