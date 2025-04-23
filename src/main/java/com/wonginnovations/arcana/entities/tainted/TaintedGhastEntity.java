package com.wonginnovations.arcana.entities.tainted;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;

public class TaintedGhastEntity extends Ghast {
	public TaintedGhastEntity(EntityType<? extends Ghast> type, Level levelIn) {
		super(type, levelIn);
	}
}
