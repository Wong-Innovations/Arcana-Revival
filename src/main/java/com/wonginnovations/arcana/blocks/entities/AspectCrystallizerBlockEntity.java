package com.wonginnovations.arcana.blocks.entities;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.handlers.AspectBattery;
import com.wonginnovations.arcana.aspects.handlers.AspectHandlerCapability;
import com.wonginnovations.arcana.aspects.handlers.AspectHolder;
import com.wonginnovations.arcana.containers.AspectCrystallizerMenu;
import com.wonginnovations.arcana.items.CrystalItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectCrystallizerBlockEntity extends BaseContainerBlockEntity implements BlockEntityTicker<AspectCrystallizerBlockEntity>, WorldlyContainer {

	protected NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
	protected LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.values());
	private static final int[] SLOTS_DOWN = new int[]{0};
//	private static final int[] SLOT_OTHER = new int[]{};
	
	public static final int MAX_PROGRESS = 80;
	
	public AspectBattery vis = new AspectBattery(/*1, 100*/);
	public int progress = 0;
	
	public AspectCrystallizerBlockEntity(BlockPos pos, BlockState state) {
		super(ArcanaBlockEntities.ASPECT_CRYSTALLIZER.get(), pos, state);
		vis.initHolders(100, 1);
	}
	
	public void tick(Level level, BlockPos pos, BlockState state, AspectCrystallizerBlockEntity entity) {
		AspectHolder holder = vis.getHolder(0);
		if (holder.getStack().getAmount() > 0
				&& ((getItem(0).getItem() instanceof CrystalItem && ((CrystalItem)getItem(0).getItem()).aspect == holder.getStack().getAspect() && getItem(0).getCount() < getMaxStackSize())
				|| ((getItem(0).isEmpty())))) {
			if (progress >= MAX_PROGRESS) {
				progress = 0;
				if (getItem(0).isEmpty())
					setItem(0, new ItemStack(AspectUtils.aspectCrystalItems.get(holder.getStack().getAspect()).get()));
				else
					getItem(0).grow(1);
				holder.drain(1, false);
			}
			progress++;
		} else if (progress > 0)
			progress--;
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		ContainerHelper.saveAllItems(compound, items);
		CompoundTag aspectsNbt = vis.serializeNBT();
		compound.put("aspects", aspectsNbt);
		compound.putInt("progress", progress);
		super.saveAdditional(compound);
	}

	@Override
	public void load(CompoundTag compound) {
		ContainerHelper.loadAllItems(compound, items);
		vis.deserializeNBT(compound.getCompound("aspects"));
		progress = compound.getInt("progress");
		super.load(compound);
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
		if (cap == AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY)
			return vis.getCapability(AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY).cast();
		return LazyOptional.empty();
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (!this.remove && side != null && cap == ForgeCapabilities.ITEM_HANDLER) {
			return handlers[side.ordinal()].cast();
		}
		return getCapability(cap);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.aspect_crystallizer");
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory player) {
		return new AspectCrystallizerMenu(id, this, player);
	}

	@Override
	public int getContainerSize() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		return items.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getItem(int index) {
		return items.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return ContainerHelper.removeItem(items, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(items, index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		items.set(index, stack);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
        return index == 0;
    }

	@Override
	public boolean stillValid(Player player) {
		if (level == null || level.getBlockEntity(getBlockPos()) != this)
			return false;
		return player.distanceToSqr(getBlockPos().getX() + .5, getBlockPos().getY() + .5, getBlockPos().getZ() + .5) <= 64;
	}

	@Override
	public void clearContent() {
		items.clear();
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return SLOTS_DOWN;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
		if (direction != Direction.DOWN) return false;
		return canPlaceItem(index, itemStack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return index == 0;
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

	@Override
	public void setRemoved() {
		super.setRemoved();
		for (LazyOptional<? extends IItemHandler> handler : handlers) {
			handler.invalidate();
		}
	}
}