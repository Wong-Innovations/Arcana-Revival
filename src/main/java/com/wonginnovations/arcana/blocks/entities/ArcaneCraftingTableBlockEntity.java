package com.wonginnovations.arcana.blocks.entities;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.containers.ArcaneCraftingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArcaneCraftingTableBlockEntity extends BaseContainerBlockEntity {

	protected NonNullList<ItemStack> items = NonNullList.withSize(11, ItemStack.EMPTY);

	public ArcaneCraftingTableBlockEntity(BlockPos pos, BlockState state) {
		super(ArcanaBlockEntities.ARCANE_WORKBENCH.get(), pos, state);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.arcane_workbench");
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory player) {
		return new ArcaneCraftingTableMenu(id,player,this);
	}

	@Override
	public int getContainerSize() {
		return this.items.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.items) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getItem(int index) {
		return this.items.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return ContainerHelper.removeItem(this.items, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(this.items, index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.items.set(index, stack);
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void clearContent() {
		items.clear();
	}

	@Override
	public void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		ContainerHelper.saveAllItems(nbt, items);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		ContainerHelper.loadAllItems(nbt, items);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag);
		return tag;
	}
}