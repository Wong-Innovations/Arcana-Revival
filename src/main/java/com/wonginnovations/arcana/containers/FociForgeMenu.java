package com.wonginnovations.arcana.containers;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.blocks.entities.FociForgeBlockEntity;
import com.wonginnovations.arcana.client.gui.FociForgeScreen;
import com.wonginnovations.arcana.containers.slots.AspectSlot;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.items.MagicDeviceItem;
import com.wonginnovations.arcana.items.attachment.FocusItem;
import com.wonginnovations.arcana.network.Connection;
import com.wonginnovations.arcana.systems.spell.Spell;
import com.wonginnovations.arcana.systems.spell.modules.core.StartCircle;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class FociForgeMenu extends AspectMenu {
    public static final int ASPECT_H_COUNT = 4;
    public static final int ASPECT_V_COUNT = 7;
    public static final Container TMP_FOCI = new SimpleContainer(9);

    protected FociForgeMenu(@Nullable MenuType<?> type, int id) {
        super(type, id);
    }

    public FociForgeBlockEntity te;
    public List<AspectSlot> scrollableSlots = new ArrayList<>();
    public List<Slot> fociSlots = new ArrayList<>();

    Player lastClickPlayer;

    public FociForgeMenu(MenuType type, int id, Container playerInventory, FociForgeBlockEntity te) {
        super(type,id);
        this.te = te;
        addOwnSlots(playerInventory);
        addPlayerSlots(playerInventory);
        addAspectSlots(playerInventory);
        addFociSlots(playerInventory);
    }

    @SuppressWarnings("ConstantConditions")
    public FociForgeMenu(int i, Inventory playerInventory, FriendlyByteBuf packetBuffer) {
        this(ArcanaMenus.FOCI_FORGE.get(),i,playerInventory,(FociForgeBlockEntity) playerInventory.player.level().getBlockEntity(packetBuffer.readBlockPos()));
    }

    @OnlyIn(Dist.CLIENT)
    private void addPlayerSlots(Container playerInventory) {
        int hotX = 88, invX = 148, baseY = FociForgeScreen.HEIGHT - 61;
        // Slots for the main inventory
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++) {
                int x = invX + col * 18;
                int y = row * 18 + baseY;
                addSlot(new Slot(playerInventory, col + row * 9 + 9, x, y));
            }

        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 3; ++col) {
                int x = hotX + col * 18;
                int y = row * 18 + baseY;
                addSlot(new Slot(playerInventory, col + row * 3, x, y));
            }
    }

    private void addOwnSlots(Container playerInventory) {
        @SuppressWarnings("ConstantConditions")
        IItemHandler itemHandler = te.getCapability(ForgeCapabilities.ITEM_HANDLER, null).orElse(null);
        // 303, 11
        addSlot(new SlotItemHandler(itemHandler, 0, 303, 11) {
            public boolean mayPlace(@Nonnull ItemStack stack) {
                // only ink
                return super.mayPlace(stack) && stack.getItem() instanceof MagicDeviceItem;
            }
        });
        // 361, 12
        addSlot(new SlotItemHandler(itemHandler, 1, 361, 12) {
            public boolean mayPlace(@Nonnull ItemStack stack) {
                // only foci or foci parts
                return super.mayPlace(stack) && stack.getItem() == ArcanaItems.FOCUS_PARTS.get() || stack.getItem() == ArcanaItems.DEFAULT_FOCUS.get();
            }

            @Override
            public int getMaxStackSize(@Nonnull ItemStack stack) {
                return 1;
            }

            @Override
            public void setChanged() {
                onFociSlotChange();
            }
        });
    }

    private void onFociSlotChange() {
        // if spell changed, keep it until saved or discarded
        // else replace current spell with nothing/new spell
        if (!te.spellState.spellModified) {
            if (te.focus() == ItemStack.EMPTY || te.focus().getItem() == ArcanaItems.FOCUS_PARTS.get()) {
                te.replaceSpell(new Spell(new StartCircle()));
            } else if (te.focus().getItem() == ArcanaItems.DEFAULT_FOCUS.get()) {
                te.replaceSpell(Spell.fromNBT(te.focus().getOrCreateTag()));
            }
        }
    }

    protected void addFociSlots(Container playerInventory) {
        int SLOT_X = 361;
        int SLOT_Y = 40;
        int SLOT_DELTA = 17;

        for (int yy = 0; yy < FociForgeScreen.FOCI_V_COUNT; yy++) {
            int y = SLOT_Y + SLOT_DELTA * yy;
            Slot slot = new Slot(TMP_FOCI, yy, SLOT_X, y) {
                @Override
                public boolean mayPickup(Player player) {
                    return false;
                }
            };
            addSlot(slot);
            fociSlots.add(slot);
            ItemStack dummyFoci = new ItemStack(ArcanaItems.DEFAULT_FOCUS.get(), 1);
            dummyFoci.getOrCreateTag().putInt("style", yy);
            slot.set(dummyFoci);
        }
    }

    @Override
    public void clicked(int slot, int button, ClickType clickType, Player player) {
        lastClickPlayer = player;
        if (slot >= 0 && slot < slots.size() && slots.get(slot).container == TMP_FOCI) {
            changeFociStyle(slots.get(slot).getItem().getOrCreateTag().getInt("style"));
        } else {
            super.clicked(slot, button, clickType, player);
        }
    }

    protected void addAspectSlots(Container playerInventory) {
        Aspect[] primals = AspectUtils.primalAspects;
        Aspect[] sins = AspectUtils.sinAspects;
        Supplier<AspectHandler> source = () -> AspectHandler.getFrom(te);

        for (int xx = 0; xx < primals.length; xx++) {
            int x = 10 + 17 * xx;
            int y = 11;
            AspectSlot slot = new AspectSlot(primals[xx], source, x, y);
            slot.description = I18n.get("tooltip.arcana.fociforge."+primals[xx]);
            slot.setSymbolic(true);
            aspectSlots.add(slot);
        }
        for (int yy = 0; yy < ASPECT_V_COUNT; yy++) {
            for (int xx = 0; xx < ASPECT_H_COUNT; xx++) {
                int x = 10 + 17 * xx;
                int y = 52 + 16 * yy;
                AspectSlot slot = new AspectSlot(Aspects.EMPTY, source, x, y);
                slot.setSymbolic(true);
                aspectSlots.add(slot);
                scrollableSlots.add(slot);
            }
        }
        for (int yy = 0; yy < sins.length ; yy++) {
            int x = 95;
            int y = 52 + 16 * yy;
            AspectSlot slot = new AspectSlot(sins[yy], source, x, y);
            slot.description = I18n.get("tooltip.arcana.fociforge."+sins[yy]);
            slot.setSymbolic(true);
            aspectSlots.add(slot);
        }
    }

    private void changeFociStyle(int style) {
        Item item = te.focus().getItem();
        if (item == ArcanaItems.FOCUS_PARTS.get())
            te.setItem(1, new ItemStack(ArcanaItems.DEFAULT_FOCUS.get(), 1));
        if (item instanceof FocusItem) {
            te.focus().getOrCreateTag().putInt("style", style);
            changeFociSpell();
        }
    }

    public void changeFociSpell() {
        te.focus().getOrCreateTag().put("spell", te.spellState.currentSpell.toNBT(new CompoundTag()).getCompound("spell"));
        Connection.sendWriteSpell(te.focus(), te.spellState.currentSpell.toNBT(new CompoundTag()));
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        this.setHeldAspect(null);

        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            if (index != 1) {
                ItemStack itemstack1 = slot.getItem();
                itemstack = itemstack1.copy();

                if (index < 2) {
                    if (!this.moveItemStackTo(itemstack1, 2, slots.size(), true))
                        return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(itemstack1, 0, 2, false))
                    return ItemStack.EMPTY;

                if (itemstack1.isEmpty())
                    slot.set(ItemStack.EMPTY);
                else
                    slot.setChanged();
            }
        }

        return itemstack;
    }

    public void onAspectSlotChange() {
        super.onAspectSlotChange();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /**
     * Gets a list of every aspect handler that's open; i.e. can be modified in this GUI and might need syncing.
     * The contents of this list must be the same on the server and client side.
     *
     * @return A list containing all open AspectHandlers.
     */
    public List<AspectHandler> getOpenHandlers() {
        return Collections.singletonList(AspectHandler.getFrom(te));
    }
}
