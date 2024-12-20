package com.wonginnovations.arcana.common.network.fx;

import com.wonginnovations.arcana.client.fx.FXDispatcher;
import com.wonginnovations.arcana.common.network.IPacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class FXBlockBamfPacket implements IPacketBase {

    private final double x;
    private final double y;
    private final double z;
    private final int color;
    private final boolean sound;
    private final boolean flair;
    private final byte face;

    public FXBlockBamfPacket(double x, double y, double z, int color, boolean sound, boolean flair, Direction face) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.sound = sound;
        this.flair = flair;

        if (face != null) {
            this.face = (byte)face.ordinal();
        } else {
            this.face = -1;
        }
    }

    public FXBlockBamfPacket(BlockPos pos, int color, boolean sound, boolean flair, Direction face) {
        this.x = (double)pos.getX() + 0.5;
        this.y = (double)pos.getY() + 0.5;
        this.z = (double)pos.getZ() + 0.5;
        this.color = color;
        this.sound = sound;
        this.flair = flair;

        if (face != null) {
            this.face = (byte)face.ordinal();
        } else {
            this.face = -1;
        }
    }

    public FXBlockBamfPacket(FriendlyByteBuf buffer) {
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
        this.color = buffer.readInt();
        this.sound = buffer.readBoolean();
        this.flair = buffer.readBoolean();
        this.face = buffer.readByte();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeDouble(this.x);
        buffer.writeDouble(this.y);
        buffer.writeDouble(this.z);
        buffer.writeInt(this.color);
        buffer.writeBoolean(this.sound);
        buffer.writeBoolean(this.flair);
        buffer.writeByte(this.face);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            boolean clientSide = sender == null;

            if (clientSide) {
                Direction side = null;
                if (face >= 0) {
                    side = Direction.from3DDataValue(face);
                }

                if (color != -9999) {
                    FXDispatcher.INSTANCE.drawBamf(x, y, z, color, sound, flair, side);
                } else {
                    FXDispatcher.INSTANCE.drawBamf(x, y, z, sound, flair, side);
                }
            }
        });
        return true;
    }
}
