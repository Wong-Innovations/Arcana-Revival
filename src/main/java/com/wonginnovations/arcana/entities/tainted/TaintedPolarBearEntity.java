package com.wonginnovations.arcana.entities.tainted;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.level.Level;

public class TaintedPolarBearEntity extends PolarBear {

	public TaintedPolarBearEntity(EntityType<? extends PolarBear> entityType, Level level) {
		super(entityType, level);
	}

}
