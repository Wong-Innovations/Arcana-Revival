package com.wonginnovations.arcana.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public final class RayTraceUtils {

	public static BlockPos getTargetBlockPos(Player player, Level level, int maxDistance) {
		BlockHitResult rayTraceResult = getTargetBlockResult(player,level, maxDistance);
		return rayTraceResult.getBlockPos();
	}
	
	public static BlockState getTargetBlock(Player player, Level level, int maxdistance) {
		BlockPos blockpos = getTargetBlockPos(player, level, maxdistance);
		return level.getBlockState(blockpos);
	}

	public static BlockHitResult getTargetBlockResult(Player player,Level level, int maxdistance) {
		Vec3 vec3 = player.getEyePosition();
		Vec3 vec3a = player.getLookAngle();
		Vec3 vec3b = vec3.add(vec3a.x * maxdistance, vec3a.y *  maxdistance, vec3a.z *  maxdistance);

		BlockHitResult rayTraceResult = level.clip(new ClipContext(vec3, vec3b, ClipContext.Block.OUTLINE,  ClipContext.Fluid.ANY, player));


		if (rayTraceResult != null)
		{
			double xm=rayTraceResult.getLocation().x;
			double ym=rayTraceResult.getLocation().y;
			double zm=rayTraceResult.getLocation().z;


			//player.sendMessage(Component.literal(rayTraceResult.getFace().toString()));
			if (rayTraceResult.getDirection() == Direction.SOUTH) {
				zm--;
			}
			if (rayTraceResult.getDirection() == Direction.EAST) {
				xm--;
			}
			if (rayTraceResult.getDirection() == Direction.UP) {
				ym--;
			}

			return new BlockHitResult(rayTraceResult.getLocation(), rayTraceResult.getDirection(), BlockPos.containing(xm,ym,zm), false);
		}
		return null;
	}

	public static <T extends Entity> List<T> rayTraceEntities(Level level, Vec3 pos, Vec3 ray, Optional<Predicate<T>> entityFilter, Class<T> entityClazz)
	{
		Vec3 end = pos.add(new Vec3(1, 1, 1));
		AABB aabb = new AABB(pos.x, pos.y, pos.z, end.x, end.y, end.z).expandTowards(ray.x, ray.y, ray.z);
		Vec3 checkVec = pos.add(ray);
		List<T> ret = Lists.newArrayList();
		for (T t : level.getEntitiesOfClass(entityClazz, aabb, entityFilter.orElse(Predicates.alwaysTrue())))
		{
			AABB entityBB = t.getBoundingBox();
			if (entityBB == null)
			{
				continue;
			}

			if (entityBB.intersects(Math.min(pos.x, checkVec.x), Math.min(pos.y, checkVec.y), Math.min(pos.z, checkVec.z), Math.max(pos.x, checkVec.x), Math.max(pos.y, checkVec.y), Math.max(pos.z, checkVec.z)))
			{
				ret.add(t);
			}
		}

		return ret;
	}
}
