package com.wonginnovations.arcana.blocks.pipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.blocks.entities.ArcanaBlockEntities;
import com.wonginnovations.arcana.containers.PumpMenu;
import com.wonginnovations.arcana.items.CrystalItem;
import com.wonginnovations.arcana.items.EnchantedFilterItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PumpBlockEntity extends TubeBlockEntity implements MenuProvider {
	
	protected static int PULL_AMOUNT = 4;
	protected static int PULL_TIME = 5;
	protected static float SPECK_SPEED = 0.5f;
	
	public boolean suppressedByRedstone = false;
	public ItemStackHandler inventory = new ItemStackHandler(2); // filter, crystal
	public Direction direction;
	
	// pull aspects from containers and convert them into specks
	public PumpBlockEntity(BlockPos pos, BlockState state) {
		super(ArcanaBlockEntities.ASPECT_PUMP.get(), pos, state);
		this.direction = Direction.UP;
	}
	
	public PumpBlockEntity(BlockPos pos, BlockState state, Direction direction) {
		super(ArcanaBlockEntities.ASPECT_PUMP.get(), pos, state);
		this.direction = direction;
	}
	
	@SuppressWarnings("ConstantConditions")
	public void tick(Level level, BlockPos pos, BlockState state) {
		super.tick(level, pos, state);
		// pull new specks
		if (!suppressedByRedstone && specks.size() < 10 && level.getGameTime() % PULL_TIME == 0) {
			BlockEntity from = level.getBlockEntity(pos.offset(direction.getOpposite().getNormal()));
			AspectHandler handler = AspectHandler.getFrom(from);
			if (handler != null) {
				boolean hasFilter = !filter().isEmpty() && filter().getItem() instanceof EnchantedFilterItem;
				int effBoost = hasFilter ? ((EnchantedFilterItem)filter().getItem()).efficiencyBoost : 0;
				AspectStack stack;
				if (filteredTo() == Aspects.EMPTY)
					stack = handler.drainAny(PULL_AMOUNT + effBoost);
				else
					stack = new AspectStack(filteredTo(), handler.drain(filteredTo(), PULL_AMOUNT + effBoost));
				if (!stack.isEmpty()) {
					int speedBoost = hasFilter ? ((EnchantedFilterItem)filter().getItem()).speedBoost : 0;
					addSpeck(new AspectSpeck(stack, SPECK_SPEED + speedBoost * 0.1f, direction, 0));
				}
			}
		}
	}
	
	protected Optional<Direction> redirect(AspectSpeck speck, boolean canPass) {
		return (!suppressedByRedstone && (crystal().isEmpty() || filteredTo() == speck.payload.getAspect()))
				? Optional.of(direction)
				: Optional.empty();
	}
	
	public ItemStack filter() {
		return inventory.getStackInSlot(0);
	}
	
	public ItemStack crystal() {
		return inventory.getStackInSlot(1);
	}
	
	public Aspect filteredTo() {
		return crystal().getItem() instanceof CrystalItem ? ((CrystalItem)crystal().getItem()).aspect : Aspects.EMPTY;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public @Nonnull <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER)
			return (LazyOptional<T>)LazyOptional.of(() -> inventory);
		return super.getCapability(cap, side);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("block.arcana.essentia_pump");
	}
	
	@Override
	public @Nullable AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
		return new PumpMenu(id, this, playerInv);
	}

	@Override
	public void load(CompoundTag nbt) {
		super.load(nbt);
		inventory.deserializeNBT(nbt.getCompound("items"));
		suppressedByRedstone = nbt.getBoolean("suppressed");
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.put("items", inventory.serializeNBT());
		compound.putBoolean("suppressed", suppressedByRedstone);
	}
}