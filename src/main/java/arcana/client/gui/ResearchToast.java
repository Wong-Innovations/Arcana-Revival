package arcana.client.gui;

import arcana.Arcana;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import arcana.api.research.ResearchEntry;
import org.jetbrains.annotations.NotNull;

public class ResearchToast implements Toast {
    ResearchEntry entry;
    private long firstDrawTime;
    private boolean newDisplay;
    ResourceLocation tex;

    public ResearchToast(ResearchEntry entry) {
        tex = new ResourceLocation(Arcana.MODID, "textures/gui/hud.png");
        this.entry = entry;
    }

    @Override
    public @NotNull Visibility render(@NotNull GuiGraphics pGuiGraphics, @NotNull ToastComponent toastGui, long delta) {
        PoseStack poseStack = pGuiGraphics.pose();
        if (newDisplay) {
            firstDrawTime = delta;
            newDisplay = false;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, tex);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        pGuiGraphics.blit(tex, 0, 0, 0, 224, 160, 32);
        GuiResearchBrowser.drawResearchIcon(pGuiGraphics, entry, 6, 8, 0.0f, false);
        pGuiGraphics.drawString(toastGui.getMinecraft().font, Component.translatable("research.complete"), 30, 7, 10631665);
        String s = entry.getLocalizedName();
        float w = (float) toastGui.getMinecraft().font.width(s);
        if (w > 124.0f) {
            w = 124.0f / w;
            poseStack.pushPose();
            poseStack.translate(30.0f, 18.0f, 0.0f);
            poseStack.scale(w, w, w);
            pGuiGraphics.drawString(toastGui.getMinecraft().font, s, 0, 0, 16755465);
            poseStack.popPose();
        } else {
            pGuiGraphics.drawString(toastGui.getMinecraft().font, s, 30, 18, 16755465);
        }
        return (delta - firstDrawTime < 5000L) ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }
}
