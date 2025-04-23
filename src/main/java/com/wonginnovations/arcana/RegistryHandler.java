package com.wonginnovations.arcana;

import com.wonginnovations.arcana.commands.NodeCommand;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import static com.wonginnovations.arcana.ClientProxy.SWAP_FOCUS_BINDING;

@EventBusSubscriber(modid = Arcana.MODID)
public class RegistryHandler {
	
//	@SubscribeEvent(priority = EventPriority.HIGH)
//	public static void onItemRegister(RegisterEvent event) {
//		if (event.getRegistryKey().equals(ForgeRegistries.ITEMS.getRegistryKey())) {
//			AspectUtils.register();
//			IForgeRegistry<Item> registry = event.getForgeRegistry();
//			ArcanaBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).forEach(block -> {
//				Item.Properties properties = new Item.Properties();
//				if (block != ArcanaBlocks.WARDENED_BLOCK.orElse(null)
//						&& block != ArcanaBlocks.VACUUM_BLOCK.orElse(null)
//						&& block != ArcanaBlocks.LIGHT_BLOCK.orElse(null)) {
//					Item blockItem = block instanceof CrystalClusterBlock ? new CrystalClusterItem(block, properties, 3) : new BlockItem(block, properties);
//					blockItem.setRegistryName(block.getRegistryName());
//					registry.register(, blockItem);
//				}
//			});
//		}
//	}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		NodeCommand.register(event.getDispatcher());
	}

	@SubscribeEvent
	public static void register(RegisterKeyMappingsEvent event) {
		event.register(SWAP_FOCUS_BINDING);
	}

}