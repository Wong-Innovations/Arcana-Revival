package arcana.common.lib.research;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import arcana.api.ArcanaApi;
import arcana.api.aspects.AspectHelper;
import arcana.api.aspects.AspectList;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.research.IScanThing;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchCategory;
import arcana.api.research.ScanningManager;

public class ScanGeneric implements IScanThing {
    @Override
    public boolean checkThing(Player player, Object obj) {
        if (obj == null) {
            return false;
        }
        AspectList al = null;
        if (obj instanceof Entity && !(obj instanceof ItemEntity)) {
//            al = AspectHelper.getEntityAspects((Entity)obj);
        } else {
            ItemStack is = ScanningManager.getItemFromParms(player, obj);
            if (is != null && !is.isEmpty()) {
                al = AspectHelper.getObjectAspects(is);
            }
        }
        return al != null && al.size() > 0;
    }

    @Override
    public void onSuccess(Player player, Object obj) {
        if (obj == null) {
            return;
        }
        AspectList al = null;

        ItemStack is = ScanningManager.getItemFromParms(player, obj);
        if (is != null && !is.isEmpty()) {
            al = AspectHelper.getObjectAspects(is);
        }
        if (al != null) {
            for (ResearchCategory category : ResearchCategories.researchCategories.values()) {
                ArcanaApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, category, category.applyFormula(al));
            }
        }
    }

    @Override
    public String getResearchKey(Player player, Object obj) {
        ItemStack is = ScanningManager.getItemFromParms(player, obj);
        if (is != null && !is.isEmpty()) {
            String s2 = "!" + ForgeRegistries.ITEMS.getKey(is.getItem()).getPath();
            if (!is.isDamageableItem()) {
                s2 += is.getDamageValue();
            }
            return s2;
        }
        return null;
    }
}
