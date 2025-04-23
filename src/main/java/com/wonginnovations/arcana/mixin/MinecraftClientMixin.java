package com.wonginnovations.arcana.mixin;

import com.wonginnovations.arcana.ClientProxy;
import com.wonginnovations.arcana.client.gui.SwapFocusScreen;
import com.wonginnovations.arcana.items.MagicDeviceItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
	
	@Shadow
	public abstract void setScreen(@Nullable Screen guiScreen);
	
	@Shadow
	@Nullable
	public LocalPlayer player;

	@Shadow
	@Final
	private static Logger LOGGER;
	
	@Inject(method = "handleKeybinds",
	        at = @At("HEAD"))
	private void processKeyBinds(CallbackInfo ci) {
		try{
			while(ClientProxy.SWAP_FOCUS_BINDING.isDown())
				for (InteractionHand hand : InteractionHand.values())
					if (player != null && player.getItemInHand(hand).getItem() instanceof MagicDeviceItem) {
						if (((MagicDeviceItem)player.getItemInHand(hand).getItem()).canUseSpells() && ((MagicDeviceItem) player.getItemInHand(hand).getItem()).canSwapFocus(player)) {
							setScreen(new SwapFocusScreen(hand));
							break;
						}
					}
		}catch(Exception exception) {
			LOGGER.error("OBJECT TYPE ERROR!!!");
		}
	}
}