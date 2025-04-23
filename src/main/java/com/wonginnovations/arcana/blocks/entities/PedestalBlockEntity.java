package com.wonginnovations.arcana.blocks.entities;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PedestalBlockEntity extends BlockEntity {
	
	public PedestalBlockEntity(BlockPos pos, BlockState state) {
		super(ArcanaBlockEntities.PEDESTAL.get(), pos, state);
	}
	
	protected ItemStackHandler items = new ItemStackHandler(1) {
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			setChanged();
		}
	};
	
	public ItemStack getItem() {
		return items.getStackInSlot(0);
	}
	
	public void setItem(ItemStack stack) {
		items.setStackInSlot(0, stack);
	}
	
	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		if (compound.contains("items"))
			items.deserializeNBT(compound.getCompound("items"));
	}
	
	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.put("items", items.serializeNBT());
	}
	
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag);
		return tag;
	}
	
	public AABB getRenderBoundingBox() {
		return new AABB(getBlockPos(), getBlockPos().offset(1, 2, 1));
	}
}