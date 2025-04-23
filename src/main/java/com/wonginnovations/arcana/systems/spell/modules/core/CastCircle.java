package com.wonginnovations.arcana.systems.spell.modules.core;

import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.systems.spell.casts.Cast;
import com.wonginnovations.arcana.systems.spell.casts.Casts;
import com.wonginnovations.arcana.systems.spell.casts.ICast;
import com.wonginnovations.arcana.systems.spell.modules.SpellModule;
import com.wonginnovations.arcana.systems.spell.modules.circle.DoubleModifierCircle;
import com.wonginnovations.arcana.systems.spell.modules.circle.SinModifierCircle;
import com.wonginnovations.arcana.systems.spell.modules.circle.SingleModifierCircle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

import static com.wonginnovations.arcana.systems.spell.SpellState.SPELL_RESOURCES;

public class CastCircle extends SpellModule {
	public ICast cast = null;

	@Override
	public String getName() {
		return "cast_circle";
	}

	@Override
	public void fromNBT(CompoundTag compound) {
		super.fromNBT(compound);
		cast = Casts.castMap.get(new ResourceLocation(compound.getString("cast")));
	}

	@Override
	public CompoundTag toNBT(CompoundTag compound) {
		super.toNBT(compound);
		if (cast instanceof Cast) {
			compound.putString("cast", ((Cast) cast).getId().toString());
		} else if (cast != null) {
			compound.putString("cast", cast.getSpellAspect().toResourceLocation().toString());
		} else {
			compound.putString("cast", Aspects.EMPTY.toResourceLocation().toString());
		}
		return compound;
	}

	@Override
	public boolean canConnectSpecial(SpellModule connectingModule) {
		if (connectingModule instanceof SinModifierCircle) {
			return boundSpecial.stream()
					.filter(module -> module != connectingModule)
					.noneMatch(module -> module instanceof SinModifierCircle);
		} else if (connectingModule instanceof SingleModifierCircle
					|| connectingModule instanceof DoubleModifierCircle) {
			return boundSpecial.stream()
					.filter(module -> module != connectingModule)
					.noneMatch(module -> module instanceof SingleModifierCircle
								|| module instanceof DoubleModifierCircle);
		} else {
			return false;
		}
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
	public boolean canAssign(int x, int y, Aspect aspect) {
		int relX = this.x - x;
		int relY = this.y - y;

		return (relX >= -8 && relX < 8
				&& relY >= -8 && relY < 8
				&& Aspects.getAll().contains(aspect));
	}

	@Override
	public void assign(int x, int y, Aspect aspect) {
		if (canAssign(x, y, aspect)) {
			cast = Casts.castMap.get(aspect.toResourceLocation());
		}
	}

	@Override
	public int getHeight() {
		return 32;
	}

	@Override
	public int getWidth() {
		return 32;
	}

	@Override
	public void renderUnderMouse(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean floating) {
		guiGraphics.blit(SPELL_RESOURCES, mouseX - getWidth() / 2, mouseY - getHeight() / 2, 32, 16, getWidth(), getHeight());
		if (!floating || cast == null) {
			guiGraphics.blit(SPELL_RESOURCES, mouseX - 8, mouseY - 8, 32, 0, 16, 16);
		} else {
			guiGraphics.renderItem(AspectUtils.getItemStackForAspect(cast.getSpellAspect()), mouseX - 8, mouseY - 8);
			guiGraphics.renderItemDecorations(Minecraft.getInstance().font, AspectUtils.getItemStackForAspect(cast.getSpellAspect()), mouseX - 8, mouseY - 8);
		}
	}

	@Override
	public void renderInMinigame(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean floating) {
		guiGraphics.blit(SPELL_RESOURCES, x - getWidth() / 2, y - getHeight() / 2, 32, 16, getWidth(), getHeight());
		if (!floating) {
			if (cast == null) {
				guiGraphics.blit(SPELL_RESOURCES, x - 8, y - 8, 32, 0, 16, 16);
			} else {
				guiGraphics.renderItem(AspectUtils.getItemStackForAspect(cast.getSpellAspect()), x - 8, y - 8);
				guiGraphics.renderItemDecorations(Minecraft.getInstance().font, AspectUtils.getItemStackForAspect(cast.getSpellAspect()), x - 8, y - 8);
			}
		}
	}
}
