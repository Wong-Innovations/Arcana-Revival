package arcana.api.research;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import arcana.api.aspects.AspectList;

import java.util.Collection;
import java.util.LinkedHashMap;

public class ResearchCategories {
    public static LinkedHashMap<String, ResearchCategory> researchCategories = new LinkedHashMap<>();

    public static ResearchCategory getResearchCategory(final String key) {
        return ResearchCategories.researchCategories.get(key);
    }

    public static String getCategoryName(String key) {
        return I18n.get("tc.research_category." + key);
    }

    public static ResearchEntry getResearch(final String key) {
        Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
        for (ResearchCategory cat : rc) {
            Collection<ResearchEntry> rl = cat.research.values();
            for (ResearchEntry ri : rl) {
                if (ri.key.equals(key)) {
                    return ri;
                }
            }
        }
        return null;
    }

    public static ResearchCategory registerCategory(String key, String researchkey, AspectList formula, ResourceLocation icon, ResourceLocation background, ResourceLocation background2) {
        if (getResearchCategory(key) == null) {
            ResearchCategory rl = new ResearchCategory(key, researchkey, formula, icon, background, background2);
            ResearchCategories.researchCategories.put(key, rl);
            return rl;
        }
        return null;
    }
}
