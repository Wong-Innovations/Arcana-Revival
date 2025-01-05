package arcana.common.lib.network;

import arcana.Arcana;
import arcana.common.lib.network.fx.PacketFXPollute;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import arcana.common.lib.network.blockentities.PacketBlockEntityToClient;
import arcana.common.lib.network.blockentities.PacketBlockEntityToServer;
import arcana.common.lib.network.fx.PacketFXBlockBamf;
import arcana.common.lib.network.misc.PacketAuraToClient;
import arcana.common.lib.network.misc.PacketKnowledgeGain;
import arcana.common.lib.network.misc.PacketStartTheoryToServer;
import arcana.common.lib.network.playerdata.PacketSyncKnowledge;
import arcana.common.lib.network.playerdata.PacketSyncProgressToServer;
import arcana.common.lib.network.playerdata.PacketSyncWarp;
import arcana.common.lib.network.playerdata.PacketWarpMessage;

import java.util.Optional;

public class PacketHandler {
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Arcana.MODID, "messages"), () -> "1.0", s -> true, s -> true);

    public static void registerPackets(final FMLCommonSetupEvent event) {
        int idx = 0;
        PacketHandler.INSTANCE.registerMessage(idx++, PacketKnowledgeGain.class, PacketKnowledgeGain::encode, PacketKnowledgeGain::decode, PacketKnowledgeGain::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketStartTheoryToServer.class, PacketStartTheoryToServer::encode, PacketStartTheoryToServer::decode, PacketStartTheoryToServer::onMessage, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketBlockEntityToServer.class, PacketBlockEntityToServer::encode, PacketBlockEntityToServer::decode, PacketBlockEntityToServer::onMessage, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketBlockEntityToClient.class, PacketBlockEntityToClient::encode, PacketBlockEntityToClient::decode, PacketBlockEntityToClient::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketAuraToClient.class, PacketAuraToClient::encode, PacketAuraToClient::decode, PacketAuraToClient::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketSyncWarp.class, PacketSyncWarp::encode, PacketSyncWarp::decode, PacketSyncWarp::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketSyncKnowledge.class, PacketSyncKnowledge::encode, PacketSyncKnowledge::decode, PacketSyncKnowledge::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketWarpMessage.class, PacketWarpMessage::encode, PacketWarpMessage::decode, PacketWarpMessage::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketSyncProgressToServer.class, PacketSyncProgressToServer::encode, PacketSyncProgressToServer::decode, PacketSyncProgressToServer::onMessage, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketFXBlockBamf.class, PacketFXBlockBamf::encode, PacketFXBlockBamf::decode, PacketFXBlockBamf::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        PacketHandler.INSTANCE.registerMessage(idx++, PacketFXPollute.class, PacketFXPollute::encode, PacketFXPollute::decode, PacketFXPollute::onMessage, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
}
