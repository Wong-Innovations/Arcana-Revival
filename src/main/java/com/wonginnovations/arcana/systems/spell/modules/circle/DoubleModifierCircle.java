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

public class DoubleModifierCircle extends SpellModule {
	public Aspect firstAspect = Aspects.EMPTY;
	public Aspect secondAspect = Aspects.EMPTY;

	@Override
	public boolean isCircleModule() {
		return true;
	}

	@Override
	public String getName() {
		return "double_modifier_circle";
	}

	@Override
	public void fromNBT(CompoundTag compound) {
		super.fromNBT(compound);
		firstAspect = AspectUtils.getAspect(compound, "aspecta");
		secondAspect = AspectUtils.getAspect(compound, "aspectb");
	}

	@Override
	public CompoundTag toNBT(CompoundTag compound) {
		super.toNBT(compound);
		AspectUtils.putAspect(compound, "aspecta", firstAspect);
		AspectUtils.putAspect(compound, "aspectb", secondAspect);
		return compound;
	}

	@Override
	public boolean canConnectSpecial(SpellModule connectingModule) {
		boolean alreadyConnected = false;
		for (SpellModule module : boundSpecial) {
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

		return (((relX >= 19 && relX < 35 && relY >= -8 && relY < 8)
					|| (relX >= -35 && relX < -19 && relY >= -8 && relY < 8))
				&& (aspect == Aspects.EMPTY
					|| Arrays.asList(AspectUtils.primalAspects).contains(aspect)));
	}

	@Override
	public void assign(int x, int y, Aspect aspect) {
		int relX = this.x - x;
		int relY = this.y - y;
		if (canAssign(x, y, aspect)) {
			if (relX >= 19 && relX < 35 && relY >= -8 && relY < 8) {
				firstAspect = aspect;
			} else if (relX >= -35 && relX < -19 && relY >= -8 && relY < 8) {
				secondAspect = aspect;
			}
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
		if (!floating || firstAspect == Aspects.EMPTY) {
			guiGraphics.blit(SPELL_RESOURCES, mouseX - 35, mouseY - 8, 48, 0, 16, 16);
		} else {
			guiGraphics.renderItem(AspectUtils.getItemStackForAspect(firstAspect), mouseX - 35, mouseY - 8);
			guiGraphics.renderItemDecorations(Minecraft.getInstance().font, AspectUtils.getItemStackForAspect(firstAspect), mouseX - 35, mouseY - 8);
		}
		if (!floating || secondAspect == Aspects.EMPTY) {
			guiGraphics.blit(SPELL_RESOURCES, mouseX + 19, mouseY - 8, 48, 0, 16, 16);
		} else {
			guiGraphics.renderItem(AspectUtils.getItemStackForAspect(secondAspect), mouseX + 19, mouseY - 8);
			guiGraphics.renderItemDecorations(Minecraft.getInstance().font, AspectUtils.getItemStackForAspect(secondAspect), mouseX + 19, mouseY - 8);
		}
	}

	@Override
	public void renderInMinigame(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean floating) {
		guiGraphics.blit(SPELL_RESOURCES, x - getWidth() / 2, y - getHeight() / 2, 0, 48, getWidth(), getHeight());
		if (!floating) {
			if (firstAspect == Aspects.EMPTY) {
				guiGraphics.blit(SPELL_RESOURCES, x - 35, y - 8, 48, 0, 16, 16);
			} else {
				guiGraphics.renderItem(AspectUtils.getItemStackForAspect(firstAspect), x - 35, y - 8);
				guiGraphics.renderItemDecorations(Minecraft.getInstance().font, AspectUtils.getItemStackForAspect(firstAspect), x - 35, y - 8);
			}
			if (secondAspect == Aspects.EMPTY) {
				guiGraphics.blit(SPELL_RESOURCES, x + 19, y - 8, 48, 0, 16, 16);
			} else {
				guiGraphics.renderItem(AspectUtils.getItemStackForAspect(secondAspect), x + 19, y - 8);
				guiGraphics.renderItemDecorations(Minecraft.getInstance().font, AspectUtils.getItemStackForAspect(secondAspect), x + 19, y - 8);
			}
		}
	}
}