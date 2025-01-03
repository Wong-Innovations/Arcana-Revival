package arcana.common.config;

import arcana.Arcana;
import arcana.api.ArcanaApiHelper;
import arcana.api.crafting.*;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import arcana.api.ArcanaApi;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.common.blocks.ModBlocks;
import arcana.api.internal.CommonInternals;
import arcana.common.items.ModItems;
import arcana.common.lib.crafting.DustTriggerOre;
import arcana.common.lib.crafting.DustTriggerSimple;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    static String defaultGroup = "";
    public static HashMap<String, ArrayList<ResourceLocation>> recipeGroups = new HashMap<>();

    public ModRecipeProvider(PackOutput pPackOutput) {
        super(pPackOutput);
    }

    public static void initializeCompoundRecipes() {
        IDustTrigger.registerDustTrigger(new DustTriggerSimple("!gotdream", Blocks.BOOKSHELF, new ItemStack(ModItems.thaumonomicon.get())));
        IDustTrigger.registerDustTrigger(new DustTriggerOre("FIRSTSTEPS@1", "workbench", new ItemStack(ModBlocks.arcaneWorkbench.get())));
    }

    public static void initializeAlchemyRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        for (Aspect aspect : Aspect.aspects.values()) {
            ResourceLocation recipeId = new ResourceLocation("arcana:vis_crystal_" + aspect.getTag());
            new CrucibleRecipeBuilder(ArcanaApiHelper.makeCrystal(aspect)).group("arcana:viscrystalgroup")
                            .research("BASEALCHEMY")
                            .aspects(new AspectList().add(aspect, 2))
                            .catalyst(Tags.Items.GEMS_QUARTZ)
                            .save(pFinishedRecipeConsumer, recipeId);
        }
    }

    public static void initializeFakeRecipes() {
        ArcanaApi.addFakeCraftingRecipe(new ResourceLocation(Arcana.MODID, "salismundusfake"), new ShapelessRecipe(new ResourceLocation(Arcana.MODID, "salismundusfake"), ModRecipeProvider.defaultGroup, CraftingBookCategory.MISC, new ItemStack(ModItems.salisMundus.get()), NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.FLINT), Ingredient.of(Items.BOWL), Ingredient.of(Items.REDSTONE), Ingredient.of(new ItemStack(ModItems.crystalEssence.get(), 1)), Ingredient.of(new ItemStack(ModItems.crystalEssence.get(), 1)), Ingredient.of(new ItemStack(ModItems.crystalEssence.get(), 1)))));
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ModRecipeProvider.initializeNormalRecipes(pFinishedRecipeConsumer);
        ModRecipeProvider.initializeArcaneRecipes(pFinishedRecipeConsumer);
        ModRecipeProvider.initializeAlchemyRecipes(pFinishedRecipeConsumer);
    }

    public static void initializeArcaneRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ShapedArcaneRecipeBuilder.shaped(ModItems.thaumometer.get()).group("arcana:arcane_workbench")
                .research("FIRSTSTEPS@2")
                .vis(20)
                .crystals(new AspectList()
                        .add(Aspect.AIR, 1)
                        .add(Aspect.EARTH, 1)
                        .add(Aspect.WATER, 1)
                        .add(Aspect.FIRE, 1)
                        .add(Aspect.ORDER, 1)
                        .add(Aspect.ENTROPY, 1))
                .pattern(" I ")
                .pattern("IGI")
                .pattern(" I ")
                .define('I', Items.GOLD_INGOT)
                .define('G', Blocks.GLASS_PANE)
                .unlockedBy("has_arcane_workbench", has(ModBlocks.arcaneWorkbench.get()))
                .save(pFinishedRecipeConsumer);
    }

    public static void initializeNormalRecipes(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.stoneArcane.get(), 9).group(ModRecipeProvider.defaultGroup)
                .pattern("KKK")
                .pattern("KCK")
                .pattern("KKK")
                .define('K', Items.STONE)
                .define('C', ModItems.crystalEssence.get())
                .unlockedBy("has_crystal", has(ModItems.crystalEssence.get()))
                .save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.stoneArcaneBrick.get(), 4).group(ModRecipeProvider.defaultGroup)
                .pattern("KK")
                .pattern("KK")
                .define('K', ModBlocks.stoneArcane.get())
                .unlockedBy("has_stone_arcane", has(ModBlocks.stoneArcane.get()))
                .save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.phial.get(), 8).group(ModRecipeProvider.defaultGroup)
                .pattern(" C ")
                .pattern("G G")
                .pattern(" G ")
                .define('G', Blocks.GLASS)
                .define('C', Items.CLAY_BALL)
                .unlockedBy("has_glass", has(Blocks.GLASS))
                .unlockedBy("has_clay", has(Items.CLAY))
                .save(pFinishedRecipeConsumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.tableWood.get()).group(ModRecipeProvider.defaultGroup)
                .pattern("SSS")
                .pattern("W W")
                .define('S', ItemTags.WOODEN_SLABS)
                .define('W', ItemTags.PLANKS)
                .unlockedBy("has_table_wood", has(ModBlocks.tableWood.get()))
                .save(pFinishedRecipeConsumer);

        String inkwellGroup = "arcana:inkwell";
        SpecialRecipeBuilder.special(ModRecipes.SCRIBING_TOOLS_SERIALIZER.get()).save(pFinishedRecipeConsumer, "arcana:scribingtoolscraft1");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.scribingTools.get()).group(inkwellGroup)
                .requires(Items.GLASS_BOTTLE)
                .requires(Items.FEATHER)
                .requires(Tags.Items.DYES_BLACK)
                .unlockedBy("has_dye", has(Tags.Items.DYES_BLACK))
                .save(pFinishedRecipeConsumer, "arcana:scribingtoolscraft2");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.scribingTools.get()).group(inkwellGroup)
                .requires(ModItems.scribingTools.get())
                .requires(Tags.Items.DYES_BLACK)
                .unlockedBy("has_dye", has(ModItems.scribingTools.get()))
                .save(pFinishedRecipeConsumer, "arcana:scribingtoolsrefill");
    }

    public static void compileGroups(Level level) {
        for (Recipe<?> recipe : level.getRecipeManager().getRecipes()) {
            if (recipe != null) {
                String group = recipe.getGroup();
                if (group.trim().isEmpty()) {
                    continue;
                }
                if (recipe.getId().getNamespace().equals("minecraft")) {
                    continue;
                }
                if (!ModRecipeProvider.recipeGroups.containsKey(group)) {
                    ModRecipeProvider.recipeGroups.put(group, new ArrayList<>());
                }
                ArrayList<ResourceLocation> list = ModRecipeProvider.recipeGroups.get(group);
                list.add(recipe.getId());
            }
        }
        for (ResourceLocation reg : CommonInternals.craftingRecipeCatalog.keySet()) {
            IResearchRecipe recipe2 = CommonInternals.craftingRecipeCatalog.get(reg);
            if (recipe2 != null) {
                String group = recipe2.getGroup();
                if (group.trim().isEmpty()) {
                    continue;
                }
                if (!ModRecipeProvider.recipeGroups.containsKey(group)) {
                    ModRecipeProvider.recipeGroups.put(group, new ArrayList<>());
                }
                ArrayList<ResourceLocation> list = ModRecipeProvider.recipeGroups.get(group);
                list.add(reg);
            }
        }
        for (ResourceLocation reg : CommonInternals.craftingRecipeCatalogFake.keySet()) {
            Object recipe3 = CommonInternals.craftingRecipeCatalogFake.get(reg);
            if (recipe3 != null) {
                String group = "";
                if (recipe3 instanceof CraftingRecipe) {
                    group = ((CraftingRecipe) recipe3).getGroup();
                } else if (recipe3 instanceof IResearchRecipe) {
                    group = ((IResearchRecipe) recipe3).getGroup();
                }
                if (group.trim().isEmpty()) {
                    continue;
                }
                if (!ModRecipeProvider.recipeGroups.containsKey(group)) {
                    ModRecipeProvider.recipeGroups.put(group, new ArrayList<>());
                }
                ArrayList<ResourceLocation> list = ModRecipeProvider.recipeGroups.get(group);
                list.add(reg);
            }
        }
    }
}
