package com.wonginnovations.arcana.items.recipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.blocks.entities.CrucibleBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AlchemyInventory implements Container {
	
	ItemStack stack = ItemStack.EMPTY;
	CrucibleBlockEntity crucible;
	Player crafter;
	
	public AlchemyInventory(CrucibleBlockEntity crucible, Player crafter) {
		this.crucible = crucible;
		this.crafter = crafter;
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty() && crucible.getAspectStackMap().isEmpty();
	}

	@Override
	public ItemStack getItem(int index) {
		return index == 0 ? stack : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return index == 0 ? stack.split(count) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack result = ItemStack.EMPTY;
		if (index == 0) {
			result = stack;
			stack = ItemStack.EMPTY;
		}
		return result;
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (index == 0)
			this.stack = stack;
	}

	@Override
	public void setChanged() {
		crucible.setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}
	
	public Map<Aspect, AspectStack> getAspectMap() {
		return crucible.getAspectStackMap();
	}
	
	public Player getCrafter() {
		return crafter;
	}

	@Override
	public void clearContent() {
		stack = ItemStack.EMPTY;
		// eww
		crucible.getAspectStackMap().clear();
	}
}
