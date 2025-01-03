package arcana.common.config;

import arcana.api.ArcanaApiHelper;
import net.minecraft.tags.BlockTags;
import arcana.api.ArcanaApi;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.internal.CommonInternals;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;

public class ConfigAspects {
    public static void postInit() {
        CommonInternals.objectTags.clear();
        registerItemAspects();
    }

    private static void registerItemAspects() {
        ArcanaApi.registerBlockTag(BlockTags.BASE_STONE_OVERWORLD, new AspectList().add(Aspect.EARTH, 5));
        ArcanaApi.registerBlockTag(Tags.Blocks.SAND, new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5));
        ArcanaApi.registerItemTag(Tags.Items.GEMS_QUARTZ, (new AspectList()).add(Aspect.CRYSTAL, 5));
        for (Aspect aspect : Aspect.aspects.values()) {
            ArcanaApi.registerObjectTag(ArcanaApiHelper.makeCrystal(aspect, 1), new AspectList().add(aspect, 1));
        }
    }
}
