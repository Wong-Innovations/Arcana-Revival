package com.wonginnovations.arcana.items;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.capabilities.Researcher;
import com.wonginnovations.arcana.network.Connection;
import com.wonginnovations.arcana.network.PkModifyResearch;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CheatersResearchBookItem extends ResearchBookItem {
	
	public CheatersResearchBookItem(Properties properties, ResourceLocation book) {
		super(properties, book);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		// also grant all research
		if (!level.isClientSide() && player instanceof ServerPlayer)
			ResearchBooks.streamEntries().forEach(entry -> {
				Researcher from = Researcher.getFrom(player);
				if (from != null && !entry.meta().contains("locked")) {
					from.completeEntry(entry);
					Connection.sendModifyResearch(PkModifyResearch.Diff.complete, entry.key(), (ServerPlayer)player);
				}
			});
		return super.use(level, player, hand);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		tooltip.add(Component.translatable("item.arcana.cheaters_arcanum.desc"));
	}
}