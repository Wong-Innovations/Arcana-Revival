package com.wonginnovations.arcana.containers;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.containers.slots.AspectCraftingResultSlot;
import com.wonginnovations.arcana.containers.slots.IWandSlotListener;
import com.wonginnovations.arcana.containers.slots.WandSlot;
import com.wonginnovations.arcana.items.recipes.AspectCraftingContainer;
import com.wonginnovations.arcana.items.recipes.ArcanaRecipes;
import com.wonginnovations.arcana.items.recipes.IArcaneCraftingRecipe;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ArcaneCraftingTableMenu extends RecipeBookMenu<AspectCraftingContainer> implements IWandSlotListener {

	public final Container inventory;
	public final Inventory playerInventory;
	public final AspectCraftingContainer craftMatrix;
	public final ResultContainer craftResult = new ResultContainer();
	public final int craftResultSlot;

	public ArcaneCraftingTableMenu(MenuType<?> type, int id, Inventory playerInventory, Container inventory) {
		super(type, id);
		this.inventory = inventory;
		this.playerInventory = playerInventory;
		WandSlot wandSlot = new WandSlot(this, inventory, 10, 160, 18);
		this.craftMatrix = new AspectCraftingContainer(this, wandSlot, 3, 3, inventory, playerInventory.player);
		this.addSlot(new AspectCraftingResultSlot(playerInventory.player, this.craftMatrix, this.craftResult, 0, 160, 64));
		this.craftResultSlot = 1;
		this.addSlot(wandSlot);
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 3; ++j)
				this.addSlot(new Slot(craftMatrix, j + i * 3, 42 + j * 23, 41 + i * 23));
		addPlayerSlots(playerInventory);
		// guarantee craft() called on serverside
		craft(this.containerId, getStateId(), this.playerInventory.player.level(), this.playerInventory.player, this.craftMatrix, this.craftResult);
	}

	public ArcaneCraftingTableMenu(int id, Inventory playerInventory, Container inventory) {
		this(ArcanaMenus.ARCANE_CRAFTING_TABLE.get(), id, playerInventory, inventory);
	}

	public ArcaneCraftingTableMenu(int i, Inventory playerInventory) {
		this(ArcanaMenus.ARCANE_CRAFTING_TABLE.get(), i, playerInventory, new SimpleContainer(10));
	}

	protected static void craft(int containerId, int stateId, Level level, Player playerEntity, AspectCraftingContainer craftingInventory, ResultContainer resultInventory) {
		if (!level.isClientSide) {
			ServerPlayer serverplayerentity = (ServerPlayer)playerEntity;
			ItemStack itemstack = ItemStack.EMPTY;
			// look for arcane crafting
			if (level.getServer() != null) {
				Optional<IArcaneCraftingRecipe> optional = level.getServer().getRecipeManager().getRecipeFor(ArcanaRecipes.Types.ARCANE_CRAFTING_SHAPED.get(), craftingInventory, level);
				if (optional.isPresent()) {
					IArcaneCraftingRecipe iarcanecraftingrecipe = optional.get();
					if (resultInventory.setRecipeUsed(level, serverplayerentity, iarcanecraftingrecipe)) {
						itemstack = iarcanecraftingrecipe.getResultItem(level.registryAccess());
					}
				}
				// if arcane crafting is not possible, look for regular crafting
				else {
					Optional<CraftingRecipe> craftingOptional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, craftingInventory, level);
					if (craftingOptional.isPresent()) {
						CraftingRecipe recipe = craftingOptional.get();
						if (resultInventory.setRecipeUsed(level, serverplayerentity, recipe))
							itemstack = recipe.getResultItem(level.registryAccess());
					}
				}
				resultInventory.setItem(0, itemstack);
				serverplayerentity.connection.send(new ClientboundContainerSetSlotPacket(containerId, stateId, 0, itemstack));
			}
		}
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	public void slotsChanged(Container inventory) {
		craft(this.containerId, getStateId(), this.playerInventory.player.level(), this.playerInventory.player, this.craftMatrix, this.craftResult);
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	@Override
	public boolean stillValid(Player player) {
		return inventory == null || inventory.stillValid(player);
	}

	private void addPlayerSlots(Container inventory) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(inventory, j + i * 9 + 9, 16 + j * 18, 151 + i * 18));
			}
		}

		for (int k = 0; k < 9; ++k) {
			this.addSlot(new Slot(inventory, k, 16 + k * 18, 209));
		}
	}

	@Override
	public void fillCraftSlotsStackedContents(StackedContents contents) {
		craftMatrix.fillStackedContents(contents);
	}

	@Override
	public void clearCraftingContent() {
		this.inventory.clearContent();
	}

	@Override
	public boolean recipeMatches(Recipe<? super AspectCraftingContainer> recipe) {
		return recipe.matches(craftMatrix, playerInventory.player.level());
	}

	@Override
	public int getResultSlotIndex() {
		return craftResultSlot;
	}

	@Override
	public int getGridWidth() {
		return 3;
	}

	@Override
	public int getGridHeight() {
		return 3;
	}

	@Override
	public int getSize() {
		return craftMatrix.getContainerSize();
	}

	@Override
	public RecipeBookType getRecipeBookType() {
		return RecipeBookType.CRAFTING;
	}

	@Override
	public boolean shouldMoveToInventory(int pSlotIndex) {
		return pSlotIndex >= 0 && pSlotIndex < this.slots.size();
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
	 * inventory and the other inventory(s).
	 */
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = slots.get(index);
		if (slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index == 0) {
				itemstack1.getItem().onCraftedBy(itemstack1, player.level(), player);
				if (!moveItemStackTo(itemstack1, 11, 47, true)) {
					return ItemStack.EMPTY;
				}

				slot.onQuickCraft(itemstack1, itemstack);
			} else if (index >= 11 && index < 47) {
				if (!moveItemStackTo(itemstack1, 1, 11, false)) {
					if (index < 38) {  // prioritize hotbar
						if (!moveItemStackTo(itemstack1, 38, 47, false)) {
							return ItemStack.EMPTY;
						}
					} else if (!moveItemStackTo(itemstack1, 11, 38, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (!moveItemStackTo(itemstack1, 11, 47, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			// ItemStack itemstack2 = slot.onTake(player, itemstack1); // if (index == 0) player.drop(itemstack2, false);
			slot.onTake(player, itemstack1);
			if (index == 0) {
				player.drop(itemstack1, false);
			}
		}

		return itemstack;
	}

	@Override
	public void onWandSlotUpdate() {
		craft(containerId, getStateId(), playerInventory.player.level(), playerInventory.player, craftMatrix, craftResult);
	}

}