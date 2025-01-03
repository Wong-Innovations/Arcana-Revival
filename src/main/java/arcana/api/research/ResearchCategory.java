package arcana.api.research;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;

import java.util.HashMap;
import java.util.Map;

public class ResearchCategory {
    public int minDisplayColumn;
    public int minDisplayRow;
    public int maxDisplayColumn;
    public int maxDisplayRow;
    public ResourceLocation icon;
    public ResourceLocation background;
    public ResourceLocation background2;
    public String researchKey;
    public String key;
    public AspectList formula;
    public Map<String, ResearchEntry> research;

    public ResearchCategory(String key, String researchkey, AspectList formula, ResourceLocation icon, ResourceLocation background) {
        this.research = new HashMap<>();
        this.key = key;
        this.researchKey = researchkey;
        this.icon = icon;
        this.background = background;
        this.background2 = null;
        this.formula = formula;
    }

    public ResearchCategory(String key, String researchKey, AspectList formula, ResourceLocation icon, ResourceLocation background, ResourceLocation background2) {
        this.research = new HashMap<>();
        this.key = key;
        this.researchKey = researchKey;
        this.icon = icon;
        this.background = background;
        this.background2 = background2;
        this.formula = formula;
    }

    public int applyFormula(AspectList as) {
        return applyFormula(as, 1);
    }

    public int applyFormula(AspectList as, double mod) {
        if (formula == null) return 0;
        double total = 0;
        for (Aspect aspect : formula.getAspects()) {
            total += (mod * mod) * as.getAmount(aspect) * (formula.getAmount(aspect) / 10d);
        }
        if (total > 0) total = Math.sqrt(total);
        return Mth.ceil(total);
    }
}
