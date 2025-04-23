package com.wonginnovations.arcana.client.research.impls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.client.research.RequirementRenderer;
import com.wonginnovations.arcana.systems.research.impls.ResearchCompletedRequirement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;

public class ResearchCompletedRequirementRenderer implements RequirementRenderer<ResearchCompletedRequirement>{
	
	private static final ResourceLocation ICON = Arcana.arcLoc("textures/item/arcanum_open.png");
	
	public void render(GuiGraphics guiGraphics, int x, int y, ResearchCompletedRequirement requirement, int ticks, float partialTicks, Player player) {
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		guiGraphics.blit(ICON, x, y, 0, 0, 16, 16, 16, 16);
	}
	
	public List<Component> tooltip(ResearchCompletedRequirement requirement, Player player) {
		return Collections.singletonList(Component.translatable("requirement.research_completed"));
	}
}