package com.wonginnovations.arcana.blocks.pipes;

import com.wonginnovations.arcana.aspects.Aspect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ConduitBlockEntity extends TubeBlockEntity {
	
	// conditionally redirect (types of) specks
	Aspect whitelist = null;
	Direction dir;

	public ConduitBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	protected @NotNull Optional<Direction> redirect(@NotNull AspectSpeck speck, boolean canPass) {
		if (speck.pos >= .5f && !getLevel().hasNeighborSignal(getBlockPos()) && (whitelist == null || speck.payload.getAspect() == whitelist))
			return Optional.of(dir);
		return Optional.empty();
	}
}