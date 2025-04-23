package com.wonginnovations.arcana.client.event;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.client.gui.ArcanaDevOptionsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = Arcana.MODID, value = Dist.CLIENT)
public class InitScreenHandler {
	
	public static final boolean DEBUG_MODE = false;
	
	@SubscribeEvent
	public static void onInitGuiEvent(final ScreenEvent.Init event) {
		if (DEBUG_MODE) {
			final Screen gui = event.getScreen();
			if (gui instanceof PauseScreen) {
				// TODO: I think this can be removed? Lets see once the menu is visible
//				Button rm_button = null;
//				for (final GuiEventListener button : event.getListenersList()) {
//					if (button.getMessage().getString().equals(I18n.get("menu.reportBugs")))
//						rm_button = button;
//				} // You can't report bugs because forge is installed
//				event.removeWidget(rm_button);
				event.addListener(Button.builder(
							Component.translatable("devtools.more"),
							onPress -> event.getScreen().getMinecraft().setScreen(new ArcanaDevOptionsScreen())
						)
						.bounds(gui.width / 2 + 4, gui.height / 4 + 72 + -16, 98, 20)
						.build());
			}
		}
	}
}
