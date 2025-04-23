package com.wonginnovations.arcana.client.gui;

import com.wonginnovations.arcana.ClientProxy;
import com.wonginnovations.arcana.items.MagicDeviceItem;
import com.wonginnovations.arcana.items.attachment.FocusItem;
import com.wonginnovations.arcana.network.Connection;
import com.wonginnovations.arcana.network.PkSwapFocus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class SwapFocusScreen extends Screen {
	private final InteractionHand hand;

	private boolean hasClicked = false;
	private static final Random random = new Random();

	public SwapFocusScreen(InteractionHand hand) {
		super(Component.literal(""));
		this.hand = hand;
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		ItemStack wand = Objects.requireNonNull(getMinecraft().player).getItemInHand(hand);
		if (wand.getItem() instanceof MagicDeviceItem && ((MagicDeviceItem)wand.getItem()).canSwapFocus(getMinecraft().player)) {
			//display current focus
			MagicDeviceItem.getFocusStack(wand).ifPresent(stack -> guiGraphics.renderItem(stack, width / 2 - 8, height / 2 - 8));
			//display all foci in the inventory
			List<ItemStack> foci = getAllFociStacks();
			int size = foci.size();
			int distance = size * 5 + 28;
			int particleCount = size * 12 + 16;
			for (int i = 0; i < particleCount; i++) {
				random.setSeed(i);
				double v = Math.toRadians((i + (getMinecraft().player.tickCount + partialTicks) / (5f + random.nextInt(5) - 2)) * (360f / (particleCount)));
				int color = UiUtil.combine(random.nextInt(128) + 127, random.nextInt(128) + 127, random.nextInt(128) + 127) | 0x6F000000;
				int distance1 = distance + random.nextInt(21) - 10;
				int x = (int)(Mth.cos((float)v) * (distance1 + 4)) - 4 + width / 2;
				int y = (int)(Mth.sin((float)v) * (distance1 + 4)) - 4 + height / 2;
				guiGraphics.fillGradient(x, y, x + 8, y + 8, color, color);
			}
			for (int i = 0; i < size; i++) {
				ItemStack focus = foci.get(i);
				int x = (int)(Mth.cos((float)Math.toRadians(i * (360f / size))) * distance) - 8 + width / 2;
				int y = (int)(Mth.sin((float)Math.toRadians(i * (360f / size))) * distance) - 8 + height / 2;
				guiGraphics.renderItem(focus, x, y);
			}
		}
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return select(mouseX, mouseY, true);
	}

	private List<ItemStack> getAllFociStacks() {
		//TODO: focus pouch?
		List<ItemStack> foci = new ArrayList<>();
		Inventory inventory = Objects.requireNonNull(getMinecraft().player).getInventory();
		for (int i = 0; i < inventory.getContainerSize(); i++) {
			ItemStack stack = inventory.getItem(i);
			if (stack.getItem() instanceof FocusItem)
				foci.add(stack);
		}
		return foci;
	}

	public boolean select(double mouseX, double mouseY, boolean clicked) {
		// if player clicked on focus ignore future requests
		hasClicked = true;

		// find the nearest focus
		// if you click the middle, you remove your focus
		if (mouseX >= -8 + (width / 2f) && mouseX < 8 + (width / 2f) && mouseY >= -8 + (height / 2f) && mouseY < 8 + (height / 2f)) {
			// if you didn't click, and the mouse is in the middle, do nothing
			if (!clicked)
				return true;
			// send swap focus packet
			// index = -1 AKA remove focus
			Connection.sendToServer(new PkSwapFocus(hand, -1));
			return true;
		}
		List<ItemStack> foci = getAllFociStacks();
		int size = foci.size();
		//int distance = size * 5 + 28;
		// get nearest focus
		// find what slice the mouse falls in
		if (size > 0) {
			double angle = Math.toDegrees(Math.atan2(mouseY - height / 2d, mouseX - width / 2d)) + (180d / size);
			angle = angle % 360;
			angle = angle < 0 ? 360 + angle : angle;
			int item = (int)(angle / (360d / size));
			Connection.sendToServer(new PkSwapFocus(hand, item));
			return true;
		}
		return false;
	}

	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		// if SWAP_FOCUS_BINDING is released, close screen
		if (!ClientProxy.SWAP_FOCUS_BINDING.isDown()) {
			double mX = getMinecraft().mouseHandler.xpos() * (double)getMinecraft().getWindow().getGuiScaledWidth() / (double)getMinecraft().getWindow().getWidth();
			double mY = getMinecraft().mouseHandler.ypos() * (double)getMinecraft().getWindow().getGuiScaledHeight() / (double)getMinecraft().getWindow().getHeight();

			// If player has clicked on foci before don't select other foci.
			if (!hasClicked)
				select(mX, mY, false);

			Minecraft.getInstance().setScreen(null);
			return true;
		}
		return false;
	}

	public boolean isPauseScreen() {
		return false;
	}
}