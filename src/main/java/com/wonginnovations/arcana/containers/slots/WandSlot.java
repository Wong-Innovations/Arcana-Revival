package com.wonginnovations.arcana.containers.slots;

import com.wonginnovations.arcana.items.GauntletItem;
import com.wonginnovations.arcana.items.MagicDeviceItem;
import com.wonginnovations.arcana.items.StaffItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WandSlot extends Slot {
	private final IWandSlotListener listener;
	public WandSlot(IWandSlotListener listener, Container inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		this.listener = listener;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		Item item = stack.getItem();
		return item instanceof MagicDeviceItem && !(item instanceof GauntletItem) && !(item instanceof StaffItem);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		listener.onWandSlotUpdate();
	}
}
