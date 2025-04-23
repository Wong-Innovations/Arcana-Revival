package com.wonginnovations.arcana.entities.tainted;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.level.Level;

public class TaintedDonkeyEntity extends Donkey {

	public TaintedDonkeyEntity(EntityType<? extends Donkey> entityType, Level level) {
		super(entityType, level);
	}

}
