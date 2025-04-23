package com.wonginnovations.arcana.aspects.handlers;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.wonginnovations.arcana.aspects.handlers.AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY;

public class AspectBattery implements AspectHandler, ICapabilitySerializable<CompoundTag> {

	private List<AspectHolder> holders = new ArrayList<>();

	public List<AspectHolder> getHolders() {
		return holders;
	}

	public CompoundTag serializeNBT() {
		CompoundTag compound = new CompoundTag();
		ListTag storedCells = new ListTag();
		holders.forEach(holder -> storedCells.add(holder.serializeNBT()));
		compound.put("holders", storedCells);
		return compound;
	}

	public void deserializeNBT(CompoundTag data) {
		ListTag cells = data.getList("holders", Tag.TAG_COMPOUND);
		holders.clear();
		for (Tag icell : cells) {
			AspectCell cell = AspectCell.fromNbt((CompoundTag)icell);
			holders.add(cell);
		}
	}

	@SuppressWarnings("unchecked")
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
		return capability == ASPECT_HANDLER_CAPABILITY ? LazyOptional.of(() -> (T)this) : LazyOptional.empty();
	}

	@SuppressWarnings("UnusedReturnValue")
	public static AspectBattery merge(AspectBattery defaultBattery, AspectBattery... batteries) {
		for (AspectBattery battery : batteries)
			defaultBattery.getHolders().addAll(battery.getHolders());
		return defaultBattery;
	}

}