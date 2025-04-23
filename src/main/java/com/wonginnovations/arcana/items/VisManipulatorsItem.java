package com.wonginnovations.arcana.items;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.systems.spell.Spell;
import com.wonginnovations.arcana.systems.spell.casts.Cast;
import com.wonginnovations.arcana.systems.spell.casts.Casts;
import com.wonginnovations.arcana.systems.taint.Taint;
import com.wonginnovations.arcana.systems.spell.casts.ICast;
import com.wonginnovations.arcana.util.RayTraceUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.atomic.AtomicInteger;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VisManipulatorsItem extends Item {
	
	public VisManipulatorsItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
			onItemUseFirst(null, context);
			return InteractionResult.SUCCESS;
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() == Blocks.SPAWNER) {
			AtomicInteger i = new AtomicInteger();
			AtomicInteger j = new AtomicInteger();
			Taint.getTaintedEntities().forEach(entityType -> {
				Entity e = entityType.create(context.getLevel());
				e.setPos(context.getClickedPos().getX()-j.get(),context.getClickedPos().getY()+2,context.getClickedPos().getZ()+ i.get());
				//e.setNoGravity(true);
				e.addTag("NoAI");
				e.setDeltaMovement(0,0,0);
				context.getLevel().addFreshEntity(e);
				i.addAndGet(2);
				if (i.get()>=10) {
					i.set(0);
					j.addAndGet(1);
				}
			});
			return InteractionResult.SUCCESS;
		} else if (context.getLevel().getBlockState(context.getClickedPos()).getBlock() == ArcanaBlocks.ASPECT_TESTER.get()) {
			ItemStack toSet = new ItemStack(ArcanaItems.DEFAULT_FOCUS.get(), 1);
			int r = context.getLevel().random.nextInt(1);
			if (r == 0) {
				toSet.setTag(Spell.Samples.createBasicSpell().toNBT(new CompoundTag()));
			}
			toSet.getOrCreateTag().putInt("style", context.getLevel().random.nextInt(36));
			context.getPlayer().addItem(toSet);
		}
		return super.onItemUseFirst(stack, context);
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level levelIn, Player playerIn, InteractionHand handIn) {
		BlockPos pos = RayTraceUtils.getTargetBlockPos(playerIn,levelIn,80);
		if (levelIn.getBlockState(pos).getBlock()== ArcanaBlocks.SMOKEY_GLASS.get()) {
			((Cast)Casts.ICE_CAST).createAOEBlast(playerIn,levelIn,pos.above(),8,true, false, 3);
		} else if (levelIn.getBlockState(pos).getBlock()== ArcanaBlocks.HARDENED_GLASS.get()) {
			((Cast)Casts.ICE_CAST).createAOEBlast(playerIn,levelIn,pos.above(),8,true, false, 1);
		}
		return super.use(levelIn, playerIn, handIn);
	}
	
	public ICast getSpell() {
		return null;
	}
}