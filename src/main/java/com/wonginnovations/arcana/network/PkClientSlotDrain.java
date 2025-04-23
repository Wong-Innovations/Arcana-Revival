package com.wonginnovations.arcana.network;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.containers.AspectMenu;
import com.wonginnovations.arcana.containers.slots.AspectSlot;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class PkClientSlotDrain {

	public static final Logger LOGGER = LogManager.getLogger();

	int windowId;
	int slotId;
	PkAspectClick.ClickType type;

	public PkClientSlotDrain(int windowId, int slotId, PkAspectClick.ClickType type) {
		this.windowId = windowId;
		this.slotId = slotId;
		this.type = type;
	}

	public static void encode(PkClientSlotDrain msg, FriendlyByteBuf buffer) {
		buffer.writeInt(msg.windowId);
		buffer.writeInt(msg.slotId);
		buffer.writeEnum(msg.type);
	}

	public static PkClientSlotDrain decode(FriendlyByteBuf buffer) {
		return new PkClientSlotDrain(buffer.readInt(),buffer.readInt(), buffer.readEnum(PkAspectClick.ClickType.class));
	}

	public static void handle(PkClientSlotDrain msg, Supplier<NetworkEvent.Context> supplier) {
		// on server
		supplier.get().enqueueWork(() -> {
			LocalPlayer epm = (LocalPlayer) Arcana.proxy.getPlayerOnClient();
			if (epm.containerMenu.containerId == msg.windowId) {
				// decrease/increase whats held on the client
				// rename to PktAspectClickConfirmed
				AspectMenu container = (AspectMenu) epm.containerMenu;
				if (container.getAspectSlots().size() > msg.slotId) {
					AspectSlot slot = container.getAspectSlots().get(msg.slotId);
					if ((msg.type == PkAspectClick.ClickType.TAKE || msg.type == PkAspectClick.ClickType.TAKE_ALL) && (container.getHeldAspect() == Aspects.EMPTY || container.getHeldAspect() == null || container.getHeldAspect() == slot.getAspect()) && slot.getAmount() > 0) {
						float drain = msg.type == PkAspectClick.ClickType.TAKE_ALL ? slot.getAmount() : 1;
						slot.drain(slot.getAspect(), drain);
					} else if ((msg.type == PkAspectClick.ClickType.PUT || msg.type == PkAspectClick.ClickType.PUT_ALL) && container.getHeldAspect() != null && container.getHeldCount() > 0 && (slot.getAspect() == container.getHeldAspect() || slot.getAspect() == null || slot.getAspect() == Aspects.EMPTY)) {
						float drain = msg.type == PkAspectClick.ClickType.PUT_ALL ? container.getHeldCount() : 1;
						slot.insert(slot.getAspect(), drain);
					}
				} else {
					LOGGER.error(String.format("Tried to click on invalid aspect slot; out of bounds! (size: %d, slot index: %d).", container.getAspectSlots().size(), msg.slotId));
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
}
