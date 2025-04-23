package com.wonginnovations.arcana.client.gui;

import com.wonginnovations.arcana.client.ClientUtils;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.wonginnovations.arcana.Arcana.MODID;

@OnlyIn(Dist.CLIENT)
public class ArcanaDevOptionsScreen extends Screen {
	
	public ArcanaDevOptionsScreen() {
		super(Component.translatable("devtools.screen"));
	}
	
	@Override
	protected void init() {
		renderables.add(Button.builder(Component.translatable("devtools.arcana_book_editmode"), b -> ClientUtils.openResearchBookUI(new ResourceLocation(MODID, "arcanum"), this, null))
						.pos(this.width / 2 - 102, this.height / 4 + 24 + -16)
						.size(204, 20)
						.build());
		renderables.add(Button.builder(Component.translatable("menu.reportBugs"), b -> minecraft.setScreen(new ConfirmLinkScreen((confirmed) -> {
							if (confirmed)
								Util.getPlatform().openUri("https://aka.ms/snapshotbugs?ref=game");
							minecraft.setScreen(this);
						}, "https://aka.ms/snapshotbugs?ref=game", true)))
						.pos(this.width / 2 - 102, this.height / 4 + 24 + -16 + 24 + 24)
						.size(204, 20)
						.build());

		super.init();
	}
	
	public void tick() {
		super.tick();
	}
	
	public void render(@NotNull GuiGraphics guiGraphics, int p_render_1_, int p_render_2_, float p_render_3_) {
		this.renderBackground(guiGraphics);
		guiGraphics.drawCenteredString(this.font, this.title.getString(), this.width / 2, 15, 16777215);
		guiGraphics.drawCenteredString(this.font, I18n.get("devtools.arcana"), this.width / 2, this.height / 4 + -16 + 6, 16777215);
		guiGraphics.drawCenteredString(this.font, I18n.get("devtools.mojang"), this.width / 2, this.height / 4 + 24 + -16 + 24 + 6, 16777215);
		super.render(guiGraphics, p_render_1_, p_render_2_, p_render_3_);
	}
	
	@Override
	public boolean isPauseScreen() {
		return false;
	}
	
	@Override
	public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
		return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
	}
}