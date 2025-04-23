package com.wonginnovations.arcana.blocks;

import com.wonginnovations.arcana.blocks.entities.AspectTesterBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class AspectTesterBlock extends BaseEntityBlock {
	public AspectTesterBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
		return RenderShape.MODEL;
	}

	@Override
	public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter levelIn, List<Component> tooltip, @NotNull TooltipFlag flagIn) {
		tooltip.add(Component.literal("DEV: Only for testing new AspectHandler.").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
		tooltip.add(Component.literal("Can crash the game!").setStyle(Style.EMPTY.withColor(0xFF0000)));
		super.appendHoverText(stack, levelIn, tooltip, flagIn);
	}

	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new AspectTesterBlockEntity(pos, state);
	}
}