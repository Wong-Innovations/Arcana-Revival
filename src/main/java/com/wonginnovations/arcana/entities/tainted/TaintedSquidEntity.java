package com.wonginnovations.arcana.entities.tainted;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TaintedSquidEntity extends Squid {

	public TaintedSquidEntity(EntityType<Squid> type, Level levelIn) {
		super(type, levelIn);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Squid.createAttributes()
				.add(Attributes.ATTACK_DAMAGE, 0.5D)
				.add(Attributes.ATTACK_KNOCKBACK, 0.0D);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0,new MeleeAttackGoal(this,1,false));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::shouldAttack));
		//this.goalSelector.addGoal(0, new PanicGoal(this, 1.25D));
		//this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 1.6D, 1.4D, EntityPredicates.NOT_SPECTATING::test));
		this.goalSelector.addGoal(4, new MoveRandomGoal(this));
	}

	private boolean shouldAttack(LivingEntity entity) {
		return true;
	}

	static class MoveRandomGoal extends Goal {
		private final TaintedSquidEntity squid;

		public MoveRandomGoal(TaintedSquidEntity pSquid) {
			this.squid = pSquid;
		}

		/**
		 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
		 * method as well.
		 */
		@Override
		public boolean canUse() {
			return true;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		@Override
		public void tick() {
			int i = this.squid.getNoActionTime();
			if (i > 100) {
				this.squid.setMovementVector(0.0F, 0.0F, 0.0F);
			} else if (this.squid.getRandom().nextInt(reducedTickDelay(50)) == 0 || !this.squid.wasTouchingWater || !this.squid.hasMovementVector()) {
				float f = this.squid.getRandom().nextFloat() * ((float)Math.PI * 2F);
				float f1 = Mth.cos(f) * 0.2F;
				float f2 = -0.1F + this.squid.getRandom().nextFloat() * 0.2F;
				float f3 = Mth.sin(f) * 0.2F;
				this.squid.setMovementVector(f1, f2, f3);
			}
		}
	}
}