package arcana.api.internal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import arcana.api.aspects.AspectList;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.capabilities.IPlayerWarp;
import arcana.api.research.ResearchCategory;
import net.minecraft.world.level.Level;

public class DummyInternalMethodHandler implements IInternalMethodHandler {
    @Override
    public boolean completeResearch(Player player, String researchkey) {
        return false;
    }

    @Override
    public float drainVis(Level level, BlockPos pos, float amount, boolean simulate) {
        return 0;
    }

    @Override
    public float drainFlux(Level level, BlockPos pos, float amount, boolean simulate) {
        return 0;
    }

    @Override
    public void addVis(Level level, BlockPos pos, float amount) {
    }

    @Override
    public void addFlux(Level level, BlockPos pos, float amount, boolean showEffect) {
    }

    @Override
    public float getVis(Level level, BlockPos pos) {
        return 0;
    }

    @Override
    public float getFlux(Level level, BlockPos pos) {
        return 0;
    }

    @Override
    public int getAuraBase(Level level, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean shouldPreserveAura(Level level, Player player, BlockPos pos) {
        return false;
    }

    @Override
    public boolean addKnowledge(Player player, IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int amount) {
        return false;
    }

    @Override
    public void addWarpToPlayer(Player player, int amount, IPlayerWarp.EnumWarpType type) {
    }

    @Override
    public AspectList getObjectAspects(ItemStack is) {
        return null;
    }

    @Override
    public boolean progressResearch(Player player, String researchkey) {
        return false;
    }
}
