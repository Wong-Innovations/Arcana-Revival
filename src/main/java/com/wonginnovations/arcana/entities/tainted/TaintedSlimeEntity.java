package com.wonginnovations.arcana.entities.tainted;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TaintedSlimeEntity extends Slime {

	public TaintedSlimeEntity(EntityType<? extends Slime> type, Level levelIn) {
		super(type, levelIn);
	}

	@Override
	public void setSize(int size, boolean resetHealth) {
		super.setSize(size,resetHealth);
		this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)(size * size)+4);
	}

	protected @NotNull ParticleOptions getParticleType() {
		return ParticleTypes.MYCELIUM;
	}

}
