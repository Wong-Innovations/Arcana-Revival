package arcana.common.lib.network.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchCategory;
import arcana.client.lib.events.HudHandler;
import arcana.client.lib.events.RenderEventHandler;
import arcana.common.lib.ModSounds;

import java.util.function.Supplier;

public class PacketKnowledgeGain {
    private byte type;
    private String cat;

    public PacketKnowledgeGain() {
    }

    public PacketKnowledgeGain(byte type, String value) {
        this.type = type;
        this.cat = ((value == null) ? "" : value);
    }

    public PacketKnowledgeGain(FriendlyByteBuf buffer) {
        this.type = buffer.readByte();
        this.cat = buffer.readUtf();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeByte(this.type);
        buffer.writeUtf(this.cat);
    }

    public static PacketKnowledgeGain fromBytes(FriendlyByteBuf buffer) {
        return new PacketKnowledgeGain(buffer);
    }

    @OnlyIn(Dist.CLIENT)
    public static void onMessage(PacketKnowledgeGain message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            processMessage(message);
        });
    }

    @OnlyIn(Dist.CLIENT)
    static void processMessage(PacketKnowledgeGain message) {
        Player p = Minecraft.getInstance().player;
        if (p == null) return;
        IPlayerKnowledge.EnumKnowledgeType type = IPlayerKnowledge.EnumKnowledgeType.values()[message.type];
        ResearchCategory cat = (!message.cat.isEmpty()) ? ResearchCategories.getResearchCategory(message.cat) : null;
        RenderEventHandler.hudHandler.knowledgeGainTrackers.add(new HudHandler.KnowledgeGainTracker(type, cat, 40 + p.level().random.nextInt(20), p.level().random.nextLong()));
        p.level().playLocalSound(p.getX(), p.getY(), p.getZ(), ModSounds.learn.get(), SoundSource.AMBIENT, 1.0f, 1.0f, false);
    }
}
