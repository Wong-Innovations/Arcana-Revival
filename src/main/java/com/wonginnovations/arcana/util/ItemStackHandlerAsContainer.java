package com.wonginnovations.arcana.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemStackHandlerAsContainer implements Container {
	
	private ItemStackHandler handler;
	public Runnable onDirty;
	
	public ItemStackHandlerAsContainer(@Nonnull ItemStackHandler handler, @Nullable Runnable onDirty) {
		this.handler = handler;
		this.onDirty = onDirty;
	}

	@Override
	public int getContainerSize() {
		return handler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < handler.getSlots(); i++) {
			if (!handler.getStackInSlot(i).isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return handler.getStackInSlot(slot);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return handler.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return handler.extractItem(index, handler.getStackInSlot(index).getCount(), false);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (stack.getCount() > getMaxStackSize()) {
			ItemStack in = stack.copy();
			in.setCount(getMaxStackSize());
			stack.setCount(stack.getCount() - getMaxStackSize());
			handler.setStackInSlot(index, in);
		} else {
			handler.setStackInSlot(index, stack);
		}
	}

	@Override
	public void setChanged() {
		if (onDirty != null) {
			onDirty.run();
		}
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return true;
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < handler.getSlots() - 1; i++) {
			handler.getStackInSlot(i).setCount(0);
		}
	}

}
