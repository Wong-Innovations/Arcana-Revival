package com.wonginnovations.arcana.blocks.pipes;

import com.wonginnovations.arcana.blocks.entities.ArcanaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class PipeWindowBlockEntity extends TubeBlockEntity {
	
	private long lastTransferTime = -1;
	
	public PipeWindowBlockEntity(BlockPos pos, BlockState state) {
		super(ArcanaBlockEntities.ASPECT_WINDOW.get(), pos, state);
	}

	@Override
	public void load(@NotNull CompoundTag nbt) {
		super.load(nbt);
		lastTransferTime = nbt.getInt("lastTransferTime");
	}

	@Override
	public void saveAdditional(@NotNull CompoundTag nbt) {
		// save if enabled
		super.saveAdditional(nbt);
		nbt.putLong("lastTransferTime", lastTransferTime);
	}
	
	public long getLastTransferTime() {
		return lastTransferTime;
	}

	@Override
	public void addSpeck(@NotNull AspectSpeck speck) {
		super.addSpeck(speck);
		if (!speck.payload.isEmpty())
			lastTransferTime = getLevel().getGameTime();
	}
}