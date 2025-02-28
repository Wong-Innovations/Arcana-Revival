package arcana.common.lib.crafting;

import arcana.api.capabilities.ModCapabilities;
import arcana.api.crafting.CrucibleRecipe;
import arcana.api.crafting.IResearchRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import arcana.api.ArcanaApi;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectHelper;
import arcana.api.aspects.AspectList;
import arcana.api.aspects.IEssentiaContainerItem;
import arcana.api.crafting.IArcaneRecipe;
import arcana.api.crafting.ShapedArcaneRecipe;
import arcana.api.internal.CommonInternals;
import arcana.common.lib.utils.InventoryUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class CraftingManager {

    public static CrucibleRecipe findMatchingCrucibleRecipe(Player player, AspectList aspects, ItemStack lastDrop) {
        int highest = 0;
        CrucibleRecipe out = null;

        for (IResearchRecipe re : ArcanaApi.getCraftingRecipes().values()) {
            if (re instanceof CrucibleRecipe recipe) {
                ItemStack temp = lastDrop.copy();
                temp.setCount(1);
                if (player != null && ModCapabilities.knowsResearchStrict(player, recipe.getResearch()) && recipe.matches(aspects, temp)) {
                    int result = recipe.getAspects().visSize();
                    if (result > highest) {
                        highest = result;
                        out = recipe;
                    }
                }
            }
        }

        return out;
    }

    public static IArcaneRecipe findMatchingArcaneRecipe(CraftingContainer matrix, Player player) {
        for (int i = 0; i < 15; ++i) {
            matrix.getItem(i);
        }
        Optional<ShapedArcaneRecipe> optional = player.level().getRecipeManager().getRecipeFor(ShapedArcaneRecipe.Type.INSTANCE, matrix, player.level());
        return optional.orElse(null);
    }

    public static AspectList getObjectTags(ItemStack itemstack) {
        return getObjectTags(itemstack, null);
    }

    private static AspectList capAspects(AspectList sourcetags, int amount) {
        if (sourcetags == null) {
            return sourcetags;
        }
        AspectList out = new AspectList();
        for (Aspect aspect : sourcetags.getAspects()) {
            if (aspect != null) {
                out.merge(aspect, Math.min(amount, sourcetags.getAmount(aspect)));
            }
        }
        return out;
    }

    private static AspectList getBonusTags(ItemStack itemstack, AspectList sourcetags) {
        AspectList tmp = new AspectList();
        if (itemstack.isEmpty()) {
            return tmp;
        }
        Item item = itemstack.getItem();
        if (item instanceof IEssentiaContainerItem && !((IEssentiaContainerItem) item).ignoreContainedAspects()) {
            if (sourcetags != null) {
                sourcetags.aspects.clear();
            }
            tmp = ((IEssentiaContainerItem) item).getAspects(itemstack);
            if (tmp != null && tmp.size() > 0) {
                for (Aspect tag : tmp.copy().getAspects()) {
                    if (tmp.getAmount(tag) <= 0) {
                        tmp.remove(tag);
                    }
                }
            }
        }
        if (tmp == null) {
            tmp = new AspectList();
        }
        if (sourcetags != null) {
            for (Aspect tag : sourcetags.getAspects()) {
                if (tag != null) {
                    tmp.add(tag, sourcetags.getAmount(tag));
                }
            }
        }
        if (tmp != null || item == Items.POTION) {
            if (item instanceof ArmorItem) {
                tmp.merge(Aspect.PROTECT, ((ArmorItem) item).getDefense() * 4);
            } else if (item instanceof SwordItem && ((SwordItem) item).getDamage() + 1.0f > 0.0f) {
                tmp.merge(Aspect.AVERSION, (int) (((SwordItem) item).getDamage() + 1.0f) * 4);
            } else if (item instanceof BowItem) {
                tmp.merge(Aspect.AVERSION, 10).merge(Aspect.FLIGHT, 5);
            } else if (item instanceof DiggerItem) {
                Tier mat = ((DiggerItem) item).getTier();
                for (Tier tm : Tiers.values()) {
                    if (tm.equals(mat)) {
                        tmp.merge(Aspect.TOOL, (tm.getLevel() + 1) * 4);
                    }
                }
            } else if (item instanceof ShearsItem || item instanceof HoeItem) {
                if (item.getMaxDamage() <= Tiers.WOOD.getUses()) {
                    tmp.merge(Aspect.TOOL, 4);
                } else if (item.getMaxDamage() <= Tiers.STONE.getUses() || item.getMaxDamage() <= Tiers.GOLD.getUses()) {
                    tmp.merge(Aspect.TOOL, 8);
                } else if (item.getMaxDamage() <= Tiers.IRON.getUses()) {
                    tmp.merge(Aspect.TOOL, 12);
                } else {
                    tmp.merge(Aspect.TOOL, 16);
                }
            }
        }
        return AspectHelper.cullTags(tmp);
    }

    public static AspectList getObjectTags(ItemStack itemstack, ArrayList<String> history) {
        if (itemstack.isEmpty()) {
            return null;
        }
        int ss = CommonInternals.generateUniqueItemstackId(itemstack);
        AspectList tmp = CommonInternals.objectTags.get(ss);
//        AspectList tmp = CommonInternals.objectTags.get(ss);
        if (tmp == null) {
            try {
                ItemStack sc = itemstack.copy();
                sc.setDamageValue(sc.getMaxDamage());
                ss = CommonInternals.generateUniqueItemstackId(sc);
                tmp = CommonInternals.objectTags.get(ss);
                if (tmp == null) {
                    if (itemstack.getDamageValue() == itemstack.getMaxDamage()) {
                        int index = 0;
                        do {
                            sc.setDamageValue(index);
                            ss = CommonInternals.generateUniqueItemstackId(sc);
                            tmp = CommonInternals.objectTags.get(ss);
                        } while (++index < 16 && tmp == null);
                    }
                    if (tmp == null) {
                        sc = itemstack.copy();
                        ss = CommonInternals.generateUniqueItemstackIdStripped(sc);
                        tmp = CommonInternals.objectTags.get(ss);
                        if (tmp == null) {
                            sc = itemstack.copy();
                            sc.setDamageValue(sc.getMaxDamage());
                            ss = CommonInternals.generateUniqueItemstackIdStripped(sc);
                            tmp = CommonInternals.objectTags.get(ss);
                        }
                    }
                    if (tmp == null) {
                        tmp = generateTags(itemstack, history);
                    }
                }
            } catch (Exception ignored) {}
        }
        return capAspects(getBonusTags(itemstack, tmp), 500);
    }

    public static AspectList generateTags(ItemStack is, ArrayList<String> history) {
        if (history == null) {
            history = new ArrayList<>();
        }
        ItemStack stack = is.copy();
        stack.setCount(1);
        if (ArcanaApi.exists(stack)) {
            return getObjectTags(stack, history);
        }
        String ss = stack.serializeNBT().toString();
        if (history.contains(ss)) {
            return null;
        }
        history.add(ss);
        if (history.size() < 100) {
            if (stack.getDamageValue() == Short.MAX_VALUE) {
                stack.setDamageValue(0);
            }
            AspectList ret = generateTagsFromRecipes(stack, history);
            ret = capAspects(ret, 500);
            ArcanaApi.registerObjectTag(is, ret);
            return ret;
        }
        return null;
    }

    private static AspectList generateTagsFromCraftingRecipes(ItemStack stack, ArrayList<String> history) {
        AspectList ret = null;
        int value = Integer.MAX_VALUE;
        for (CraftingRecipe recipe : Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {
            if (recipe != null && recipe.getResultItem(null) != null && Item.getId(recipe.getResultItem(null).getItem()) > 0) {
                if (recipe.getResultItem(null).getItem() == null) {
                    continue;
                }
                int idR = (recipe.getResultItem(null).getDamageValue() == Short.MAX_VALUE) ? 0 : recipe.getResultItem(null).getDamageValue();
                int idS = (stack.getDamageValue() == Short.MAX_VALUE) ? 0 : stack.getDamageValue();
                if (recipe.getResultItem(null).getItem() != stack.getItem() || idR != idS) {
                    continue;
                }
                ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();
                AspectList ph = new AspectList();
                int cval = 0;
                try {
                    ph = getAspectsFromIngredients(recipe.getIngredients(), recipe.getResultItem(null), recipe, history);
                    if (recipe instanceof IArcaneRecipe) {
                        IArcaneRecipe ar = (IArcaneRecipe) recipe;
                        if (ar.getVis() > 0) {
                            ph.add(Aspect.MAGIC, (int) (Math.sqrt(1 + (double) ar.getVis() / 2) / (float) recipe.getResultItem(null).getCount()));
                        }
                    }
                    for (Aspect as : ph.copy().getAspects()) {
                        if (ph.getAmount(as) <= 0) {
                            ph.remove(as);
                        }
                    }
                    if (ph.visSize() >= value || ph.visSize() <= 0) {
                        continue;
                    }
                    ret = ph;
                    value = ph.visSize();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    private static AspectList getAspectsFromIngredients(NonNullList<Ingredient> nonNullList, ItemStack recipeOut, CraftingRecipe recipe, ArrayList<String> history) {
        AspectList out = new AspectList();
        AspectList mid = new AspectList();
        NonNullList<ItemStack> exlist = NonNullList.create();
        if (recipe != null) {
            CraftingContainer inv = new TransientCraftingContainer(new ContainerFake(), 3, 3);
            int index = 0;
            for (Ingredient is : nonNullList) {
                if (InventoryUtils.getMatchingStacks(is).length > 0) {
                    inv.setItem(index, InventoryUtils.getMatchingStacks(is)[0]);
                }
                ++index;
            }
            exlist = recipe.getRemainingItems(inv);
        }
        int index2 = -1;
        for (Ingredient is2 : nonNullList) {
            ++index2;
            if (InventoryUtils.getMatchingStacks(is2).length <= 0) {
                continue;
            }
            AspectList obj = getObjectTags(InventoryUtils.getMatchingStacks(is2)[0], history);
            if (obj == null) {
                continue;
            }
            for (Aspect as : obj.getAspects()) {
                if (as != null) {
                    mid.add(as, obj.getAmount(as));
                }
            }
        }
        if (exlist != null) {
            for (ItemStack ri : exlist) {
                if (!ri.isEmpty()) {
                    AspectList obj = getObjectTags(ri, history);
                    for (Aspect as : obj.getAspects()) {
                        mid.reduce(as, obj.getAmount(as));
                    }
                }
            }
        }
        for (Aspect as2 : mid.getAspects()) {
            if (as2 != null) {
                float v = mid.getAmount(as2) * 0.75f / recipeOut.getCount();
                if (v < 1.0f && v > 0.75) {
                    v = 1.0f;
                }
                out.add(as2, (int) v);
            }
        }
        for (Aspect as2 : out.getAspects()) {
            if (out.getAmount(as2) <= 0) {
                out.remove(as2);
            }
        }
        return out;
    }

    private static AspectList generateTagsFromRecipes(ItemStack stack, ArrayList<String> history) {
        return generateTagsFromCraftingRecipes(stack, history);
    }
}
