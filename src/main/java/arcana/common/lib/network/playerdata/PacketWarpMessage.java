package arcana.common.lib.network.playerdata;

import arcana.common.lib.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketWarpMessage {
    protected int data;
    protected byte type;

    public PacketWarpMessage() {
        this.data = 0;
        this.type = 0;
    }

    public PacketWarpMessage(Player player, byte type, int change) {
        this.data = change;
        this.type = type;
    }

    public PacketWarpMessage(FriendlyByteBuf buffer) {
        this.data = buffer.readInt();
        this.type = buffer.readByte();
    }

    public static void encode(PacketWarpMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.data);
        buffer.writeByte(message.type);
    }

    public static PacketWarpMessage decode(FriendlyByteBuf buffer) {
        return new PacketWarpMessage(buffer);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onMessage(PacketWarpMessage message, Supplier<NetworkEvent.Context> ctx) {
        if (message.data != 0) {
            ctx.get().enqueueWork(() -> {
                processMessage(message);
                ctx.get().setPacketHandled(true);
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void processMessage(PacketWarpMessage message) {
        if (message.type == 0 && message.data > 0) {
            String text = I18n.get("tc.addwarp");
            if (message.data < 0) {
                text = I18n.get("tc.removewarp");
            } else {
                Minecraft.getInstance().player.playSound(ModSounds.whispers.get(), 0.5f, 1.0f);
            }
        } else if (message.type == 1) {
            String text = I18n.get("tc.addwarpsticky");
            if (message.data < 0) {
                text = I18n.get("tc.removewarpsticky");
            } else {
                Minecraft.getInstance().player.playSound(ModSounds.whispers.get(), 0.5f, 1.0f);
            }
            Minecraft.getInstance().player.displayClientMessage(Component.translatable(text), true);
        } else if (message.data > 0) {
            String text = I18n.get("tc.addwarptemp");
            if (message.data < 0) {
                text = I18n.get("tc.removewarptemp");
            }
            Minecraft.getInstance().player.displayClientMessage(Component.translatable(text), true);
        }
    }
}
