package com.wonginnovations.arcana.items.recipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.items.WandItem;
import com.wonginnovations.arcana.items.attachment.Cap;
import com.wonginnovations.arcana.items.attachment.CapItem;
import com.wonginnovations.arcana.items.attachment.Core;
import com.wonginnovations.arcana.items.attachment.CoreItem;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WandsRecipe extends CustomRecipe {
	
	// For items that aren't CapItems or CoreItems that are associated with a Cap or Core.
	// The item is counted as a cap or core by isCap and isCore if the function returns nonnull.
	// toCap and toCore will use the Function value.
	public static Map<Item, Function<ItemStack, Cap>> EXTRA_CAPS = new HashMap<>();
	public static Map<Item, Function<ItemStack, Core>> EXTRA_CORES = new HashMap<>();
	
	static {
		EXTRA_CORES.put(Items.STICK, __ -> ArcanaItems.WOOD_WAND_CORE);
	}
	
	public WandsRecipe(ResourceLocation id, CraftingBookCategory category) {
		super(id, category);
	}
	
	public boolean matches(CraftingContainer inv, Level levelIn) {
		if (inv.getWidth() >= 3 && inv.getHeight() >= 3) {
			// check TL slot
			// if there's a cap, TL->BR
			// else, TR->BL
			
			ItemStack TL = inv.getItem(0);
			if (isCap(TL)) {
				if (TL.is(inv.getItem(8).getItem()))
					return isCore(inv.getItem(4)) && toCore(inv.getItem(4)).capAllowed(toCap(TL));
			} else {
				ItemStack stack = inv.getItem(2);
				if (isCap(stack))
					if (stack.is(inv.getItem(6).getItem()))
						return isCore(inv.getItem(4)) && toCore(inv.getItem(4)).capAllowed(toCap(stack));
			}
		}
		return false;
	}

	@Override
	public ItemStack assemble(CraftingContainer inv, RegistryAccess registryAccess) {
		Cap caps;
		if (inv.getItem(0).getItem() instanceof Cap)
			caps = (Cap)inv.getItem(0).getItem();
		else
			caps = (Cap)inv.getItem(2).getItem();
		Item item = inv.getItem(4).getItem();
		return item instanceof CoreItem ? WandItem.withCapAndCore(caps, (Core)item) : WandItem.withCapAndCore(caps, EXTRA_CORES.get(item).apply(inv.getItem(4)));
	}

	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return false;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return ItemStack.EMPTY;
	}

	public boolean canFit(int width, int height) {
		return width >= 3 && height >= 3;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ArcanaRecipes.Serializers.CRAFTING_WANDS.get();
	}

    private static boolean isCore(@Nullable ItemStack stack) {
		return stack != null && (stack.getItem() instanceof CoreItem || (EXTRA_CORES.get(stack.getItem()) != null && EXTRA_CORES.get(stack.getItem()).apply(stack) != null));
	}
	
	private static Core toCore(@Nonnull ItemStack stack) {
		if (stack.getItem() instanceof CoreItem)
			return ((CoreItem)stack.getItem());
		return EXTRA_CORES.get(stack.getItem()).apply(stack);
	}
	
	private static boolean isCap(@Nullable ItemStack stack) {
		return stack != null && (stack.getItem() instanceof CapItem || (EXTRA_CAPS.get(stack.getItem()) != null && EXTRA_CAPS.get(stack.getItem()).apply(stack) != null));
	}
	
	private static Cap toCap(@Nonnull ItemStack stack) {
		if (stack.getItem() instanceof CapItem)
			return ((CapItem)stack.getItem());
		return EXTRA_CAPS.get(stack.getItem()).apply(stack);
	}
}