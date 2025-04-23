package com.wonginnovations.arcana.items.armor;

import com.wonginnovations.arcana.items.ArcanaItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public enum ArcanaArmorMaterials implements ArmorMaterial {
	GOGGLES("arcana:goggles_of_revealing", 5, new int[]{2, 0, 0, 0}, 15, SoundEvents.ARMOR_EQUIP_GOLD, 0, Ingredient::of),
	ARCANIUM("arcana:arcanium_armor", 20, new int[]{3, 7, 5, 2}, 20, SoundEvents.ARMOR_EQUIP_IRON, 1, () -> Ingredient.of(ArcanaItems.ARCANIUM_INGOT.get())),
	VOID_METAL("arcana:void_metal_armor", 17, new int[]{4, 8, 6, 3}, 10, SoundEvents.ARMOR_EQUIP_IRON, 2, () -> Ingredient.of(ArcanaItems.VOID_METAL_INGOT.get()))
	;
	
	private static final int[] MAX_DAMAGE_ARRAY = new int[]{13, 16, 15, 11};
	private final String name;
	private final int maxDamageFactor;
	private final int[] damageReductionAmountArray;
	private final int enchantability;
	private final SoundEvent soundEvent;
	private final float toughness;
	private final Supplier<Ingredient> repairMaterial;
	
	ArcanaArmorMaterials(String name, int maxDamageFactor, int[] damageReductionAmounts, int enchantability, SoundEvent equipSound, float toughness, Supplier<Ingredient> repairMaterial) {
		this.name = name;
		this.maxDamageFactor = maxDamageFactor;
		this.damageReductionAmountArray = damageReductionAmounts;
		this.enchantability = enchantability;
		this.soundEvent = equipSound;
		this.toughness = toughness;
		this.repairMaterial = repairMaterial;
	}
	
	public int getDurabilityForType(ArmorItem.Type type) {
		return MAX_DAMAGE_ARRAY[type.ordinal()] * this.maxDamageFactor;
	}
	
	public int getDefenseForType(ArmorItem.Type type) {
		return damageReductionAmountArray[type.ordinal()];
	}
	
	public int getEnchantmentValue() {
		return enchantability;
	}
	
	@Nonnull
	public SoundEvent getEquipSound() {
		return soundEvent;
	}
	
	public @NotNull Ingredient getRepairIngredient() {
		return repairMaterial.get();
	}

	@Nonnull
	@OnlyIn(Dist.CLIENT)
	public String getName() {
		return name;
	}
	
	public float getToughness() {
		return toughness;
	}
	
	public float getKnockbackResistance() {
		return 0;
	}
}