package com.wonginnovations.arcana.items;

import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.items.attachment.Cap;
import com.wonginnovations.arcana.items.attachment.Core;
import com.wonginnovations.arcana.systems.spell.Spell;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GauntletItem extends MagicDeviceItem {
	public GauntletItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public boolean canCraft() {
		return false;
	}

	@Override
	public boolean canUseSpells() {
		return true;
	}

	@Override
	public String getDeviceName() {
		return "Gauntlet";
	}

	@Override
	protected float getVisModifier() {
		return 0.75f;
	}

	@Override
	protected float getDifficultyModifier() {
		return 3;
	}

	@Override
	protected float getComplexityModifier() {
		return 2;
	}

	public static ItemStack withCapAndCore(String cap, String core) {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("cap", cap);
		nbt.putString("core", core);
		ItemStack stack = new ItemStack(ArcanaItems.WAND.get(), 1);
		stack.setTag(nbt);
		return stack;
	}

	public static ItemStack withCapAndCore(ResourceLocation cap, ResourceLocation core) {
		return withCapAndCore(cap.toString(), core.toString());
	}

	public static ItemStack withCapAndCore(Cap cap, Core core) {
		return withCapAndCore(cap.getId(), core.getId());
	}

	public int getUseDuration(@NotNull ItemStack stack) {
		return 72000;
	}

	// TODO: put these in CT
//	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
//		if (isInGroup(group)) {
//			// iron/wooden, silver/dair, gold/greatwood, thaumium/silverwood, void/arcanium
//			items.add(withCapAndCoreForCt("iron_cap", "wood_wand"));
//			items.add(withCapAndCoreForCt("silver_cap", "dair_wand"));
//			items.add(withCapAndCoreForCt("gold_cap", "greatwood_wand"));
//			items.add(withCapAndCoreForCt("thaumium_cap", "silverwood_wand"));
//			items.add(withCapAndCoreForCt("void_cap", "arcanium_wand"));
//		}
//	}

	public static ItemStack withCapAndCoreForCt(String cap, String core) {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("cap", "arcana:" + cap);
		nbt.putString("core", "arcana:" + core);
		ItemStack stack = new ItemStack(ArcanaItems.GAUNTLET.get(), 1);
		stack.setTag(nbt);
		return stack;
	}

	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		// Add focus info
		Spell spell = getFocus(stack).getSpell(stack);
		if (spell != null) {
			Optional<Component> name = spell.getName(getFocusData(stack).getCompound("Spell"));
			name.ifPresent(e -> tooltip.add(Component.translatable("tooltip.arcana.spell", e,
					spell.getSpellCosts().toList().stream()
							.map(AspectStack::getAspect)
							.map(aspect -> I18n.get("aspect." + aspect.name()))
							.collect(Collectors.joining(", ")))));
		}
	}
}
