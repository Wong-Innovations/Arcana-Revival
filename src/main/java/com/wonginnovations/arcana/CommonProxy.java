package com.wonginnovations.arcana;

import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;


/**
 * Common Proxy
 *
 * @author Atlas
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy{
	
	public void construct() {
	}
	
	public void preInit(FMLCommonSetupEvent event) {
	}

	public Player getPlayerOnClient() {
		return null;
	}
	
	public Level getLevelOnClient() {
		return null;
	}
	
	public void scheduleOnClient(Runnable runnable) {
	}
	
	public ItemStack getAspectItemStackForDisplay() {
		return AspectUtils.getItemStackForAspect(Aspects.DUMMY);
	}
}