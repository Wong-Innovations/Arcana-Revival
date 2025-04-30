package com.wonginnovations.arcana.client.gui;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.handlers.AspectBattery;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.aspects.handlers.AspectHolder;
import com.wonginnovations.arcana.blocks.entities.AlembicBlockEntity;
import com.wonginnovations.arcana.blocks.entities.CrucibleBlockEntity;
import com.wonginnovations.arcana.blocks.entities.JarBlockEntity;
import com.wonginnovations.arcana.client.ClientAuraHandler;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.items.MagicDeviceItem;
import com.wonginnovations.arcana.items.attachment.Core;
import com.wonginnovations.arcana.items.settings.GogglePriority;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

import static com.wonginnovations.arcana.Arcana.arcLoc;

@Mod.EventBusSubscriber(modid = Arcana.MODID, value = Dist.CLIENT)
public final class Huds {
	
	public static final ResourceLocation FLUX_METER_FRAME = arcLoc("textures/gui/hud/flux_meter_frame.png");
	public static final ResourceLocation FLUX_METER_FILLING = arcLoc("textures/gui/hud/flux_chaos.png");
	
	@SubscribeEvent
	public static void onRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null /* && event.getType().equals(RenderGameOverlayEvent.ElementType.ALL) */) {
			ItemStack mainHand = player.getMainHandItem();
			ItemStack offHand = player.getOffhandItem();

			GuiGraphics guiGraphics = event.getGuiGraphics();
			
			ItemStack wand = ItemStack.EMPTY;
			ItemStack meter = ItemStack.EMPTY;
			if (mainHand.getItem() instanceof MagicDeviceItem) {
				wand = mainHand;
			} else if (offHand.getItem() instanceof MagicDeviceItem) {
				wand = offHand;
			}
			if (mainHand.getItem().equals(ArcanaItems.FLUX_METER.get())) {
				meter = mainHand;
			} else if (offHand.getItem().equals(ArcanaItems.FLUX_METER.get())) {
				meter = offHand;
			}
			
			// wand GUI (high render priority)
			if (wand != ItemStack.EMPTY) {
				Core core = MagicDeviceItem.getCore(wand);
				AspectHandler aspects = AspectHandler.getFrom(wand);
				if (aspects != null) {
					int offX = ArcanaConfig.WAND_HUD_X.get().intValue();
					int offY = ArcanaConfig.WAND_HUD_Y.get().intValue();
					float scale = ArcanaConfig.WAND_HUD_SCALING.get().floatValue();
					int baseX = (int)(ArcanaConfig.WAND_HUD_LEFT.get() ? offX / scale : (event.getWindow().getGuiScaledWidth() - offX) / scale - 49);
					int baseY = (int)(ArcanaConfig.WAND_HUD_TOP.get() ? offY / scale : (event.getWindow().getGuiScaledHeight() - offY) / scale - 49);
					guiGraphics.pose().pushPose();
					guiGraphics.pose().scale(scale, scale, 2);
					ClientUiUtil.renderVisCore(guiGraphics, core, baseX, baseY);
					ClientUiUtil.renderVisMeter(guiGraphics, aspects, baseX, baseY);
					MagicDeviceItem.getFocusStack(wand).ifPresent(item -> guiGraphics.renderItem(item, baseX + 1, baseY + 1));
					if (player.isCrouching())
						ClientUiUtil.renderVisDetailInfo(guiGraphics, aspects);
					guiGraphics.pose().popPose();
				}
				// flux meter GUI
			} else if (meter != ItemStack.EMPTY) {
				// display filling at 8,8
				// 10 frames, 32x100
				int frame = (int)((player.tickCount + event.getPartialTick()) % 10);
				float flux = ClientAuraHandler.currentFlux;
				int pixHeight = (int)Math.min(flux, 100);
				guiGraphics.blit(FLUX_METER_FILLING, 8, 8 + (100 - pixHeight), 0, 100 * frame, 32, pixHeight, 1024, 1024);
				// display the frame at top-left
				guiGraphics.blit(FLUX_METER_FRAME, 0, 0, 0, 0, 48, 116);
				// if flux is over max, flash white
				if (flux > 100) {
					int amount = (int)(Math.abs(((Mth.sin((player.tickCount + event.getPartialTick()) / 3f)) / 3f)) * 255);
					int color = 0x00ffffff | (amount << 24);
					guiGraphics.fillGradient(8, 8, 40, 108, 1, color, color);
				}
				// if the player is sneaking, display the amount of flux "exactly"
				// rounded to 2dp
				if (player.isCrouching())
					guiGraphics.drawString(Minecraft.getInstance().font, String.format("%.2f", flux), 47, 8 + (97 - pixHeight), -1);
			}
			double reach = player.getAttribute(ForgeMod.BLOCK_REACH.get()).getValue();
			Vec3 start = player.getEyePosition(1);
			Vec3 facing = player.getLookAngle();
			Vec3 end = start.add(facing.x * reach, facing.y * reach, facing.z * reach);
			BlockPos targeted = player.level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getBlockPos();
			BlockEntity te = player.level().getBlockEntity(targeted);
			if (te instanceof CrucibleBlockEntity) {
//				CrucibleBlockEntity crucible = (CrucibleBlockEntity)te;
//				GogglePriority priority = GogglePriority.getClientGogglePriority();
//				if (priority == GogglePriority.SHOW_NODE || priority == GogglePriority.SHOW_ASPECTS) {
//					List<AspectStack> stacks = new ArrayList<>(crucible.getAspectStackMap().values());
//					int size = 20;
//					int baseX = (event.getWindow().getGuiScaledWidth() - stacks.size() * size) / 2;
//					int baseY = (event.getWindow().getGuiScaledHeight() - size) / 2 - (ArcanaConfig.BLOCK_HUDS_TOP.get() ? 10 : -10);
//					for (int i = 0, stacksSize = stacks.size(); i < stacksSize; i++) {
//						AspectStack aspStack = stacks.get(i);
//						int x = baseX + i * size;
//						int y = baseY - 10;
//						if (i % 2 == 0)
//							y += 8;
//						ClientUiUtil.renderAspectStack(guiGraphics, aspStack, x, y);
//					}
//				}
			}
			if (te instanceof AlembicBlockEntity alembic) {
                GogglePriority priority = GogglePriority.getClientGogglePriority();
				if (priority == GogglePriority.SHOW_NODE || priority == GogglePriority.SHOW_ASPECTS) {
					AspectBattery stacks = alembic.aspects;
					int size = 20;
					int baseX = (event.getWindow().getGuiScaledWidth() - stacks.countHolders() * size) / 2;
					int baseY = (event.getWindow().getGuiScaledHeight() - size) / 2 - (ArcanaConfig.BLOCK_HUDS_TOP.get() ? 10 : -10);
					for (int i = 0, stacksSize = stacks.countHolders(); i < stacksSize; i++) {
						AspectHolder aspStack = stacks.getHolder(i);
						int x = baseX + i * size;
						int y = baseY - 10;
						if (i % 2 == 0)
							y += 8;
						AspectStack stack1 = aspStack.getStack();
						if (!stack1.isEmpty())
							ClientUiUtil.renderAspectStack(guiGraphics, stack1, x, y);
					}
				}
			}
			if (te instanceof JarBlockEntity jar) {
				GogglePriority priority = GogglePriority.getClientGogglePriority();
				if (priority == GogglePriority.SHOW_NODE || priority == GogglePriority.SHOW_ASPECTS) {
					AspectBattery stacks = jar.vis;
					int size = 20;
					int baseX = (event.getWindow().getGuiScaledWidth() - stacks.countHolders() * size) / 2;
					int baseY = (event.getWindow().getGuiScaledHeight() - size) / 2 - (ArcanaConfig.BLOCK_HUDS_TOP.get() ? 10 : -10);
					for (int i = 0, stacksSize = stacks.countHolders(); i < stacksSize; i++) {
						AspectHolder aspStack = stacks.getHolder(i);
						int x = baseX + i * size;
						int y = baseY - 10;
						if (i % 2 == 0)
							y += 8;
						AspectStack stack1 = aspStack.getStack();
						if (!stack1.isEmpty())
							ClientUiUtil.renderAspectStack(guiGraphics, stack1, x, y);
					}
				}
			}
		}
	}
}