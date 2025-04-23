package com.wonginnovations.arcana.blocks;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class LightBlock extends Block {

	public LightBlock(Block.Properties properties) {
		super(properties);
	}

	@Override
	public void animateTick(@NotNull BlockState stateIn, @NotNull Level levelIn, @NotNull BlockPos pos, @NotNull RandomSource rand) {
		super.animateTick(stateIn, levelIn, pos, rand);
		
		if (rand.nextInt(2) == 0)
			levelIn.addParticle(ParticleTypes.FLAME, pos.getX(), pos.getY(), pos.getZ(), 0.0D, 0.0D, 0.0D);
	}

}
