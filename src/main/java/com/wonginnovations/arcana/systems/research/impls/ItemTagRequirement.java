package com.wonginnovations.arcana.systems.research.impls;

import com.wonginnovations.arcana.systems.research.Requirement;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static com.wonginnovations.arcana.Arcana.arcLoc;

public class ItemTagRequirement extends Requirement {
	
	protected TagKey<Item> tag;
	protected ResourceLocation tagName;
	
	public static final ResourceLocation TYPE = arcLoc("item_tag");
	
	public ItemTagRequirement(@NotNull ResourceLocation tagName) {
		this(TagKey.create(Registries.ITEM, tagName), tagName);
	}
	
	public ItemTagRequirement(@NotNull TagKey<Item> tag, @NotNull ResourceLocation tagName) {
		this.tag = tag;
		this.tagName = tagName;
	}

	public boolean satisfied(Player player){
		int itemCount = 0;
		for (ItemStack stack : player.getInventory().items) {
			if (stack.getItem().builtInRegistryHolder().is(this.tag)) {
				itemCount += stack.getCount();
			}
		}
		return itemCount >= (getAmount() == 0 ? 1 : getAmount());
	}

	public void take(Player player) {
		int maxToConsume = getAmount() == 0 ? 1 : getAmount();
		int consumed = 0;
		for (ItemStack stack : player.getInventory().items) {
			if (stack.getItem().builtInRegistryHolder().is(this.tag)) {
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
		compound.putString("itemTag", tagName.toString());
		return compound;
	}
	
	public TagKey<Item> getTag() {
		return tag;
	}
	
	public ResourceLocation getTagName() {
		return tagName;
	}
}