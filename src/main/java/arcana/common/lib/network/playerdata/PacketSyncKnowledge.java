package arcana.common.lib.network.playerdata;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.capabilities.ModCapabilities;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchEntry;
import arcana.client.gui.ResearchToast;
import arcana.common.lib.utils.Utils;

import java.util.function.Supplier;

public class PacketSyncKnowledge {

    protected CompoundTag data;

    public PacketSyncKnowledge(Player player) {
        IPlayerKnowledge pk = ModCapabilities.getKnowledge(player);
        this.data = pk.serializeNBT();
        for (String key : pk.getResearchList()) {
            pk.clearResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP);
        }
    }

    public PacketSyncKnowledge(FriendlyByteBuf buffer) {
        this.data = Utils.readNBTTagCompoundFromBuffer(buffer);
    }

    public static void encode(PacketSyncKnowledge message, FriendlyByteBuf buffer) {
        Utils.writeNBTTagCompoundToBuffer(buffer, message.data);
    }

    public static PacketSyncKnowledge decode(FriendlyByteBuf buffer) {
        return new PacketSyncKnowledge(buffer);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onMessage(PacketSyncKnowledge message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            IPlayerKnowledge pk = ModCapabilities.getKnowledge(player);
            pk.deserializeNBT(message.data);
            for (String key : pk.getResearchList()) {
                if (pk.hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP)) {
                    ResearchEntry ri = ResearchCategories.getResearch(key);
                    if (ri != null) {
                        Minecraft.getInstance().getToasts().addToast(new ResearchToast(ri));
                    }
                }
                pk.clearResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
