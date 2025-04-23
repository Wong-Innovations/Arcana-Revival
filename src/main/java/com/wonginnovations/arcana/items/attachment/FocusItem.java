package com.wonginnovations.arcana.items.attachment;

import com.wonginnovations.arcana.systems.spell.Spell;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FocusItem extends Item implements Focus {
	public static final int DEFAULT_NUMSTYLES = 36;

	private int numStyles;
	private final List<ResourceLocation> modelLocations;
	
	public FocusItem(Properties properties, int numStyles, ResourceLocation... locations) {
		super(properties);
		this.numStyles = numStyles;
		modelLocations = Arrays.asList(locations);
		Focus.FOCI.add(this);
		// TODO: property overrides are now defined separately
		//addPropertyOverride(new ResourceLocation("style"), (stack, world, entity) -> stack.getOrCreateTag().getInt("style"));
	}
	
	public ResourceLocation getModelLocation(CompoundTag nbt) {
		return modelLocations.get(Math.min(nbt.getInt("style"), modelLocations.size() - 1));
	}
	
	public List<ResourceLocation> getAllModelLocations() {
		return modelLocations;
	}
	
	public int numStyles() {
		return numStyles;
	}
	
	public Optional<Item> getAssociatedItem() {
		return Optional.of(this);
	}

	public Spell getSpell(ItemStack stack) {
		// Spell.fromNBT already uses .getCompound("spell"), so it's not needed here
		return Spell.fromNBT(stack.getOrCreateTag().getCompound("focusData")); // .getCompound("spell"));
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level levelIn, Player playerIn, @NotNull InteractionHand handIn) {
		Logger logger = LogManager.getLogger();
		logger.debug(playerIn.getItemInHand(handIn).getOrCreateTag().toString());
		return super.use(levelIn, playerIn, handIn);
	}

	public static int getColorAspect(ItemStack stack) {
		Spell spell = Spell.fromNBT(stack.getOrCreateTag().getCompound("spell"));
		return spell.getSpellColor();
	}
}
