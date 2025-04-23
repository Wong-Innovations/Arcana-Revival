package com.wonginnovations.arcana.mixin;

import com.wonginnovations.arcana.blocks.bases.SolidVisibleBlock;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockClientMixin {
	
	// Fix Smokey Glass side rendering
	// Vanilla uses solid-ness for too many things
	@Inject(method = "shouldRenderFace",
	        at = @At("HEAD"),
	        cancellable = true)
	private static void fixSmokeyGlassSideRendering(BlockState state, BlockGetter level, BlockPos pos, Direction direction, BlockPos neighborPos, CallbackInfoReturnable<Boolean> cir) {
		BlockState neighborState = level.getBlockState(neighborPos);
		if (neighborState.getBlock() instanceof SolidVisibleBlock)
			cir.setReturnValue(state.getBlock() != neighborState.getBlock());
	}
}