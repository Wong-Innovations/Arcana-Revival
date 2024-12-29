package arcana.common.items.consumables;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.NotNull;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.aspects.IEssentiaContainerItem;
import arcana.common.items.ModItems;
import arcana.common.items.ItemEssentiaContainer;

public class ItemPhial extends ItemEssentiaContainer {
    public ItemPhial() {
        super(10);
    }

    public static ItemStack makePhial(Aspect aspect, int amt) {
        ItemStack i = new ItemStack(ModItems.phial.get(), 1);
        ((IEssentiaContainerItem) i.getItem()).setAspects(i, new AspectList().add(aspect, amt));
        return i;
    }

    public static ItemStack makeFilledPhial(Aspect aspect) {
        return makePhial(aspect, 10);
    }

//    @Override
//    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
//        if (/* tab == ConfigItems.TABTC || */tab == CreativeModeTabs.searchTab()) {
//            items.add(new ItemStack(this, 1));
//            for (Aspect tag : Aspect.aspects.values()) {
//                ItemStack i = new ItemStack(this, 1);
//                setAspects(i, new AspectList().add(tag, base));
//                items.add(i);
//            }
//        }
//    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        return (getAspects(pStack) != null && !getAspects(pStack).aspects.isEmpty()) ? Component.translatable(getDescriptionId(pStack), getAspects(pStack).getAspects()[0].getName()) : super.getName(pStack);
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull ItemStack pStack) {
        return (getAspects(pStack) != null && !getAspects(pStack).aspects.isEmpty()) ? super.getDescriptionId(pStack) + ".filled" : super.getDescriptionId(pStack);
    }

    @Override
    public @NotNull String getDescriptionId() {
        return super.getDescriptionId();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack pStack, Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {

    }

    @Override
    public void onCraftedBy(@NotNull ItemStack pStack, Level pLevel, @NotNull Player pPlayer) {

    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return true;
    }
}
