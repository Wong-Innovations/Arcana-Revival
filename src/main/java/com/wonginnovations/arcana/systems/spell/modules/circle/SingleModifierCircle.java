package com.wonginnovations.arcana.systems.spell.modules.circle;

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

public class SingleModifierCircle extends SpellModule{
	public Aspect aspect = Aspects.EMPTY;

	@Override
	public boolean isCircleModule() {
		return true;
	}

	@Override
	public String getName() {
		return "single_modifier_circle";
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
	public boolean canConnectSpecial(SpellModule connectingModule) {
		boolean alreadyConnected = false;
		for (SpellModule module : bound) {
			if (module != connectingModule && module instanceof SinModifierCircle) {
				alreadyConnected = true;
				break;
			}
		}
		return (!alreadyConnected && connectingModule instanceof SinModifierCircle);
	}

	@Override
	public Point getSpecialPoint(SpellModule module) {
		Point ret = null;
		if (canConnectSpecial(module)) {
			ret = new Point(x, y);
		}
		return ret;
	}

	@Override
	public SpellModule getConnectionEnd(boolean special) {
		if (parent != null) {
			return parent.getConnectionEnd(special);
		} else if (special) {
			return this;
		} else {
			return null;
		}
	}

	@Override
	public boolean canAssign(int x, int y, Aspect aspect) {
		int relX = this.x - x;
		int relY = this.y - y;

		return (relX >= -8 && relX < 8
				&& relY >= 19 && relY < 35
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
		return 80;
	}

	@Override
	public int getWidth() {
		return 80;
	}

	@Override
	public void renderUnderMouse(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean floating) {
		guiGraphics.blit(SPELL_RESOURCES, mouseX - getWidth() / 2, mouseY - getHeight() / 2, 0, 48, getWidth(), getHeight());
		if (!floating || aspect == Aspects.EMPTY) {
			guiGraphics.blit(SPELL_RESOURCES, mouseX - 8, mouseY - 35, 48, 0, 16, 16);
		} else {
			guiGraphics.renderItem(AspectUtils.getItemStackForAspect(aspect), mouseX - 8, mouseY - 35);
			guiGraphics.renderItemDecorations(Minecraft.getInstance().font, AspectUtils.getItemStackForAspect(aspect), mouseX - 8, mouseY - 35);
		}
	}

	@Override
	public void renderInMinigame(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean floating) {
		guiGraphics.blit(SPELL_RESOURCES, x - getWidth() / 2, y - getHeight() / 2, 0, 48, getWidth(), getHeight());
		if (!floating) {
			if (aspect == Aspects.EMPTY) {
				guiGraphics.blit(SPELL_RESOURCES, x - 8, y - 35, 48, 0, 16, 16);
			} else {
				guiGraphics.renderItem(AspectUtils.getItemStackForAspect(aspect), mouseX - 8, mouseY - 35);
				guiGraphics.renderItemDecorations(Minecraft.getInstance().font, AspectUtils.getItemStackForAspect(aspect), x - 8, y - 35);
			}
		}
	}
}
