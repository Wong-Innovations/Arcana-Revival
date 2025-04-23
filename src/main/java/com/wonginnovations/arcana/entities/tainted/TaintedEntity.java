package com.wonginnovations.arcana.entities.tainted;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class TaintedEntity extends Monster {
	public EntityType<?> parentEntity;

	public TaintedEntity(EntityType<? extends Monster> type, Level levelIn, EntityType<?> entity) {
		super(type, levelIn);
		parentEntity = entity;
	}

	public EntityType<?> getParentEntity() {
		return parentEntity;
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
		this.applyEntityAI();
	}

	protected void applyEntityAI() {
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
	}

	// TODO: Adjust values for every entity
	public static @NotNull AttributeSupplier.Builder createLivingAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.FOLLOW_RANGE, 35.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.25D)
				.add(Attributes.ATTACK_DAMAGE, 3.0D);
	}

	@Override
	public @NotNull MobType getMobType() {
		return MobType.UNDEFINED;
	}

	@OnlyIn(Dist.CLIENT)
	public float getYHeadRot() {
		return 0.0F;
	}

	@OnlyIn(Dist.CLIENT)
	public float getViewYRot(float partialTicks) {
		return 0.0F;
	}

	@OnlyIn(Dist.CLIENT)
	public float getViewXRot(float partialTicks) {
		return this.getXRot() * ((float) Math.PI / 180F);
	}
}
