package com.wonginnovations.arcana.common.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BlockUtils {

    public static ItemStack getSilkTouchDrop(BlockState bs, Level level, BlockPos pos, Player player) {
        return bs.getCloneItemStack(new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, true), level, pos, player);
    }

    public static List<ItemStack> getDrops(BlockState bs, ServerLevel level, BlockPos pos, int fortune) {
        ItemStack toolWithFortune = new ItemStack(Items.DIAMOND_PICKAXE);
        toolWithFortune.enchant(Enchantments.BLOCK_FORTUNE, fortune);

        return getDrops(bs, level, pos, toolWithFortune);
    }

    public static List<ItemStack> getDrops(BlockState bs, ServerLevel level, BlockPos pos, ItemStack tool) {
        LootParams.Builder loot = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, tool);
        return bs.getDrops(loot);
    }

    public static boolean isBlockExposed(Level world, BlockPos pos) {
        for (Direction face : Direction.values()) {
            if (!world.getBlockState(pos.offset(face.getNormal())).isSolid()) {
                return true;
            }
        }

        return false;
    }
}
