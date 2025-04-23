package com.wonginnovations.arcana.items;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.entities.TaintBottleEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TaintBottleItem extends Item{
	
	public TaintBottleItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (level.random.nextFloat() * 0.4F + 0.8F));
		
		if (!level.isClientSide()) {
			TaintBottleEntity entity = new TaintBottleEntity(player, level);
			entity.shoot(player.getLookAngle().x, player.getLookAngle().y, player.getLookAngle().z, .5f, 1);
			level.addFreshEntity(entity);
		}
		
		ItemStack itemstack = player.getItemInHand(hand);
		player.awardStat(Stats.ITEM_USED.get(this));
		if (!player.isCreative())
			itemstack.shrink(1);
		
		return InteractionResultHolder.success(itemstack);
	}
}