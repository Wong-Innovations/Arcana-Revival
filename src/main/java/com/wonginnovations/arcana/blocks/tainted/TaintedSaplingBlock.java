package com.wonginnovations.arcana.blocks.tainted;

import com.wonginnovations.arcana.systems.taint.Taint;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.TreeFeature;

public class TaintedSaplingBlock extends SaplingBlock {
	
	public TaintedSaplingBlock(Block parent) {
		super(Taint.taintedTreeOf((SaplingBlock) parent), Block.Properties.copy(parent));
		Taint.addTaintMapping(parent, this);
	}
	
	public TaintedSaplingBlock(Block parent, AbstractTreeGrower tree, Properties properties) {
		super(tree, properties);
		Taint.addTaintMapping(parent, this);
	}
}
