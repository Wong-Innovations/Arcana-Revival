package com.wonginnovations.arcana.blocks.entities;

import com.wonginnovations.arcana.blocks.multiblocks.taint_scrubber.ITaintScrubberExtension;
import com.wonginnovations.arcana.util.Pair;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class TaintScrubberBlockEntity extends BlockEntity {

	private int nextRefresh = 10;
	private List<Pair<ITaintScrubberExtension, BlockPos>> extensions = new ArrayList<>();

	public TaintScrubberBlockEntity(BlockPos pos, BlockState state) {
		super(ArcanaBlockEntities.TAINT_SCRUBBER.get(), pos, state);
	}

	public void tick(Level level, BlockPos pos, BlockState state) {
		CompoundTag compoundNBT = new CompoundTag();
		List<Pair<ITaintScrubberExtension, BlockPos>> toRemove = new ArrayList<>();
		//Update and get data
		for (Pair<ITaintScrubberExtension, BlockPos> extension : extensions) {
			if (extension.getFirst().isValidConnection(level,extension.getSecond())) {
				extension.getFirst().sendUpdate(level,extension.getSecond());
				extension.getFirst().getShareableData(compoundNBT);
			} else {
				extension.getFirst().sendUpdate(level,extension.getSecond()); toRemove.add(extension);
			}
		}

		if (compoundNBT.getInt("h_range") == 0) compoundNBT.putInt("h_range",8);
		if (compoundNBT.getInt("v_range") == 0) compoundNBT.putInt("v_range",8);
		compoundNBT.putBoolean("silk_touch",false);

		//Run Task
		for (Pair<ITaintScrubberExtension, BlockPos> extension : extensions) {
			if (extension.getFirst().isValidConnection(level,extension.getSecond())) {
				extension.getFirst().run(level,extension.getSecond(),compoundNBT);
			} else {
				extension.getFirst().sendUpdate(level,extension.getSecond()); toRemove.add(extension);
			}
		}
		extensions.removeAll(toRemove);

		if (nextRefresh>=10) {
			searchForExtensions(level, pos);
			nextRefresh%=10;
		} else nextRefresh++;
	}

	private void searchForExtensions(Level level, BlockPos pos) {
		extensions.clear();
		BlockPos.betweenClosed(pos.north(2).east(2).above(3),pos.south(2).west(2).below(3)).forEach(currPos -> {
			BlockState state = level.getBlockState(currPos);
			if (state.getBlock() instanceof ITaintScrubberExtension extension) {
                if (extension.isValidConnection(level,currPos)) {
					extensions.add(Pair.of(extension,new BlockPos(currPos)));
				}
				extension.sendUpdate(level,currPos);
			}
		});
	}
}
