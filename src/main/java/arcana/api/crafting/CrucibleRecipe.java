package arcana.api.crafting;

import arcana.api.ArcanaApiHelper;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.internal.CommonInternals;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CrucibleRecipe implements IResearchRecipe, Recipe<SimpleContainer> {
    private ItemStack recipeOutput;
    private Ingredient catalyst;
    private AspectList aspects;
    private String research;
    private String name;
    private String group = "";
    private final ResourceLocation id;

    public CrucibleRecipe(ResourceLocation pRecipeId, String researchKey, ItemStack result, Object catalyst, AspectList tags) {
        this.id = pRecipeId;
        this.recipeOutput = result;
        this.name = "";
        this.setAspects(tags);
        this.research = researchKey;
        this.setCatalyst(ArcanaApiHelper.getIngredient(catalyst));
        if (this.getCatalyst() == null) {
            throw new RuntimeException("Invalid crucible recipe catalyst: " + catalyst);
        }
    }

    public boolean matches(AspectList itags, ItemStack cat) {
        if (!this.getCatalyst().test(cat)) {
            return false;
        } else if (itags == null) {
            return false;
        } else {
            for (Aspect tag : this.getAspects().getAspects()) {
                if (itags.getAmount(tag) < this.getAspects().getAmount(tag)) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean catalystMatches(ItemStack cat) {
        return this.getCatalyst().test(cat);
    }

    public AspectList removeMatching(AspectList itags) {
        AspectList temptags = new AspectList();
        temptags.aspects.putAll(itags.aspects);

        for (Aspect tag : this.getAspects().getAspects()) {
            temptags.remove(tag, this.getAspects().getAmount(tag));
        }

        itags = temptags;
        return itags;
    }

    public ItemStack getRecipeOutput() {
        return this.recipeOutput;
    }

    public String getResearch() {
        return this.research;
    }

    public Ingredient getCatalyst() {
        return this.catalyst;
    }

    public void setCatalyst(Ingredient catalyst) {
        this.catalyst = catalyst;
    }

    public AspectList getAspects() {
        return this.aspects;
    }

    public void setAspects(AspectList aspects) {
        this.aspects = aspects;
    }

    @Override
    public boolean matches(@NotNull SimpleContainer pContainer, @NotNull Level pLevel) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer pContainer, @NotNull RegistryAccess pRegistryAccess) {
        return this.recipeOutput;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess pRegistryAccess) {
        return recipeOutput;
    }

    public @NotNull String getGroup() {
        return this.group;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<CrucibleRecipe> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public CrucibleRecipe setGroup(ResourceLocation s) {
        this.group = s.toString();
        return this;
    }

    public static class Type implements RecipeType<CrucibleRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "crucible";
    }

    public static class Serializer implements RecipeSerializer<CrucibleRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull CrucibleRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject pSerializedRecipe) {
            String group = GsonHelper.getAsString(pSerializedRecipe, "group", "");
            String research = GsonHelper.getAsString(pSerializedRecipe, "research", "");
            ItemStack itemstack = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pSerializedRecipe, "result"), true, true);
            AspectList vis = new AspectList();
            var visObject = GsonHelper.getAsJsonObject(pSerializedRecipe, "vis");
            var aspects = visObject.keySet();
            for (var aspect : aspects) {
                vis.add(Aspect.getAspect(aspect), GsonHelper.getAsInt(visObject, aspect, 0));
            }
            Object catalyst;
            if (pSerializedRecipe.has("catalyst")) {
                catalyst = VanillaIngredientSerializer.INSTANCE.parse(pSerializedRecipe.getAsJsonObject("catalyst"));
//                catalyst = Ingredient.fromJson(pSerializedRecipe.get("catalyst"));
            } else if (pSerializedRecipe.has("nbt_catalyst")) {
                catalyst = NBTIngredient.fromJson(pSerializedRecipe.get("nbt_catalyst"));
            } else {
                throw new JsonSyntaxException("CrucibleRecipe catalyst not found. CrucibleRecipe json must have a \"catalyst\" or \"nbt_catalyst\".");
            }

            CrucibleRecipe recipe = new CrucibleRecipe(pRecipeId, research, itemstack, catalyst, vis).setGroup(new ResourceLocation(group));
            CommonInternals.craftingRecipeCatalog.put(pRecipeId, recipe);
            return recipe;
        }

        @Override
        public @Nullable CrucibleRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, @NotNull FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();
            String research = pBuffer.readUtf();
            ItemStack result = pBuffer.readItem();
            int aspectCount = pBuffer.readVarInt();
            AspectList aspects = new AspectList();
            for (int k = 0; k < aspectCount; k++) {
                String tag = pBuffer.readUtf();
                int amount = pBuffer.readVarInt();
                aspects.add(Aspect.getAspect(tag), amount);
            }
            String catalystType = pBuffer.readUtf();
            if (catalystType.equals("nbt_catalyst")) {
                NBTIngredient.fromNetwork(pBuffer);
            } else if (catalystType.equals("catalyst")) {
                Ingredient.fromNetwork(pBuffer);
            }
            return new CrucibleRecipe(pRecipeId, research, result, null, aspects).setGroup(new ResourceLocation(group));
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf pBuffer, @NotNull CrucibleRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.getGroup());
            pBuffer.writeUtf(pRecipe.research);
            pBuffer.writeItem(pRecipe.getResultItem(null));
            pBuffer.writeVarInt(pRecipe.aspects.size());
            for (Aspect aspect : pRecipe.aspects.getAspects()) {
                pBuffer.writeUtf(aspect.getTag());
                pBuffer.writeVarInt(pRecipe.aspects.getAmount(aspect));
            }
            if (pRecipe.getCatalyst() instanceof NBTIngredient) {
                ((NBTIngredient) pRecipe.getCatalyst()).writeNetwork(pBuffer);
            } else if (pRecipe.getCatalyst() instanceof Ingredient) {
                pRecipe.getCatalyst().toNetwork(pBuffer);
            }
        }
    }

}
