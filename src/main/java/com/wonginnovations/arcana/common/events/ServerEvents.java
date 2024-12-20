package com.wonginnovations.arcana.common.events;

import com.google.common.base.Predicate;
import com.wonginnovations.arcana.common.entities.ModEntityTypes;
import com.wonginnovations.arcana.common.entities.SpecialItemEntity;
import com.wonginnovations.arcana.common.network.PacketHandler;
import com.wonginnovations.arcana.common.network.fx.FXBlockBamfPacket;
import com.wonginnovations.arcana.common.utils.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Mod.EventBusSubscriber
public class ServerEvents {

    static HashMap<ResourceKey<Level>, Integer> serverTicks = new HashMap<>();

    public static HashMap<ResourceKey<Level>, LinkedBlockingQueue<VirtualSwapper>> swapList = new HashMap<>();
    public static final Predicate<SwapperPredicate> DEFAULT_PREDICATE = pred -> true;
    private static final HashMap<ResourceKey<Level>, LinkedBlockingQueue<RunnableEntry>> serverRunList = new HashMap<>();

    @SubscribeEvent
    public static void worldTick(TickEvent.LevelTickEvent event) {
        if (event.side != LogicalSide.SERVER || !(event.level instanceof ServerLevel)) return;
        ResourceKey<Level> dim = event.level.dimension();
        if (event.phase != TickEvent.Phase.END) return;
        if (!serverTicks.containsKey(dim)) {
            serverTicks.put(dim, 0);
        }

        LinkedBlockingQueue<RunnableEntry> rlist = serverRunList.get(dim);
        if (rlist == null) {
            serverRunList.put(dim, new LinkedBlockingQueue<>());
        } else if (!rlist.isEmpty()) {
            LinkedBlockingQueue<RunnableEntry> temp = new LinkedBlockingQueue<>();

            while(!rlist.isEmpty()) {
                RunnableEntry current = rlist.poll();
                if (current != null) {
                    if (current.delay > 0) {
                        --current.delay;
                        temp.offer(current);
                    } else {
                        try {
                            current.runnable.run();
                        } catch (Exception ignored) {}
                    }
                }
            }

            while(!temp.isEmpty()) {
                rlist.offer(temp.poll());
            }
        }

        int ticks = serverTicks.get(dim);
        tickBlockSwap((ServerLevel) event.level);
//        tickBlockBreak(event.world);

        serverTicks.put(dim, ticks + 1);
    }

    private static void tickBlockSwap(ServerLevel level) {
        ResourceKey<Level> dim = level.dimension();
        LinkedBlockingQueue<VirtualSwapper> queue = swapList.get(dim);
        LinkedBlockingQueue<VirtualSwapper> queue2 = new LinkedBlockingQueue<>();
        if (queue != null) {
            while(true) {
                if (queue.isEmpty()) {
                    swapList.put(dim, queue2);
                    break;
                }

                VirtualSwapper vs = queue.poll();
                if (vs != null) {
                    BlockState bs = level.getBlockState(vs.pos);
                    boolean allow = bs.getDestroySpeed(level, vs.pos) >= 0.0F;
//                    if (vs.source != null && vs.source instanceof BlockState && vs.source != bs || vs.source != null && vs.source instanceof Material && (Material)vs.source != bs.getMaterial()) {
//                        allow = false;
//                    }

//                    if (vs.visCost > 0.0F && AuraHelper.getVis(level, vs.pos) < vs.visCost) {
//                        allow = false;
//                    }

                    if (!level.getWorldBorder().isWithinBounds(vs.pos)) allow = false;
                    if (level instanceof ServerLevel && level.getServer().isUnderSpawnProtection(level, vs.pos, vs.player)) allow = false;

                    Event blockPlaceEvent = new BlockEvent.EntityPlaceEvent(BlockSnapshot.create(level.dimension(), level, vs.pos), level.getBlockState(vs.pos.offset(Direction.DOWN.getNormal())), vs.player);

                    if (allow && (vs.target == null || vs.target.isEmpty() || !ItemStack.isSameItem(vs.target, new ItemStack(bs.getBlock(), 1))) && !MinecraftForge.EVENT_BUS.post(blockPlaceEvent) && vs.allowSwap.apply(new SwapperPredicate(level, vs.player, vs.pos))) {
                        int slot;
                        if (vs.consumeTarget && vs.target != null && !vs.target.isEmpty()) {
                            slot = vs.player.getInventory().findSlotMatchingItem(vs.target);
                        } else {
                            slot = 1;
                        }

                        if (vs.player.isCreative()) {
                            slot = 1;
                        }

                        boolean matches = false;
                        // no longer needed?
//                        if (vs.source instanceof Material) {
//                            matches = bs.getMaterial() == (Material)vs.source;
//                        }

                        if (vs.source instanceof BlockState) {
                            matches = bs == vs.source;
                        }

                        if ((vs.source == null || matches) && slot >= 0) {
                            if (!vs.player.isCreative()) {
                                if (vs.consumeTarget) {
                                    vs.player.getInventory().removeItem(slot, 1);
                                }

                                if (vs.pickup) {
                                    List<ItemStack> ret = new ArrayList<>();
                                    if (vs.silk && !BlockUtils.getSilkTouchDrop(bs, level, vs.pos, vs.player).isEmpty()) {
                                        ItemStack itemstack = BlockUtils.getSilkTouchDrop(bs, level, vs.pos, vs.player);
                                        if (itemstack != null && !itemstack.isEmpty()) {
                                            ret.add(itemstack);
                                        }
                                    } else {
                                        ret = BlockUtils.getDrops(bs, level, vs.pos, vs.fortune);
                                    }

                                    if (!ret.isEmpty()) {
                                        for (ItemStack is : ret) {
                                            if (!vs.player.getInventory().add(is)) {
                                                Vec3 center = Vec3.atCenterOf(vs.pos);
                                                level.addFreshEntity(new ItemEntity(level, center.x, center.y, center.z, is));
                                            }
                                        }
                                    }
                                }

//                                if (vs.visCost > 0.0F) {
//                                    ThaumcraftApi.internalMethods.drainVis(level, vs.pos, vs.visCost, false);
//                                }
                            }

                            if (vs.target != null && !vs.target.isEmpty()) {
                                Block tb = Block.byItem(vs.target.getItem());
                                if (tb != Blocks.AIR) {
                                    level.setBlock(vs.pos, tb.defaultBlockState(), 3);
                                } else {
                                    level.setBlock(vs.pos, Blocks.AIR.defaultBlockState(), 3);
                                    SpecialItemEntity entityItem = new SpecialItemEntity(level, (double)vs.pos.getX() + 0.5, (double)vs.pos.getY() + 0.1, (double)vs.pos.getZ() + 0.5, vs.target.copy());
                                    entityItem.setDeltaMovement(0,0,0);
                                    level.addFreshEntity(entityItem);
                                }
                            } else {
                                level.setBlock(vs.pos, Blocks.AIR.defaultBlockState(), 3);
                            }

                            if (vs.fx) {
                                PacketHandler.sendToNear(level, vs.pos, 32, new FXBlockBamfPacket(vs.pos, vs.color, true, vs.fancy, null));
                            }

                            if (vs.lifespan > 0) {
                                for(int xx = -1; xx <= 1; ++xx) {
                                    for(int yy = -1; yy <= 1; ++yy) {
                                        for(int zz = -1; zz <= 1; ++zz) {
                                            matches = false;
//                                            if (vs.source instanceof Material) {
//                                                BlockState bb = level.getBlockState(vs.pos.add(xx, yy, zz));
//                                                matches = bb.getBlock().getMaterial(bb) == vs.source;
//                                            }

                                            if (vs.source instanceof BlockState) {
                                                matches = level.getBlockState(vs.pos.offset(xx, yy, zz)) == vs.source;
                                            }

                                            if ((xx != 0 || yy != 0 || zz != 0) && matches && BlockUtils.isBlockExposed(level, vs.pos.offset(xx, yy, zz))) {
                                                queue2.offer(new VirtualSwapper(vs.pos.offset(xx, yy, zz), vs.source, vs.target, vs.consumeTarget, vs.lifespan - 1, vs.player, vs.fx, vs.fancy, vs.color, vs.pickup, vs.silk, vs.fortune, vs.allowSwap, vs.visCost));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public static void addSwapper(Level level, BlockPos pos, Object source, ItemStack target, boolean consumeTarget, int life, Player player, boolean fx, boolean fancy, int color, boolean pickup, boolean silk, int fortune, Predicate<SwapperPredicate> allowSwap, float visCost) {
        ResourceKey<Level> dim = level.dimension();
        LinkedBlockingQueue<VirtualSwapper> queue = swapList.get(dim);
        if (queue == null) {
            swapList.put(dim, new LinkedBlockingQueue<>());
            queue = swapList.get(dim);
        }

        queue.offer(new VirtualSwapper(pos, source, target, consumeTarget, life, player, fx, fancy, color, pickup, silk, fortune, allowSwap, visCost));
        swapList.put(dim, queue);
    }

    public static void addRunnableServer(Level level, Runnable runnable, int delay) {
        if (!level.isClientSide()) {
            LinkedBlockingQueue<RunnableEntry> rlist = serverRunList.get(level.dimension());
            if (rlist == null) {
                serverRunList.put(level.dimension(), rlist = new LinkedBlockingQueue<>());
            }

            rlist.add(new RunnableEntry(runnable, delay));
        }
    }

    public static class RunnableEntry {
        Runnable runnable;
        int delay;

        public RunnableEntry(Runnable runnable, int delay) {
            this.runnable = runnable;
            this.delay = delay;
        }
    }

    public static class VirtualSwapper {
        int color;
        boolean fancy;
        Predicate<SwapperPredicate> allowSwap;
        int lifespan;
        BlockPos pos;
        Object source;
        ItemStack target;
        Player player;
        boolean fx;
        boolean silk;
        boolean pickup;
        boolean consumeTarget;
        int fortune;
        float visCost;

        VirtualSwapper(BlockPos pos, Object source, ItemStack t, boolean consumeTarget, int life, Player p, boolean fx, boolean fancy, int color, boolean pickup, boolean silk, int fortune, Predicate<SwapperPredicate> allowSwap, float cost) {
            this.pos = pos;
            this.source = source;
            this.target = t;
            this.lifespan = life;
            this.player = p;
            this.consumeTarget = consumeTarget;
            this.fx = fx;
            this.fancy = fancy;
            this.allowSwap = allowSwap;
            this.silk = silk;
            this.fortune = fortune;
            this.pickup = pickup;
            this.color = color;
            this.visCost = cost;
        }
    }

    public static class SwapperPredicate {
        public Level level;
        public Player player;
        public BlockPos pos;

        public SwapperPredicate(Level level, Player player, BlockPos pos) {
            this.level = level;
            this.player = player;
            this.pos = pos;
        }
    }

}
