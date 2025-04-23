package com.wonginnovations.arcana.containers;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.pipes.PumpBlockEntity;
import com.wonginnovations.arcana.items.CrystalItem;
import com.wonginnovations.arcana.items.EnchantedFilterItem;
import com.wonginnovations.arcana.util.ItemStackHandlerAsContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PumpMenu extends AbstractContainerMenu {
	
	public PumpBlockEntity te;
	public final Inventory playerInventory;
	
	public PumpMenu(int id, PumpBlockEntity te, Inventory inventory) {
		super(ArcanaMenus.PUMP.get(), id);
		this.te = te;
		this.playerInventory = inventory;
		ItemStackHandlerAsContainer in = new ItemStackHandlerAsContainer(te.inventory, te::setChanged);
		// Filter @ 57,34
		addSlot(new Slot(in, 0, 57, 34) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof EnchantedFilterItem;
			}
		});
		// Crystal @ 103,34
		addSlot(new Slot(in, 1, 103, 34) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof CrystalItem;
			}
		});
		addPlayerSlots(inventory);
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
	
	private void addPlayerSlots(Inventory inventory) {
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 9; ++j)
				addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
		
		for (int k = 0; k < 9; ++k)
			addSlot(new Slot(inventory, k, 8 + k * 18, 142));
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < 9) {
				if (!moveItemStackTo(itemstack1, 2, 37, true))
					return ItemStack.EMPTY;
			} else if (!moveItemStackTo(itemstack1, 0, 2, false))
				return ItemStack.EMPTY;
			if (itemstack1.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
			if (itemstack1.getCount() == itemstack.getCount())
				return ItemStack.EMPTY;
			slot.onTake(player, itemstack1);
		}
		
		return itemstack;
	}
}