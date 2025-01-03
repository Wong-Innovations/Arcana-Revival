package arcana.common.lib.events;

import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import arcana.common.config.ConfigAspects;
import arcana.common.config.ModRecipeProvider;
import arcana.common.world.aura.AuraHandler;

@Mod.EventBusSubscriber
public class WorldEvents {
    private static boolean loaded;

    @SubscribeEvent
    public static void worldLoad(LevelEvent.Load event) {
        if (!loaded) {
            ConfigAspects.postInit();
            ModRecipeProvider.compileGroups((Level) event.getLevel());

            loaded = true;
        }

        if (event.getLevel() instanceof Level level) {
            if (!level.isClientSide) {
                AuraHandler.addAuraWorld(level.dimension());
            }
        }
    }

    @SubscribeEvent
    public static void worldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            if (level.isClientSide) {
                return;
            }
            AuraHandler.removeAuraWorld(level.dimension());
        }
    }
}
