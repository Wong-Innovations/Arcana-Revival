package com.wonginnovations.arcana.systems.spell.casts.impl;

import com.wonginnovations.arcana.ArcanaVariables;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.systems.spell.casts.Cast;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class FabricCast extends Cast {

	@Override
	public ResourceLocation getId() {
		return ArcanaVariables.arcLoc("fabric");
	}

	@Override
	public Aspect getSpellAspect() {
		return Aspects.FABRIC;
	}

	@Override
	public int getSpellDuration() {
		return 0;
	}

	@Override
	public InteractionResult useOnEntity(Player caster, Entity target) {
		target.sendSystemMessage(Component.literal("MCP is broken everyone shold use Yarn"));
		target.sendSystemMessage(Component.literal(target.getName().getString() + " gets gold award on r/minecraft"));
		target.sendSystemMessage(Component.literal(target.getName().getString() + " gets gold award on r/minecraft"));
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOnBlock(Player caster, Level level, BlockPos blockTarget) {
		caster.sendSystemMessage(Component.literal("hehe Ticking block entity"));
		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult useOnPlayer(Player playerTarget) {
		playerTarget.sendSystemMessage(Component.literal("MCP is broken everyone shold use Yarn"));
		playerTarget.sendSystemMessage(Component.literal(playerTarget.getName().getString() + " gets gold award on r/minecraft"));
		playerTarget.sendSystemMessage(Component.literal(playerTarget.getName().getString() + " gets gold award on r/minecraft"));
		return InteractionResult.SUCCESS;
	}
}
