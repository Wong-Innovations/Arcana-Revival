package com.wonginnovations.arcana.systems.research.impls;

import com.wonginnovations.arcana.systems.research.Requirement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import static com.wonginnovations.arcana.Arcana.arcLoc;

public class ItemRequirement extends Requirement {
	
	// perhaps support NBT in the future? will be required for enchantments in the future at least.
	protected Item item;
	protected ItemStack stack;
	
	public static final ResourceLocation TYPE = arcLoc("item");
	
	public ItemRequirement(Item item) {
		this.item = item;
	}

	public boolean satisfied(Player player) {
		int itemCount = 0;
		for (ItemStack stack : player.getInventory().items) {
			if (stack.getItem() == item) {
				itemCount += stack.getCount();
			}
		}
		return itemCount >= (getAmount() == 0 ? 1 : getAmount());
	}

	public void take(Player player) {
		int maxToConsume = getAmount() == 0 ? 1 : getAmount();
		int consumed = 0;
		for (ItemStack stack : player.getInventory().items) {
			if (stack.getItem() == item) {
				int amount = Math.min(maxToConsume - consumed, stack.getCount());
				stack.shrink(amount);
				consumed += amount;
			}
		}
	}
	
	public ResourceLocation type() {
		return TYPE;
	}
	
	public CompoundTag data() {
		CompoundTag compound = new CompoundTag();
		compound.putString("itemType", String.valueOf(ForgeRegistries.ITEMS.getKey(item)));
		return compound;
	}
	
	public Item getItem() {
		return item;
	}
	
	public ItemStack getStack() {
		if (stack == null)
			return stack = new ItemStack(getItem());
		return stack;
	}
}