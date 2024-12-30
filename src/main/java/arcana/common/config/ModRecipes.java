package arcana.common.config;

import arcana.Arcana;
import arcana.api.crafting.ShapedArcaneRecipe;
import arcana.common.lib.crafting.RecipeMagicDust;
import arcana.common.lib.crafting.RecipeScribingTools;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Arcana.MODID);

    public static final RegistryObject<RecipeSerializer<ShapedArcaneRecipe>> SHAPED_ARCANE_SERIALIZER = SERIALIZERS.register("shaped_arcane", ShapedArcaneRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<RecipeMagicDust>> MAGIC_DUST_SERIALIZER = SERIALIZERS.register("salismundus", () -> RecipeMagicDust.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<RecipeScribingTools>> SCRIBING_TOOLS_SERIALIZER = SERIALIZERS.register("scribing_tools", () -> new SimpleCraftingRecipeSerializer<>((id, category) -> new RecipeScribingTools(id)));

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
