package arcana.client.gui.plugins;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class GuiImageButton extends Button {
    ResourceLocation loc;
    int lx;
    int ly;
    int ww;
    int hh;
    public int color;

    public GuiImageButton(int pX, int pY, int pWidth, int pHeight, Component pMessage, CreateNarration pCreateNarration, ResourceLocation loc, int lx, int ly, int ww, int hh, int color, OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pMessage, pOnPress, pCreateNarration);
        this.color = color;
        this.loc = loc;
        this.lx = lx;
        this.ly = ly;
        this.ww = ww;
        this.hh = hh;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        PoseStack poseStack = pGuiGraphics.pose();
        if (visible) {
            Font font = Minecraft.getInstance().font;
            isHovered = (pMouseX >= getX() - width / 2 && pMouseY >= getY() - height / 2 && pMouseX < getX() - width / 2 + width && pMouseY < getY() - height / 2 + height);
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
            Color c = new Color(color);
            float cc = 0.9f;
            float ac = 1.0f;
            if (isHovered) {
                ac = 1.0f;
                cc = 1.0f;
            }
            if (!active) {
                cc = 0.5f;
                ac = 0.9f;
            }
            RenderSystem.setShaderColor(cc * (c.getRed() / 255.0f), cc * (c.getGreen() / 255.0f), cc * (c.getBlue() / 255.0f), ac);
            RenderSystem.setShaderTexture(0, loc);
            pGuiGraphics.blit(loc, getX() - ww / 2, getY() - hh / 2, lx, ly, ww, hh);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            if (!getMessage().getString().isEmpty()) {
                int j = 0xFFFFFF;
                if (!active) {
                    j = 0xA0A0A0;
                } else if (isHovered) {
                    j = 0xFFFFA0;
                }
                poseStack.pushPose();
                poseStack.translate(getX(), getY(), 0.0);
                poseStack.scale(0.5f, 0.5f, 0.0f);
                pGuiGraphics.drawCenteredString(font, getMessage(), 0, -4, j);
                poseStack.popPose();
            }
        }
    }

    @Override
    protected boolean clicked(double pMouseX, double pMouseY) {
        return active && visible && pMouseX >= getX() - width / 2f && pMouseY >= getY() - height / 2f && pMouseX < getX() - width / 2f + width && pMouseY < getY() - height / 2f + height;
    }
}
