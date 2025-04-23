package com.wonginnovations.arcana.capabilities;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Attached to all living entities. Provides methods for checking whether an entity is within a taint biome,
 * whether it should be tracked, and setting these values.
 */
public interface TaintTrackable{
	
	boolean isTracking();
	
	void setTracking(boolean tracking);
	
	boolean isInTaintBiome();
	
	void setInTaintBiome(boolean inTaintBiome);
	
	int getTimeInTaintBiome();
	
	void setTimeInTaintBiome(int timeInTaintBiome);
	
	void addTimeInTaintBiome(int timeInTaintBiome);
	
	CompoundTag serializeNBT();
	
	void deserializeNBT(@Nonnull CompoundTag data);
	
	@SuppressWarnings("ConstantConditions")
	@Nullable
	static TaintTrackable getFrom(@Nonnull LivingEntity holder) {
		return holder.getCapability(TaintTrackableCapability.TAINT_TRACKABLE_CAPABILITY, null).orElse(null);
	}
}