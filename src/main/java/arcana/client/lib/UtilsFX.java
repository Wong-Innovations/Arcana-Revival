package arcana.client.lib;


import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.IForgeGuiGraphics;
import arcana.api.aspects.Aspect;
import arcana.client.fx.ParticleEngine;
import arcana.common.config.ModConfig;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UtilsFX {
    static DecimalFormat myFormatter = new DecimalFormat("#######.##");
    public static boolean hideStackOverlay = false;

    public static final VertexFormat POSITION_TEX_COLOR_LIGHTMAP_NORMAL = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder()
            .put("Position", DefaultVertexFormat.ELEMENT_POSITION)
            .put("UV0", DefaultVertexFormat.ELEMENT_UV0)
            .put("Color", DefaultVertexFormat.ELEMENT_COLOR)
            .put("UV2", DefaultVertexFormat.ELEMENT_UV2)
            .put("Normal", DefaultVertexFormat.ELEMENT_NORMAL)
            .put("Padding", DefaultVertexFormat.ELEMENT_PADDING)
            .build());

    public static ShaderInstance SHADER_POSITION_TEX_COLOR_LIGHTMAP_NORMAL;

    public static ShaderInstance getPositionTexColorLightmapNormalShader() {
        return SHADER_POSITION_TEX_COLOR_LIGHTMAP_NORMAL;
    }

    public static void drawTexturedQuad(PoseStack poseStack, float x, float y, float uOffset, float vOffset, float uWidth, float vHeight, double zLevel) {
        float var7 = 0.00390625f;
        float var8 = 0.00390625f;
        Matrix4f pMatrix = poseStack.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(pMatrix, x + 0.0f, y + vHeight, (float) zLevel).uv((uOffset + 0.0f) * var7, (vOffset + vHeight) * var8).endVertex();
        bufferbuilder.vertex(pMatrix, x + uWidth, y + vHeight, (float) zLevel).uv((uOffset + uWidth) * var7, (vOffset + vHeight) * var8).endVertex();
        bufferbuilder.vertex(pMatrix, x + uWidth, y + 0.0f, (float) zLevel).uv((uOffset + uWidth) * var7, (vOffset + 0.0f) * var8).endVertex();
        bufferbuilder.vertex(pMatrix, x + 0.0f, y + 0.0f, (float) zLevel).uv((uOffset + 0.0f) * var7, (vOffset + 0.0f) * var8).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    public static void drawTexturedQuadF(PoseStack poseStack, float x, float y, float uOffset, float vOffset, float uWidth, float vHeight, double zLevel) {
        var pMatrix = poseStack.last().pose();
        final float d = 0.0625f;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(pMatrix, x + 0.0f, y + 16.0f, (float) zLevel).uv((uOffset + 0.0f) * d, (vOffset + vHeight) * d).endVertex();
        buffer.vertex(pMatrix, x + 16.0f, y + 16.0f, (float) zLevel).uv((uOffset + uWidth) * d, (vOffset + vHeight) * d).endVertex();
        buffer.vertex(pMatrix, x + 16.0f, y + 0.0f, (float) zLevel).uv((uOffset + uWidth) * d, (vOffset + 0.0f) * d).endVertex();
        buffer.vertex(pMatrix, x + 0.0f, y + 0.0f, (float) zLevel).uv((uOffset + 0.0f) * d, (vOffset + 0.0f) * d).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    public static void drawTexturedQuadFull(PoseStack pose, float x, float y, double zLevel) {
        var pMatrix = pose.last().pose();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(pMatrix, x + 0.0f, y + 16.0f, (float) zLevel).uv(0.0f, 1.0f).endVertex();
        buffer.vertex(pMatrix, x + 16.0f, y + 16.0f, (float) zLevel).uv(1.0f, 1.0f).endVertex();
        buffer.vertex(pMatrix, x + 16.0f, y + 0.0f, (float) zLevel).uv(1.0f, 0.0f).endVertex();
        buffer.vertex(pMatrix, x + 0.0f, y + 0.0f, (float) zLevel).uv(0.0f, 0.0f).endVertex();
        BufferUploader.drawWithShader(buffer.end());
    }

    public static void drawCustomTooltip(GuiGraphics pGuiGraphics, Screen gui, Font fr, List<String> textList, int x, int y, int subTipColor) {
        drawCustomTooltip(pGuiGraphics, gui, fr, textList, x, y, subTipColor, false);
    }

    public static void drawCustomTooltip(GuiGraphics pGuiGraphics, Screen gui, Font fr, List<String> textList, int x, int y, int subTipColor, boolean ignoremouse) {
        PoseStack poseStack = pGuiGraphics.pose();
        if (!textList.isEmpty()) {
            Minecraft mc = Minecraft.getInstance();
            Window scaledresolution = mc.getWindow();
            int sf = (int) scaledresolution.getGuiScale();
            int max = 240;
            int mx = x;
            boolean flip = false;
            if (!ignoremouse && (max + 24) * sf + mx > mc.getWindow().getWidth()) {
                max = (mc.getWindow().getWidth() - mx) / sf - 24;
                if (max < 120) {
                    max = 240;
                    flip = true;
                }
            }
            int widestLineWidth = 0;
            Iterator<String> textLineEntry = textList.iterator();
            boolean b = false;
            while (textLineEntry.hasNext()) {
                String textLine = textLineEntry.next();
                if (fr.width(textLine) > max) {
                    b = true;
                    break;
                }
            }
            if (b) {
                List tl = new ArrayList<>();
                for (String o : textList) {
                    List tl2 = fr.split(FormattedText.of(o), o.startsWith("@@") ? (max * 2) : max);
                    for (Object o2 : tl2) {
                        String textLine3 = ((String) o2).trim();
                        if (o.startsWith("@@")) {
                            textLine3 = "@@" + textLine3;
                        }
                        tl.add(textLine3);
                    }
                }
                textList = tl;
            }
            Iterator<String> textLines = textList.iterator();
            int totalHeight = -2;
            while (textLines.hasNext()) {
                String textLine4 = textLines.next();
                int lineWidth = fr.width(textLine4);
                if (textLine4.startsWith("@@") /*&& !fr.getUnicodeFlag()*/) {
                    lineWidth /= 2;
                }
                if (lineWidth > widestLineWidth) {
                    widestLineWidth = lineWidth;
                }
                totalHeight += ((textLine4.startsWith("@@") /*&& !fr.getUnicodeFlag()*/) ? 7 : 10);
            }
            int sX = x + 12;
            int sY = y - 12;
            if (textList.size() > 1) {
                totalHeight += 2;
            }
            if (sY + totalHeight > scaledresolution.getGuiScaledHeight()) {
                sY = scaledresolution.getGuiScaledHeight() - totalHeight - 5;
            }
            if (flip) {
                sX -= widestLineWidth + 24;
            }
            poseStack.pushPose();
            poseStack.translate(0, 0, 300);
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            Matrix4f matrix4f = poseStack.last().pose();
            int backgroundColor = IForgeGuiGraphics.DEFAULT_BACKGROUND_COLOR;
            drawGradientRect(matrix4f, bufferbuilder, sX - 3, sY - 4, sX + widestLineWidth + 3, sY - 3, backgroundColor, backgroundColor);
            drawGradientRect(matrix4f, bufferbuilder, sX - 3, sY + totalHeight + 3, sX + widestLineWidth + 3, sY + totalHeight + 4, backgroundColor, backgroundColor);
            drawGradientRect(matrix4f, bufferbuilder, sX - 3, sY - 3, sX + widestLineWidth + 3, sY + totalHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(matrix4f, bufferbuilder, sX - 4, sY - 3, sX - 3, sY + totalHeight + 3, backgroundColor, backgroundColor);
            drawGradientRect(matrix4f, bufferbuilder, sX + widestLineWidth + 3, sY - 3, sX + widestLineWidth + 4, sY + totalHeight + 3, backgroundColor, backgroundColor);
            int borderColorStart = IForgeGuiGraphics.DEFAULT_BORDER_COLOR_START;
            int borderColorEnd = IForgeGuiGraphics.DEFAULT_BORDER_COLOR_END;
            drawGradientRect(matrix4f, bufferbuilder, sX - 3, sY - 3 + 1, sX - 3 + 1, sY + totalHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(matrix4f, bufferbuilder, sX + widestLineWidth + 2, sY - 3 + 1, sX + widestLineWidth + 3, sY + totalHeight + 3 - 1, borderColorStart, borderColorEnd);
            drawGradientRect(matrix4f, bufferbuilder, sX - 3, sY - 3, sX + widestLineWidth + 3, sY - 3 + 1, borderColorStart, borderColorStart);
            drawGradientRect(matrix4f, bufferbuilder, sX - 3, sY + totalHeight + 2, sX + widestLineWidth + 3, sY + totalHeight + 3, borderColorEnd, borderColorEnd);
            RenderSystem.enableDepthTest();
//            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            BufferUploader.drawWithShader(bufferbuilder.end());
            RenderSystem.disableBlend();
//            RenderSystem.enableTexture();
            for (int i = 0; i < textList.size(); ++i) {
                poseStack.pushPose();
                poseStack.translate((float) sX, (float) sY, 0.0f);
                String tl3 = textList.get(i);
                boolean shift = false;
                poseStack.pushPose();
                if (tl3.startsWith("@@") /*&& !fr.getUnicodeFlag()*/) {
                    sY += 7;
                    poseStack.scale(0.5f, 0.5f, 1.0f);
                    shift = true;
                } else {
                    sY += 10;
                }
                tl3 = tl3.replaceAll("@@", "");
                if (subTipColor != 0XFFFFFF9D) {
                    if (i == 0) {
                        tl3 = "§" + Integer.toHexString(subTipColor) + tl3;
                    } else {
                        tl3 = "§7" + tl3;
                    }
                }
                poseStack.translate(0.0, 0.0, 301.0);
                pGuiGraphics.drawString(fr, tl3, 0.0f, shift ? 3.0f : 0.0f, -1, true);
                poseStack.popPose();
                if (i == 0) {
                    sY += 2;
                }
                poseStack.popPose();
            }
            poseStack.popPose();
            poseStack.translate(0,0, -poseStack.last().pose().m32());
        }
    }

    public static void renderQuadFromIcon(@NotNull PoseStack pPoseStack, TextureAtlasSprite icon, float scale, float red, float green, float blue, int brightness, int blend, float opacity) {
        boolean blendon = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean depthon = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        RenderSystem.setShaderTexture(0, icon.atlasLocation());
        float f1 = icon.getU1();
        float f2 = icon.getV0();
        float f3 = icon.getU0();
        float f4 = icon.getV1();
        pPoseStack.scale(scale, scale, scale);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(770, blend);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(red, green, blue, opacity);

        Matrix4f matrix = pPoseStack.last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        if (brightness > -1) {
            RenderSystem.setShader(UtilsFX::getPositionTexColorLightmapNormalShader);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, POSITION_TEX_COLOR_LIGHTMAP_NORMAL);
        } else {
            RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);
            bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        }

        bufferBuilder.vertex(matrix, 0.0F, 0.0F, 0.0F).uv(f1, f4).color(red, green, blue, opacity);
        if (brightness > -1) {
            bufferBuilder.uv2(brightness);
        }
        bufferBuilder.normal(0.0F, 0.0F, 1.0F).endVertex();

        bufferBuilder.vertex(matrix, 1.0F, 0.0F, 0.0F).uv(f3, f4).color(red, green, blue, opacity);
        if (brightness > -1) {
            bufferBuilder.uv2(brightness);
        }
        bufferBuilder.normal(0.0F, 0.0F, 1.0F).endVertex();

        bufferBuilder.vertex(matrix, 1.0F, 1.0F, 0.0F).uv(f3, f2).color(red, green, blue, opacity);
        if (brightness > -1) {
            bufferBuilder.uv2(brightness);
        }
        bufferBuilder.normal(0.0F, 0.0F, 1.0F);
        bufferBuilder.endVertex();

        bufferBuilder.vertex(matrix, 0.0F, 1.0F, 0.0F).uv(f1, f2).color(red, green, blue, opacity);
        if (brightness > -1) {
            bufferBuilder.uv2(brightness);
        }
        bufferBuilder.normal(0.0F, 0.0F, 1.0F);
        bufferBuilder.endVertex();

        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.blendFunc(770, 771);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (!blendon) {
            RenderSystem.disableBlend();
        }
        if (!blendon) {
            RenderSystem.disableDepthTest();
        }

    }

    public static void drawTag(GuiGraphics pGuiGraphics, int x, int y, Aspect aspect, float amt, int bonus, double z) {
        drawTag(pGuiGraphics, x, y, aspect, amt, bonus, z, GlConst.GL_ONE_MINUS_SRC_ALPHA, 1.0f, false);
    }

    public static void drawTag(GuiGraphics pGuiGraphics, int x, int y, Aspect aspect, float amount, int bonus, double z, int blend, float alpha, boolean bw) {
        drawTag(pGuiGraphics, x, (double) y, aspect, amount, bonus, z, blend, alpha, bw);
    }

    public static void drawTag(GuiGraphics pGuiGraphics, double x, double y, Aspect aspect, float amount, int bonus, double z, int blend, float alpha, boolean bw) {
        PoseStack poseStack = pGuiGraphics.pose();
        if (aspect == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        Color color = new Color(aspect.getColor());
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, blend);

        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, aspect.getImage());
        if (!bw) {
            RenderSystem.setShaderColor(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, alpha);
        } else {
            RenderSystem.setShaderColor(0.1f, 0.1f, 0.1f, alpha * 0.8f);
        }
        pGuiGraphics.blit(aspect.getImage(), 0, 0, 0, 0, 16, 16, 16, 16);
        poseStack.popPose();

        if (amount > 0.0f) {
            poseStack.pushPose();
            float q = 0.5f;
            if (!ModConfig.CONFIG_GRAPHICS.largeTagText.get()) {
                poseStack.scale(0.5f, 0.5f, 0.5f);
                q = 1.0f;
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            String am = UtilsFX.myFormatter.format(amount);
            int sw = mc.font.width(am);
            for (Direction e : Direction.Plane.HORIZONTAL) {
                pGuiGraphics.drawString(mc.font, am, (32 - sw + (int) x * 2) * q + e.getStepX(), (32 - mc.font.lineHeight + (int) y * 2) * q + e.getStepZ(), 0, false);
            }
            pGuiGraphics.drawString(mc.font, am, (32 - sw + (int) x * 2) * q, (32 - mc.font.lineHeight + (int) y * 2) * q, 0XFFFFFF, false);
            poseStack.popPose();
        }
        if (bonus > 0) {
            poseStack.pushPose();
            RenderSystem.setShaderTexture(0, ParticleEngine.particleTexture);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            int px = 16 * (mc.player.tickCount % 16);
            drawTexturedQuad(poseStack, (float) ((int) x - 4), (float) ((int) y - 4), (float) px, 80.0f, 16.0f, 16.0f, z);
            if (bonus > 1) {
                float q2 = 0.5f;
                if (!ModConfig.CONFIG_GRAPHICS.largeTagText.get()) {
                    poseStack.scale(0.5f, 0.5f, 0.5f);
                    q2 = 1.0f;
                }
                String am2 = "" + bonus;
                int sw2 = mc.font.width(am2) / 2;
                poseStack.translate(0.0, 0.0, -1.0);
                pGuiGraphics.drawString(mc.font, am2, (8 - sw2 + (int) x * 2) * q2, (15 - mc.font.lineHeight + (int) y * 2) * q2, 0XFFFFFF, true);
            }
            poseStack.popPose();
        }
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
    }

    public static void drawColoredTag(GuiGraphics pGuiGraphics, double x, double y, Aspect aspect, float amount, int bonus, double z) {
        drawColoredTag(pGuiGraphics, x, y, aspect, amount, bonus, z, GlConst.GL_ONE_MINUS_SRC_ALPHA, 1.0F);
    }

    public static void drawColoredTag(GuiGraphics pGuiGraphics, double x, double y, Aspect aspect, float amount, int bonus, double z, int blend, float alpha) {
        PoseStack poseStack = pGuiGraphics.pose();
        if (aspect == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, blend);

        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, aspect.getImage());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        pGuiGraphics.blit(aspect.getImage(), 0, 0, 0, 0, 16, 16, 16, 16);
        poseStack.popPose();

        if (amount > 0.0f) {
            poseStack.pushPose();
            float q = 0.5f;
            if (!ModConfig.CONFIG_GRAPHICS.largeTagText.get()) {
                poseStack.scale(0.5f, 0.5f, 0.5f);
                q = 1.0f;
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            String am = UtilsFX.myFormatter.format(amount);
            int sw = mc.font.width(am);
            for (Direction e : Direction.Plane.HORIZONTAL) {
                pGuiGraphics.drawString(mc.font, am, (32 - sw + (int) x * 2) * q + e.getStepX(), (32 - mc.font.lineHeight + (int) y * 2) * q + e.getStepZ(), 0, false);
            }
            pGuiGraphics.drawString(mc.font, am, (32 - sw + (int) x * 2) * q, (32 - mc.font.lineHeight + (int) y * 2) * q, 0XFFFFFF, false);
            poseStack.popPose();
        }
        if (bonus > 0) {
            poseStack.pushPose();
            RenderSystem.setShaderTexture(0, ParticleEngine.particleTexture);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            int px = 16 * (mc.player.tickCount % 16);
            drawTexturedQuad(poseStack, (float) ((int) x - 4), (float) ((int) y - 4), (float) px, 80.0f, 16.0f, 16.0f, z);
            if (bonus > 1) {
                float q2 = 0.5f;
                if (!ModConfig.CONFIG_GRAPHICS.largeTagText.get()) {
                    poseStack.scale(0.5f, 0.5f, 0.5f);
                    q2 = 1.0f;
                }
                String am2 = "" + bonus;
                int sw2 = mc.font.width(am2) / 2;
                poseStack.translate(0.0, 0.0, -1.0);
                pGuiGraphics.drawString(mc.font, am2, (8 - sw2 + (int) x * 2) * q2, (15 - mc.font.lineHeight + (int) y * 2) * q2, 0XFFFFFF, true);
            }
            poseStack.popPose();
        }
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
    }

    public static void renderAspect(GuiGraphics pGuiGraphics, Aspect aspect, int x, int y){
        pGuiGraphics.blit(aspect.getImage(), x, y, 1, 0, 0, 16, 16, 16, 16);
    }

    public static void renderAspectWithAmount(GuiGraphics pGuiGraphics, Aspect aspect, float amount, int x, int y){
        renderAspectWithAmount(pGuiGraphics, aspect, amount, x, y, aspect.getColor());
    }

    public static void renderAspectWithAmount(GuiGraphics pGuiGraphics, Aspect aspect, float amount, int x, int y, int color){
        Minecraft mc = Minecraft.getInstance();
        PoseStack poseStack = pGuiGraphics.pose();
        // render aspect
        renderAspect(pGuiGraphics, aspect, x, y);
        // render amount, if there is a fractional part, round it
        String s = (amount % 1 > 0.1) ? String.format("%.1f", amount) : String.format("%.0f", amount);
        poseStack.translate(0, 0, 200.0F);
        pGuiGraphics.drawString(mc.font, s, x + 19 - mc.font.width(s), y + 10, 0xFFFFFFFF, true);
    }

    public static void drawGradientRect(Matrix4f pMatrix, BufferBuilder pBuilder, int startX, int startY, int endX, int endY, int startColor, int endColor) {
        float startAlpha = (startColor >> 24 & 0xFF) / 255.0f;
        float startRed = (startColor >> 16 & 0xFF) / 255.0f;
        float startGreen = (startColor >> 8 & 0xFF) / 255.0f;
        float startBlue = (startColor & 0xFF) / 255.0f;
        float endAlpha = (endColor >> 24 & 0xFF) / 255.0f;
        float endRed = (endColor >> 16 & 0xFF) / 255.0f;
        float endGreen = (endColor >> 8 & 0xFF) / 255.0f;
        float endBlue = (endColor & 0xFF) / 255.0f;
        pBuilder.vertex(pMatrix, endX, startY, 300.0f).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        pBuilder.vertex(pMatrix, startX, startY, 300.0f).color(startRed, startGreen, startBlue, startAlpha).endVertex();
        pBuilder.vertex(pMatrix, startX, endY, 300.0f).color(endRed, endGreen, endBlue, endAlpha).endVertex();
        pBuilder.vertex(pMatrix, endX, endY, 300.0f).color(endRed, endGreen, endBlue, endAlpha).endVertex();
    }

    public static boolean renderItemStack(GuiGraphics pGuiGraphics, Minecraft mc, ItemStack itm, int x, int y, String txt) {
        PoseStack poseStack = pGuiGraphics.pose();
        boolean rc = false;
        if (itm != null && !itm.isEmpty()) {
            rc = true;
            poseStack.pushPose();
            poseStack.translate(0.0f, 0.0f, 32.0f);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            pGuiGraphics.renderItemDecorations(mc.font, itm, x, y);
            if (!UtilsFX.hideStackOverlay) {
                pGuiGraphics.renderItemDecorations(mc.font, itm, x, y, txt);
            }
            poseStack.popPose();
        }
        return rc;
    }

//    public static void renderItemIn2D(PoseStack pPoseStack, ResourceLocation sprite, float thickness) {
//        renderItemIn2D(pPoseStack, Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(sprite), thickness);
//    }
//
//    public static void renderItemIn2D(PoseStack pPoseStack, TextureAtlasSprite icon, float thickness) {
//        pPoseStack.pushPose();
//        float minu = icon.getU0();
//        float minv = icon.getV0();
//        float maxu = icon.getU1();
//        float maxv = icon.getV1();
//        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
//        renderTextureIn3D(pPoseStack, maxu, maxv, minu, minv, 16, 16, thickness);
//        pPoseStack.popPose();
//    }

    // TODO: add lightmap cuz uhhhh that quill is illuminated in the dark lol
    public static void renderTextureIn3D(PoseStack pPoseStack, float maxu, float maxv, float minu, float minv, int width, int height, float thickness) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorNormalShader);

        Matrix4f matrix = pPoseStack.last().pose();

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        bufferBuilder.vertex(matrix, 0, 0, 0).uv(maxu, maxv).color(0xFFFFFFFF).normal(0, 0, 1).endVertex();
        bufferBuilder.vertex(matrix, 1, 0, 0).uv(minu, maxv).color(0xFFFFFFFF).normal(0, 0, 1).endVertex();
        bufferBuilder.vertex(matrix, 1, 1, 0).uv(minu, minv).color(0xFFFFFFFF).normal(0, 0, 1).endVertex();
        bufferBuilder.vertex(matrix, 0, 1, 0).uv(maxu, minv).color(0xFFFFFFFF).normal(0, 0, 1).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        bufferBuilder.vertex(matrix, 0, 1, 0 - thickness).uv(maxu, minv).color(0xFFFFFFFF).normal(0, 0, -1).endVertex();
        bufferBuilder.vertex(matrix, 1, 1, 0 - thickness).uv(minu, minv).color(0xFFFFFFFF).normal(0, 0, -1).endVertex();
        bufferBuilder.vertex(matrix, 1, 0, 0 - thickness).uv(minu, maxv).color(0xFFFFFFFF).normal(0, 0, -1).endVertex();
        bufferBuilder.vertex(matrix, 0, 0, 0 - thickness).uv(maxu, maxv).color(0xFFFFFFFF).normal(0, 0, -1).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());

        float f5 = 0.5f * (maxu - minu) / width;
        float f6 = 0.5f * (maxv - minv) / height;

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        for (int k = 0; k < width; ++k) {
            float f7 = k / (float) width;
            float f8 = maxu + (minu - maxu) * f7 - f5;
            bufferBuilder.vertex(matrix, f7, 0.0F, 0.0f - thickness).uv(f8, maxv).color(0xFFFFFFFF).normal(-1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, f7, 0.0F, 0.0F).uv(f8, maxv).color(0xFFFFFFFF).normal(-1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, f7, 1.0F, 0.0F).uv(f8, minv).color(0xFFFFFFFF).normal(-1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, f7, 1.0F, 0.0f - thickness).uv(f8, minv).color(0xFFFFFFFF).normal(-1.0f, 0.0f, 0.0f).endVertex();
        }
        BufferUploader.drawWithShader(bufferBuilder.end());

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        for (int k = 0; k < width; ++k) {
            float f7 = k / (float) width;
            float f8 = maxu + (minu - maxu) * f7 - f5;
            float f9 = f7 + 1.0f / width;
            bufferBuilder.vertex(matrix, f9, 1.0F, 0.0f - thickness).uv(f8, minv).color(0xFFFFFFFF).normal(1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, f9, 1.0F, 0.0F).uv(f8, minv).color(0xFFFFFFFF).normal(1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, f9, 0.0F, 0.0F).uv(f8, maxv).color(0xFFFFFFFF).normal(1.0f, 0.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, f9, 0.0F, 0.0f - thickness).uv(f8, maxv).color(0xFFFFFFFF).normal(1.0f, 0.0f, 0.0f).endVertex();
        }
        BufferUploader.drawWithShader(bufferBuilder.end());

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        for (int k = 0; k < height; ++k) {
            float f7 = k / (float) height;
            float f8 = maxv + (minv - maxv) * f7 - f6;
            float f9 = f7 + 1.0f / height;
            bufferBuilder.vertex(matrix, 0.0F, f9, 0.0F).uv(maxu, f8).color(0xFFFFFFFF).normal(0.0f, 1.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, 1.0F, f9, 0.0F).uv(minu, f8).color(0xFFFFFFFF).normal(0.0f, 1.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, 1.0F, f9, 0.0f - thickness).uv(minu, f8).color(0xFFFFFFFF).normal(0.0f, 1.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, 0.0F, f9, 0.0f - thickness).uv(maxu, f8).color(0xFFFFFFFF).normal(0.0f, 1.0f, 0.0f).endVertex();
        }
        BufferUploader.drawWithShader(bufferBuilder.end());

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL);
        for (int k = 0; k < height; ++k) {
            float f7 = k / (float) height;
            float f8 = maxv + (minv - maxv) * f7 - f6;
            bufferBuilder.vertex(matrix, 1.0F, f7, 0.0F).uv(minu, f8).color(0xFFFFFFFF).normal(0.0f, -1.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, 0.0F, f7, 0.0F).uv(maxu, f8).color(0xFFFFFFFF).normal(0.0f, -1.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, 0.0F, f7, 0.0f - thickness).uv(maxu, f8).color(0xFFFFFFFF).normal(0.0f, -1.0f, 0.0f).endVertex();
            bufferBuilder.vertex(matrix, 1.0F, f7, 0.0f - thickness).uv(minu, f8).color(0xFFFFFFFF).normal(0.0f, -1.0f, 0.0f).endVertex();
        }
        BufferUploader.drawWithShader(bufferBuilder.end());
    }
}
