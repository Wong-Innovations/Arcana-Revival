package arcana.common.container.slot;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import arcana.api.ArcanaApiHelper;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.crafting.ContainerDummy;
import arcana.api.crafting.IArcaneRecipe;
import arcana.api.crafting.ShapedArcaneRecipe;
import arcana.common.items.ModItems;
import arcana.common.blockentities.crafting.BlockEntityArcaneWorkbench;
import arcana.common.lib.crafting.CraftingManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SlotCraftingArcaneWorkbench extends Slot {
    private final Player player;
    private final CraftingContainer craftMatrix;
    private int amountCrafted;
    private final BlockEntityArcaneWorkbench blockEntity;

    public SlotCraftingArcaneWorkbench(BlockEntityArcaneWorkbench te, Player player, CraftingContainer inventory, ResultContainer resultContainer, int pSlot, int pX, int pY) {
        super(resultContainer, pSlot, pX, pY);
        this.player = player;
        this.craftMatrix = inventory;
        this.blockEntity = te;
    }

    public boolean mayPlace(@NotNull ItemStack pStack) {
        return false;
    }

    @Override
    public @NotNull ItemStack remove(int pAmount) {
        if (this.hasItem()) {
            this.amountCrafted += Math.min(pAmount, this.getItem().getCount());
        }
        return super.remove(pAmount);
    }

    @Override
    protected void onQuickCraft(@NotNull ItemStack pStack, int pAmount) {
        this.amountCrafted += pAmount;
        this.checkTakeAchievements(pStack);
    }

    @Override
    protected void onSwapCraft(int pNumItemsCrafted) {
        this.amountCrafted += pNumItemsCrafted;
    }

    @Override
    protected void checkTakeAchievements(@NotNull ItemStack pStack) {
        if (this.amountCrafted > 0) {
            pStack.onCraftedBy(this.player.level(), this.player, this.amountCrafted);
            net.minecraftforge.event.ForgeEventFactory.firePlayerCraftingEvent(this.player, pStack, this.craftMatrix);
        }
        amountCrafted = 0;
        ResultContainer inventorycraftresult = (ResultContainer) container;
        Recipe<?> irecipe = inventorycraftresult.getRecipeUsed();
        if (irecipe != null) {
            ((RecipeHolder) this.container).awardUsedRecipes(this.player, List.of(pStack));
            inventorycraftresult.setRecipeUsed(null);
        }

        this.amountCrafted = 0;
    }

    @Override
    public void onTake(@NotNull Player pPlayer, @NotNull ItemStack pStack) {
        checkTakeAchievements(pStack);
        IArcaneRecipe recipe = CraftingManager.findMatchingArcaneRecipe(craftMatrix, pPlayer);
        CraftingContainer ic = craftMatrix;
        ForgeHooks.setCraftingPlayer(pPlayer);
        NonNullList<ItemStack> nonnulllist;
        if (recipe != null) {
            nonnulllist = pPlayer.level().getRecipeManager().getRemainingItemsFor(ShapedArcaneRecipe.Type.INSTANCE, craftMatrix, pPlayer.level());
        } else {
            ic = new TransientCraftingContainer(new ContainerDummy(), 3, 3);
            for (int a = 0; a < 9; ++a) {
                ic.setItem(a, craftMatrix.getItem(a));
            }
            nonnulllist = pPlayer.level().getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, ic, pPlayer.level());
        }
        ForgeHooks.setCraftingPlayer(null);
        int vis = 0;
        AspectList crystals = null;
        if (recipe != null) {
            vis = recipe.getVis();
            crystals = recipe.getCrystals();
            if (vis > 0) {
                blockEntity.getAura();
                blockEntity.spendAura(vis);
            }
        }
        for (int i = 0; i < Math.min(9, nonnulllist.size()); ++i) {
            ItemStack itemstack = ic.getItem(i);
            ItemStack itemstack2 = nonnulllist.get(i);
            if (!itemstack.isEmpty()) {
                craftMatrix.removeItem(i, 1);
                itemstack = ic.getItem(i);
            }
            if (!itemstack2.isEmpty()) {
                if (itemstack.isEmpty()) {
                    craftMatrix.setItem(i, itemstack2);
                } else if (ItemStack.matches(itemstack, itemstack2) && ItemStack.isSameItemSameTags(itemstack, itemstack2)) {
                    itemstack2.grow(itemstack.getCount());
                    craftMatrix.setItem(i, itemstack2);
                } else if (!player.getInventory().add(itemstack2)) {
                    player.drop(itemstack2, false);
                }
            }
        }
        if (crystals != null) {
            for (Aspect aspect : crystals.getAspects()) {
                ItemStack cs = ArcanaApiHelper.makeCrystal(aspect, crystals.getAmount(aspect));
                for (int j = 0; j < 6; ++j) {
                    ItemStack itemstack3 = craftMatrix.getItem(9 + j);
                    if (itemstack3.getItem() == ModItems.crystalEssence.get() && ItemStack.isSameItemSameTags(cs, itemstack3)) {
                        craftMatrix.removeItem(9 + j, cs.getCount());
                    }
                }
            }
        }
    }
}
