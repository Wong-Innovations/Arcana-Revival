package com.wonginnovations.arcana.common.crafting;

import com.wonginnovations.arcana.api.crafting.IDustInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class SimpleDustInteraction implements IDustInteraction {

    Block target;
    ItemStack result;
    String research;

    public SimpleDustInteraction(Block target, ItemStack result, String research) {
        this.target = target;
        this.result = result;
        this.research = research;
    }

    @Override
    public Placement getValidFace(Level level, Player player, BlockPos blockPos, Direction direction) {
        return (level.getBlockState(blockPos).getBlock() != this.target || this.research != null
                /*&& !ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(this.research)*/)?
                null : new Placement(0,0,0, null);
    }

    @Override
    public void execute(Level level, Player player, BlockPos blockPos, Placement placement, Direction direction) {
//        MinecraftForge.EVENT_BUS.post(new PlayerEvent.ItemCraftedEvent(player, this.result, new ));
//        ServerEvents.addRunnableServer(level, new Runnable() {
//            public void run() {
//                ServerEvents.addSwapper(level, pos, state, DustTriggerSimple.this.result, false, 0, player, true, true, -9999, false, false, 0, ServerEvents.DEFAULT_PREDICATE, 0.0F);
//            }
//        }, 50);
    }
}
