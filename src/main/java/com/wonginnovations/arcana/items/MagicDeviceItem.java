package com.wonginnovations.arcana.items;

import com.google.common.collect.Sets;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.ArcanaSounds;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.aspects.handlers.*;
import com.wonginnovations.arcana.capabilities.Researcher;
import com.wonginnovations.arcana.client.render.particles.AspectHelixParticleData;
import com.wonginnovations.arcana.items.attachment.Cap;
import com.wonginnovations.arcana.items.attachment.Core;
import com.wonginnovations.arcana.items.attachment.Focus;
import com.wonginnovations.arcana.items.attachment.FocusItem;
import com.wonginnovations.arcana.systems.research.ResearchBooks;
import com.wonginnovations.arcana.systems.research.ResearchEntry;
import com.wonginnovations.arcana.systems.spell.MDModifier;
import com.wonginnovations.arcana.systems.spell.Spell;
import com.wonginnovations.arcana.systems.spell.casts.ICast;
import com.wonginnovations.arcana.world.AuraView;
import com.wonginnovations.arcana.world.Node;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public abstract class MagicDeviceItem extends Item {
	public MagicDeviceItem(Properties properties) {
		super(properties);
	}
	
	public abstract boolean canCraft();
	
	public abstract boolean canUseSpells();
	
	public abstract String getDeviceName();
	
	public static Cap getCap(ItemStack stack) {
		String cap = stack.getOrCreateTag().getString("cap");
		return Cap.getCapOrError(new ResourceLocation(cap));
	}
	
	public static Focus getFocus(ItemStack stack) {
		int focus = stack.getOrCreateTag().getInt("focus");
		return Focus.getFocusById(focus).orElse(Focus.NO_FOCUS);
	}
	
	public static Core getCore(ItemStack stack) {
		String core = stack.getOrCreateTag().getString("core");
		return Core.getCoreOrError(new ResourceLocation(core));
	}
	
	public static CompoundTag getFocusData(ItemStack stack) {
		return stack.getOrCreateTagElement("focusData");
	}
	
	public static Optional<ItemStack> getFocusStack(ItemStack stack) {
		return getFocus(stack).getAssociatedItem().map(ItemStack::new).map(stack1 -> {
			stack1.setTag(getFocusData(stack));
			return stack1;
		});
	}
	
	public static void setFocusFromStack(ItemStack wand, ItemStack focus) {
		if (focus.getItem() instanceof FocusItem) {
			wand.getOrCreateTag().put("focusData", focus.getOrCreateTag());
			wand.getOrCreateTag().putInt("focus", Focus.FOCI.indexOf(focus.getItem()));
		} else {
			wand.getOrCreateTag().put("focusData", new CompoundTag());
			wand.getOrCreateTag().putInt("focus", 0);
		}
	}
	
	@Nullable
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
		AspectBattery battery = new AspectBattery(/*6, 0*/);
		for (Aspect aspect : AspectUtils.primalAspects) {
			AspectCell e = new AspectCell((int)((getCore(stack).maxVis() + getCap(stack).visStorage()) * getVisModifier()));
			e.setWhitelist(Collections.singletonList(aspect));
			battery.getHolders().add(e);
		}
		return battery;
	}
	
	protected abstract float getVisModifier();
	
	protected abstract float getDifficultyModifier();
	
	protected abstract float getComplexityModifier();
	
	public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
		Level level = player.level();
		AuraView view = AuraView.SIDED_FACTORY.apply(level);
		Optional<Node> nodeOptional = view.raycast(player.getEyePosition(0), player.getAttribute(ForgeMod.BLOCK_REACH.get()).getValue(), player);
		if (nodeOptional.isPresent()) {
			int ASPECT_DRAIN_AMOUNT = 2;
			int ASPECT_DRAIN_WAIT = 6;
			ResearchEntry channelingEntry = ResearchBooks.getEntry(Arcana.arcLoc("node_channeling"));
			ResearchEntry channeling2Entry = ResearchBooks.getEntry(Arcana.arcLoc("node_channeling_2"));
			if (Researcher.getFrom((Player)player).entryStage(channeling2Entry) >= channelingEntry.sections().size())
				ASPECT_DRAIN_WAIT = 3;
			else if (Researcher.getFrom((Player)player).entryStage(channelingEntry) >= channelingEntry.sections().size()) {
				ASPECT_DRAIN_WAIT = 2;
				ASPECT_DRAIN_AMOUNT = 3;
			}
			// drain
			AspectHandler wandHolder = AspectHandler.getFrom(stack);
			// TODO: non-destructive node draining?
			// with research, of course
			if (wandHolder != null)
				if (level.getGameTime() % (ASPECT_DRAIN_WAIT + 1 + level.random.nextInt(3)) == 0) {
					Node node = nodeOptional.get();
					AspectHandler aspects = node.getAspects();
					AspectHolder holder = aspects.getHolder(level.random.nextInt(aspects.countHolders()));
					Aspect aspect = holder.getLabelAspect();
					boolean moved = !holder.getStack().isEmpty();
					VisUtils.moveAspects(holder, wandHolder, ASPECT_DRAIN_AMOUNT + level.random.nextInt(1));
					if (moved) {
						// spawn aspect helix particles
						Vec3 nodePos = new Vec3(node.getX(), node.getY(), node.getZ());
						Vec3 playerPos = player.getEyePosition(1);
						Vec3 diff = nodePos.subtract(playerPos);
						Vec3 direction = diff.normalize().multiply(-1, -1, -1);
						int life = (int)Math.ceil(diff.length() / .05f);
						level.addParticle(new AspectHelixParticleData(aspect, life, level.random.nextInt(180), direction), nodePos.x, nodePos.y, nodePos.z, 0, 0, 0);
					}
				}
		} else
			player.stopUsingItem();
		//world.addParticle(new AspectHelixParticleData(Aspects.EXCHANGE, 450, level.random.nextInt(180), player.getLookVec()), player.getPosX(), player.getPosYEye(), player.getPosZ(), 0, 0, 0);
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
		// TODO: only do this if you're casting a spell
		// first do node raycast check, and then check if you have a focus
		if (canUseSpells()) {
			ArcanaSounds.playSpellCastSound(player);
			Focus focus = getFocus(player.getItemInHand(hand));
			if (focus != Focus.NO_FOCUS) {
				Spell spell = focus.getSpell(player.getItemInHand(hand));
				if (spell != null && spell.mainModule != null) {
					AspectHandler handler = AspectHandler.getFrom(player.getItemInHand(hand));
					// oh my god this code is terrible // YES, I know Xd.
					// time for more VisUtils I guess
					if (handler != null)
						if (spell.getSpellCosts().toList().stream().allMatch(stack -> handler.findFirstHolderContaining(stack.getAspect()).getStack().getAmount() >= stack.getAmount()) ||
								spell.getSpellCosts().toList().stream().allMatch(stack -> stack.isEmpty())) {
							Spell.runSpell(spell, level, player, player.getItemInHand(hand), player.isCrouching() ? ICast.Action.SPECIAL : ICast.Action.USE);
							// remove aspects from wand if spell successes.
							for (AspectStack cost : spell.getSpellCosts().toList())
								if (cost.getAspect() != Aspects.EMPTY)
									handler.findFirstHolderContaining(cost.getAspect()).drain(cost.getAmount(), false);
						}
				} else
					player.displayClientMessage(Component.translatable("status.arcana.null_spell"), true);
			}
		}
		AuraView view = AuraView.SIDED_FACTORY.apply(level);
		ItemStack itemstack = player.getItemInHand(hand);
		AtomicReference<InteractionResultHolder<ItemStack>> ret = new AtomicReference<>(InteractionResultHolder.consume(itemstack));
		view.raycast(player.getEyePosition(0), player.getAttribute(ForgeMod.BLOCK_REACH.get()).getValue(), player).ifPresent(node -> {
			player.startUsingItem(hand);
			ret.set(InteractionResultHolder.consume(itemstack));
		});
		return ret.get();
	}
	
	@Nonnull
	public Component getDisplayName(@Nonnull ItemStack stack) {
		return Component.translatable(getCore(stack).getCoreTranslationKey(), Component.translatable(getCap(stack).getPrefixTranslationKey()), Component.translatable(getDeviceName()));
	}
	
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		// Add info
		boolean creative = getCore(stack).modifier() instanceof MDModifier.Creative;
		if (creative)
			tooltip.add(Component.translatable("tooltip.arcana.creative_wand").withStyle(ChatFormatting.AQUA));
		tooltip.add(Component.literal(""));
		tooltip.add(Component.translatable("tooltip.arcana.properties").withStyle(ChatFormatting.GRAY));
		tooltip.add(Component.literal(" " + (creative ? I18n.get("tooltip.arcana.infinity") : (int)((getCore(stack).maxVis() + getCap(stack).visStorage()) * getVisModifier()) + " "+I18n.get("tooltip.arcana.max")) + " "+I18n.get("tooltip.arcana.vis")).withStyle(ChatFormatting.DARK_GREEN));
		tooltip.add(Component.literal(" " + (creative ? I18n.get("tooltip.arcana.infinity") : (int)(getCore(stack).difficulty() * getDifficultyModifier())) + " "+I18n.get("tooltip.arcana.difficulty")).withStyle(ChatFormatting.DARK_GREEN));
		tooltip.add(Component.literal(" " + (creative ? I18n.get("tooltip.arcana.infinity") : (int)(getCap(stack).complexity() * getComplexityModifier())) + " "+I18n.get("tooltip.arcana.complexity")).withStyle(ChatFormatting.DARK_GREEN));
	}
	
	public boolean canSwapFocus(Player player) {
		return player.getInventory().hasAnyOf(Sets.newHashSet(ArcanaItems.DEFAULT_FOCUS.get())) || (getFocus(player.getItemInHand(InteractionHand.MAIN_HAND)) != Focus.NO_FOCUS||getFocus(player.getItemInHand(InteractionHand.OFF_HAND)) != Focus.NO_FOCUS);
	}
}