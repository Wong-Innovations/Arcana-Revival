package arcana.common.config;

import net.minecraft.tags.BlockTags;
import arcana.api.ArcanaApi;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.internal.CommonInternals;

public class ConfigAspects {
    public static void postInit() {
        CommonInternals.objectTags.clear();
        registerItemAspects();
    }

    private static void registerItemAspects() {
        ArcanaApi.registerObjectTag(BlockTags.BASE_STONE_OVERWORLD, new AspectList().add(Aspect.EARTH, 5));
        ArcanaApi.registerObjectTag(BlockTags.SAND, new AspectList().add(Aspect.EARTH, 5).add(Aspect.ENTROPY, 5));
    }
}
