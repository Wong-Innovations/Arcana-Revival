package arcana.api.internal;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import arcana.api.aspects.AspectList;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.capabilities.IPlayerWarp;
import arcana.api.research.ResearchCategory;

public interface IInternalMethodHandler {
    boolean addKnowledge(Player player, IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int amount);

    boolean progressResearch(Player player, String researchkey);

    void addWarpToPlayer(Player player, int amount, IPlayerWarp.EnumWarpType type);

    AspectList getObjectAspects(ItemStack is);

    boolean completeResearch(Player player, String res);
}
