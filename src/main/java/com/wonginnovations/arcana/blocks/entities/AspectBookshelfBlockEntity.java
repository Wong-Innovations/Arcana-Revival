package com.wonginnovations.arcana.blocks.entities;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.VisShareable;
import com.wonginnovations.arcana.aspects.handlers.AspectBattery;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.aspects.handlers.AspectHandlerCapability;
import com.wonginnovations.arcana.aspects.handlers.AspectHolder;
import com.wonginnovations.arcana.items.PhialItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectBookshelfBlockEntity extends BaseContainerBlockEntity implements BlockEntityTicker<AspectBookshelfBlockEntity>, VisShareable {

	private final int size = 9;
	private NonNullList<ItemStack> stacks = NonNullList.withSize(size, ItemStack.EMPTY);
	AspectBattery vis = new AspectBattery(/*9, 8*/);
	private double lastVis;
	public Direction rotation;
	
	public AspectBookshelfBlockEntity(BlockPos pos, BlockState state) {
		super(ArcanaBlockEntities.ASPECT_SHELF.get(), pos, state);
		vis.initHolders(8, 9);
	}
	
	public AspectBookshelfBlockEntity(BlockPos pos, BlockState state, Direction rotation) {
		this(pos, state);
		this.rotation = rotation;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return this.getItem(index).isEmpty();
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack itemstack = ContainerHelper.removeItem(this.stacks, slot, amount);
		if (!itemstack.isEmpty()) {
			this.setChanged();
		}
		return itemstack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack itemstack = this.stacks.get(slot);
		if (itemstack.isEmpty()) {
			return ItemStack.EMPTY;
		} else {
			this.stacks.set(slot, ItemStack.EMPTY);
			return itemstack;
		}
	}

	public int getVisTotal() {
		int vis = 0;
		for (ItemStack stack : stacks)
			if (stack.getItem() instanceof PhialItem)
				vis += (int) ((PhialItem)stack.getItem()).getAspectStacks(stack).get(0).getAmount();
		return vis;
	}

	@Override
	public int getContainerSize() {
		return size;
	}
	
	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return true;
	}

	@Override
	public ItemStack getItem(int slot) {
		return slot >= 0 && slot < this.stacks.size() ? this.stacks.get(slot) : ItemStack.EMPTY;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		this.stacks.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		this.setChanged();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : this.stacks) {
			if (!itemstack.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.aspectbookshelf");
	}
	
	protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return new DispenserMenu(id, inventory, this);
	}
	
	public int getRedstoneOut() {
		float vis;
		vis = getVisTotal();
		return (int)((vis / 72F) * 15);
	}
	
	protected NonNullList<ItemStack> getItems() {
		return this.stacks;
	}
	
	protected void setItems(NonNullList<ItemStack> itemsIn) {
		this.stacks = itemsIn;
	}
	
	@Override
	public void tick(Level level, BlockPos pos, BlockState state, AspectBookshelfBlockEntity entity) {
		double newVis = vis.countHolders();
		if (level != null && lastVis != newVis && !level.isClientSide) {
			level.updateNeighbourForOutputSignal(getBlockPos(), level.getBlockState(getBlockPos()).getBlock());
		}
		lastVis = newVis;
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		CompoundTag aspectsNbt = vis.serializeNBT();
		compound.put("aspects", aspectsNbt);
		ContainerHelper.saveAllItems(compound, this.stacks);
//		if (!this.checkLootAndWrite(compound)) {
//			ContainerHelper.saveAllItems(compound, this.stacks);
//		}
	}

	@Override
	public void load(CompoundTag compound) {
		vis.deserializeNBT(compound.getCompound("aspects"));
		this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(compound, this.stacks);
//		if (!this.checkLootAndRead(compound)) {
//			ContainerHelper.loadAllItems(compound, this.stacks);
//		}
		super.load(compound);
	}
	
	public AspectBattery updateBatteryAndReturn() {
		vis.initHolders(8, 9);
		for (int i = stacks.size() - 1; i >= 0; i--) {
			if (stacks.get(i).getItem() instanceof PhialItem) {
				AspectBattery aspectBattery = (AspectBattery)AspectHandler.getFrom(stacks.get(i));
				AspectHolder target;
				if (aspectBattery != null) {
					target = aspectBattery.getHolder(0);
					vis.getHolders().set(i, target);
				}
			} else if (vis.hasHolder(i))
				vis.getHolders().remove(i);
		}
		return vis;
	}
	
	
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
		if (cap == AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY) {
			return updateBatteryAndReturn().getCapability(AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY).cast();
		} else {
			return updateBatteryAndReturn().getCapability(cap, null);
		}
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		return this.getCapability(cap);
	}
	
	public boolean addPhial(ItemStack stack, int slot) {
		if (this.stacks.get(slot).isEmpty()) {
			stack = stack.copy();
			stack.setCount(1);
			this.setItem(slot, stack);
			return true;
		}
		return false;
	}
	
	public ItemStack removePhial(int slot) {
		if (!this.stacks.get(slot).isEmpty()) {
			ItemStack removedPhial = this.stacks.get(slot);
			this.stacks.set(slot, ItemStack.EMPTY);
			return removedPhial;
		}
		return ItemStack.EMPTY;
	}
	
//	@Override
//	public void setInventorySlotContents(int index, ItemStack stack) {
//		this.setChanged();
//		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
//		super.setInventorySlotContents(index, stack);
//	}
	
	@Override
	@Nullable
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
//	@Override
//	public void onDataPacket(NetworkManager net, Packet<ClientGamePacketListener> pkt) {
//		load(getBlockState(), pkt.getNbtCompound());
//	}
	
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbtTagCompound = new CompoundTag();
		saveAdditional(nbtTagCompound);
		return nbtTagCompound;
	}
	
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
	}
	
	@Override
	public boolean isVisShareable() {
		return true;
	}
	
	@Override
	public boolean isManual() {
		return true;
	}
	
	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public void clearContent() {

	}
}