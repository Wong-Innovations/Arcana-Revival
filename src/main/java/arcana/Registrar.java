package arcana;

import arcana.common.config.ModRecipeProvider;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import arcana.client.ItemPropertiesHandler;
import arcana.common.world.biomes.BiomeHandler;

@Mod.EventBusSubscriber(modid = Arcana.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Registrar {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ItemPropertiesHandler.registerItemProperties();
    }

    @SubscribeEvent
    public static void onRegisterEvent(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.BIOMES, Registrar::registerBiomes);
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, Registrar::initializeRecipes);
    }

    private static void initializeRecipes(RegisterEvent.RegisterHelper<RecipeSerializer<?>> event) {
        ModRecipeProvider.initializeFakeRecipes();
        ModRecipeProvider.initializeCompoundRecipes();
    }

    private static void registerBiomes(RegisterEvent.RegisterHelper<Biome> event) {
        BiomeHandler.registerBiomes();
    }

}
