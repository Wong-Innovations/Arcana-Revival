package com.wonginnovations.arcana.systems.spell.modules.core;

import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.systems.spell.modules.SpellModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;

import java.awt.*;
import java.util.Arrays;

import static com.wonginnovations.arcana.systems.spell.SpellState.SPELL_RESOURCES;

public class CastMethod extends SpellModule {
	public Aspect aspect = Aspects.EMPTY;

	@Override
	public String getName() {
		return "cast_method";
	}

	@Override
	public void fromNBT(CompoundTag compound) {
		super.fromNBT(compound);
		aspect = AspectUtils.getAspect(compound, "aspect");
	}

	@Override
	public CompoundTag toNBT(CompoundTag compound) {
		super.toNBT(compound);
		AspectUtils.putAspect(compound, "aspect", aspect);
		return compound;
	}

	@Override
	public int getOutputAmount() {
		return 4;
	}

	@Override
	public boolean canConnect(SpellModule module, boolean special) {
		return (super.canConnect(module, special)
				&& (!special
					|| (module instanceof CastMethodSin
		 				&& this.boundSpecial.isEmpty())));
	}

	@Override
	public boolean canConnectSpecial(SpellModule connectingModule) {
		return (boundSpecial.contains(connectingModule)
				|| (boundSpecial.isEmpty() && connectingModule.isCastModifier()));
	}

	@Override
	public Point getSpecialPoint(SpellModule module) {
		Point ret = null;
		if (canConnectSpecial(module)) {
			ret = new Point(x + 36, y);
		}
		return ret;
	}

	@Override
	public boolean canAssign(int x, int y, Aspect aspect) {
		int relX = this.x - x;
		int relY = this.y - y;

		return (relX >= -8 && relX < 8
				&& relY >= -8 && relY < 8
				&& (aspect == Aspects.EMPTY
					|| Arrays.asList(AspectUtils.primalAspects).contains(aspect)));
	}

	@Override
	public void assign(int x, int y, Aspect aspect) {
		if (canAssign(x, y, aspect)) {
			this.aspect = aspect;
		}
	}

	@Override
	public int getHeight() {
		return 36;
	}

	@Override
	public int getWidth() {
		return 36;
	}

	@Override
	public void renderUnderMouse(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean floating) {
		guiGraphics.blit(SPELL_RESOURCES, mouseX - getWidth() / 2, mouseY - getHeight() / 2, 94, 54, getWidth(), getHeight());
		if (!floating || aspect == Aspects.EMPTY) {
			guiGraphics.blit(SPELL_RESOURCES, mouseX - 8, mouseY - 8, 48, 0, 16, 16);
		} else {
			guiGraphics.renderItem(AspectUtils.getItemStackForAspect(aspect), mouseX - 8, mouseY - 8);
			guiGraphics.renderItemDecorations(Minecraft.getInstance().font, AspectUtils.getItemStackForAspect(aspect), mouseX - 8, mouseY - 8);
		}
	}

	@Override
	public void renderInMinigame(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean floating) {
		guiGraphics.blit(SPELL_RESOURCES, x - getWidth() / 2, y - getHeight() / 2, 94, 54, getWidth(), getHeight());
		if (!floating) {
			if (aspect == Aspects.EMPTY) {
				guiGraphics.blit(SPELL_RESOURCES, x - 8, y - 8, 48, 0, 16, 16);
			} else {
				guiGraphics.renderItem(AspectUtils.getItemStackForAspect(aspect), x - 8, y - 8);
				guiGraphics.renderItemDecorations(Minecraft.getInstance().font, AspectUtils.getItemStackForAspect(aspect), x - 8, y - 8);
			}
		}
	}

	@Override
	public Point getConnectionRenderStart() {
		SpellModule sin = boundSpecial.stream().filter(module -> module instanceof CastMethodSin).findFirst().orElse(null);
		if (sin != null) {
			return new Point(x + 36, y);
		} else {
			return new Point(this.x, this.y);
		}
	}

}
