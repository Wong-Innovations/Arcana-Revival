package com.wonginnovations.arcana.items.recipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.containers.slots.WandSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectCraftingContainer extends TransientCraftingContainer {
	
	private final WandSlot wandSlot;
	private final Container deferred;
	private final AbstractContainerMenu eventHandler; // just store this twice
	private final Player crafter;
	
	public AspectCraftingContainer(AbstractContainerMenu eventHandler, WandSlot wandSlot, int width, int height, Container deferred, Player crafter) {
		super(eventHandler, width, height);
		this.eventHandler = eventHandler;
		this.wandSlot = wandSlot;
		this.deferred = deferred;
		this.crafter = crafter;
	}

	public WandSlot getWandSlot() {
		return wandSlot;
	}

	@Override
	public int getContainerSize() {
		return deferred.getContainerSize();
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return deferred.removeItemNoUpdate(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack itemstack = deferred.removeItem(index, count);
		if (!itemstack.isEmpty()) {
			this.eventHandler.slotsChanged(this);
		}
		return itemstack;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		deferred.setItem(index, stack);
		this.eventHandler.slotsChanged(this);
	}

	@Override
	public void setChanged() {
		deferred.setChanged();
	}

	@Override
	public void clearContent() {
		deferred.clearContent();
	}

	@Override
	public int getMaxStackSize() {
		return deferred.getMaxStackSize();
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return deferred.canPlaceItem(index, stack);
	}

	@Override
	public int countItem(Item item) {
		return deferred.countItem(item);
	}

	@Override
	public boolean hasAnyOf(Set<Item> set) {
		return deferred.hasAnyOf(set);
	}

	@Override
	public boolean isEmpty() {
		return deferred.isEmpty();
	}

	@Override
	public boolean stillValid(Player player) {
		return deferred.stillValid(player);
	}

	@Override
	public ItemStack getItem(int index) {
		return deferred.getItem(index);
	}
	
	public Player getCrafter() {
		return crafter;
	}

}