package com.wonginnovations.arcana.systems.spell.casts.impl;

import com.wonginnovations.arcana.ArcanaVariables;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.systems.spell.SpellValues;
import com.wonginnovations.arcana.systems.spell.casts.Cast;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MiningCast extends Cast {

	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("mining");
	}
	
	@Override
	public Aspect getSpellAspect() {
		return Aspects.MINING;
	}

	@Override
	public int getSpellDuration() {
		return 1;
	}

	public int getMiningLevel() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"firstModifier"), 2);
	}

	public int getExplosivePower() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"secondModifier"), 0);
	}

	public int getFortune() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"sinModifier"), 0);
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget) {
		playerTarget.displayClientMessage(Component.translatable("status.arcana.invalid_spell"), true);
		return InteractionResult.FAIL;
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity entityTarget) {
		caster.displayClientMessage(Component.translatable("status.arcana.invalid_spell"), true);
		return InteractionResult.FAIL;
	}

	@SuppressWarnings("deprecation")
	public InteractionResult useOnBlock(Player caster, Level level, BlockPos blockTarget) {
		if (caster.level().isClientSide) return InteractionResult.SUCCESS;
		BlockState blockToDestroy = caster.level().getBlockState(blockTarget);
		ItemStack pickaxe = createDummyPickaxe(getMiningLevel()); // Below is a check for indestructibility I believe? probably not needed
		if (pickaxe.isCorrectToolForDrops(blockToDestroy) && /* blockToDestroy.getBlockHardness(level, blockTarget) != -1 && */ blockTarget.getY() != 0) {
			// Spawn block_break particles TODO: just spawn the particles? why this.
			level.levelEvent(2001, blockTarget, Block.getId(blockToDestroy));

			// Check of it has tile entity
			BlockEntity tileentity = blockToDestroy.hasBlockEntity() ? level.getBlockEntity(blockTarget) : null;

			// Create dummy Pickaxe with enchantments and mining level
			HashMap<Enchantment, Integer> map = new HashMap<>();
			map.put(Enchantments.BLOCK_FORTUNE,getFortune());
			EnchantmentHelper.setEnchantments(map, pickaxe);

			// Spawn drops and destroy block.
			Block.dropResources(blockToDestroy, level, blockTarget, tileentity, caster, pickaxe);
			FluidState ifluidstate = blockToDestroy.getBlock().getFluidState(blockToDestroy);
			level.setBlock(blockTarget, ifluidstate.createLegacyBlock(), Block.UPDATE_ALL);
			blockToDestroy.updateNeighbourShapes(caster.level(), blockTarget, Block.UPDATE_ALL);
		}
		return InteractionResult.SUCCESS;
	}

	private ItemStack createDummyPickaxe(int miningLevel) { // TODO: Check if it works
		return new ItemStack(new PickaxeItem(new Tier() {
			@Override
			public int getUses() {
				return 1;
			}

			@Override
			public float getSpeed() {
				return 1;
			}

			@Override
			public float getAttackDamageBonus() {
				return 1;
			}

			@Override
			public int getLevel() {
				return miningLevel;
			}

			@Override
			public int getEnchantmentValue() {
				return 0;
			}

			@Override
			public @NotNull Ingredient getRepairIngredient() {
				return Ingredient.EMPTY;
			}
		},0,0,new Item.Properties()),1);
	}
}
