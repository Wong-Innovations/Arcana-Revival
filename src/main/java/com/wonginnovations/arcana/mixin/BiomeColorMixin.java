package com.wonginnovations.arcana.mixin;

import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.client.gui.UiUtil;
import com.wonginnovations.arcana.systems.taint.Taint;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

import static net.minecraft.client.renderer.BiomeColors.WATER_COLOR_RESOLVER;

// Lets mixin into BiomeColors for taint "biome" water :)
@OnlyIn(Dist.CLIENT)
@Mixin(BiomeColors.class)
public class BiomeColorMixin {
	// TODO: I think that is better way to do this. Currently chunk loading is slower than normal
	@Inject(method = "getAverageWaterColor", at = @At("HEAD"), cancellable = true)
	private static void getAverageWaterColor(BlockAndTintGetter pLevel, BlockPos pBlockPos, CallbackInfoReturnable<Integer> callbackInfoReturnable) {
		if (pLevel.getBlockState(pBlockPos.above()).getBlock() != Blocks.WATER) {
			Iterator<BlockPos> im1 = BlockPos.betweenClosed(pBlockPos.offset(4, 2, 4), pBlockPos.offset(-4, -2, -4)).iterator();
			while (im1.hasNext()) {
				try {
					BlockPos b = im1.next();
					if (Taint.isTainted(pLevel.getBlockState(b).getBlock())) {
						callbackInfoReturnable.setReturnValue(UiUtil.blend(0x6e298e, pLevel.getBlockTint(pBlockPos, WATER_COLOR_RESOLVER), 0.25f));
						break;
					}
				} catch (ArrayIndexOutOfBoundsException ignore) {
				}
			}
			Iterator<BlockPos> i0 = BlockPos.betweenClosed(pBlockPos.offset(3, 2, 3), pBlockPos.offset(-3, -2, -3)).iterator();
			while (i0.hasNext()) {
				try {
					BlockPos b = i0.next();
					if (Taint.isTainted(pLevel.getBlockState(b).getBlock())) {
						if (pLevel.getBlockState(b).getBlock() != ArcanaBlocks.TAINTED_GRAVEL.get()) {
							callbackInfoReturnable.setReturnValue(UiUtil.blend(0x6e298e, pLevel.getBlockTint(pBlockPos, WATER_COLOR_RESOLVER), 0.50f));
							break;
						}
					}
				} catch (ArrayIndexOutOfBoundsException ignore) {
				}
			}
			Iterator<BlockPos> i1 = BlockPos.betweenClosed(pBlockPos.offset(2, 3, 2), pBlockPos.offset(-2, -3, -2)).iterator();
			while (i1.hasNext()) {
				try {
					if (Taint.isTainted(pLevel.getBlockState(i1.next()).getBlock())) {
						callbackInfoReturnable.setReturnValue(UiUtil.blend(0x6e298e, pLevel.getBlockTint(pBlockPos, WATER_COLOR_RESOLVER), 0.75f));
						break;
					}
				} catch (ArrayIndexOutOfBoundsException ignore) {
				}
			}
			int i = 1;//for (int i = 0; i < Minecraft.getInstance().gameSettings.biomeBlendRadius; i++) {
				Iterator<BlockPos> i2 = BlockPos.betweenClosed(pBlockPos.offset(i, 3, i), pBlockPos.offset(-i, -3, -i)).iterator();
				while (i2.hasNext()) {
					try {
						if (Taint.isTainted(pLevel.getBlockState(i2.next()).getBlock())) {
							callbackInfoReturnable.setReturnValue(UiUtil.blend(0x6e298e, pLevel.getBlockTint(pBlockPos, WATER_COLOR_RESOLVER),/*Minecraft.getInstance().gameSettings.biomeBlendRadius/((float)100)*i)*/1f));
							break;
						}
					} catch (ArrayIndexOutOfBoundsException ignore) {
					}
				}
			//}
		}
	}
}
