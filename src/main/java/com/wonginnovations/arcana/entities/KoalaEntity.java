package com.wonginnovations.arcana.entities;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class KoalaEntity extends Animal {
	
	private EatBlockGoal eatGrassGoal;
	private int koalaTimer;
	
	public KoalaEntity(EntityType<? extends KoalaEntity> type, Level levelIn) {
		super(type, levelIn);
	}
	
	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageable) {
		KoalaEntity entity = new KoalaEntity(ArcanaEntities.KOALA_ENTITY.get(), this.level());
		entity.finalizeSpawn(level, level.getCurrentDifficultyAt(new BlockPos(entity.blockPosition())),
				MobSpawnType.BREEDING, null, null);
		return entity;
	}
	
	@Override
	protected void registerGoals() {
		super.registerGoals();
		eatGrassGoal = new EatBlockGoal(this);
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(1, new PanicGoal(this, 1.25));
		goalSelector.addGoal(2, new BreedGoal(this, 1));
		goalSelector.addGoal(3,
				new TemptGoal(this, 1.1, Ingredient.of(Items.WHEAT), false));
		goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
		goalSelector.addGoal(5, eatGrassGoal);
		goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6));
		goalSelector.addGoal(8, new RandomLookAroundGoal(this));
	}
	
	@Override
	protected void customServerAiStep() {
		koalaTimer = eatGrassGoal.getEatAnimationTick();
		super.customServerAiStep();
	}
	
	@Override
	public void aiStep() {
		if (this.level().isClientSide)
			koalaTimer = Math.max(0, koalaTimer - 1);
		super.aiStep();
	}

	public static AttributeSupplier.Builder createLivingAttributes() {
		// TODO: stats
		return LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 16).add(Attributes.MOVEMENT_SPEED, .23);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void handleEntityEvent(byte id) {
		if (id == 10)
			koalaTimer = 40;
		else
			super.handleEntityEvent(id);
	}
	
	/*@OnlyIn(Dist.CLIENT)
	public float getHeadRotationPointY(float p_70894_1_) {
		if (this.koalaTimer <= 0) {
			return 0.0F;
		} else if (this.koalaTimer >= 4 && this.koalaTimer <= 36) {
			return 1.0F;
		} else {
			return this.koalaTimer < 4 ? ((float)this.koalaTimer - p_70894_1_) / 4.0F
					: -((float)(this.koalaTimer - 40) - p_70894_1_) / 4.0F;
		}
	}*/
	
	@OnlyIn(Dist.CLIENT)
	public float getHeadRotationAngleX(float last) {
		if (koalaTimer > 4 && koalaTimer <= 36) {
			float f = ((float)(koalaTimer - 4) - last) / 32f;
			return ((float)Math.PI / 5) + .21991149f * Mth.sin(f * 28.7f);
		} else
			return koalaTimer > 0 ? ((float)Math.PI / 5) : this.getXRot() * ((float)Math.PI / 180);
	}
}
