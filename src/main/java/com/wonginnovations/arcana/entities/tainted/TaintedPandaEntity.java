package com.wonginnovations.arcana.entities.tainted;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.level.Level;

public class TaintedPandaEntity extends Panda {
	public TaintedPandaEntity(EntityType<? extends Panda> type, Level level) {
		super(type, level);
	}
}
