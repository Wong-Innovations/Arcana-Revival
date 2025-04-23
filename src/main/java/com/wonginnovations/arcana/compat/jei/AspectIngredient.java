package com.wonginnovations.arcana.compat.jei;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import com.wonginnovations.arcana.aspects.*;
import com.wonginnovations.arcana.client.gui.ClientUiUtil;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class AspectIngredient extends AspectStack {

	public static final IIngredientType<AspectIngredient> TYPE = () -> AspectIngredient.class;
	
	public boolean isUndecided = false;
	public boolean primalsOnly = false;
	
	public AspectIngredient(Aspect aspect, float amount, boolean isUndecided) {
		super(aspect, amount);
		this.isUndecided = isUndecided;
	}
	
	public static AspectIngredient fromStack(AspectStack stack) {
		return new AspectIngredient(stack.getAspect(), stack.getAmount(),false);
	}
	
	public static AspectIngredient fromUndecidedStack(UndecidedAspectStack stack) {
		return new AspectIngredient(stack.stack.getAspect(), stack.stack.getAmount(),stack.any);
	}
	
	public static AspectIngredient fromSingleton(Aspect aspect) {
		return new AspectIngredient(aspect, 1, false);
	}

	public AspectIngredient primalsOnly() {
		primalsOnly = true;
		return this;
	}

    public static class Helper implements IIngredientHelper<AspectIngredient> {

		@Override
		public @NotNull IIngredientType<AspectIngredient> getIngredientType() {
			return TYPE;
		}

//		@Nullable
//		@Override
//		public AspectIngredient getMatch(Iterable<AspectIngredient> ingredients, AspectIngredient destIngredient) {
//			for (AspectIngredient ingredient : ingredients) {
//				if (ingredient.getAspect() == destIngredient.getAspect()) {
//					return ingredient;
//				}
//			}
//			return null;
//		}
		
		@Override
		public @NotNull String getDisplayName(AspectIngredient aspectIngredient) {
			return AspectUtils.getLocalizedAspectDisplayName(aspectIngredient.getAspect());
		}

		@Override
		public @NotNull String getUniqueId(@NotNull AspectIngredient aspectIngredient, @NotNull UidContext uidContext) {
			return getAspectLoc(aspectIngredient).toString();
		}

		@Override
		public @NotNull ResourceLocation getResourceLocation(@NotNull AspectIngredient aspectIngredient) {
			return getAspectLoc(aspectIngredient);
		}
		
		@Override
		public @NotNull AspectIngredient copyIngredient(@NotNull AspectIngredient aspectIngredient) {
			return new AspectIngredient(aspectIngredient.getAspect(), aspectIngredient.getAmount(), aspectIngredient.isUndecided);
		}
		
		@Override
		public @NotNull String getErrorInfo(@Nullable AspectIngredient aspectIngredient) {
			if (aspectIngredient == null) {
				return "Aspect Ingredient is null";
			}
			return "Aspect Ingredient errored: "+ aspectIngredient;
		}
		
		private ResourceLocation getAspectLoc(AspectIngredient aspectIngredient) {
			return aspectIngredient.getAspect().toResourceLocation();
		}
	}
	
	public static class Renderer implements IIngredientRenderer<AspectIngredient> {
		
		@Override
		public void render(@NotNull GuiGraphics pGuiGraphics, @Nullable AspectIngredient aspectIngredient) {
//			ClientUiUtil.renderAspectStack(pGuiGraphics, aspectIngredient);
		}

		@Override
		public void render(@NotNull GuiGraphics guiGraphics, @NotNull AspectIngredient ingredient, int posX, int posY) {
			if (ingredient.isUndecided) {
				int index;
				AspectStack aspect;
				if (ingredient.primalsOnly) {
					index = Minecraft.getInstance().player.tickCount / 20 % Aspects.getPrimals().size();
					aspect = new AspectStack(Aspects.getPrimals().get(index), ingredient.getAmount());
				} else {
					index = Minecraft.getInstance().player.tickCount / 20 % Aspects.getWithoutEmpty().size();
					aspect = new AspectStack(Aspects.getWithoutEmpty().get(index), ingredient.getAmount());
				}
				ClientUiUtil.renderAspectStack(guiGraphics, aspect, posX, posY);
			} else {
				ClientUiUtil.renderAspectStack(guiGraphics, ingredient, posX, posY);
			}
		}

		@SuppressWarnings("removal")
		@Override
		public @NotNull List<Component> getTooltip(AspectIngredient aspectIngredient, @NotNull TooltipFlag iTooltipFlag) {
			if (aspectIngredient.isUndecided) {
				int index;
				Aspect aspect;
				if (aspectIngredient.primalsOnly) {
					index = Minecraft.getInstance().player.tickCount / 20 % Aspects.getPrimals().size();
					aspect = Aspects.getPrimals().get(index);
				} else {
					index = Minecraft.getInstance().player.tickCount / 20 % Aspects.getWithoutEmpty().size();
					aspect = Aspects.getWithoutEmpty().get(index);
				}
				Collections.singletonList(Component.literal(AspectUtils.getLocalizedAspectDisplayName(aspect)));
			}
			return Collections.singletonList(Component.literal(AspectUtils.getLocalizedAspectDisplayName(aspectIngredient.getAspect())));
		}
	}
}
