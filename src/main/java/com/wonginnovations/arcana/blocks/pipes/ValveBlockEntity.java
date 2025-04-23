package com.wonginnovations.arcana.blocks.pipes;

import com.wonginnovations.arcana.blocks.entities.ArcanaBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ValveBlockEntity extends TubeBlockEntity {
	
	private boolean enabled = true;
	private boolean suppressedByRedstone = false;
	private long lastChangedTick = -1;
	
	public ValveBlockEntity(BlockPos pos, BlockState state) {
		super(ArcanaBlockEntities.ASPECT_VALVE.get(), pos, state);
	}
	
	public boolean enabled() {
		return enabled && !suppressedByRedstone;
	}
	
	public boolean enabledByHand() {
		return enabled;
	}
	
	public void setEnabledAndNotify(boolean enabled) {
		this.enabled = enabled;
		notifyChange();
	}

	public boolean isSuppressedByRedstone() {
		return suppressedByRedstone;
	}
	
	public void setSuppressedByRedstone(boolean suppress) {
		if (suppressedByRedstone != suppress) {
			suppressedByRedstone = suppress;
			notifyChange();
		}
	}
	
	@SuppressWarnings("ConstantConditions")
	private void notifyChange() {
		lastChangedTick = level.getGameTime();
	}
	
	public long getLastChangedTick() {
		return lastChangedTick;
	}

	@Override
	public void load(@NotNull CompoundTag nbt) {
		enabled = nbt.getBoolean("enabled");
		suppressedByRedstone = nbt.getBoolean("suppressed");
	}

	@Override
	public void saveAdditional(@NotNull CompoundTag nbt) {
		// save if enabled
		nbt.putBoolean("enabled", enabled);
		nbt.putBoolean("suppressed", isSuppressedByRedstone());
	}

	@Override
	public @Nonnull CompoundTag getUpdateTag() {
		CompoundTag nbt = super.getUpdateTag();
		nbt.putBoolean("suppressed", isSuppressedByRedstone());
		return nbt;
	}

	@Override
	public void handleUpdateTag(@Nonnull CompoundTag tag) {
		super.handleUpdateTag(tag);
		setSuppressedByRedstone(tag.getBoolean("suppressed"));
	}

	@Override
	public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
//	public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SUpdateBlockEntityPacket pkt) {
//		handleUpdateTag(getBlockState(), pkt.getNbtCompound());
//	}
}