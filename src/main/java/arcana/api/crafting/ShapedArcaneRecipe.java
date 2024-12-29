package arcana.api.crafting;

import arcana.common.lib.utils.Utils;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import arcana.api.ArcanaApiHelper;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.common.items.ModItems;

import java.util.Map;

public class ShapedArcaneRecipe extends ShapedRecipe implements IArcaneRecipe {
    private final String research;
    private final int vis;
    private final AspectList crystals;

    public ShapedArcaneRecipe(ResourceLocation pId, String pGroup, String res, int vis, AspectList crystals, int pWidth, int pHeight, NonNullList<Ingredient> pRecipeItems, ItemStack pResult) {
        super(pId, pGroup, CraftingBookCategory.MISC, pWidth, pHeight, pRecipeItems, pResult);
        this.research = res;
        this.vis = vis;
        this.crystals = crystals;
    }

    @Override
    public boolean matches(CraftingContainer pInv, @NotNull Level pLevel) {
        if (pInv.getContainerSize() < 15) {
            return false;
        }

        CraftingContainer dummy = new TransientCraftingContainer(new ContainerDummy(), 3, 3);
        for (int a = 0; a < 9; a++) {
            dummy.setItem(a, pInv.getItem(a));
        }

        if (crystals != null && pInv.getContainerSize() >= 15) {
            for (Aspect aspect : crystals.getAspects()) {
                ItemStack cs = ArcanaApiHelper.makeCrystal(aspect, crystals.getAmount(aspect));
                boolean b = false;
                for (int i = 0; i < 6; ++i) {
                    ItemStack itemstack1 = pInv.getItem(9 + i);
                    if (itemstack1.getItem() == ModItems.crystalEssence.get() && itemstack1.getCount() >= cs.getCount() && ItemStack.isSameItemSameTags(cs, itemstack1)) {
                        b = true;
                    }
                }
                if (!b) return false;
            }
        }

        return super.matches(dummy, pLevel);
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public int getVis() {
        return this.vis;
    }

    @Override
    public String getResearch() {
        return this.research;
    }

    @Override
    public AspectList getCrystals() {
        return this.crystals;
    }

    public static class Type implements RecipeType<ShapedArcaneRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "arcane_shaped";
    }

    public static class Serializer implements RecipeSerializer<ShapedArcaneRecipe> {
        private static final ResourceLocation NAME = new ResourceLocation("arcana", "arcane_shaped");

        @Override
        public @NotNull ShapedArcaneRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject pJson) {
            String s = GsonHelper.getAsString(pJson, "group", "");
            Map<String, Ingredient> map = Utils.keyFromJson(GsonHelper.getAsJsonObject(pJson, "key"));
            String[] astring = Utils.shrink(Utils.patternFromJson(GsonHelper.getAsJsonArray(pJson, "pattern")));
            int i = astring[0].length();
            int j = astring.length;

            NonNullList<Ingredient> nonnulllist = Utils.dissolvePattern(astring, map, i, j);

            ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));

            String res = GsonHelper.getAsString(pJson, "research", "");
            int vis = GsonHelper.getAsInt(pJson, "vis", 0);
            AspectList crystals = new AspectList();
            var crystalsObject = GsonHelper.getAsJsonObject(pJson, "crystals");
            var aspects = crystalsObject.keySet();
            for (var aspect : aspects) {
                crystals.add(Aspect.getAspect(aspect), GsonHelper.getAsInt(crystalsObject, aspect));
            }

            return new ShapedArcaneRecipe(pRecipeId, s, res, vis, crystals, i, j, nonnulllist, itemstack);
        }

        @Override
        public @Nullable ShapedArcaneRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int i = pBuffer.readVarInt();
            int j = pBuffer.readVarInt();
            String s = pBuffer.readUtf();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

            nonnulllist.replaceAll(ignored -> Ingredient.fromNetwork(pBuffer));

            ItemStack itemstack = pBuffer.readItem();

            String res = pBuffer.readUtf();
            int vis = pBuffer.readVarInt();
            int count = pBuffer.readVarInt();

            AspectList crystals = new AspectList();

            for (int k = 0; k < count; k++) {
                String tag = pBuffer.readUtf();
                int amount = pBuffer.readVarInt();
                crystals.add(Aspect.getAspect(tag), amount);
            }

            return new ShapedArcaneRecipe(pRecipeId, s, res, vis, crystals, i, j, nonnulllist, itemstack);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, ShapedArcaneRecipe pRecipe) {
            pBuffer.writeVarInt(pRecipe.getRecipeWidth());
            pBuffer.writeVarInt(pRecipe.getRecipeHeight());
            pBuffer.writeUtf(pRecipe.getGroup());

            for (Ingredient ingredient : pRecipe.getIngredients()) {
                ingredient.toNetwork(pBuffer);
            }

            pBuffer.writeItem(pRecipe.getResultItem(null));

            pBuffer.writeUtf(pRecipe.research);
            pBuffer.writeVarInt(pRecipe.vis);
            pBuffer.writeVarInt(pRecipe.crystals.size());
            for (Aspect aspect : pRecipe.crystals.getAspects()) {
                pBuffer.writeUtf(aspect.getTag());
                pBuffer.writeVarInt(pRecipe.crystals.getAmount(aspect));
            }
        }
    }
}
