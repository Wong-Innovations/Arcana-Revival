package com.wonginnovations.arcana.entities.tainted;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.Level;

public class TaintedRabbitEntity extends Rabbit {

	public TaintedRabbitEntity(EntityType<? extends Rabbit> entityType, Level level) {
		super(entityType, level);
	}

}
