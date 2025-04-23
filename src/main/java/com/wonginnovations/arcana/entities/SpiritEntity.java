package com.wonginnovations.arcana.entities;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class SpiritEntity extends FlyingMob {
    public SpiritEntity(EntityType<? extends SpiritEntity> type, Level level) {
        super(type, level);
        xpReward = 5;
        moveControl = new SpiritEntity.MoveHelperController(this);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(5, new SpiritEntity.RandomFlyGoal(this));
        goalSelector.addGoal(7, new SpiritEntity.LookAroundGoal(this));
    }

    public static @NotNull AttributeSupplier.Builder createLivingAttributes() {
        // TODO: stats
        return LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 16).add(Attributes.MOVEMENT_SPEED, .23);
    }

    static class LookAroundGoal extends Goal {

        private final SpiritEntity parentEntity;

        public LookAroundGoal(SpiritEntity entity) {
            parentEntity = entity;
            setFlags(EnumSet.of(Flag.LOOK));
        }

        public boolean canUse() {
            return true;
        }

        public void tick() {
            if (parentEntity.getTarget() == null) {
                Vec3 motion = parentEntity.getDeltaMovement();
                parentEntity.setYRot(-((float) Mth.atan2(motion.x, motion.z)) * 57.295776F);
                parentEntity.setYBodyRot(parentEntity.getYRot());
            } else {
                LivingEntity target = parentEntity.getTarget();
                if (target.distanceToSqr(parentEntity) < 4096.0D) {
                    double deltaX = target.getX() - parentEntity.getX();
                    double deltaZ = target.getZ() - parentEntity.getZ();
                    parentEntity.setYRot(-((float)Mth.atan2(deltaX, deltaZ)) * 57.295776F);
                    parentEntity.setYBodyRot(parentEntity.getYRot());
                }
            }
        }
    }

    static class RandomFlyGoal extends Goal {
        private final SpiritEntity parentEntity;

        public RandomFlyGoal(SpiritEntity entity) {
            parentEntity = entity;
            setFlags(EnumSet.of(Flag.MOVE));
        }

        public boolean canUse() {
            MoveControl movementController = parentEntity.getMoveControl();
            if (!movementController.hasWanted()) {
                return true;
            } else {
                double x = movementController.getWantedX() - parentEntity.getX();
                double y = movementController.getWantedY() - parentEntity.getY();
                double z = movementController.getWantedZ() - parentEntity.getZ();
                double distSquared = x * x + y * y + z * z;
                return distSquared < 1 || distSquared > 3600;
            }
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void start() {
            RandomSource rand = parentEntity.random;
            double x = parentEntity.getX() + (rand.nextFloat() * 2f - 1) * 16;
            double y = parentEntity.getY() + (rand.nextFloat() * 2f - 1) * 16;
            double z = parentEntity.getZ() + (rand.nextFloat() * 2f - 1) * 16;
            parentEntity.moveControl.setWantedPosition(x, y, z, 1);
        }
    }

    static class MoveHelperController extends MoveControl {
        private final SpiritEntity parentEntity;
        private int courseChangeCooldown;

        public MoveHelperController(SpiritEntity entity) {
            super(entity);
            this.parentEntity = entity;
        }

        public void tick() {
            if (this.operation == Operation.MOVE_TO)
                if (courseChangeCooldown-- <= 0) {
                    courseChangeCooldown += parentEntity.random.nextInt(5) + 2;
                    Vec3 posDiff = new Vec3(wantedX - parentEntity.getX(), wantedY - parentEntity.getY(), wantedZ - parentEntity.getZ());
                    double posLength = posDiff.length();
                    posDiff = posDiff.normalize();
                    if (canMove(posDiff, Mth.ceil(posLength)))
                        parentEntity.moveTo(parentEntity.getDeltaMovement().add(posDiff.scale(.1)));
                    else
                        this.operation = Operation.WAIT;
                }
        }

        private boolean canMove(Vec3 pos, int size) {
            AABB boundingBox = this.parentEntity.getBoundingBox();

            for (int i = 1; i < size; ++i) {
                boundingBox = boundingBox.move(pos);
                // Check collision
                if (!parentEntity.level().noCollision(this.parentEntity, boundingBox))
                    return false;
            }
            return true;
        }
    }
}
