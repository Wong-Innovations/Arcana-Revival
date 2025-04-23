package com.wonginnovations.arcana.client.research.impls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wonginnovations.arcana.client.research.RequirementRenderer;
import com.wonginnovations.arcana.systems.research.impls.XpRequirement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;

public class XpRequirementRenderer implements RequirementRenderer<XpRequirement>{
	
	private static final ResourceLocation EXPERIENCE_ORB_TEXTURES = new ResourceLocation("textures/entity/experience_orb.png");
	
	public void render(GuiGraphics guiGraphics, int x, int y, XpRequirement requirement, int ticks, float partialTicks, Player player) {
		doXPRender(guiGraphics, ticks, x, y, partialTicks);
	}
	
	public List<Component> tooltip(XpRequirement requirement, Player player) {
		return Collections.singletonList(Component.translatable("requirement.experience", requirement.getAmount()));
	}
	
	public static void doXPRender(GuiGraphics guiGraphics, int ticks, double x, double y, float partialTicks) {
		final int u = 0, v = 16;
		float f8 = (ticks + partialTicks) / 2f;
		final float i1 = (Mth.sin(f8 + 0.0F) + 1.0F) * 0.5F;
		final float k1 = (Mth.sin(f8 + 4.1887903F) + 1.0F) * 0.1F;
		RenderSystem.setShaderColor(i1, 1.0F, k1, 1.0F);
		guiGraphics.blit(EXPERIENCE_ORB_TEXTURES, (int)x, (int)y, 16, 16, u, v, 16, 16, 64, 64);
//        RenderSystem.disableBlend();
//        RenderSystem.disableRescaleNormal();
	}
}