package com.wonginnovations.arcana.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public interface IPacketBase {

    void write(FriendlyByteBuf buffer);

    boolean handle(NetworkEvent.Context context);

}
