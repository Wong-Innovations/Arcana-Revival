package com.wonginnovations.arcana.containers;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.entities.AspectCrystallizerBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectCrystallizerMenu extends AbstractContainerMenu {
	
	public final Container inventory;
	public final Inventory playerInventory;
	public AspectCrystallizerBlockEntity te;
	
	public AspectCrystallizerMenu(int id, Container inventory, Inventory playerInventory) {
		super(ArcanaMenus.ASPECT_CRYSTALLIZER.get(), id);
		this.inventory = inventory;
		this.playerInventory = playerInventory;
		addOwnSlots(inventory);
		addPlayerSlots(playerInventory);
		if (inventory instanceof AspectCrystallizerBlockEntity)
			te = (AspectCrystallizerBlockEntity)inventory;
	}

	@Override
	public boolean stillValid(Player player) {
		return inventory.stillValid(player);
	}
	
	private void addPlayerSlots(Inventory playerInventory) {
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
		
		for (int k = 0; k < 9; ++k)
			addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
	}
	
	private void addOwnSlots(Container slots) {
		// Crystal @ 108,35
		addSlot(new Slot(slots, 0, 108, 35) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}
		});
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < 9) {
				if (!moveItemStackTo(itemstack1, 1, 37, true))
					return ItemStack.EMPTY;
			} else if (!moveItemStackTo(itemstack1, 0, 1, false))
				return ItemStack.EMPTY;
			if (itemstack1.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
			if (itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;
			slot.onTake(playerIn, itemstack1);
		}
		
		return itemstack;
	}
}