package com.wonginnovations.arcana.client.research;

import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.client.gui.ClientUiUtil;
import com.wonginnovations.arcana.client.gui.ResearchEntryScreen;
import com.wonginnovations.arcana.client.research.impls.StringSectionRenderer;
import com.wonginnovations.arcana.mixin.FontAccessor;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import com.wonginnovations.arcana.systems.research.ResearchEntry;
import com.wonginnovations.arcana.systems.research.impls.StringSection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TextFormatter {

	private TextFormatter() {
	}

	private static float getTextWidth() {
		return ResearchEntryScreen.PAGE_WIDTH / StringSectionRenderer.textScaling();
	}

	public interface Span {

		void render(GuiGraphics pGuiGraphics, int x, int y);

		float getWidth();

		float getHeight();
	}

	// TODO: text size, shadow
	public static class TextSpan implements Span {

		private final CustomTextStyle renderStyle;
		String text;

		public TextSpan(String text, CustomTextStyle style){
			this.text = text;
			this.renderStyle = style;
		}

		public void render(GuiGraphics pGuiGraphics, int x, int y){
			if(renderStyle.getSize() != 1){
				pGuiGraphics.pose().pushPose();
				pGuiGraphics.pose().scale(renderStyle.getSize(), renderStyle.getSize(), 1);
			}
			renderStringWithCustomFormatting(pGuiGraphics, text, renderStyle, x / renderStyle.getSize(), y / renderStyle.getSize());
			if(renderStyle.getSize() != 1)
				pGuiGraphics.pose().popPose();
		}

		public float getWidth(){
			return width(text, renderStyle) * renderStyle.getSize() * (renderStyle.isSubscript() || renderStyle.isSuperscript() ? .6f : 1);
		}

		public float getHeight(){
			return (9 + (renderStyle.isWavy() ? 1 : 0)) * renderStyle.getSize();
		}
	}

	public static class AspectSpan implements Span {

		Aspect aspect;

		public AspectSpan(Aspect aspect){
			this.aspect = aspect;
		}

		public void render(GuiGraphics pGuiGraphics, int x, int y){
			ClientUiUtil.renderAspect(pGuiGraphics, aspect, x, y);
		}

		public float getWidth(){
			return 16;
		}

		public float getHeight(){
			return 17;
		}
	}

	public static class MultiSpan implements Span{

		private List<Span> spans;

		public MultiSpan(List<Span> spans){
			this.spans = spans;
		}

		public void render(GuiGraphics pGuiGraphics, int x, int y){
			for(Span span : spans){
				span.render(pGuiGraphics, x, y);
				x += span.getWidth();
			}
		}

		public float getWidth(){
			float width = 0;
			for(Span span : spans)
				width += span.getWidth();
			return width;
		}

		public float getHeight(){
			float height = 0;
			for(Span span : spans)
				height = Math.max(span.getHeight(), height);
			return height;
		}
	}

	public interface Paragraph {

		void render(GuiGraphics pGuiGraphics, int x, int y, float scale);

		float getHeight();
	}

	public static class SpanParagraph implements Paragraph {

		List<Span> spans;
		boolean centred;

		List<List<Span>> lines = new ArrayList<>();
		float height;

		public SpanParagraph(List<Span> spans, boolean centred){
			this.spans = spans;
			this.centred = centred;

			// put the spans into different lines and keep track of height
			lines.add(new ArrayList<>());
			int curLine = 0;
			float curLineWidth = 0;
			float curLineHeight = 0;
			for(int i = 0; i < spans.size(); i++){
				Span span = spans.get(i);
				if((curLineWidth + span.getWidth()) < getTextWidth()){
					lines.get(curLine).add(span);
					curLineWidth += span.getWidth() + 5;
					curLineHeight = Math.max(curLineHeight, (span.getHeight() + 1));
				}else{
					curLine++;
					lines.add(new ArrayList<>());
					curLineWidth = 0;
					height += curLineHeight;
					curLineHeight = 0;
					if(span.getWidth() < getTextWidth())
						// make sure this span gets added to the next line instead
						i--;
					else
						lines.get(curLine).add(span);
				}
			}
			height += curLineHeight;
		}

		public SpanParagraph(List<Span> spans){
			this(spans, false);
		}

		public void render(GuiGraphics pGuiGraphics, int x, int y, float scale){
			float curY = 0;
			for(List<Span> line : lines){
				float curX = 0;
				// recaulculate width/height
				// maybe cache these in the future?
				float lineWidth = (float)line.stream().mapToDouble(value -> value.getWidth() + 2).sum();
				float lineHeight = (float)line.stream().mapToDouble(Span::getHeight).max().orElse(1);
				if(centred)
					curX = (getTextWidth() - lineWidth) / 2;
				for(Span span : line){
					span.render(pGuiGraphics, (int)(x + curX), (int)(y + curY + (lineHeight - span.getHeight()) / 2));
					curX += span.getWidth() + 5;
				}
				curY += lineHeight;
			}
		}

		public float getHeight(){
			return height;
		}
	}

	public static class SeparatorParagraph implements Paragraph {

		public void render(GuiGraphics pGuiGraphics, int x, int y, float scale){
			pGuiGraphics.blit(((ResearchEntryScreen)(Minecraft.getInstance().screen)).bg, (int)(x + (getTextWidth() - 86) / 2), y + 3, 29, 184, 86, 3);
		}

		public float getHeight(){
			return 6;
		}
	}

	public static float width(String str, Style style){
		return width(str, style, Minecraft.getInstance().font);
	}

	public static float width(String str, Style style, Font font){
		float ret = 0;
		FontSet fontSet = ((FontAccessor) font).callGetFontSet(style.getFont());
		boolean formatting = false;
		for(char c : str.toCharArray())
			if(c == '\u00a7')
				formatting = true;
			else if(!formatting)
				ret += fontSet.getGlyphInfo(c, true).getAdvance(style.isBold());
			else
				formatting = false;
		return ret;
	}

	public static float width(String str, CustomTextStyle style){
		float ret = 0;
		FontSet fontSet = ((FontAccessor) Minecraft.getInstance().font).callGetFontSet(Style.DEFAULT_FONT);
		boolean formatting = false;
		for(char c : str.toCharArray())
			if(c == '\u00a7')
				formatting = true;
			else if(!formatting)
				ret += fontSet.getGlyphInfo(c, true).getAdvance(style.isBold());
			else
				formatting = false;
		return ret;
	}

	public static List<Paragraph> compile(String in, @Nullable StringSection section) {
		// split up by (\n\n)s
		String[] paragraphs = in.split("\n+");
		List<Paragraph> ret = new ArrayList<>(paragraphs.length);
		for(String paragraph : paragraphs){
			if(paragraph.equals("{~sep}")){
				ret.add(new SeparatorParagraph());
				continue;
			}
			CustomTextStyle curStyle = CustomTextStyle.EMPTY;
			boolean styleNeedsCopy = true, centred = false;
			List<Span> list = new ArrayList<>();
			// splits at spaces
			for(String word : paragraph.split("([ ]+)")){
				List<Span> segments = new ArrayList<>();
				// splits before { and after }
				for(String s : word.split("(?=\\{)|(?<=})")){
					// if it begins with { and ends with }, its a formatting fragment
					if(s.startsWith("{") && s.endsWith("}")){
						s = s.substring(1, s.length() - 1);
						if(s.startsWith("aspect:"))
							list.add(new AspectSpan(Aspects.ASPECTS.get(new ResourceLocation(s.substring(7)))));
							// todo: move config inlining here?
						else if(s.equals("r")){
							curStyle = CustomTextStyle.EMPTY;
							styleNeedsCopy = true;
						}else{
							if(styleNeedsCopy){
								curStyle = curStyle.copy();
								styleNeedsCopy = false;
							}
							if(s.equals("b")) // Boolean formatting
								curStyle.setBold(!curStyle.isBold());
							else if(s.equals("i"))
								curStyle.setItalics(!curStyle.isItalics());
							else if(s.equals("s"))
								curStyle.setStrikethrough(!curStyle.isStrikethrough());
							else if(s.equals("u"))
								curStyle.setUnderline(!curStyle.isUnderline());
							else if(s.equals("o"))
								curStyle.setObfuscated(!curStyle.isObfuscated());
							else if(s.equals("w"))
								curStyle.setWavy(!curStyle.isWavy());
							else if(s.equals("sh"))
								curStyle.setShadow(!curStyle.isShadow());
							else if(s.equals("super"))
								curStyle.setSuperscript(!curStyle.isSuperscript());
							else if(s.equals("sub"))
								curStyle.setSubscript(!curStyle.isSubscript());
								// Vanilla colour codes, prefixed with 'c'
							else if(s.equals("c0")) // Black
								curStyle.setColor(0x000000);
							else if(s.equals("c1")) // Dark Blue
								curStyle.setColor(0x0000aa);
							else if(s.equals("c2")) // Dark Green
								curStyle.setColor(0x00aa00);
							else if(s.equals("c3")) // Dark Aqua
								curStyle.setColor(0x00aaaa);
							else if(s.equals("c4")) // Dark Red
								curStyle.setColor(0xaa0000);
							else if(s.equals("c5")) // Dark Purple
								curStyle.setColor(0xaa00aa);
							else if(s.equals("c6")) // Gold
								curStyle.setColor(0xffaa00);
							else if(s.equals("c7")) // Gray
								curStyle.setColor(0xaaaaaa);
							else if(s.equals("c8")) // Dark Gray
								curStyle.setColor(0x555555);
							else if(s.equals("c9")) // Blue
								curStyle.setColor(0x5555ff);
							else if(s.equals("ca")) // Green
								curStyle.setColor(0x55ff55);
							else if(s.equals("cb")) // Aqua
								curStyle.setColor(0x55ffff);
							else if(s.equals("cc")) // Red
								curStyle.setColor(0xff5555);
							else if(s.equals("cd")) // Light Purple
								curStyle.setColor(0xff55ff);
							else if(s.equals("ce")) // Yellow
								curStyle.setColor(0xffff55);
							else if(s.equals("cf")) // White
								curStyle.setColor(0xffffff);
							else if(s.equals("c")) // Centred
								centred = true;
							else if(s.startsWith("size:"))
								curStyle.setSize(Float.parseFloat(s.substring(5)));
							else if(s.startsWith("colour:"))
								curStyle.setColor(Integer.parseInt(s.substring(7), 16));
						}
					}else if(!s.isEmpty()){
						segments.add(new TextSpan(s, curStyle));
						styleNeedsCopy = true;
					}
				}
				if(segments.size() == 1)
					list.add(segments.get(0));
				else if(!segments.isEmpty())
					list.add(new MultiSpan(segments));
			}
			ret.add(new SpanParagraph(list, centred));
		}
		return ret;
	}

	public static String process(String in, @Nullable StringSection section){
		// Formatted sections appear as such:
		//    {$config:arcana:General.MaxAlembicAir}
		// An open brace, a dollar sign, a formatting type, colon separated parameters, and a closing brace.
		// There's currently only config-formatted sections, but hey, might wanna extend that later.
		if(section != null /* && ArcanaConfig.ENTRY_TITLES.get()*/){
			ResearchEntry entry = ResearchBooks.getEntry(section.getEntry());
			if(entry.sections().get(0) == section)
				in = "{c}{size:1.5}" + I18n.get(entry.name()) + "{r}{~sep}" + in;
		}
		if(in.contains("{$")){
			Pattern findBraces = Pattern.compile("(\\{\\$.*?})");
			Matcher braces = findBraces.matcher(in);
			while(braces.find()){
				String inlineSection = braces.group().substring(2, braces.group().length() - 1);
				String[] parts = inlineSection.split(":");
				String replaceWith = I18n.get("researchEntry.invalidInline", inlineSection);
				String name = parts[0];
				if(name.equals("config") && parts.length == 3)
					replaceWith = inlineConfig(parts[1], parts[2]);
				else if(name.equals("numOfAspects"))
					replaceWith = String.valueOf(Aspects.getWithoutEmpty().size());
				in = in.replace(braces.group(), replaceWith);
			}
		}
		return in;
	}

	public static String inlineConfig(String modid, String configName){
		// iterate through mod containers
		AtomicReference<String> ret = new AtomicReference<>(I18n.get("researchEntry.invalidConfig", modid, configName));
//        ModList.get().forEachModContainer((s, container) -> {
//            if(s.equals(modid))
//                ((ModContainerAccessor)container).getConfigs().forEach((type, config) -> {
//                    if(config.getConfigData().contains(configName))
//                        // Have to cast to integer
//                        // get() returns a T, as in "whatever you ask for"
//                        // Java assumes the char[] version of valueOf and dies trying to cast it
//                        ret.set(String.valueOf((Object)config.getConfigData().get(configName)));
//                });
//        });

		return ret.get();
	}

	// vanilla copy: FontRenderer, line 288
	public static void renderStringWithCustomFormatting(GuiGraphics pGuiGraphics, String text, CustomTextStyle style, float x, float y, FontSet font){
		PoseStack poseStack = pGuiGraphics.pose();
		if(style.isSubscript() || style.isSuperscript()){
			poseStack.pushPose();
			poseStack.scale(.5f, .5f, 1);
			x /= .5f;
			y /= .5f;
			y = style.isSuperscript() ? y - 3 : y + 8;
		}
		MultiBufferSource.BufferSource buffer = pGuiGraphics.bufferSource();
		int colour = style.getColor();
		float red = (float)(colour >> 16 & 255) / 255.0F;
		float green = (float)(colour >> 8 & 255) / 255.0F;
		float blue = (float)(colour & 255) / 255.0F;
		List<BakedGlyph.Effect> effects = new ArrayList<>();
		for(char c : text.toCharArray()){
			GlyphInfo glyph = font.getGlyphInfo(c, true);
			BakedGlyph texturedglyph = style.isObfuscated() && c != 32 ? font.getRandomGlyph(glyph) : font.getGlyph(c);
			VertexConsumer vertexConsumer = buffer.getBuffer(texturedglyph.renderType(Font.DisplayMode.NORMAL));
			if(!(texturedglyph instanceof EmptyGlyph)){
				float boldOffset = style.isBold() ? glyph.getBoldOffset() : 0;
				float shadowOffset = style.isShadow() ? glyph.getShadowOffset() : 0;
				float wavyOffset = style.isWavy() ? Mth.sin(x * 2 + (Minecraft.getInstance().getPartialTick() + Minecraft.getInstance().level.getGameTime()) / 2f) * 1.1f : 0;
				if(style.isShadow()){
					texturedglyph.render(style.isItalics(), x + shadowOffset, y + shadowOffset + wavyOffset, poseStack.last().pose(), vertexConsumer, red * .25f, green * .25f, blue * .25f, .25f, 0xf000f0);
					if(style.isBold())
						texturedglyph.render(style.isItalics(), x + shadowOffset + boldOffset, y + shadowOffset + wavyOffset, poseStack.last().pose(), vertexConsumer, red * .25f, green * .25f, blue * .25f, .25f, 0xf000f0);
				}
				texturedglyph.render(style.isItalics(), x, y + wavyOffset, poseStack.last().pose(), vertexConsumer, red, green, blue, 1, 0xf000f0);
				if(style.isBold())
					texturedglyph.render(style.isItalics(), x + boldOffset, y + wavyOffset, poseStack.last().pose(), vertexConsumer, red, green, blue, 1, 0xf000f0);
			}
			buffer.endBatch();

			float advance = glyph.getAdvance(style.isBold());
			float shadowed = style.isShadow() ? 1 : 0;
			if(style.isStrikethrough())
				effects.add(new BakedGlyph.Effect(x + shadowed - 1, y + shadowed + 4, x + shadowed + advance, y + shadowed + 4.5F - 1, 0.01F, red, green, blue, 1));

			if(style.isUnderline())
				effects.add(new BakedGlyph.Effect(x + shadowed - 1, y + shadowed + 9, x + shadowed + advance, y + shadowed + 9.0F - 1, 0.01F, red, green, blue, 1));
			x += advance;
		}
		BakedGlyph texturedglyph = font.whiteGlyph();
		VertexConsumer vertexConsumer = buffer.getBuffer(texturedglyph.renderType(Font.DisplayMode.NORMAL));
		for(BakedGlyph.Effect effect : effects)
			texturedglyph.renderEffect(effect, poseStack.last().pose(), vertexConsumer, 0xf000f0);
		buffer.endBatch();
		if(style.isSubscript() || style.isSuperscript())
			poseStack.popPose();
	}

	public static void renderStringWithCustomFormatting(GuiGraphics pGuiGraphics, String text, CustomTextStyle style, float x, float y){
		FontSet font = ((FontAccessor) Minecraft.getInstance().font).callGetFontSet(Style.DEFAULT_FONT);
		renderStringWithCustomFormatting(pGuiGraphics, text, style, x, y, font);
	}
}
