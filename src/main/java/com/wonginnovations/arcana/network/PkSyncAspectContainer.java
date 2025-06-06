package com.wonginnovations.arcana.network;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.containers.AspectMenu;
import com.wonginnovations.arcana.containers.slots.AspectSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PkSyncAspectContainer {

	public static final Logger LOGGER = LogManager.getLogger();

	// so, what do I need to sync?
	// 1. held aspect & held count
	// 2. the contents of every slot
	// regular slots can be synced just by syncing the underlying AspectHandler
	// store slots need that + their own aspect
	
	float heldCount;
	Aspect heldAspect;
	List<Pair<Integer, CompoundTag>> handlers = new ArrayList<>();
	List<Pair<Integer, Aspect>> storeSlotAspects = new ArrayList<>();

	public PkSyncAspectContainer(AspectMenu container) {
		heldCount = container.getHeldCount();
		heldAspect = container.getHeldAspect();

		for (int i = 0; i < container.getAllOpenHandlers().size(); i++)
			handlers.add(Pair.of(i, container.getAllOpenHandlers().get(i).serializeNBT()));

		for (int i = 0; i < container.getAspectSlots().size(); i++) {
			AspectSlot slot = container.getAspectSlots().get(i);
			if (slot.storeSlot)
				storeSlotAspects.add(Pair.of(i, slot.getAspect()));
		}
	}

	public PkSyncAspectContainer(float heldCount, Aspect heldAspect, List<Pair<Integer, CompoundTag>> handlers, List<Pair<Integer, Aspect>> storeSlotAspects) {
		this.heldCount = heldCount;
		this.heldAspect = heldAspect;
		this.handlers = handlers;
		this.storeSlotAspects = storeSlotAspects;
	}

	public static void encode(PkSyncAspectContainer msg, FriendlyByteBuf buf) {
		buf.writeFloat(msg.heldCount);
		writeAspect(buf, msg.heldAspect);

		buf.writeInt(msg.handlers.size());
		for (Pair<Integer, CompoundTag> handler : msg.handlers) {
			buf.writeInt(handler.getLeft());
			buf.writeNbt(handler.getRight());
		}

		buf.writeInt(msg.storeSlotAspects.size());
		for (Pair<Integer, Aspect> storeSlot : msg.storeSlotAspects) {
			buf.writeInt(storeSlot.getLeft());
			writeAspect(buf, storeSlot.getRight());
		}
	}

	public static PkSyncAspectContainer decode(FriendlyByteBuf buf) {
		float temp_heldCount = buf.readFloat();
		Aspect temp_heldAspect = readAspect(buf);
		List<Pair<Integer, CompoundTag>> temp_handlers = new ArrayList<>();
		List<Pair<Integer, Aspect>> temp_storeSlotAspects = new ArrayList<>();

		int handlerCount = buf.readInt();
		for (int i = 0; i < handlerCount; i++) {
			int index = buf.readInt();
			CompoundTag data = null;
			data = buf.readNbt();
			temp_handlers.add(Pair.of(index, data));
		}

		int storeSlots = buf.readInt();
		for (int i = 0; i < storeSlots; i++) {
			int index = buf.readInt();
			Aspect aspect = readAspect(buf);
			temp_storeSlotAspects.add(Pair.of(index, aspect));
		}
		return new PkSyncAspectContainer(temp_heldCount,temp_heldAspect,temp_handlers,temp_storeSlotAspects);
	}

	public static void handle(PkSyncAspectContainer msg, Supplier<NetworkEvent.Context> supplier) {
		supplier.get().enqueueWork(() -> {
			Player eps = Arcana.proxy.getPlayerOnClient();
			if (eps != null) {
				AspectMenu container = (AspectMenu) eps.containerMenu;
				container.setHeldAspect(msg.heldAspect);
				container.setHeldCount(msg.heldCount);
				for (Pair<Integer, Aspect> storeSlot : msg.storeSlotAspects)
					container.getAspectSlots().get(storeSlot.getLeft()).setAspect(storeSlot.getRight());
				for (Pair<Integer, CompoundTag> handler : msg.handlers)
					container.getAllOpenHandlers().get(handler.getLeft()).deserializeNBT(handler.getRight());
				container.onAspectSlotChange();
			}
		});
		supplier.get().setPacketHandled(true);
	}

	private static void writeAspect(FriendlyByteBuf pb, Aspect aspect) {
		pb.writeBoolean(aspect != null);
		if (aspect != null)
			pb.writeInt(aspect.getId());
	}

	private static Aspect readAspect(FriendlyByteBuf pb) {
		if (pb.readBoolean())
			return Aspect.fromId(pb.readInt());
		else
			return null;
	}
}
