package arcana.common.lib.network.playerdata;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.Tags;
import net.minecraftforge.network.NetworkEvent;
import arcana.api.capabilities.ModCapabilities;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchEntry;
import arcana.api.research.ResearchStage;
import arcana.common.lib.research.ResearchManager;
import arcana.common.lib.utils.InventoryUtils;

import java.util.function.Supplier;

public class PacketSyncProgressToServer {
    private String key;
    private boolean first;
    private boolean checks;
    private boolean noFlags;

    public PacketSyncProgressToServer() {
    }

    public PacketSyncProgressToServer(String key, boolean first, boolean checks, boolean noFlags) {
        this.key = key;
        this.first = first;
        this.checks = checks;
        this.noFlags = noFlags;
    }

    public PacketSyncProgressToServer(String key, boolean first) {
        this(key, first, false, true);
    }

    public PacketSyncProgressToServer(FriendlyByteBuf buffer) {
        key = buffer.readUtf();
        first = buffer.readBoolean();
        checks = buffer.readBoolean();
        noFlags = buffer.readBoolean();
    }

    public static void encode(PacketSyncProgressToServer message, FriendlyByteBuf buffer) {
        buffer.writeUtf(message.key);
        buffer.writeBoolean(message.first);
        buffer.writeBoolean(message.checks);
        buffer.writeBoolean(message.noFlags);
    }

    public static PacketSyncProgressToServer decode(FriendlyByteBuf buffer) {
        return new PacketSyncProgressToServer(buffer);
    }

    public static void onMessage(PacketSyncProgressToServer message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && message.first != ModCapabilities.knowsResearch(player, message.key)) {
                if (message.checks && !checkRequisites(player, message.key)) {
                    return;
                }
                if (message.noFlags) {
                    ResearchManager.noFlags = true;
                }
                ResearchManager.progressResearch(player, message.key);
            }
            ctx.get().setPacketHandled(true);
        });
    }

    private static boolean checkRequisites(ServerPlayer player, String key) {
        ResearchEntry research = ResearchCategories.getResearch(key);
        if (research.getStages() != null) {
            int currentStage = ModCapabilities.getKnowledge(player).getResearchStage(key) - 1;
            if (currentStage < 0) {
                return false;
            }
            if (currentStage >= research.getStages().length) {
                return true;
            }
            ResearchStage stage = research.getStages()[currentStage];
            Object[] o = stage.getObtain();
            if (o != null) {
                for (Object object : o) {
                    ItemStack ts = ItemStack.EMPTY;
                    boolean ore = false;
                    if (object instanceof ItemStack itemStack) {
                        ts = itemStack;
                        if (itemStack.is(Tags.Items.ORES)) {
                            ore = true;
                        }
                    }
                    if (!InventoryUtils.isPlayerCarryingAmount(player, ts, ore)) {
                        return false;
                    }
                }
                for (Object object : o) {
                    boolean ore2 = false;
                    ItemStack ts2 = ItemStack.EMPTY;
                    if (object instanceof ItemStack itemStack) {
                        ts2 = itemStack;
                        if (itemStack.is(Tags.Items.ORES)) {
                            ore2 = true;
                        }
                    }
                    InventoryUtils.consumePlayerItem(player, ts2, true, ore2);
                }
            }
            Object[] c = stage.getCraft();
            if (c != null) {
                for (int a2 = 0; a2 < c.length; ++a2) {
                    if (!ModCapabilities.getKnowledge(player).isResearchKnown("[#]" + stage.getCraftReference()[a2])) {
                        return false;
                    }
                }
            }
            String[] r = stage.getResearch();
            if (r != null) {
                for (String s : r) {
                    if (!ModCapabilities.knowsResearchStrict(player, s)) {
                        return false;
                    }
                }
            }
            ResearchStage.Knowledge[] k = stage.getKnow();
            if (k != null) {
                for (ResearchStage.Knowledge knowledge : k) {
                    int pk = ModCapabilities.getKnowledge(player).getKnowledge(knowledge.type, knowledge.category);
                    if (pk < knowledge.amount) {
                        return false;
                    }
                }
                for (ResearchStage.Knowledge knowledge : k) {
                    ResearchManager.addKnowledge(player, knowledge.type, knowledge.category, -knowledge.amount * knowledge.type.getProgression());
                }
            }
        }
        return true;
    }
}
