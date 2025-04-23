package com.wonginnovations.arcana.client.event;

import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.systems.taint.Taint;
import com.wonginnovations.arcana.capabilities.TaintTrackable;
import com.wonginnovations.arcana.client.gui.UiUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class FogHandler {
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void setFogColor(ViewportEvent.ComputeFogColor fog) {
		Entity entity = fog.getCamera().getEntity();
		Level w = entity.level();
		BlockPos pos = fog.getCamera().getBlockPosition();
		BlockState bs = w.getBlockState(pos);
		Block b = bs.getBlock();
		if (b.equals(ArcanaBlocks.TAINT_FLUID_BLOCK.get())) {
			float red = 78 / 255F, green = 44 / 255F, blue = 92 / 255F;
			fog.setRed(red);
			fog.setGreen(green);
			fog.setBlue(blue);
		} else if (entity instanceof LivingEntity living && Taint.isAreaInTaintBiome(pos, w)) {
            if (TaintTrackable.getFrom(living)!=null) {
				int color = 0x4E2C5C;
				float progress = Math.min(20, TaintTrackable.getFrom(living).getTimeInTaintBiome()) / 20f;
				int blended = UiUtil.blend(color, ((int) (fog.getRed() * 255) << 16) | ((int) (fog.getGreen() * 255) << 8) | ((int) (fog.getBlue() * 255)), progress);

				fog.setRed(UiUtil.red(blended) / 255f);
				fog.setGreen(UiUtil.green(blended) / 255f);
				fog.setBlue(UiUtil.blue(blended) / 255f);
			}
		}
	}

	// TODO: I just don't think this is a thing anymore
//	@SubscribeEvent
//	@OnlyIn(Dist.CLIENT)
//	public static void setFogDensity(FogRenderer fog) {
//		Entity entity = fog.getInfo().getRenderViewEntity();
//		World w = entity.getEntityWorld();
//		BlockPos pos = fog.getInfo().getBlockPos();
//		if (entity instanceof LivingEntity living && Taint.isAreaInTaintBiome(pos, w)) {
//            if (TaintTrackable.getFrom(living)!=null) {
//				float progress = Math.min(20, TaintTrackable.getFrom(living).getTimeInTaintBiome()) / 20f;
//				fog.setDensity(fog.getDensity() + progress * .3f);
//			}
//		}
//	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void setFogLength(ViewportEvent.RenderFog fog) {
		Entity entity = fog.getCamera().getEntity();
		Level w = entity.level();
		BlockPos pos = fog.getCamera().getBlockPosition();
		if (entity instanceof LivingEntity living && Taint.isAreaInTaintBiome(pos, w)) {
            TaintTrackable from = TaintTrackable.getFrom(living);
			if (from!=null && from.isInTaintBiome()) {
				float progress = Math.min(20, from.getTimeInTaintBiome()) / 30f;
				fog.setNearPlaneDistance((1 - progress) * fog.getFarPlaneDistance() * .75f);
				fog.setFarPlaneDistance(fog.getFarPlaneDistance() * (1 - .8f * progress));
			}
		}
	}
}