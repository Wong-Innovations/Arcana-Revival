package com.wonginnovations.arcana.blocks;

import com.wonginnovations.arcana.systems.taint.Taint;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;

public class DeadRotatedPillarBlock extends RotatedPillarBlock {

    public DeadRotatedPillarBlock(Block block) {
        super(Properties.copy(block));
        Taint.addDeadMapping(block, this);
    }

}
