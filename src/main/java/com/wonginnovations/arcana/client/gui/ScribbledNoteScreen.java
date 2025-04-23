package com.wonginnovations.arcana.client.gui;

import com.wonginnovations.arcana.Arcana;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ScribbledNoteScreen extends Screen {
    
    public static final ResourceLocation SCRIBBLED_NOTE_TEXTURE = new ResourceLocation(Arcana.MODID, "textures/gui/research/scribbled_notes.png");

    public ScribbledNoteScreen(Component component) {
        super(component);
    }
    
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        String text = I18n.get("scribbledNote.text");
//        guiGraphics.draw(getMinecraft().font, text, (width - getMinecraft().font.width(text)) / 2f, height / 2f, 1, false);

        String[] lines = text.split("\n");
        int lineHeight = font.lineHeight;
        for (int i = 0; i < lines.length; i++) {
            guiGraphics.drawString(font, lines[i], (width - getMinecraft().font.width(lines[i])) / 2f, height / 2f + i * lineHeight, 1, false);
        }
//        guiGraphics.blit(SCRIBBLED_NOTE_TEXTURE, width, height);
    }
    
    public boolean isPauseScreen() {
        return false;
    }
}