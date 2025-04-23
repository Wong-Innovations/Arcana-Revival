package com.wonginnovations.arcana.blocks;

import com.wonginnovations.arcana.blocks.entities.VacuumBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

@SuppressWarnings({"deprecation"})
public class VacuumBlock extends BaseEntityBlock {
 
	public VacuumBlock(Properties properties) {
		super(properties);
	}

    @Override
	public @NotNull RenderShape getRenderShape(@Nonnull BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new VacuumBlockEntity(pos, state);
	}

}
