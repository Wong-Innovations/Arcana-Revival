package com.wonginnovations.arcana.items.tools;

import com.wonginnovations.arcana.items.ArcanaItems;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum ArcanaToolTiers implements Tier {
	ARCANIUM(3, 1125, 7.0F, 2.5F, 17, () -> Ingredient.of(ArcanaItems.ARCANIUM_INGOT.get())),
	SILVER(1, 125, 10F, 1F, 5, () -> Ingredient.of(ArcanaItems.ARCANIUM_INGOT.get())),
	VOID_METAL(3, 300, 8.0F, 3.5F, 10, () -> Ingredient.of(ArcanaItems.VOID_METAL_INGOT.get()));

	private final int harvestLevel;
	private final int maxUses;
	private final float efficiency;
	private final float attackDamage;
	private final int enchantability;
	private final Supplier<Ingredient> repairMaterial;
	
	ArcanaToolTiers(int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability, Supplier<Ingredient> repairMaterial) {
		this.harvestLevel = harvestLevel;
		this.maxUses = maxUses;
		this.efficiency = efficiency;
		this.attackDamage = attackDamage;
		this.enchantability = enchantability;
		this.repairMaterial = repairMaterial;
	}
	
	public int getUses() {
		return maxUses;
	}
	
	public float getSpeed() {
		return efficiency;
	}
	
	public float getAttackDamageBonus() {
		return attackDamage;
	}
	
	public int getLevel() {
		return harvestLevel;
	}
	
	public int getEnchantmentValue() {
		return enchantability;
	}
	
	public @NotNull Ingredient getRepairIngredient() {
		return repairMaterial.get();
	}
}