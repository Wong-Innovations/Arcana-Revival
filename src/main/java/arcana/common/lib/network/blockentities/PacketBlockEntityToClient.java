package arcana.common.lib.network.blockentities;

import arcana.Arcana;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import arcana.common.blockentities.BlockEntityArcana;
import arcana.common.lib.utils.Utils;

import java.util.function.Supplier;

public class PacketBlockEntityToClient {
    private final long pos;
    private final CompoundTag nbt;

    public PacketBlockEntityToClient(BlockPos pos, CompoundTag nbt) {
        this.pos = pos.asLong();
        this.nbt = nbt;
    }

    public PacketBlockEntityToClient(FriendlyByteBuf buffer) {
        pos = buffer.readLong();
        nbt = Utils.readNBTTagCompoundFromBuffer(buffer);
    }

    public static void encode(PacketBlockEntityToClient message, FriendlyByteBuf buffer) {
        buffer.writeLong(message.pos);
        Utils.writeNBTTagCompoundToBuffer(buffer, message.nbt);
    }

    public static PacketBlockEntityToClient decode(FriendlyByteBuf buffer) {
        return new PacketBlockEntityToClient(buffer);
    }

    public static void onMessage(PacketBlockEntityToClient message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Level world = Arcana.proxy.getClientWorld();
            BlockPos bp = BlockPos.of(message.pos);
            if (world != null) {
                BlockEntity te = world.getBlockEntity(bp);
                if (te instanceof BlockEntityArcana) {
                    ((BlockEntityArcana) te).messageFromServer((message.nbt == null) ? new CompoundTag() : message.nbt);
                }
            }
            ctx.get().setPacketHandled(true);
        });
    }
}
