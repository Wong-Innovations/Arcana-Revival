package arcana.common.lib.network.fx;

import arcana.client.fx.FXDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketFXPollute {
    private int x;
    private int y;
    private int z;
    private byte amount;

    public PacketFXPollute() {
    }

    public PacketFXPollute(BlockPos pos, float amt) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        if (amt < 1.0F && amt > 0.0F) {
            amt = 1.0F;
        }

        this.amount = (byte)((int)amt);
    }

    public PacketFXPollute(BlockPos pos, float amt, boolean vary) {
        this(pos, amt);
    }

    public PacketFXPollute(FriendlyByteBuf buffer) {
        this.x = buffer.readInt();
        this.y = buffer.readInt();
        this.z = buffer.readInt();
        this.amount = buffer.readByte();
    }

    public static void encode(PacketFXPollute message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.x);
        buffer.writeInt(message.y);
        buffer.writeInt(message.z);
        buffer.writeByte(message.amount);
    }

    public static PacketFXPollute decode(FriendlyByteBuf buffer) {
        return new PacketFXPollute(buffer);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onMessage(PacketFXPollute message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            for (int a = 0; a < Math.min(40, message.amount); ++a) {
                FXDispatcher.INSTANCE.drawPollutionParticles(new BlockPos(message.x, message.y, message.z));
            }
            ctx.get().setPacketHandled(true);
        });
    }
}
