package com.wonginnovations.arcana.entities;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SpellTrapEntity extends Entity {

	SpellCloudEntity.CloudVariableGrid variableGrid;

	public SpellTrapEntity(SpellCloudEntity.CloudVariableGrid variableGrid) {
		super(ArcanaEntities.SPELL_TRAP.get(), variableGrid.level);
		this.variableGrid = variableGrid;
	}

	public SpellTrapEntity(EntityType<?> spellTrapEntityEntityType, Level level) {
		super(spellTrapEntityEntityType, level);
	}

	@Override
	protected void defineSynchedData() {

	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag pCompound) {

	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag pCompound) {

	}

}
