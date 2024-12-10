package com.wonginnovations.arcana.common.events;

import com.google.common.base.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@Mod.EventBusSubscriber
public class ServerEvents {

    static HashMap<ResourceKey<Level>, Integer> serverTicks = new HashMap<>();
    public static HashMap<ResourceKey<Level>, LinkedBlockingQueue<VirtualSwapper>> swapList = new HashMap<>();
    private static final HashMap<ResourceKey<Level>, LinkedBlockingQueue<RunnableEntry>> serverRunList = new HashMap<>();
    private static final LinkedBlockingQueue<RunnableEntry> clientRunList = new LinkedBlockingQueue<>();

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.side == LogicalSide.CLIENT) {
            if (event.phase == TickEvent.Phase.END && !clientRunList.isEmpty()) {
                LinkedBlockingQueue<RunnableEntry> temp = new LinkedBlockingQueue<>();

                while(!clientRunList.isEmpty()) {
                    RunnableEntry current = clientRunList.poll();
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
                    clientRunList.offer(temp.poll());
                }
            }

        }
    }

    @SubscribeEvent
    public static void levelTick(TickEvent.LevelTickEvent event) {
        if (event.side == LogicalSide.SERVER) {
            ResourceKey<Level> dim = event.level.dimension();

            if (!serverTicks.containsKey(dim)) {
                serverTicks.put(dim, 0);
            }

            LinkedBlockingQueue<RunnableEntry> runList = serverRunList.get(dim);
            if (runList == null) {
                serverRunList.put(dim, new LinkedBlockingQueue<>());
            } else if (!runList.isEmpty()) {
                LinkedBlockingQueue<RunnableEntry> temp = new LinkedBlockingQueue<>();

                while(!runList.isEmpty()) {
                    RunnableEntry current = runList.poll();
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
                    runList.offer(temp.poll());
                }
            }

            int ticks = serverTicks.get(dim);
            tickBlockSwap(event.level);
//          tickBlockBreak(event.world);

            serverTicks.put(dim, ticks + 1);
        }
    }

    private static void tickBlockSwap(Level level) {
        ResourceKey<Level> dim = level.dimension();
        LinkedBlockingQueue<VirtualSwapper> queue = swapList.get(dim);
        if (queue != null) {
            while(true) {
                if (queue.isEmpty()) {
                    swapList.put(dim, new LinkedBlockingQueue<>());
                    break;
                }

                VirtualSwapper vs = queue.poll();
                if (vs != null) {
                    BlockState bs = level.getBlockState(vs.pos);
                    boolean allow = bs.getBlockHardness(level, vs.pos) >= 0.0F;
                    if (vs.source != null && vs.source instanceof BlockState && vs.source != bs || vs.source != null && vs.source instanceof Material && (Material)vs.source != bs.getMaterial()) {
                        allow = false;
                    }

                    if (bs.getBlock().canHarvestBlock(bs, level, vs.pos, vs.player) && allow && (vs.target == null || vs.target.isEmpty() || !ItemStack.isSameItem(vs.target, new ItemStack(bs.getBlock().asItem()))) && !ForgeEventFactory.onBlockPlace(vs.player, new BlockSnapshot(level, vs.pos, bs), Direction.UP).isCanceled()) {
                        if (vs.target != null && !vs.target.isEmpty()) {
                            Block targetBlock = Block.byItem(vs.target.getItem());
                            if (targetBlock != Blocks.AIR) {
                                level.setBlock(vs.pos, targetBlock.defaultBlockState(), 3);
                            } else {
                                level.setBlock(vs.pos, Blocks.AIR.defaultBlockState(), 3);
//                                    EntitySpecialItem entityItem = new EntitySpecialItem(world, (double)vs.pos.getX() + 0.5, (double)vs.pos.getY() + 0.1, (double)vs.pos.getZ() + 0.5, vs.target.copy());
//                                    entityItem.motionY = 0.0;
//                                    entityItem.motionX = 0.0;
//                                    entityItem.motionZ = 0.0;
//                                    world.spawnEntity(entityItem);
                            }
                        } else {
                            level.setBlock(vs.pos, Blocks.AIR.defaultBlockState(), 3);
                        }

                        PacketHandler.INSTANCE.sendToAllAround(new PacketFXBlockBamf(vs.pos, vs.color, true, vs.fancy, (EnumFacing)null), new NetworkRegistry.TargetPoint(world.provider.getDimension(), (double)vs.pos.getX(), (double)vs.pos.getY(), (double)vs.pos.getZ(), 32.0));
                    }
                }
            }
        }
    }

    public static void addSwapper(Level level, BlockPos pos, Object source, ItemStack target, Player player) {
        ResourceKey<Level> dim = level.dimension();
        LinkedBlockingQueue<VirtualSwapper> queue = swapList.get(dim);
        if (queue == null) {
            swapList.put(dim, new LinkedBlockingQueue<>());
            queue = swapList.get(dim);
        }

        queue.offer(new VirtualSwapper(pos, source, target, player));
        swapList.put(dim, queue);
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
        BlockPos pos;
        Object source;
        ItemStack target;
        Player player = null;

        VirtualSwapper(BlockPos pos, Object source, ItemStack target, Player p) {
            this.pos = pos;
            this.source = source;
            this.target = target;
            this.player = p;
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
