package arcana.common.lib.network.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import arcana.common.blockentities.crafting.BlockEntityResearchTable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class PacketStartTheoryToServer {
    private long pos;
    private Set<String> aids;

    public PacketStartTheoryToServer() {
        aids = new HashSet<>();
    }

    public PacketStartTheoryToServer(BlockPos pos, Set<String> aids) {
        this.aids = new HashSet<>();
        this.pos = pos.asLong();
        this.aids = aids;
    }

    public PacketStartTheoryToServer(FriendlyByteBuf buffer) {
        aids = new HashSet<>();
        pos = buffer.readLong();
        for (int s = buffer.readByte(), a = 0; a < s; ++a) {
            aids.add(buffer.readUtf());
        }
    }

    public static void encode(PacketStartTheoryToServer message, FriendlyByteBuf buffer) {
        buffer.writeLong(message.pos);
        buffer.writeByte(message.aids.size());
        for (String aid : message.aids) {
            buffer.writeUtf(aid);
        }
    }

    public static PacketStartTheoryToServer decode(FriendlyByteBuf buffer) {
        return new PacketStartTheoryToServer(buffer);
    }

    public static void onMessage(PacketStartTheoryToServer message, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getSender() == null) return;
            Level level = context.getSender().level();
            Player player = context.getSender();
            BlockPos bp = BlockPos.of(message.pos);
            if (level != null && player != null) {
                BlockEntity te = level.getBlockEntity(bp);
                if (te instanceof BlockEntityResearchTable) {
                    ((BlockEntityResearchTable) te).startNewTheory(player, message.aids);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
