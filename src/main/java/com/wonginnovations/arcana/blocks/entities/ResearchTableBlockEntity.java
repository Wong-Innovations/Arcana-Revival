package com.wonginnovations.arcana.blocks.entities;

import io.netty.buffer.Unpooled;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.VisShareable;
import com.wonginnovations.arcana.aspects.handlers.AspectBattery;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.aspects.handlers.AspectHandlerCapability;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.containers.ResearchTableMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

import static com.wonginnovations.arcana.blocks.multiblocks.research_table.ResearchTableComponentBlock.PAPER;
import static com.wonginnovations.arcana.blocks.multiblocks.research_table.ResearchTableCoreBlock.FACING;
import static com.wonginnovations.arcana.blocks.multiblocks.research_table.ResearchTableCoreBlock.INK;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ResearchTableBlockEntity extends BaseContainerBlockEntity {
	ArrayList<BlockPos> visContainers = new ArrayList<>();
	AspectBattery battery = new AspectBattery(/*Integer.MAX_VALUE, 100*/);

	public boolean batteryIsDirty = true;

	public ResearchTableBlockEntity(BlockPos pos, BlockState state) {
		super(ArcanaBlockEntities.RESEARCH_TABLE.get(), pos, state);
	}

	// Three slots for wand (OR any AspectHandler, but research games can only be performed with a wand), ink, note
	// up to 9 for crafting guesswork for arcane crafting
	// up to, idk, 12 for arcane infusion
	// golemancy will be weird
	// so its ~15 max?

	// slots 0-2 are always there, the rest are reserved for the games themselves

	protected ItemStackHandler items = new ItemStackHandler(14) {
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			setChanged();
		}
	};

	public AspectBattery getVisBattery() {
		return getVisShareablesAsBattery();
	}

	//TODO: There is better way to do it
	private AspectBattery getVisShareablesAsBattery() {
		battery.clear();
		BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 7; y++) {
				for (int z = 0; z < 9; z++) {
					blockPos.set(getBlockPos());
					blockPos.move(x - 4, y - 2, z - 4);
					if (level.getBlockState(blockPos).hasBlockEntity()) {
						BlockEntity tileEntityInBox = level.getBlockEntity(blockPos);
						if (tileEntityInBox != null)
							if (tileEntityInBox instanceof VisShareable)
								if (((VisShareable)tileEntityInBox).isVisShareable() && ((VisShareable)tileEntityInBox).isManual()) {
									AspectBattery vis = (AspectBattery)AspectHandler.getFrom(tileEntityInBox);
									if (vis != null) {
										visContainers.add(new BlockPos(blockPos)); // Removing reference
										AspectBattery.merge(battery, vis);
									}
								}
					}
				}
			}
		}
		return battery;
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

	@Override
	protected Component getDefaultName() {
		return Component.literal("research_table");
	}

	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory player) {
		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer(8, 8));
		buffer.writeBlockPos(getBlockPos());
		return new ResearchTableMenu(id, player, buffer);
	}

	@Nonnull
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY) {
			AspectBattery battery = getVisBattery();
			return battery.getCapability(AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY).cast();
		}
		return super.getCapability(capability, facing).cast();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
		return this.getCapability(cap, null);
	}

	public ItemStack visItem() {
		return items.getStackInSlot(0);
	}

	public ItemStack ink() {
		return items.getStackInSlot(1);
	}

	@Nonnull
	public ItemStack note() {
		return items.getStackInSlot(2);
	}

	@Override
	public int getContainerSize() {
		return items.getSlots();
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public ItemStack getItem(int index) {
		return items.getStackInSlot(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return items.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return items.extractItem(index, items.getStackInSlot(index).getCount(), false);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (stack.getCount() > getMaxStackSize()) {
			ItemStack in = stack.copy();
			in.setCount(getMaxStackSize());
			stack.setCount(stack.getCount() - getMaxStackSize());
			items.setStackInSlot(index, in);
		} else {
			items.setStackInSlot(index, stack);
		}
		if (level != null) {
			level.setBlockAndUpdate(getBlockPos(), level.getBlockState(getBlockPos()).setValue(INK, !ink().isEmpty()));
			Direction facing = level.getBlockState(getBlockPos()).getValue(FACING);
			BlockPos componentPos = getBlockPos().offset(-facing.getStepZ(), facing.getStepY(), facing.getStepX());
			if (level.getBlockState(componentPos).getBlock() == ArcanaBlocks.RESEARCH_TABLE_COMPONENT.get()) {
				level.setBlockAndUpdate(componentPos, level.getBlockState(componentPos).setValue(PAPER, !note().isEmpty()));
			}
		}
	}

	@Override
	public boolean stillValid(Player player) {
		if (level.getBlockEntity(getBlockPos()) != this)
			return false;
		return player.distanceToSqr(getBlockPos().getX() + .5, getBlockPos().getY() + .5, getBlockPos().getZ() + .5) <= 64;
	}

	@Override
	public void clearContent() {
		for (int i = 0; i < items.getSlots() - 1; i++)
			items.getStackInSlot(i).setCount(0);
	}
}