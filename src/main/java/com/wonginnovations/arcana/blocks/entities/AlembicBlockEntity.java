package com.wonginnovations.arcana.blocks.entities;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.handlers.AspectBattery;
import com.wonginnovations.arcana.aspects.handlers.AspectHandlerCapability;
import com.wonginnovations.arcana.aspects.handlers.AspectHolder;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.blocks.pipes.AspectSpeck;
import com.wonginnovations.arcana.blocks.pipes.TubeBlockEntity;
import com.wonginnovations.arcana.client.render.particles.AspectHelixParticleData;
import com.wonginnovations.arcana.containers.AlembicMenu;
import com.wonginnovations.arcana.items.EnchantedFilterItem;
import com.wonginnovations.arcana.world.AuraView;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.wonginnovations.arcana.aspects.Aspects.EMPTY;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AlembicBlockEntity extends BlockEntity implements MenuProvider {
	
	// 50 of 5 aspects
	// TODO: see usage of ALEMBIC_BASE_DISTILL_EFFICIENCY
	public AspectBattery aspects = new AspectBattery(/*5, 50*/);
	public boolean suppressedByRedstone = false;
	public ItemStackHandler inventory = new ItemStackHandler(2);
	public int burnTicks = 0;
	public int maxBurnTicks = 0;
	
	protected int crucibleLevel = -1;
	protected boolean stacked = false;
	
	public AlembicBlockEntity(BlockPos pos, BlockState state) {
		super(ArcanaBlockEntities.ALEMBIC.get(), pos, state);
		for (int i = 0; i < 5; i++) {
			aspects.initHolders(50, 5);
			aspects.getHolders().forEach(h -> h.setCanInsert(false));
		}
	}

	public static void tick(Level level, BlockPos pos, BlockState state, AlembicBlockEntity entity) {
		if (entity.isOn() && level != null) {
			// scan for boiling crucibles
			// 1-4 blocks of air, +3 of other alembics by default
			int maxAirs = ArcanaConfig.MAX_ALEMBIC_AIR.get(), maxAlembics = ArcanaConfig.MAX_ALEMBIC_STACK.get();
			// if alembics are present, don't show particles
			entity.crucibleLevel = -1;
			boolean pocket = false;
			int stack = 0;
			int airs = -1;
			for (int i = pos.getY() - 1; i > pos.getY() - (maxAirs + maxAlembics + 1); i--) {
				int passes = (pos.getY() - 1) - i;
				BlockPos blockPos = new BlockPos(pos.getX(), i, pos.getZ());
				BlockState blockState = level.getBlockState(blockPos);
				airs = passes - stack;
				if (blockState.getBlock() == ArcanaBlocks.ALEMBIC.get()) {
					// air followed by alembic is invalid
					if (pocket)
						break;
					stack++;
				}
				// only three alembics
				if (stack > maxAlembics)
					break;
				if (blockState.isAir()) {
					// up to 3 alembics + 4 air blocks
					if (airs > maxAirs)
						break;
					pocket = true;
				}
				if (level.getBlockEntity(blockPos) instanceof CrucibleBlockEntity) {
					// found it
					// if we haven't gotten any air, don't save (it's invalid)
					if (airs > 0)
						entity.crucibleLevel = i;
					break;
				}
			}
			entity.stacked = stack > 0;
			if (entity.crucibleLevel != -1) {
				if (entity.burnTicks == 0) {
					ItemStack fuel = entity.fuel();
					int newTicks = ForgeHooks.getBurnTime(fuel, null) / 2; //furnaces need significantly longer to work
					if (newTicks > 0) {
						entity.burnTicks = newTicks;
						entity.maxBurnTicks = newTicks;
						if (fuel.getCount() == 1)
							entity.inventory.setStackInSlot(1, fuel.getCraftingRemainingItem());
						else
							fuel.setCount(fuel.getCount() - 1);
						entity.setChanged();
					}
				}
				if (entity.burnTicks > 0) {
					BlockPos cruciblePos = new BlockPos(pos.getX(), entity.crucibleLevel, pos.getZ());
					CrucibleBlockEntity te = (CrucibleBlockEntity)level.getBlockEntity(cruciblePos);
					if (te != null && !te.getAspectStackMap().isEmpty()) {
						Aspect aspect = EMPTY;
						// find an aspect stack we can actually pull
						AspectHolder adding = null;
						for (AspectHolder holder : entity.aspects.getHolders()) {
							if (holder.getCapacity() - holder.getStack().getAmount() > 0) {
								adding = holder;
								Aspect maybe = te.getAspectStackMap().values().stream().filter(stack1 -> stack1.getAspect() == holder.getStack().getAspect() || holder.getStack().isEmpty()).findFirst().map(AspectStack::getAspect).orElse(EMPTY);
								if (maybe != EMPTY) {
									aspect = maybe;
									break;
								}
							}
						}
						
						if (aspect != EMPTY) {
							AspectStack aspectStack = te.getAspectStackMap().get(aspect);
							if (!entity.stacked)
								level.addParticle(new AspectHelixParticleData(aspect, 20 * airs + 15, level.random.nextInt(180), new Vec3(0, 1, 0)), cruciblePos.getX() + .5 + level.random.nextFloat() * .1, cruciblePos.getY() + .7, cruciblePos.getZ() + .5 + level.random.nextFloat() * .1, 0, 0, 0);
							// pick a random aspect, take from it, and store them in our actual aspect handler
							int freeTicks = entity.filter().isEmpty() ? 0 : ((EnchantedFilterItem)entity.filter().getItem()).speedBoost;
							if (level.getGameTime() % (ArcanaConfig.ALEMBIC_DISTILL_TIME.get() - freeTicks * 2L) == 0) {
								float diff = Math.min(aspectStack.getAmount(), 1);
								AspectStack newStack = new AspectStack(aspectStack.getAspect(), aspectStack.getAmount() - 1);
								if (!newStack.isEmpty())
									te.getAspectStackMap().put(aspect, newStack);
								else
									te.getAspectStackMap().remove(aspect);
								
								int efficiencyBoost = entity.filter().isEmpty() ? 0 : ((EnchantedFilterItem)entity.filter().getItem()).efficiencyBoost;
								// -1: 0.6 multiplier, 0.4 flux
								// +0: 0.7 multiplier, 0.3 flux
								// +1: 0.8 multiplier, 0.3 flux
								// +2: 0.8 multiplier, 0.2 flux
								// +3: 0.9 multiplier, 0.1 flux
								float effMultiplier = 0.7f;
								float fluxMultiplier = switch (efficiencyBoost) {
                                    case -1 -> {
                                        effMultiplier = 0.6f;
                                        yield 0.4f;
                                    }
                                    case 1 -> {
                                        effMultiplier = 0.8f;
                                        yield 0.3f;
                                    }
                                    case 2 -> {
                                        effMultiplier = 0.8f;
                                        yield 0.2f;
                                    }
                                    case 3 -> {
                                        effMultiplier = 0.9f;
                                        yield 0.1f;
                                    }
                                    default -> 0.3f;
                                };
                                adding.setCanInsert(true);
								adding.insert(new AspectStack(aspectStack.getAspect(), diff * effMultiplier), false);
								adding.setCanInsert(false);
								AuraView.SIDED_FACTORY.apply(level).addFluxAt(entity.getBlockPos(), diff * fluxMultiplier);
							}
						}
					}
					// then push them out into the total pipe system from sides
					if (level.getGameTime() % 5 == 0)
						for (Direction dir : Direction.Plane.HORIZONTAL) {
							BlockEntity tubeTe = level.getBlockEntity(entity.getBlockPos().offset(dir.getNormal()));
							if (tubeTe instanceof TubeBlockEntity aspectTube) {
                                AspectHolder holder = entity.aspects.findFirstFullHolder();
								// try not to add specks that can't transfer
								if (aspectTube.getSpecks().size() < 6 && holder != null && holder.getStack().getAmount() >= 0.5) {
									AspectStack speck = entity.aspects.drainAny(ArcanaConfig.MAX_ALEMBIC_ASPECT_OUT.get());
									if (!speck.isEmpty())
										aspectTube.addSpeck(new AspectSpeck(speck, 0.8f, dir, 0));
								}
							}
						}
					// aspects can be pulled from the top when pulling becomes a thing but that doesn't matter here
				}
				if (entity.burnTicks > 0)
					entity.burnTicks--;
			}
		}
	}
	
	public boolean isOn() {
		return !suppressedByRedstone;
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		aspects.deserializeNBT(compound.getCompound("aspects"));
		suppressedByRedstone = compound.getBoolean("suppressed");
		inventory.deserializeNBT(compound.getCompound("items"));
		burnTicks = compound.getInt("burnTicks");
		maxBurnTicks = compound.getInt("maxBurnTicks");
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.put("aspects", aspects.serializeNBT());
		compound.putBoolean("suppressed", suppressedByRedstone);
		compound.put("items", inventory.serializeNBT());
		compound.putInt("burnTicks", burnTicks);
		compound.putInt("maxBurnTicks", maxBurnTicks);
	}
	
	public ItemStack filter() {
		return inventory.getStackInSlot(0);
	}
	
	public ItemStack fuel() {
		return inventory.getStackInSlot(1);
	}
	
	@SuppressWarnings("unchecked") // bad generics checkers
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY)
			return aspects.getCapability(AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY).cast();
		if (cap == ForgeCapabilities.ITEM_HANDLER)
			return (LazyOptional<T>)LazyOptional.of(() -> inventory);
		return super.getCapability(cap, side);
	}

	@Override
	public Component getDisplayName() {
		return Component.translatable("block.arcana.alembic");
	}
	
	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return new AlembicMenu(id, this, inventory);
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag nbtTagCompound = new CompoundTag();
		saveAdditional(nbtTagCompound);
		return nbtTagCompound;
	}
}