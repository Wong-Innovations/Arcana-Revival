package com.wonginnovations.arcana.aspects.handlers;

import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.Aspects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AspectCell implements AspectHolder {
	
	private AspectStack stored = new AspectStack();
	private float capacity;
	private boolean voids = false, canInsert = true;
	// not serialized, set these yourself
	private Consumer<Float> overfillingCallback = __ -> {};
	private List<Aspect> whitelist = null;
	
	public AspectCell() {
		this(100);
	}
	
	public AspectCell(float capacity) {
		this.capacity = capacity;
	}
	
	public AspectStack getStack() {
		return stored;
	}
	
	public float getCapacity() {
		return capacity;
	}
	
	public List<Aspect> getWhitelist() {
		return whitelist;
	}
	
	public boolean voids() {
		return voids;
	}
	
	public boolean canInsert() {
		return canInsert;
	}
	
	public Consumer<Float> overfillingCallback() {
		return overfillingCallback;
	}
	
	public void setStack(AspectStack stack) {
		stored = stack;
	}
	
	public void setCapacity(float capacity) {
		this.capacity = capacity;
	}
	
	public void setWhitelist(List<Aspect> whitelist) {
		this.whitelist = whitelist;
	}
	
	public void setVoids(boolean voids) {
		this.voids = voids;
	}
	
	public void setCanInsert(boolean canInsert) {
		this.canInsert = canInsert;
	}
	
	public void setOverfillingCallback(Consumer<Float> callback) {
		this.overfillingCallback = callback == null ? __ -> {} : callback;
	}
	
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putFloat("amount", getStack().getAmount());
		nbt.putFloat("capacity", getCapacity());
		nbt.putString("aspect", getStack().getAspect().name().toLowerCase());
		nbt.putBoolean("voids", voids);
		nbt.putBoolean("canInsert", canInsert);
		if (whitelist != null && !whitelist.isEmpty())
			nbt.putIntArray("whitelist", whitelist.stream().map(Aspect::getId).toList());
		return nbt;
	}
	
	public void deserializeNBT(CompoundTag data) {
		capacity = data.getInt("capacity");
		int amount = data.getInt("amount");
		Aspect aspect = Aspects.valueOf(data.getString("aspect").toUpperCase());
		voids = data.getBoolean("voids");
		// getter returns false by default, but we want this to be true by default
		canInsert = !data.contains("canInsert") || data.getBoolean("canInsert");
		setStack(new AspectStack(aspect, amount));
		if (data.contains("whitelist")) {
			ArrayList<Aspect> list = new ArrayList<>();
			for (int i : data.getIntArray("whitelist"))
				list.add(Aspect.fromId(i));
			whitelist = List.copyOf(list);
		}

	}
	
	public static AspectCell fromNbt(CompoundTag data) {
		AspectCell ret = new AspectCell();
		ret.deserializeNBT(data);
		return ret;
	}
}