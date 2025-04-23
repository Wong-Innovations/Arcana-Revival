package com.wonginnovations.arcana.blocks;

import com.wonginnovations.arcana.blocks.entities.WardenedBlockBlockEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class WardenedBlock extends BaseEntityBlock {
	protected WardenedBlockBlockEntity t;

	public WardenedBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		t = new WardenedBlockBlockEntity(pos, state);
		return t;
	}

	@SuppressWarnings("deprecation")
	@Override
	public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder builder) {
		if (t != null) return t.getState().orElse(Blocks.AIR.defaultBlockState()).getBlock().getDrops(state, builder); else return super.getDrops(state, builder);
	}

	@Override
	public void destroy(@NotNull LevelAccessor levelIn, @NotNull BlockPos pos, @NotNull BlockState state) {
		super.destroy(levelIn, pos, state);
		//levelIn.setBlockState(pos,t.getState().orElse(Blocks.AIR.defaultBlockState()),3);
	}
}
