package arcana.common.lib.network.misc;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import arcana.client.lib.events.HudHandler;
import arcana.common.world.aura.AuraChunk;

import java.util.function.Supplier;

public class PacketAuraToClient {
    short base;
    float vis;
    float flux;

    public PacketAuraToClient() {
    }

    public PacketAuraToClient(AuraChunk ac) {
        base = ac.getBase();
        vis = ac.getVis();
        flux = ac.getFlux();
    }

    public PacketAuraToClient(FriendlyByteBuf dat) {
        base = dat.readShort();
        vis = dat.readFloat();
        flux = dat.readFloat();
    }

    public static void encode(PacketAuraToClient message, FriendlyByteBuf dos) {
        dos.writeShort(message.base);
        dos.writeFloat(message.vis);
        dos.writeFloat(message.flux);
    }

    public static PacketAuraToClient decode(FriendlyByteBuf buffer) {
        return new PacketAuraToClient(buffer);
    }

    public static void onMessage(PacketAuraToClient message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            HudHandler.currentAura = new AuraChunk(null, message.base, message.vis, message.flux);
            ctx.get().setPacketHandled(true);
        });
    }
}
