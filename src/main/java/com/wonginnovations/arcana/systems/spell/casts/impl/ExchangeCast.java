package com.wonginnovations.arcana.systems.spell.casts.impl;

import com.wonginnovations.arcana.ArcanaVariables;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.systems.spell.SpellValues;
import com.wonginnovations.arcana.systems.spell.casts.Cast;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class ExchangeCast extends Cast {

	public static final Map<TagKey<Block>, Integer> MINIMUM_LEVEL = new HashMap<>();

	static {
		// STONE not needed since no SpellValues are a below a 1, done this way for add-on compatibility
		MINIMUM_LEVEL.put(BlockTags.NEEDS_IRON_TOOL, 2);
		MINIMUM_LEVEL.put(BlockTags.NEEDS_DIAMOND_TOOL, 3);
	}

	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("exchange");
	}
	
	@Override
	public Aspect getSpellAspect() {
		return Aspects.EXCHANGE;
	}

	@Override
	public int getSpellDuration() {
		return 1;
	}

	public int getMiningLevel() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"firstModifier"), 2);
	}

	public int getSize() {
		return SpellValues.getOrDefault(AspectUtils.getAspect(data,"secondModifier"), 1);
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity targetEntity) {
		caster.displayClientMessage(Component.translatable("status.arcana.invalid_spell"), true);
		return InteractionResult.FAIL;
	}

	@Override
	public InteractionResult useOnBlock(Player caster, Level level, BlockPos blockTarget) {
		if (caster.level().isClientSide) return InteractionResult.SUCCESS;
		BlockState blockToDestroy = caster.level().getBlockState(blockTarget);
		// TODO: does the casting device need to have a really high harvest level for canHarvestBlock to pass?
		if (blockToDestroy.getBlock().canHarvestBlock(blockToDestroy, caster.level(), blockTarget, caster) && canHarvest(blockToDestroy, getMiningLevel())) {
			ItemStack held = caster.getItemInHand(InteractionHand.OFF_HAND);
			if (!held.isEmpty() && Block.byItem(held.getItem()) != Blocks.AIR) {
				for (ItemStack stack : caster.level().getBlockState(blockTarget).getDrops(new LootParams.Builder((ServerLevel) caster.level())
						.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockTarget)).withParameter(LootContextParams.TOOL, new ItemStack(getMiningLevel() >= 3 ? Items.DIAMOND_PICKAXE : Items.IRON_PICKAXE)))) {
					caster.addItem(stack);
				}
				caster.level().setBlockAndUpdate(blockTarget, Block.byItem(held.getItem()).defaultBlockState());
				held.shrink(1);
				blockToDestroy.updateNeighbourShapes(caster.level(), blockTarget, 3);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget) {
		playerTarget.displayClientMessage(Component.translatable("status.arcana.invalid_spell"), true);
		return InteractionResult.FAIL;
	}

	private static boolean canHarvest(BlockState state, int miningLevel) {
		if (!state.requiresCorrectToolForDrops()) return true;
		for (TagKey<Block> tag : MINIMUM_LEVEL.keySet()) {
			if (state.is(tag))
				return miningLevel >= MINIMUM_LEVEL.get(tag);
		}
		return true;
	}
}
