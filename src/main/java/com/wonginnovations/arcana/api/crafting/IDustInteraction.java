package com.wonginnovations.arcana.api.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface IDustInteraction {

    ArrayList<IDustInteraction> interactions = new ArrayList<>();

    Placement getValidFace(Level level, Player player, BlockPos blockPos, Direction direction);

    void execute(Level level, Player player, BlockPos blockPos, Placement placement, Direction direction);

    default List<BlockPos> sparkle(Level level, Player player, BlockPos blockPos, Placement placement) {
        return Arrays.asList(blockPos);
    }

    static void registerDustInteraction(IDustInteraction interaction) {
        interactions.add(interaction);
    }

    class Placement {
        public int xOffset;
        public int yOffset;
        public int zOffset;
        public Direction facing;

        public Placement(int xOffset, int yOffset, int zOffset, Direction facing) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.zOffset = zOffset;
            this.facing = facing;
        }
    }
}
