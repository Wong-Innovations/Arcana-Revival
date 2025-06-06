package com.wonginnovations.arcana.network;

import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.containers.AspectMenu;
import com.wonginnovations.arcana.containers.slots.AspectSlot;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class PkAspectClick{
	
	public static final Logger LOGGER = LogManager.getLogger();
	
	int windowId;
	int slotId;
	ClickType type;
	@Nonnull
	Aspect expectedAspect;
	
	public PkAspectClick(int windowId, int slotId, ClickType type, @Nonnull Aspect expectedAspect) {
		this.windowId = windowId;
		this.slotId = slotId;
		this.type = type;
		this.expectedAspect = expectedAspect;
	}
	
	public static void encode(PkAspectClick msg, FriendlyByteBuf buffer) {
		buffer.writeInt(msg.windowId);
		buffer.writeInt(msg.slotId);
		buffer.writeEnum(msg.type);
		buffer.writeUtf(msg.expectedAspect.name());
	}
	
	public static PkAspectClick decode(FriendlyByteBuf buffer) {
		return new PkAspectClick(buffer.readInt(), buffer.readInt(), buffer.readEnum(ClickType.class), AspectUtils.getAspectByName(buffer.readUtf()));
	}
	
	public static void handle(PkAspectClick msg, Supplier<NetworkEvent.Context> supplier) {
		// on server
		supplier.get().enqueueWork(() -> {
			ServerPlayer spe = supplier.get().getSender();
			if (spe.containerMenu.containerId == msg.windowId) {
				// decrease/increase whats held on the server
				// send back a PktAspectClickConfirmed with new heldAspect and new heldCount for client
				AspectMenu container = (AspectMenu)spe.containerMenu;
				if (container.getAspectSlots().size() > msg.slotId) {
					AspectSlot slot = container.getAspectSlots().get(msg.slotId);
					if (slot.getAspect() == null)
						slot.setAspect(Aspects.EMPTY); // Quick fix. TODO: Fix null problems.
					if (msg.type == ClickType.TAKE || msg.type == ClickType.TAKE_ALL) {
						if (slot.isSymbolic()) {
							container.setHeldAspect(msg.expectedAspect);
						} else if ((container.getHeldAspect() == Aspects.EMPTY || container.getHeldAspect() == null || container.getHeldAspect() == slot.getAspect()) && slot.getAmount() > 0) {
							container.setHeldAspect(slot.getAspect());
							float drain = msg.type == ClickType.TAKE_ALL ? slot.getAmount() : 1;
							container.setHeldCount(container.getHeldCount() + syncAndGet(slot, drain, msg.windowId, msg.slotId, msg.type, spe, true));
							if (slot.getAmount() <= 0 && slot.storeSlot)
								slot.setAspect(Aspects.EMPTY);
							slot.onChange();
						}
					} else if (msg.type == ClickType.PUT || msg.type == ClickType.PUT_ALL) {
						if (slot.isSymbolic()) {
							container.setHeldCount(0);
							container.setHeldAspect(null);
						} else if (container.getHeldAspect() != null && container.getHeldCount() > 0 && (slot.getAspect() == container.getHeldAspect() || slot.getAspect() == Aspects.EMPTY || slot.getAmount() == 0)) {
							float drain = msg.type == ClickType.PUT_ALL ? container.getHeldCount() : 1;
							if (slot.getAspect() == Aspects.EMPTY && slot.storeSlot)
								slot.setAspect(container.getHeldAspect());
							container.setHeldCount(container.getHeldCount() - (syncAndGet(slot, drain, msg.windowId, msg.slotId, msg.type, spe, false)));
							if (container.getHeldCount() <= 0) {
								container.setHeldCount(0);
								container.setHeldAspect(null);
							}
							slot.onChange();
						}
					}
					container.onAspectSlotChange();
					Connection.sendSyncAspectContainer(container, spe);
				} else {
					LOGGER.error(String.format("Tried to click on invalid aspect slot; out of bounds! (size: %d, slot index: %d).", container.getAspectSlots().size(), msg.slotId));
				}
			}
		});
		supplier.get().setPacketHandled(true);
	}
	
	private static float syncAndGet(AspectSlot s, float d, int windowId, int slotId, ClickType type, ServerPlayer spe, boolean isDrain) {
		float temp = isDrain ? s.drain(s.getAspect(), d) : s.insert(s.getAspect(), d);
		Connection.sendClientSlotDrain(windowId, slotId, type, spe);
		return temp;
	}
	
	public enum ClickType{
		TAKE,
		PUT,
		TAKE_ALL,
		PUT_ALL
	}
}
