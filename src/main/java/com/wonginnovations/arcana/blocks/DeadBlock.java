package com.wonginnovations.arcana.blocks;

import com.wonginnovations.arcana.systems.taint.Taint;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class DeadBlock extends DelegatingBlock {

	@Deprecated() // Use Taint#deadOf instead
	public DeadBlock(Block block) {
		super(block);
		Taint.addDeadMapping(block, this);
	}
	/**
	 * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
	 * this method is unrelated to randomTick and needsRandomTick, and will always be called regardless
	 * of whether the block can receive random update ticks
	 */
	@OnlyIn(Dist.CLIENT)
	@Override
	public void animateTick(@NotNull BlockState stateIn, @NotNull Level levelIn, @NotNull BlockPos pos, @NotNull RandomSource rand) {
		super.animateTick(stateIn, levelIn, pos, rand);
		if (stateIn.getBlock()==ArcanaBlocks.DEAD_GRASS_BLOCK.get())
			if (rand.nextInt(4) == 0) {
				levelIn.addParticle(ParticleTypes.MYCELIUM, (double)pos.getX() + (double)rand.nextFloat(), (double)pos.getY() + 1.1D, (double)pos.getZ() + (double)rand.nextFloat(), 0.0D, 0.0D, 0.0D);
			}
		//TODO: If mod ported to 1.16 change it to minecraft:ash
	}

}