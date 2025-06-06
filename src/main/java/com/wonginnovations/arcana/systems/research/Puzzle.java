package com.wonginnovations.arcana.systems.research;

import com.google.gson.JsonObject;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.containers.ResearchTableMenu;
import com.wonginnovations.arcana.containers.slots.AspectSlot;
import com.wonginnovations.arcana.systems.research.impls.Chemistry;
import com.wonginnovations.arcana.systems.research.impls.Fieldwork;
import com.wonginnovations.arcana.systems.research.impls.Guesswork;
import com.wonginnovations.arcana.systems.research.impls.Thaumaturgy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Puzzle{
	
	////////////////////// STATIC STUFF
	
	// I can't bother to add addon support for custom puzzles,
	// if anyone wants to do that, *then* I'll add it
	private static Map<String, Supplier<Puzzle>> factories = new LinkedHashMap<>();
	private static Map<String, Function<CompoundTag, Puzzle>> deserializers = new LinkedHashMap<>();
	
	public static Puzzle makePuzzle(String type, @Nullable String desc, ResourceLocation key, @Nullable ResourceLocation icon, JsonObject content, ResourceLocation file) {
		if (getBlank(type) != null) {
			Puzzle puzzle = getBlank(type).get();
			puzzle.key = key;
			puzzle.desc = desc != null ? desc : puzzle.getDefaultDesc();
			puzzle.icon = icon != null ? icon : puzzle.getDefaultIcon();
			puzzle.load(content, file);
			return puzzle;
		} else
			return null;
	}
	
	public static Supplier<Puzzle> getBlank(String type) {
		return factories.get(type);
	}
	
	public static Puzzle deserialize(CompoundTag passData) {
		String type = passData.getString("type");
		String desc = passData.getString("desc");
		ResourceLocation key = new ResourceLocation(passData.getString("key"));
		ResourceLocation icon = new ResourceLocation(passData.getString("icon"));
		CompoundTag data = passData.getCompound("data");
		if (deserializers.get(type) != null) {
			Puzzle puzzle = deserializers.get(type).apply(data);
			puzzle.key = key;
			puzzle.desc = desc;
			puzzle.icon = icon;
			return puzzle;
		} else
			return null;
	}
	
	public static void init() {
		factories.put(Guesswork.TYPE, Guesswork::new);
		deserializers.put(Guesswork.TYPE, Guesswork::fromNBT);
		factories.put(Fieldwork.TYPE, Fieldwork::new);
		deserializers.put(Fieldwork.TYPE, __ -> new Fieldwork());
		factories.put(Chemistry.TYPE, Chemistry::new);
		deserializers.put(Chemistry.TYPE, Chemistry::fromNBT);
		factories.put(Thaumaturgy.TYPE, Thaumaturgy::new);
		deserializers.put(Thaumaturgy.TYPE, Thaumaturgy::fromNBT);
	}
	
	////////////////////// INSTANCE STUFF
	
	ResourceLocation key, icon;
	String desc;
	
	public abstract void load(JsonObject data, ResourceLocation file);
	
	public abstract String type();
	
	public abstract CompoundTag getData();
	
	public abstract String getDefaultDesc();
	
	public abstract ResourceLocation getDefaultIcon();
	
	public abstract List<SlotInfo> getItemSlotLocations(Player player);
	
	public abstract List<AspectSlot> getAspectSlots(Supplier<AspectHandler> returnInv);
	
	public abstract boolean validate(List<AspectSlot> aspectSlots, List<Slot> itemSlots, Player player, ResearchTableMenu container);
	
	public String getDesc() {
		return desc;
	}
	
	public ResourceLocation getIcon() {
		return icon;
	}
	
	public CompoundTag getPassData() {
		CompoundTag passData = new CompoundTag();
		passData.putString("type", type());
		passData.putString("key", getKey().toString());
		passData.putString("desc", getDesc() != null ? getDesc() : "null");
		passData.putString("icon", getIcon().toString());
		passData.put("data", getData());
		return passData;
	}
	
	public ResourceLocation getKey() {
		return key;
	}
	
	int guiLeft(int screenWidth) {
		return (screenWidth - /*ResearchTableContainer.WIDTH*/1) / 2;
	}
	
	int guiTop(int screenHeight) {
		return (screenHeight - /*ResearchTableContainer.HEIGHT*/1) / 2;
	}
	
	protected int paperLeft(int screenWidth) {
		return guiLeft(screenWidth) + 141;
	}
	
	protected int paperTop(int screenHeight) {
		return guiTop(screenHeight) + 35;
	}
	
	public static class SlotInfo{
		public final int x, y, max;
		public final String bg_name;
		
		public SlotInfo(int x, int y) {
			this(x, y, -1, null);
		}
		
		public SlotInfo(int x, int y, int max, String bg_name) {
			this.x = x;
			this.y = y;
			this.max = max;
			this.bg_name = bg_name;
		}
	}
}