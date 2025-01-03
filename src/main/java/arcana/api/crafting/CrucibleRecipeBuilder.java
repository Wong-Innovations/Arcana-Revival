package arcana.api.crafting;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.common.config.ModRecipes;
import arcana.common.lib.utils.Utils;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class CrucibleRecipeBuilder implements RecipeBuilder {

    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private final ItemStack result;
    private Object catalyst;
    private String group;
    private String research;
    private AspectList vis = new AspectList();

    public CrucibleRecipeBuilder(ItemStack pResult) {
        result = pResult;
    }

    public CrucibleRecipeBuilder catalyst(Object pCatalyst) {
        this.catalyst = pCatalyst;
        return this;
    }

    public CrucibleRecipeBuilder research(String pResearch) {
        this.research = pResearch;
        return this;
    }

    public CrucibleRecipeBuilder aspects(@NotNull AspectList pVis) {
        this.vis = pVis;
        return this;
    }

    public CrucibleRecipeBuilder aspects(Aspect pAspect, int pAmount) {
        vis.add(pAspect, pAmount);
        return this;
    }

    @Override
    public @NotNull CrucibleRecipeBuilder unlockedBy(@NotNull String pCriterionName, @NotNull CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public @NotNull CrucibleRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    @Override
    public @NotNull Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(@NotNull Consumer<FinishedRecipe> pFinishedRecipeConsumer, @NotNull ResourceLocation pRecipeId) {
        pFinishedRecipeConsumer.accept(new Result(new CrucibleRecipe(pRecipeId, this.research, this.result, this.catalyst, this.vis).setGroup(new ResourceLocation(this.group)), this.advancement, new ResourceLocation(pRecipeId.getNamespace(), "recipes/" /* + this.result.getItemCategory().getRecipeFolderName() + "/" */ + pRecipeId.getPath())));
    }

    public static class Result implements FinishedRecipe {

        private final CrucibleRecipe recipe;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(CrucibleRecipe pRecipe, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId) {

            this.recipe = pRecipe;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
        }

        @Override
        public void serializeRecipeData(@NotNull JsonObject pJson) {
            if (!recipe.getGroup().isEmpty()) {
                pJson.addProperty("group", recipe.getGroup());
            }
            pJson.add("result", Utils.serializeItemStack(recipe.getResultItem(null)));
            pJson.addProperty("research", recipe.getResearch());
            JsonObject visObject = new JsonObject();
            for (Aspect aspect : recipe.getAspects().getAspects()) {
                visObject.addProperty(aspect.getTag(), recipe.getAspects().getAmount(aspect));
            }
            pJson.add("vis", visObject);
            pJson.add((recipe.getCatalyst() instanceof NBTIngredient)? "nbt_catalyst" : "catalyst", recipe.getCatalyst().toJson());
        }

        @Override
        public @NotNull ResourceLocation getId() {
            return this.recipe.getId();
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return ModRecipes.CRUCIBLE_RECIPE_SERIALIZER.get();
        }

        @Override
        public @Nullable JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Override
        public @Nullable ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }

}
