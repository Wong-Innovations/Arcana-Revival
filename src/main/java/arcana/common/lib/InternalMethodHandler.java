package arcana.common.lib;

import arcana.common.lib.network.fx.PacketFXPollute;
import arcana.common.world.aura.AuraHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import arcana.api.aspects.AspectList;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.capabilities.IPlayerWarp;
import arcana.api.capabilities.ModCapabilities;
import arcana.api.internal.IInternalMethodHandler;
import arcana.api.research.ResearchCategory;
import arcana.common.lib.crafting.CraftingManager;
import arcana.common.lib.network.PacketHandler;
import arcana.common.lib.network.playerdata.PacketWarpMessage;
import arcana.common.lib.research.ResearchManager;

public class InternalMethodHandler implements IInternalMethodHandler {
    @Override
    public boolean addKnowledge(Player player, IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory field, int amount) {
        return amount != 0 && !player.level().isClientSide && ResearchManager.addKnowledge(player, type, field, amount);
    }

    @Override
    public void addWarpToPlayer(Player player, int amount, IPlayerWarp.EnumWarpType type) {
        if (amount == 0 || player.level().isClientSide) {
            return;
        }
        IPlayerWarp pw = ModCapabilities.getWarp(player);
        int cur = pw.get(type);
        if (amount < 0 && cur + amount < 0) {
            amount = cur;
        }
        pw.add(type, amount);
        if (type == IPlayerWarp.EnumWarpType.PERMANENT) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new PacketWarpMessage(player, (byte) 0, amount));
        }
        if (type == IPlayerWarp.EnumWarpType.NORMAL) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new PacketWarpMessage(player, (byte) 1, amount));
        }
        if (type == IPlayerWarp.EnumWarpType.TEMPORARY) {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new PacketWarpMessage(player, (byte) 2, amount));
        }
        if (amount > 0) {
            pw.setCounter(pw.get(IPlayerWarp.EnumWarpType.TEMPORARY) + pw.get(IPlayerWarp.EnumWarpType.PERMANENT) + pw.get(IPlayerWarp.EnumWarpType.NORMAL));
        }
        if (type != IPlayerWarp.EnumWarpType.TEMPORARY && ModCapabilities.knowsResearchStrict(player, "FIRSTSTEPS") && !ModCapabilities.knowsResearchStrict(player, "WARP")) {
            this.completeResearch(player, "WARP");
            player.displayClientMessage(Component.translatable("research.WARP.warn"), true);
        }
        pw.sync((ServerPlayer) player);
    }

    @Override
    public boolean progressResearch(Player player, String researchkey) {
        return researchkey != null && !player.level().isClientSide && ResearchManager.progressResearch(player, researchkey);
    }

    @Override
    public boolean completeResearch(Player player, final String researchkey) {
        return researchkey != null && !player.level().isClientSide && ResearchManager.completeResearch(player, researchkey);
    }

    @Override
    public float drainVis(Level level, BlockPos pos, float amount, boolean simulate) {
        return AuraHandler.drainVis(level, pos, amount, simulate);
    }

    @Override
    public float drainFlux(Level level, BlockPos pos, float amount, boolean simulate) {
        return AuraHandler.drainFlux(level, pos, amount, simulate);
    }

    @Override
    public void addVis(Level level, BlockPos pos, float amount) {
        AuraHandler.addVis(level, pos, amount);
    }

    @Override
    public void addFlux(Level level, BlockPos pos, float amount, boolean showEffect) {
        if (!level.isClientSide()) {
            AuraHandler.addFlux(level, pos, amount);
            if (showEffect && amount > 0.0F) {
                PacketHandler.INSTANCE.send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), 32, level.dimension())), new PacketFXPollute(pos, amount));
            }
        }
        AuraHandler.addFlux(level, pos, amount);
    }

    @Override
    public float getVis(Level level, BlockPos pos) {
        return AuraHandler.getVis(level, pos);
    }

    @Override
    public float getFlux(Level level, BlockPos pos) {
        return AuraHandler.getFlux(level, pos);
    }

    @Override
    public int getAuraBase(Level level, BlockPos pos) {
        return AuraHandler.getAuraBase(level, pos);
    }

    @Override
    public boolean shouldPreserveAura(Level level, Player player, BlockPos pos) {
        return AuraHandler.shouldPreserveAura(level, player, pos);
    }

    @Override
    public AspectList getObjectAspects(ItemStack is) {
        return CraftingManager.getObjectTags(is);
    }
}
