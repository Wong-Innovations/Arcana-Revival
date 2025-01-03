package arcana.common.blockentities.crafting;

import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.api.aura.AuraHelper;
import arcana.api.crafting.CrucibleRecipe;
import arcana.client.fx.FXDispatcher;
import arcana.common.InventoryFake;
import arcana.common.blockentities.ModBlockEntities;
import arcana.common.blocks.ModBlocks;
import arcana.common.entities.EntitySpecialItem;
import arcana.common.lib.ModSounds;
import arcana.common.lib.crafting.CraftingManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public class BlockEntityCrucible extends BlockEntity {

    public short heat;
    public AspectList aspects = new AspectList();
    public final int maxTags = 500;
    int bellows = -1;
    private int delay = 0;
    private long counter = -100L;
    int prevcolor = 0;
    int prevx = 0;
    int prevy = 0;
    public FluidTank tank;

    public BlockEntityCrucible(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CRUCIBLE_ENTITY.get(), pPos, pBlockState);
        this.tank = new FluidTank(1000);
        this.tank.setFluid(new FluidStack(Fluids.WATER, 0));
        this.heat = 0;
    }

    @Override
    public void load(@NotNull CompoundTag nbttagcompound) {
        super.load(nbttagcompound);
        this.heat = nbttagcompound.getShort("Heat");
        this.tank.readFromNBT(nbttagcompound);
        if (nbttagcompound.getAllKeys().contains("Empty")) {
            this.tank.setFluid(null);
        }

        this.aspects.readFromNBT(nbttagcompound);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbttagcompound) {
        super.saveAdditional(nbttagcompound);
        nbttagcompound.putShort("Heat", this.heat);
        this.tank.writeToNBT(nbttagcompound);
        this.aspects.writeToNBT(nbttagcompound);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        ++this.counter;
        int prevheat = this.heat;
        if (!pLevel.isClientSide()) {
            if (this.tank.getFluidAmount() > 0) {
                BlockState block = pLevel.getBlockState(this.getBlockPos().below());
                // TODO: maybe allow campfires too?
                if (!block.is(Blocks.LAVA) && !block.is(Blocks.FIRE) && !block.is(Blocks.MAGMA_BLOCK) && !block.is(Blocks.SOUL_FIRE) /* && !ModBlocks.nitor.containsValue(block.getBlock()) && */) {
                    if (this.heat > 0) {
                        --this.heat;
                        if (this.heat == 149) {
                            this.setChanged();
                            this.syncTile(false);
                        }
                    }
                } else if (this.heat < 200) {
                    ++this.heat;
                    if (prevheat < 151 && this.heat >= 151) {
                        this.setChanged();
                        this.syncTile(false);
                    }
                }
            } else if (this.heat > 0) {
                --this.heat;
            }

            if (this.aspects.visSize() > 500) {
                this.spillRandom();
            }

            if (this.counter >= 100L) {
                this.spillRandom();
                this.counter = 0L;
            }
        } else if (this.tank.getFluidAmount() > 0) {
            this.drawEffects();
        }

        if (pLevel.isClientSide() && prevheat < 151 && this.heat >= 151) {
            ++this.heat;
        }

    }

    private void drawEffects() {
        int a;
        if (this.heat > 150) {
            FXDispatcher.INSTANCE.crucibleFroth((float)this.getBlockPos().getX() + 0.2F + this.level.random.nextFloat() * 0.6F, (float)this.getBlockPos().getY() + this.getFluidHeight(), (float)this.getBlockPos().getZ() + 0.2F + this.level.random.nextFloat() * 0.6F);
            if (this.aspects.visSize() > 500) {
                for(a = 0; a < 2; ++a) {
                    FXDispatcher.INSTANCE.crucibleFrothDown((float)this.getBlockPos().getX(), (float)(this.getBlockPos().getY() + 1), (float)this.getBlockPos().getZ() + this.level.random.nextFloat());
                    FXDispatcher.INSTANCE.crucibleFrothDown((float)(this.getBlockPos().getX() + 1), (float)(this.getBlockPos().getY() + 1), (float)this.getBlockPos().getZ() + this.level.random.nextFloat());
                    FXDispatcher.INSTANCE.crucibleFrothDown((float)this.getBlockPos().getX() + this.level.random.nextFloat(), (float)(this.getBlockPos().getY() + 1), (float)this.getBlockPos().getZ());
                    FXDispatcher.INSTANCE.crucibleFrothDown((float)this.getBlockPos().getX() + this.level.random.nextFloat(), (float)(this.getBlockPos().getY() + 1), (float)(this.getBlockPos().getZ() + 1));
                }
            }
        }

        if (this.level.random.nextInt(6) == 0 && this.aspects.size() > 0) {
            a = this.aspects.getAspects()[this.level.random.nextInt(this.aspects.size())].getColor() + -16777216;
            int x = 5 + this.level.random.nextInt(22);
            int y = 5 + this.level.random.nextInt(22);
            this.delay = this.level.random.nextInt(10);
            this.prevcolor = a;
            this.prevx = x;
            this.prevy = y;
            Color c = new Color(a);
            float r = (float)c.getRed() / 255.0F;
            float g = (float)c.getGreen() / 255.0F;
            float b = (float)c.getBlue() / 255.0F;
            FXDispatcher.INSTANCE.crucibleBubble((float)this.getBlockPos().getX() + (float)x / 32.0F + 0.015625F, (float)this.getBlockPos().getY() + 0.05F + this.getFluidHeight(), (float)this.getBlockPos().getZ() + (float)y / 32.0F + 0.015625F, r, g, b);
        }

    }

    public void ejectItem(ItemStack items) {
        boolean first = true;

        do {
            ItemStack spitout = items.copy();
            if (spitout.getCount() > spitout.getMaxStackSize()) {
                spitout.setCount(spitout.getMaxStackSize());
            }

            items.shrink(spitout.getCount());
            EntitySpecialItem entityitem = new EntitySpecialItem(this.level, (float)this.getBlockPos().getX() + 0.5F, (float)this.getBlockPos().getY() + 0.71F, (float)this.getBlockPos().getZ() + 0.5F, spitout);
            entityitem.setDeltaMovement(first ? 0.0 : (double)((this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.01F),0.07500000298023224, first ? 0.0 : (double)((this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.01F));
            this.level.addFreshEntity(entityitem);
            first = false;
        } while(items.getCount() > 0);

    }

    public ItemStack attemptSmelt(ItemStack item, Entity thrower) {
        boolean bubble = false;
        boolean craftDone = false;
        int stacksize = item.getCount();
        if (!(thrower instanceof Player player)) return null;

        for(int a = 0; a < stacksize; ++a) {
            CrucibleRecipe rc = CraftingManager.findMatchingCrucibleRecipe(player, this.aspects, item);
            if (rc != null && this.tank.getFluidAmount() > 0) {
                ItemStack out = rc.getRecipeOutput().copy();
                if (player != null) {
                    InventoryFake inv = new InventoryFake(1);
                    inv.addItem(item);
                    MinecraftForge.EVENT_BUS.post(new PlayerEvent.ItemCraftedEvent(player, out, inv));
//                    FMLCommonHandler.instance().firePlayerCraftingEvent(player, out, new InventoryFake(new ItemStack[]{item}));
                }

                this.aspects = rc.removeMatching(this.aspects);
                this.tank.drain(50, FluidAction.EXECUTE);
                this.ejectItem(out);
                craftDone = true;
                --stacksize;
                this.counter = -250L;
            } else {
                AspectList ot = CraftingManager.getObjectTags(item);
                if (ot != null && ot.size() != 0) {
                    for (Aspect tag : ot.getAspects()) {
                        this.aspects.add(tag, ot.getAmount(tag));
                    }

                    bubble = true;
                    --stacksize;
                    this.counter = -150L;
                }
            }
        }

        if (bubble) {
            this.level.playSound(Minecraft.getInstance().player, this.getBlockPos(), ModSounds.bubble.get(), SoundSource.BLOCKS, 0.2F, 1.0F + this.level.random.nextFloat() * 0.4F);
            this.syncTile(false);
            this.level.blockEvent(this.getBlockPos(), ModBlocks.crucible.get(), 2, 1);
        }

        if (craftDone) {
            this.syncTile(false);
            this.level.blockEvent(this.getBlockPos(), ModBlocks.crucible.get(), 99, 0);
        }

        this.setChanged();
        if (stacksize <= 0) {
            return null;
        } else {
            item.setCount(stacksize);
            return item;
        }
    }

    public void attemptSmelt(ItemEntity entity) {
        ItemStack item = entity.getItem();
        ItemStack res = this.attemptSmelt(item, entity.getOwner());
        if (res != null && res.getCount() > 0) {
            item.setCount(res.getCount());
            entity.setItem(item);
        } else {
            entity.discard();
        }

    }

    public float getFluidHeight() {
        float base = 0.3F + 0.5F * ((float)this.tank.getFluidAmount() / (float)this.tank.getCapacity());
        float out = base + (float)this.aspects.visSize() / 500.0F * (1.0F - base);
        if (out > 1.0F) {
            out = 1.001F;
        }

        if (out == 1.0F) {
            out = 0.9999F;
        }

        return out;
    }

    public void spillRandom() {
        if (this.aspects.size() > 0) {
            Aspect tag = this.aspects.getAspects()[this.level.random.nextInt(this.aspects.getAspects().length)];
            this.aspects.remove(tag, 1);
            AuraHelper.polluteAura(this.level, this.getBlockPos(), tag == Aspect.FLUX ? 1.0F : 0.25F, true);
        }

        this.setChanged();
        this.syncTile(false);
    }

    public void spillRemnants() {
        int vs = this.aspects.visSize();
        if (this.tank.getFluidAmount() > 0 || vs > 0) {
            this.tank.setFluid(new FluidStack(Fluids.WATER, 0));
            AuraHelper.polluteAura(this.level, this.getBlockPos(), (float)vs * 0.25F, true);
            int f = this.aspects.getAmount(Aspect.FLUX);
            if (f > 0) {
                AuraHelper.polluteAura(this.level, this.getBlockPos(), (float)f * 0.75F, false);
            }

            this.aspects = new AspectList();
            this.level.blockEvent(this.getBlockPos(), ModBlocks.crucible.get(), 2, 5);
            this.setChanged();
            this.syncTile(false);
        }

    }

    @Override
    public boolean triggerEvent(int i, int j) {
        if (i == 99) {
            if (this.level.isClientSide()) {
                FXDispatcher.INSTANCE.drawBamf((double)this.getBlockPos().getX() + 0.5, (float)this.getBlockPos().getY() + 1.25F, (double)this.getBlockPos().getZ() + 0.5, true, true, Direction.UP);
                this.level.playLocalSound((float)this.getBlockPos().getX() + 0.5F, (float)this.getBlockPos().getY() + 0.5F, (float)this.getBlockPos().getZ() + 0.5F, ModSounds.spill.get(), SoundSource.BLOCKS, 0.2F, 1.0F, false);
            }

            return true;
        } else if (i == 1) {
            if (this.level.isClientSide()) {
                FXDispatcher.INSTANCE.drawBamf(this.getBlockPos().above(), true, true, Direction.UP);
            }

            return true;
        } else if (i != 2) {
            return super.triggerEvent(i, j);
        } else {
            this.level.playLocalSound((float)this.getBlockPos().getX() + 0.5F, (float)this.getBlockPos().getY() + 0.5F, (float)this.getBlockPos().getZ() + 0.5F, ModSounds.spill.get(), SoundSource.BLOCKS, 0.2F, 1.0F, false);
            if (this.level.isClientSide()) {
                for(int q = 0; q < 10; ++q) {
                    FXDispatcher.INSTANCE.crucibleBoil(this.getBlockPos(), this, j);
                }
            }

            return true;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ(), this.getBlockPos().getX() + 1, this.getBlockPos().getY() + 1, this.getBlockPos().getZ() + 1);
    }

    public AspectList getAspects() {
        return this.aspects;
    }

    public void setAspects(AspectList aspects) {
    }

    public int addToContainer(Aspect tag, int amount) {
        return 0;
    }

    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }

    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }

    public boolean doesContainerContain(AspectList ot) {
        return false;
    }

    public int containerContains(Aspect tag) {
        return 0;
    }

    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }

    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
        return capability == ForgeCapabilities.FLUID_HANDLER || super.getCapability(capability, facing).isPresent();
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        return capability == ForgeCapabilities.FLUID_HANDLER ? LazyOptional.of(() -> this.tank).cast() : super.getCapability(capability, facing);
    }

//    public IFluidTankProperties[] getTankProperties() {
//        return this.tank.getTankProperties();
//    }

    public int fill(FluidStack resource, FluidAction action) {
        this.setChanged();
        this.syncTile(false);
        return this.tank.fill(resource, action);
    }

    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack fs = this.tank.drain(resource, action);
        this.setChanged();
        this.syncTile(false);
        return fs;
    }

    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack fs = this.tank.drain(maxDrain, action);
        this.setChanged();
        this.syncTile(false);
        return fs;
    }

    public void syncTile(boolean rerender) {
        BlockState state = this.level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), state, state, 2 + (rerender ? 4 : 0));
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }
    
}
