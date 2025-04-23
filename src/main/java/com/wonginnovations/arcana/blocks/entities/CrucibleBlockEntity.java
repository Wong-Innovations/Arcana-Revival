package com.wonginnovations.arcana.blocks.entities;

import com.wonginnovations.arcana.items.recipes.ArcanaRecipes;
import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.ItemAspectRegistry;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.blocks.CrucibleBlock;
import com.wonginnovations.arcana.items.recipes.AlchemyInventory;
import com.wonginnovations.arcana.items.recipes.AlchemyRecipe;
import com.wonginnovations.arcana.world.AuraView;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.wonginnovations.arcana.blocks.CrucibleBlock.FULL;
import static net.minecraft.world.level.block.Block.UPDATE_CLIENTS;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CrucibleBlockEntity extends BlockEntity {
	
	// Not an aspect handler; cannot be drawn directly from, and has infinite size
	// Should decay or something - to avoid very large NBT, at least.
	Map<Aspect, AspectStack> aspectStackMap = new HashMap<>();
	boolean boiling = false;
	
	public CrucibleBlockEntity(BlockPos pos, BlockState state) {
		super(ArcanaBlockEntities.CRUCIBLE.get(), pos, state);
	}
	
	public Map<Aspect, AspectStack> getAspectStackMap() {
		return aspectStackMap;
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, CrucibleBlockEntity entity) {
		BlockState below = level.getBlockState(pos.below());
		FluidState fluidState = level.getFluidState(pos.below());
		// TODO: use a block+fluid tag
		entity.boiling = entity.hasWater() && (below.getBlock() == Blocks.FIRE || below.getBlock() == Blocks.MAGMA_BLOCK || below.getBlock() == ArcanaBlocks.NITOR.get() || fluidState.getType() == Fluids.FLOWING_LAVA || fluidState.getType() == Fluids.LAVA);
		
		// check for items
		// if there are items that have aspects, boil them :)
		if (!level.isClientSide() && entity.isBoiling()) {
			List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, CrucibleBlock.INSIDE.toAabbs().get(0).move(pos));
			for (ItemEntity item : items) {
				ItemStack stack = item.getItem();
				// check if that item makes a recipe
				// requires a player; droppers and such will never craft alchemy recipes
				// yet; we could make a knowledgeable dropper or such later
				boolean melt = true;
				if (item.getOwner() != null && item.getOwner() instanceof Player thrower) {
					AlchemyInventory inventory = new AlchemyInventory(entity, thrower);
					inventory.setItem(0, stack);
					Optional<AlchemyRecipe> optionalRecipe = level.getRecipeManager().getRecipeFor(ArcanaRecipes.Types.ALCHEMY.get(), inventory, level);
					if (optionalRecipe.isPresent()) {
						melt = false;
						AlchemyRecipe recipe = optionalRecipe.get();
						if (stack.getCount() == 1)
							item.remove(Entity.RemovalReason.DISCARDED);
						else
							stack.shrink(1);
						ItemStack result = recipe.getResultItem(level.registryAccess());
						if (!thrower.addItem(result))
							thrower.drop(result, false);
						for (AspectStack aspectStack : recipe.getAspects()) {
							Aspect aspect = aspectStack.getAspect();
							AspectStack newStack = new AspectStack(aspect, entity.aspectStackMap.get(aspect).getAmount() - aspectStack.getAmount());
							if (!newStack.isEmpty())
								entity.aspectStackMap.put(aspect, newStack);
							else
								entity.aspectStackMap.remove(aspect);
						}
						entity.setChanged();
						level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), UPDATE_CLIENTS);
					}
				}
				if (melt) {
					List<AspectStack> aspects = ItemAspectRegistry.get(stack);
					if (!aspects.isEmpty()) {
						item.remove(Entity.RemovalReason.DISCARDED);
						level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
						entity.setChanged();
						level.sendBlockUpdated(pos, level.getBlockState(pos), level.getBlockState(pos), UPDATE_CLIENTS);
						for (AspectStack aspect : aspects)
							if (aspect.getAmount() != 0)
								entity.aspectStackMap.put(aspect.getAspect(), new AspectStack(aspect.getAspect(), aspect.getAmount() * stack.getCount() + (entity.aspectStackMap.containsKey(aspect.getAspect()) ? entity.aspectStackMap.get(aspect.getAspect()).getAmount() : 0)));
					}
				}
			}
		}
	}
	
	public void empty() {
		// release aspects as flux
		AuraView.SIDED_FACTORY.apply(getLevel()).addFluxAt(getBlockPos(), (float)(aspectStackMap.values().stream().mapToDouble(AspectStack::getAmount).sum() * ArcanaConfig.ASPECT_DUMPING_WASTE.get()));
		aspectStackMap.clear();
		// block handles changing state
	}
	
	private boolean hasWater() {
		return getLevel().getBlockState(getBlockPos()).getValue(FULL);
	}
	
	public boolean isBoiling() {
		return boiling;
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		ListTag aspects = compound.getList("aspects", Tag.TAG_COMPOUND);
		aspectStackMap.clear();
		for (Tag inbt : aspects) {
			CompoundTag aspectNbt = ((CompoundTag)inbt);
			Aspect aspect = AspectUtils.getAspectByName(aspectNbt.getString("aspect"));
			int amount = aspectNbt.getInt("amount");
			aspectStackMap.put(aspect, new AspectStack(aspect, amount));
		}
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		ListTag aspects = new ListTag();
		for (AspectStack stack : aspectStackMap.values()) {
			CompoundTag stackNbt = new CompoundTag();
			stackNbt.putString("aspect", stack.getAspect().name());
			stackNbt.putFloat("amount", stack.getAmount());
			aspects.add(stackNbt);
		}
		compound.put("aspects", aspects);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag);
		return tag;
	}

//	public void handleUpdateTag(BlockState state, CompoundTag tag) {
//		read(state, tag);
//	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
//	public void onDataPacket(NetworkManager net, SUpdateBlockEntityPacket pkt) {
//		handleUpdateTag(getBlockState(), pkt.getNbtCompound());
//	}
}