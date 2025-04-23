package com.wonginnovations.arcana.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.wonginnovations.arcana.Arcana.arcLoc;

public class TaintTrackableCapability implements TaintTrackable {

	boolean tracking = false, inTaintBiome = false;
	int timeInTaintBiome = 0;

	public boolean isTracking() {
		return tracking;
	}

	public void setTracking(boolean tracking) {
		this.tracking = tracking;
	}

	public boolean isInTaintBiome() {
		return inTaintBiome;
	}

	public void setInTaintBiome(boolean inTaintBiome) {
		this.inTaintBiome = inTaintBiome;
	}

	public int getTimeInTaintBiome() {
		return timeInTaintBiome;
	}

	public void setTimeInTaintBiome(int timeInTaintBiome) {
		this.timeInTaintBiome = timeInTaintBiome;
	}

	public void addTimeInTaintBiome(int timeInTaintBiome) {
		this.timeInTaintBiome += timeInTaintBiome;
	}

	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putBoolean("tracking", isTracking());
		nbt.putBoolean("inTaintBiome", inTaintBiome);
		nbt.putInt("timeInTaintBiome", timeInTaintBiome);
		return nbt;
	}

	public void deserializeNBT(@Nonnull CompoundTag data) {
		tracking = data.getBoolean("tracking");
		inTaintBiome = data.getBoolean("inTaintBiome");
		timeInTaintBiome = data.getInt("timeInTaintBiome");
	}

	/* Capability management */

	public static Capability<TaintTrackable> TAINT_TRACKABLE_CAPABILITY =
			CapabilityManager.get(new CapabilityToken<>() {
			});
	
	public static final ResourceLocation KEY = arcLoc("taint_trackable_capability");

	/* Capability provider */
	
	public static class Provider implements ICapabilitySerializable<CompoundTag> {
		
		private final TaintTrackable cap = new TaintTrackableCapability();
		
		public CompoundTag serializeNBT() {
			return cap.serializeNBT();
		}
		
		public void deserializeNBT(CompoundTag nbt) {
			cap.deserializeNBT(nbt);
		}
		
		@SuppressWarnings("unchecked")
		@Nonnull
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
			return capability == TAINT_TRACKABLE_CAPABILITY ? LazyOptional.of(() -> (T)cap) : LazyOptional.empty();
		}

	}

}