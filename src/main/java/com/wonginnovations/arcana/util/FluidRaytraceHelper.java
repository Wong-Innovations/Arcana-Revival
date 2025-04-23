package com.wonginnovations.arcana.util;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Predicate;

public class FluidRaytraceHelper {

	public static HitResult rayTrace(Entity projectile, boolean checkEntityCollision, boolean includeShooter, @Nullable Entity shooter, ClipContext.Block blockModeIn) {
		return rayTrace(projectile, checkEntityCollision, includeShooter, shooter, blockModeIn, true, (entity) -> !entity.isSpectator() && entity.canBeCollidedWith() && (includeShooter || !entity.is(shooter)) && !entity.noPhysics, projectile.getBoundingBox().expandTowards(projectile.getDeltaMovement()).inflate(1.0D));
	}

	public static HitResult rayTrace(Entity projectile, AABB boundingBox, Predicate<Entity> filter, ClipContext.Block blockModeIn, boolean checkEntityCollision) {
		return rayTrace(projectile, checkEntityCollision, false, null, blockModeIn, false, filter, boundingBox);
	}

	private static HitResult rayTrace(Entity projectile, boolean checkEntityCollision, boolean includeShooter, @Nullable Entity shooter, ClipContext.Block blockModeIn, boolean p_221268_5_, Predicate<Entity> filter, AABB boundingBox) {
		Vec3 vec3d = projectile.getDeltaMovement();
		Level level = projectile.level();
		Vec3 vec3d1 = projectile.position();
		if (p_221268_5_ && !level.noCollision(projectile, projectile.getBoundingBox()/*, ((Set<Entity>)(!includeShooter && shooter != null ? getEntityAndMount(shooter) : ImmutableSet.of()))::contains*/)) {
			return new BlockHitResult(vec3d1, Direction.getNearest(vec3d.x, vec3d.y, vec3d.z), new BlockPos(projectile.blockPosition()), false);
		} else {
			Vec3 vec3d2 = vec3d1.add(vec3d);
			BlockHitResult raytraceresult = level.clip(new ClipContext(vec3d1, vec3d2, blockModeIn, ClipContext.Fluid.SOURCE_ONLY, projectile));
			if (checkEntityCollision) {
				if (raytraceresult.getType() != HitResult.Type.MISS) {
					vec3d2 = raytraceresult.getLocation();
				}

				EntityHitResult raytraceresult1 = ProjectileUtil.getEntityHitResult(level, projectile, vec3d1, vec3d2, boundingBox, filter);
				if (raytraceresult1 != null) {
					return raytraceresult1;
				}
			}

			return raytraceresult;
		}
	}

	private static Set<Entity> getEntityAndMount(Entity rider) {
		Entity mount = rider.getVehicle();
		return mount != null ? ImmutableSet.of(rider, mount) : ImmutableSet.of(rider);
	}
}
