package com.wonginnovations.arcana.systems.research.impls;

import com.google.gson.JsonObject;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.containers.ResearchTableMenu;
import com.wonginnovations.arcana.containers.slots.AspectSlot;
import com.wonginnovations.arcana.systems.research.Puzzle;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class Thaumaturgy extends Puzzle {
	private static final ResourceLocation ICON = new ResourceLocation(Arcana.MODID, "textures/item/research_note.png");
	public static final String TYPE = "thaumaturgy";

	public Thaumaturgy() {}

	@Override
	public void load(JsonObject data, ResourceLocation file) {

	}

	@Override
	public String type() {
		return TYPE;
	}

	@Override
	public CompoundTag getData() {
		return new CompoundTag();
	}

	@Override
	public String getDefaultDesc() {
		return "requirement.thaumaturgy";
	}

	@Override
	public ResourceLocation getDefaultIcon() {
		return ICON;
	}

	@Override
	public List<SlotInfo> getItemSlotLocations(Player player) {
		return Collections.emptyList();
	}

	@Override
	public List<AspectSlot> getAspectSlots(Supplier<AspectHandler> returnInv) {
		return Collections.emptyList();
	}

	@Override
	public boolean validate(List<AspectSlot> aspectSlots, List<Slot> itemSlots, Player player, ResearchTableMenu container) {
		return false;
	}

	public static Thaumaturgy fromNBT(CompoundTag passData) {
		return new Thaumaturgy();
	}
}
