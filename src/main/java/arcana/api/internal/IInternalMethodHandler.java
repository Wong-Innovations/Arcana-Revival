package arcana.api.internal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import arcana.api.aspects.AspectList;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.capabilities.IPlayerWarp;
import arcana.api.research.ResearchCategory;
import net.minecraft.world.level.Level;

public interface IInternalMethodHandler {
    boolean addKnowledge(Player player, IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int amount);

    boolean progressResearch(Player player, String researchkey);

    void addWarpToPlayer(Player player, int amount, IPlayerWarp.EnumWarpType type);

    AspectList getObjectAspects(ItemStack is);

    boolean completeResearch(Player player, String res);

    float drainVis(Level level, BlockPos pos, float amount, boolean simulate);

    float drainFlux(Level level, BlockPos pos, float amount, boolean simulate);

    void addVis(Level level, BlockPos pos, float amount);

    void addFlux(Level level, BlockPos pos, float amount, boolean showEffect);

//    float getTotalAura(Level level, BlockPos pos);

    float getVis(Level level, BlockPos pos);

    float getFlux(Level level, BlockPos pos);

    int getAuraBase(Level level, BlockPos pos);

    boolean shouldPreserveAura(Level level, Player player, BlockPos pos);
}
