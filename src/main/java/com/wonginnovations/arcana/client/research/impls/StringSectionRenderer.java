package com.wonginnovations.arcana.client.research.impls;

import com.wonginnovations.arcana.client.gui.ResearchEntryScreen;
import com.wonginnovations.arcana.client.research.EntrySectionRenderer;
import com.wonginnovations.arcana.client.research.TextFormatter;
import com.wonginnovations.arcana.systems.research.impls.StringSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringSectionRenderer implements EntrySectionRenderer<StringSection> {

	private int PAGE_HEIGHT = (int)((ResearchEntryScreen.PAGE_HEIGHT / textScaling()) + 1);

	private static Map<StringSection, List<TextFormatter.Paragraph>> textCache = new HashMap<>();
	private static final int PARAGRAPH_SPACING = 6;

	public String getTranslatedText(StringSection section){
		// TODO: make this only run when needed
		return TextFormatter.process(I18n.get(section.getText()), section).replace("{~sep}", "\n{~sep}\n");
	}

	public int span(StringSection section, Player player){
		List<TextFormatter.Paragraph> paragraphs = textCache.computeIfAbsent(section, s -> TextFormatter.compile(getTranslatedText(s), s));
		int curPage = 1;
		float curPageHeight = 0;
		for(int i = 0; i < paragraphs.size(); i++){
			TextFormatter.Paragraph paragraph = paragraphs.get(i);
			if((curPageHeight + paragraph.getHeight()) < PAGE_HEIGHT)
				curPageHeight += paragraph.getHeight() + PARAGRAPH_SPACING;
			else{
				curPage++;
				curPageHeight = 0;
				if(paragraph.getHeight() < PAGE_HEIGHT)
					// make sure this span gets added to the next line instead
					i--;
			}
		}
		return curPage;
	}

	@Override
	public void render(GuiGraphics pGuiGraphics, StringSection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player){
		List<TextFormatter.Paragraph> paragraphs = textCache.computeIfAbsent(section, s -> TextFormatter.compile(getTranslatedText(s), s));
		pGuiGraphics.pose().pushPose();
		float textScaling = textScaling();
		pGuiGraphics.pose().scale(textScaling, textScaling, 1);
		int x = right ? ResearchEntryScreen.PAGE_X + ResearchEntryScreen.RIGHT_X_OFFSET : ResearchEntryScreen.PAGE_X;
		float lineX = ((int)((screenWidth - 256) / 2f) + x) / textScaling;
		float curY = ((int)((screenHeight - 181) / 2f) + ResearchEntryScreen.PAGE_Y + ResearchEntryScreen.HEIGHT_OFFSET) / textScaling;
		// pick which paragraphs to display
		int curPage = 0;
		float curPageHeight = 0;
		for(int i = 0; i < paragraphs.size(); i++){
			TextFormatter.Paragraph paragraph = paragraphs.get(i);
			if((curPageHeight + paragraph.getHeight()) < PAGE_HEIGHT){
				if(curPage == pageIndex){
					paragraph.render(pGuiGraphics, (int)lineX, (int)curY, textScaling);
					curY += paragraph.getHeight() + 6;
				}
				curPageHeight += paragraph.getHeight() + PARAGRAPH_SPACING;
			}else{
				curPage++;
				curPageHeight = 0;
				if(paragraph.getHeight() < PAGE_HEIGHT)
					// make sure this span gets added to the next line instead
					i--;
				else if(curPage == pageIndex){
					paragraph.render(pGuiGraphics, (int)lineX, (int)curY, textScaling);
					curY += paragraph.getHeight() + 6;
				}
			}

		}
		pGuiGraphics.pose().popPose();
	}

	@Override
	public void renderAfter(GuiGraphics pGuiGraphics, StringSection section, int pageIndex, int screenWidth, int screenHeight, int mouseX, int mouseY, boolean right, Player player){}

	public static void clearCache(){
		textCache = new HashMap<>();
	}

	public static float textScaling(){
		int scale = (int) Minecraft.getInstance().getWindow().getGuiScale();
//        float textScaling = ResearchEntryScreen.TEXT_SCALING;
//        if(scale * textScaling < 1)
//            textScaling = 1;
//        return textScaling;
		return 1F;
	}
}