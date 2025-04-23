package com.wonginnovations.arcana.network;

import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.containers.FociForgeMenu;
import com.wonginnovations.arcana.systems.spell.SpellState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PkFociForgeAction {

    int windowId;
    Type action;
    int ax, ay;
    int bx, by;
    int sequence;
    Aspect aspect;

    public PkFociForgeAction(int windowId, Type action, int ax, int ay, int bx, int by, int sequence, Aspect aspect) {
        this.windowId = windowId;
        this.action = action;
        this.ax = ax;
        this.ay = ay;
        this.bx = bx;
        this.by = by;
        this.sequence = sequence;
        this.aspect = aspect;
    }

    public static void encode(PkFociForgeAction msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.windowId);
        buffer.writeEnum(msg.action);
        buffer.writeShort(msg.ax);
        buffer.writeShort(msg.ay);
        buffer.writeShort(msg.bx);
        buffer.writeShort(msg.by);
        buffer.writeShort(msg.sequence);
        buffer.writeUtf(msg.aspect.name());
    }

    public static PkFociForgeAction decode(FriendlyByteBuf buffer) {
        return new PkFociForgeAction(
                buffer.readInt(),
                buffer.readEnum(Type.class),
                buffer.readShort(),
                buffer.readShort(),
                buffer.readShort(),
                buffer.readShort(),
                buffer.readShort(),
                AspectUtils.getAspectByName(buffer.readUtf()));
    }


    public static void handle(PkFociForgeAction msg, Supplier<NetworkEvent.Context> supplier) {
        supplier.get().enqueueWork(() -> {
            ServerPlayer spe = supplier.get().getSender();
            // TODO: spe.containerCounter might need to be spe.containerMenu.containerId
            if (spe.containerCounter == msg.windowId) {
                FociForgeMenu container = (FociForgeMenu)spe.containerMenu;
                SpellState state = container.te.spellState;
                boolean valid = false;
                if (msg.sequence == state.sequence) {
                    switch (msg.action) {
                        case PLACE:
                            valid = state.place(msg.ax, msg.ay, msg.bx, false);
                            break;
                        case RAISE:
                            valid = state.raise(msg.ax, msg.ay, spe.getUUID(), false);
                            break;
                        case LOWER:
                            valid = state.lower(msg.ax, msg.ay, msg.bx, msg.by, spe.getUUID(), false);
                            break;
                        case CONNECT:
                            valid = state.connect(msg.ax, msg.ay, msg.bx, msg.by, false);
                            break;
                        case DELETE:
                            valid = state.delete(msg.ax, msg.ay, spe.getUUID(), false);
                            break;
                        case ASSIGN:
                            valid = state.assign(msg.ax, msg.ay, msg.aspect, false);
                            break;
                    }
                }
                if (valid) {
                    state.sequence++;
                    container.te.setChanged();
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }

    public enum Type {
        PLACE,
        RAISE,
        LOWER,
        CONNECT,
        DELETE,
        ASSIGN
    }
}
