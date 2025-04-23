package com.wonginnovations.arcana.client.research;

import com.wonginnovations.arcana.client.research.impls.*;
import com.wonginnovations.arcana.systems.research.EntrySection;
import com.wonginnovations.arcana.systems.research.impls.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public interface EntrySectionRenderer<T extends EntrySection>{
	
	Map<String, EntrySectionRenderer<?>> map = new HashMap<>();
	
	static void init() {
		map.put(StringSection.TYPE, new StringSectionRenderer());
		map.put(CraftingSection.TYPE, new CraftingSectionRenderer());
		map.put(SmeltingSection.TYPE, new SmeltingSectionRenderer());
		map.put(AlchemySection.TYPE, new AlchemySectionRenderer());
		map.put(ArcaneCraftingSection.TYPE, new ArcaneCraftingSectionRenderer());
		map.put(ImageSection.TYPE, new ImageSectionRenderer());
		map.put(AspectCombosSection.TYPE, new AspectCombosSectionRenderer());
	}
	
	void render(GuiGraphics guiGraphics, T section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player);
	
	void renderAfter(GuiGraphics guiGraphics, T section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player);
	
	int span(T section, Player player);
	
	/**
	 * Called when the mouse is clicked anywhere on the screen while this section is visible.
	 *  @param section
	 * 		The section that is visible.
	 * @param pageIndex
	 * 		The index within the section that is visible.
	 * @param screenWidth
 * 		The width of the screen.
	 * @param screenHeight
* 		The height of the screen.
	 * @param mouseX
* 		The x location of the mouse.
	 * @param mouseY
* 		The y location of the mouse.
	 * @param right
* 		Whether the section that is visible is on the left or right.
	 * @param player
	 */
	default boolean onClick(T section, int pageIndex, int screenWidth, int screenHeight, double mouseX, double mouseY, boolean right, Player player) {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	static <T extends EntrySection> EntrySectionRenderer<T> get(String type) {
		return (EntrySectionRenderer<T>)map.get(type);
	}
	
	@SuppressWarnings("unchecked")
	static <T extends EntrySection> EntrySectionRenderer<T> get(EntrySection type) {
		return (EntrySectionRenderer<T>)map.get(type.getType());
	}
	
	default Minecraft mc() {
		return Minecraft.getInstance();
	}
	
	default Font fr() {
		return mc().font;
	}
}