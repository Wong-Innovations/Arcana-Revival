package com.wonginnovations.arcana.blocks.entities;

import io.netty.buffer.Unpooled;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.aspects.handlers.AspectBattery;
import com.wonginnovations.arcana.aspects.handlers.AspectHandlerCapability;
import com.wonginnovations.arcana.containers.FociForgeMenu;
import com.wonginnovations.arcana.systems.spell.Spell;
import com.wonginnovations.arcana.systems.spell.SpellState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FociForgeBlockEntity extends BaseContainerBlockEntity {

	public Spell currentSpell = null;
	public SpellState spellState = new SpellState();

	public FociForgeBlockEntity(BlockPos pos, BlockState state) {
		super(ArcanaBlockEntities.FOCI_FORGE.get(), pos, state);
	}

	protected ItemStackHandler items = new ItemStackHandler(2) {
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			setChanged();
		}
	};

	// Read from file
	// wipes the floating module list, resets spellState sequence
	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		if (compound.contains("items")) {
			items.deserializeNBT(compound.getCompound("items"));
		}
		if (compound.contains("spellstate")) {
			spellState = SpellState.fromNBT(compound.getCompound("spellstate"));
			spellState.sequence = 0;
			spellState.floating.clear();
		}
	}

	// Read from packet
	// update spellState sequence, adjust current module
	public void readPacket(CompoundTag compound) {
		super.load(compound);
		if (compound.contains("items")) {
			items.deserializeNBT(compound.getCompound("items"));
		}
		if (compound.contains("spellstate")) {
			CompoundTag state = compound.getCompound("spellstate");
			spellState = SpellState.fromNBT(state);
			spellState.sequence = state.getInt("sequence");
			UUID player = Arcana.proxy.getPlayerOnClient().getUUID();
			if (spellState.floating.containsValue(player)) {
				spellState.activeModule = spellState.floating.inverse().get(player);
				spellState.activeModuleIndex = -1;
			}
		}
	}

	// write spellState to NBT
	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.put("items", items.serializeNBT());
		compound.put("spellstate", spellState.toNBT(new CompoundTag()));
	}

	// On server send, create tag for initial chunk load
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag state = new CompoundTag();
		saveAdditional(state);
		state.getCompound("spellstate").putInt("sequence", spellState.sequence);
		return state;
	}

	// On server send, update tile already loaded
	// just send the entire block again
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	// On client receive, update tile already loaded
	// just recreate the entire block again, nothing lost
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.readPacket(tag);
	}
	
	@Override
	protected Component getDefaultName() {
		return Component.translatable("container.arcana.foci_forge");
	}
	
	@Override
	protected AbstractContainerMenu createMenu(int id, Inventory player) {
		FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer(8,8));
		buffer.writeBlockPos(getBlockPos());
		return new FociForgeMenu(id,player,buffer);
	}

	// TODO: Required to show aspects on screen!
	@Nonnull
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY) {
			AspectBattery battery = new AspectBattery(/*0, 0*/);
			return battery.getCapability(AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY).cast();
		}
		return super.getCapability(capability, facing).cast();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
		return this.getCapability(cap,null);
	}

	public ItemStack visItem() {
		return items.getStackInSlot(0);
	}

	public ItemStack focus() {
		return items.getStackInSlot(1);
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
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
		return this.items.getStackInSlot(index);
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
	}
	
	@Override
	public boolean stillValid(Player player) {
		if (level.getBlockEntity(getBlockPos()) != this) {
			return false;
		} else {
			return player.distanceToSqr(getBlockPos().getX() + .5, getBlockPos().getY() + .5, getBlockPos().getZ() + .5) <= 64;
		}
	}
	
	@Override
	public void clearContent() {
		for (int i = 0; i < items.getSlots() - 1; i++) {
			items.getStackInSlot(i).setCount(0);
		}
	}

	public void replaceSpell(@Nonnull Spell newSpell) {
		currentSpell = newSpell;
		spellState.replaceSpell(currentSpell);
		setChanged();
	}

}
