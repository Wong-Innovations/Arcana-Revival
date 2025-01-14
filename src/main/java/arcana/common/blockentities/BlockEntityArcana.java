package arcana.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;
import arcana.common.lib.network.PacketHandler;
import arcana.common.lib.network.blockentities.PacketBlockEntityToClient;
import arcana.common.lib.network.blockentities.PacketBlockEntityToServer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockEntityArcana extends BlockEntity {
    public BlockEntityArcana(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public void sendMessageToClient(CompoundTag nbt, @Nullable ServerPlayer player) {
        if (player == null) {
            if (getLevel() != null) {
                PacketHandler.sendToPlayersNear(getBlockPos().getX() + 0.5, getBlockPos().getY(), getBlockPos().getZ(), 128.0, getLevel().dimension(), new PacketBlockEntityToClient(getBlockPos(), nbt));
            }
        } else {
            PacketHandler.sendToPlayer(player, new PacketBlockEntityToClient(getBlockPos(), nbt));
        }
    }

    public void sendMessageToServer(CompoundTag nbt) {
        PacketHandler.sendToServer(new PacketBlockEntityToServer(getBlockPos(), nbt));
    }

    public void messageFromServer(CompoundTag nbt) {
    }

    public void messageFromClient(CompoundTag nbt, ServerPlayer player) {
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        readSyncNBT(pTag);
    }

    public void readSyncNBT(CompoundTag nbt) {
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        writeSyncNBT(pTag);
    }

    @Override
    public CompoundTag serializeNBT() {
        return writeSyncNBT(super.serializeNBT());
    }

    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        return nbt;
    }

    public void syncTile(boolean rerender) {
        BlockState state = level.getBlockState(getBlockPos());
        level.sendBlockUpdated(getBlockPos(), state, state, 2 + (rerender ? 4 : 0));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        readSyncNBT(pkt.getTag());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return writeSyncNBT(setupNbt());
    }

    private CompoundTag setupNbt() {
        CompoundTag nbt = super.serializeNBT();
        nbt.remove("ForgeData");
        nbt.remove("ForgeCaps");
        return nbt;
    }
}
