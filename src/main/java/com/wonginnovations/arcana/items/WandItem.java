package com.wonginnovations.arcana.items;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.blocks.CrucibleBlock;
import com.wonginnovations.arcana.items.attachment.Cap;
import com.wonginnovations.arcana.items.attachment.Core;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WandItem extends MagicDeviceItem {
	
	public WandItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canCraft() {
		return true;
	}

	@Override
	public boolean canUseSpells() {
		return true;
	}

	@Override
	public String getDeviceName() {
		return "item.arcana.wand.variant.wand";
	}

	@Override
	protected float getVisModifier() {
		return 1;
	}

	@Override
	protected float getDifficultyModifier() {
		return 1;
	}

	@Override
	protected float getComplexityModifier() {
		return 1;
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

	@Override
	public InteractionResult useOn(UseOnContext context) {
		return convert(context.getLevel(), context.getClickedPos(), context.getPlayer());
	}
	
	public static InteractionResult convert(Level level, BlockPos pos, @Nullable Player player) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() == Blocks.CAULDRON) {
			level.setBlockAndUpdate(pos, ArcanaBlocks.CRUCIBLE.get().defaultBlockState().setValue(CrucibleBlock.FULL, state.getValue(LayeredCauldronBlock.LEVEL) >= 2));
			level.playSound(player, pos, SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1, 1);
			for (int i = 0; i < 20; i++)
				level.addParticle(ParticleTypes.END_ROD, pos.getX() + level.random.nextDouble(), pos.getY() + level.random.nextDouble(), pos.getZ() + level.random.nextDouble(), 0, 0, 0);
			return InteractionResult.SUCCESS;
		}
		if (state.getBlock() == Blocks.CRAFTING_TABLE) {
			level.setBlockAndUpdate(pos, ArcanaBlocks.ARCANE_CRAFTING_TABLE.get().defaultBlockState());
			level.playSound(player, pos, SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1, 1);
			for (int i = 0; i < 20; i++)
				level.addParticle(ParticleTypes.END_ROD, (pos.getX() - .1f) + level.random.nextDouble() * 1.2f, (pos.getY() - .1f) + level.random.nextDouble() * 1.2f, (pos.getZ() - .1f) + level.random.nextDouble() * 1.2f, 0, 0, 0);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	// TODO: add to CT
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
	
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		// Add focus info
//		try {
//			if (Minecraft.getInstance().player!=null)
//				tooltip.add(Component.literal("Charms: "+Arrays.toString(Arcana.authManager.getUserLevel(Minecraft.getInstance().player.getDisplayName().getString()))).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xdec7fc))));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}