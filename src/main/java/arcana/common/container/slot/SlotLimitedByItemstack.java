package arcana.common.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotLimitedByItemstack extends Slot {
    ItemStack limitItem;

    public SlotLimitedByItemstack(ItemStack item, Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
        limitItem = item;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack pStack) {
        return ItemStack.isSameItem(pStack, limitItem);
    }
}
