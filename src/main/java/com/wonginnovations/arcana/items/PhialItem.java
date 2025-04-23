package com.wonginnovations.arcana.items;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.*;
import com.wonginnovations.arcana.aspects.handlers.AspectBattery;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.aspects.handlers.AspectHandlerCapability;
import com.wonginnovations.arcana.aspects.handlers.AspectHolder;
import net.minecraft.world.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

import static com.wonginnovations.arcana.ArcanaSounds.playPhialCorkpopSound;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PhialItem extends Item implements IOverrideAspects {
	
	public PhialItem() {
		super(new Properties());
	}
	
	@SuppressWarnings("ConstantConditions")
	@Override
	public InteractionResult useOn(UseOnContext context) {
		BlockPos pos = context.getClickedPos();
		BlockEntity tile = context.getLevel().getBlockEntity(pos);
		if (tile != null) {
			LazyOptional<AspectHandler> cap = tile.getCapability(AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY);
			if (cap.isPresent()) {
				//noinspection ConstantConditions
				AspectHandler tileHandle = cap.orElse(null);
				AspectHolder myHandle = AspectHandler.getFrom(context.getItemInHand()).getHolder(0);
				if (myHandle.getStack().getAmount() <= 0) {
					// drain from block
					// pick first holder with >0 vis
					// and take from it
					for (AspectHolder holder : tileHandle.getHolders())
						if (holder.getStack().getAmount() > 0) {
							float min = Math.min(holder.getStack().getAmount(), 8);
							playPhialCorkpopSound(context.getPlayer());
							Aspect aspect = holder.getStack().getAspect();
							// create a filled phial
							ItemStack capedItemStack = new ItemStack(ArcanaItems.PHIAL.get());
							AspectHandler.getFrom(capedItemStack).getHolder(0).insert(new AspectStack(aspect, min), false);
							if (capedItemStack.getTag() == null)
								capedItemStack.setTag(capedItemStack.getShareTag());
							// take an empty phial and give the filled one
							context.getItemInHand().shrink(1);
							context.getPlayer().getInventory().add(capedItemStack); //player.addItemStackToInventory gives sound and player.inventory.addItemStackToInventory not.
							holder.drain(min, false);
							return InteractionResult.SUCCESS;
						}
				} else {
					// insert to block
					for (AspectHolder holder : tileHandle.getHolders())
						if ((holder.getCapacity() - holder.getStack().getAmount() > 0 || holder.voids()) && (holder.getStack().getAspect() == myHandle.getStack().getAspect() || holder.getStack().isEmpty())) {
							float inserted = holder.insert(new AspectStack(myHandle.getStack().getAspect(), myHandle.getStack().getAmount()), false);
							playPhialCorkpopSound(context.getPlayer());
							if (inserted != 0) {
								ItemStack new_phial = new ItemStack(this, 1);
								AspectHolder old_holder = AspectHandler.getFrom(context.getItemInHand()).getHolder(0);
								AspectHolder new_holder = AspectHandler.getFrom(new_phial).getHolder(0);
								new_holder.insert(new AspectStack(old_holder.getStack().getAspect(), inserted), false);
								new_phial.setTag(new_phial.getShareTag());
								if (!context.getPlayer().isCreative())
									context.getPlayer().addItem(new_phial);
							} else
								if (!context.getPlayer().isCreative())
									context.getPlayer().getInventory().add(new ItemStack(ArcanaItems.PHIAL.get())); //player.addItemStackToInventory gives sound and player.inventory.addItemStackToInventory not.
							context.getItemInHand().shrink(1);
							return InteractionResult.SUCCESS;
						}
				}
			}
		}
		return super.useOn(context);
	}
	
	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		AspectBattery battery = new AspectBattery(/*1, 8*/);
		battery.initHolders(8, 1);
		return battery;
	}
	
	@Override
	public Component getName(ItemStack stack) {
		AspectHandler aspectHandler = AspectHandler.getFrom(stack);
		if (aspectHandler != null && aspectHandler.getHolder(0) != null) {
			if (!aspectHandler.getHolder(0).getStack().isEmpty()) {
				String aspectName = AspectUtils.getLocalizedAspectDisplayName(aspectHandler.getHolder(0).getStack().getAspect());
				return Component.translatable("item.arcana.phial", aspectName).withStyle(Rarity.RARE.color);
			}
		}
		return Component.translatable("item.arcana.empty_phial");
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level levelIn, List<Component> tooltip, TooltipFlag flagIn) {
		AspectBattery vis = (AspectBattery)AspectHandler.getFrom(stack);
		if (vis != null) {
			if (vis.getHolder(0) != null) {
				if (!vis.getHolder(0).getStack().isEmpty()) {
					AspectStack aspectStack = vis.getHolder(0).getStack();
					tooltip.add(Component.translatable("tooltip.contains_aspect",
							aspectStack.getAspect().name().toLowerCase().substring(0, 1).toUpperCase() + aspectStack.getAspect().name().toLowerCase().substring(1), (int)aspectStack.getAmount()));
				}
			}
		}
		super.appendHoverText(stack, levelIn, tooltip, flagIn);
	}

	@Override
	public @Nullable CompoundTag getShareTag(ItemStack stack) {
		AspectHandler vis = AspectHandler.getFrom(stack);
		if (vis != null) {
			if (vis.getHolder(0) != null) {
				Aspect aspect = vis.getHolder(0).getStack().getAspect();
				float amount = vis.getHolder(0).getStack().getAmount();
				if (aspect != null && amount != 0) {
					CompoundTag compoundNBT = new CompoundTag();
					compoundNBT.putInt("id", aspect.getId() - 1);
					compoundNBT.putFloat("amount", amount);
					return compoundNBT;
				}
			}
		}
		return null;
	}
	
	@Override
	public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
		if (nbt != null) {
			AspectHandler cap = AspectHandler.getFrom(stack);
			if (cap != null)
				cap.getHolder(0).insert(new AspectStack(Aspects.getAll().get(nbt.getInt("id") - 1), nbt.getInt("amount")), false);
		}
	}

	// TODO: add to CT
//	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
//		if (isInGroup(group)) {
//			for (Aspect aspect : Aspects.getAll()) {
//				items.add(withAspect(aspect));
//			}
//		}
//	}
	
	public ItemStack withAspect(Aspect aspect) {
		ItemStack stack = new ItemStack(this);
		AspectHandler cap = AspectHandler.getFrom(stack);
		if (cap != null)
			cap.getHolder(0).insert(new AspectStack(aspect, 8), false);
		stack.setTag(stack.getShareTag());
		return stack;
	}
	
	@Override
	public List<AspectStack> getAspectStacks(ItemStack stack) {
		AspectHolder myHolder = AspectHandler.getFrom(stack).getHolder(0);
		if (myHolder == null || myHolder.getStack().isEmpty())
			return Collections.singletonList(new AspectStack(Aspects.EMPTY));
		return Collections.singletonList(myHolder.getStack());
	}
	
	public static Aspect getAspect(ItemStack stack) {
		AspectHolder myHolder = AspectHandler.getFrom(stack).getHolder(0);
		return myHolder.getStack().getAspect();
	}
}