package com.wonginnovations.arcana.items;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AspectItem extends Item {
	
	private String aspectName;
	
	public AspectItem(String aspectName) {
		super(new Properties());
		if (aspectName.startsWith("aspect_"))
			aspectName = aspectName.substring(7);
		this.aspectName = aspectName;
	}

	@Override
	public Component getName(ItemStack stack) {
		return Component.translatable("aspect." + aspectName).withStyle(ChatFormatting.AQUA);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {
		tooltip.add(Component.translatable("aspect." + aspectName + ".desc"));
	}
	
	// getCreatorModId may be useful to override, to show who registered the aspect.
}