package com.wonginnovations.arcana.blocks.entities;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.items.MagicDeviceItem;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.Objects;
import java.util.Optional;

@ParametersAreNullableByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "RedundantTypeArguments", "unused"})
public class WardenedBlockBlockEntity extends BlockEntity {
	private Optional<BlockState> copyState = null;
	private Boolean holdingSpell = false;

	public WardenedBlockBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		super(ArcanaBlockEntities.WARDENED_BLOCK.get(), pos, state);
	}

	public void setState(Optional<BlockState> state) {
		copyState = state;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (copyState.orElse(Blocks.BEDROCK.defaultBlockState()).getBlock() != ArcanaBlocks.WARDENED_BLOCK.get())
			tag.putString("type", ForgeRegistries.BLOCKS.getKey(copyState.orElse(Blocks.BEDROCK.defaultBlockState()).getBlock()).toString());
	}

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		// wtf why does this use level.players().get(0) :desolate:
		holdingSpell = level.players().get(0).getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof MagicDeviceItem;
		copyState = Optional.<BlockState>of(Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tag.getString("type")))).defaultBlockState());
	}

	public Optional<BlockState> getState() {
		return copyState;
	}

	//  When the world loads from disk, the server needs to send the BlockEntity information to the client
	//  it uses getUpdatePacket(), getUpdateTag(), onDataPacket(), and handleUpdateTag() to do this:
	//  getUpdatePacket() and onDataPacket() are used for one-at-a-time BlockEntity updates
	//  getUpdateTag() and handleUpdateTag() are used by vanilla to collate together into a single chunk update packet
	//  Not really required for this example since we only use the timer on the client, but included anyway for illustration
	@Override
	public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
//		CompoundTag nbtTagCompound = new CompoundTag();
//		saveAdditional(nbtTagCompound);
//		int tileEntityType = ArcanaBlockEntities.WARDENED_BLOCK.hashCode();
		return ClientboundBlockEntityDataPacket.create(this);
	}

//	@Override
//	public void onDataPacket(NetworkManager net, SUpdateBlockEntityPacket pkt) {
//		read(getBlockState(), pkt.getNbtCompound());
//	}

	/* Creates a tag containing all of the BlockEntity information, used by vanilla to transmit from server to client */
	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbtTagCompound = new CompoundTag();
		saveAdditional(nbtTagCompound);
		return nbtTagCompound;
	}

	/* Populates this BlockEntity with information from the tag, used by vanilla to transmit from server to client */
	@Override
	public void handleUpdateTag(CompoundTag tag) {
		this.load(tag);
	}

	public void tick(Level level, BlockPos pos, BlockState state) {
		holdingSpell = level.players().get(0).getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof MagicDeviceItem;
	}

	public Boolean isHoldingWand() {
		return holdingSpell;
	}
}
