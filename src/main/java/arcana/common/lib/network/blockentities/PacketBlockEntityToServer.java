package arcana.common.lib.network.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import arcana.common.blockentities.BlockEntityArcana;
import arcana.common.lib.utils.Utils;

import java.util.function.Supplier;

public class PacketBlockEntityToServer {
    private long pos;
    private CompoundTag nbt;

    public PacketBlockEntityToServer() {
    }

    public PacketBlockEntityToServer(BlockPos pos, CompoundTag nbt) {
        this.pos = pos.asLong();
        this.nbt = nbt;
    }

    public PacketBlockEntityToServer(FriendlyByteBuf buffer) {
        pos = buffer.readLong();
        nbt = Utils.readNBTTagCompoundFromBuffer(buffer);
    }

    public static void encode(PacketBlockEntityToServer message, FriendlyByteBuf buffer) {
        buffer.writeLong(message.pos);
        Utils.writeNBTTagCompoundToBuffer(buffer, message.nbt);
    }

    public static PacketBlockEntityToServer decode(FriendlyByteBuf buffer) {
        return new PacketBlockEntityToServer(buffer);
    }

    public static void onMessage(PacketBlockEntityToServer message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getSender() == null) return;
            Level level = ctx.get().getSender().level();
            BlockPos bp = BlockPos.of(message.pos);
            if (level != null) {
                BlockEntity te = level.getBlockEntity(bp);
                if (te instanceof BlockEntityArcana) {
                    ((BlockEntityArcana) te).messageFromClient((message.nbt == null) ? new CompoundTag() : message.nbt, ctx.get().getSender());
                }
            }
            ctx.get().setPacketHandled(true);
        });
    }
}
