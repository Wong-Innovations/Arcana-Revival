package com.wonginnovations.arcana.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public final class LocalAxis{
	
	private LocalAxis() {}
	
	// Adapted from LookingPosArgument::toAbsolutePos (Yarn names)
	public static Vec3 toAbsolutePos(Vec3 localPos, Vec2 rotation, Vec3 worldPos) {
		float yCos = Mth.cos((rotation.y + 90) * .017453292F);
		float ySin = Mth.sin((rotation.y + 90) * .017453292F);
		float xCos = Mth.cos(-rotation.x * .017453292F);
		float xSin = Mth.sin(-rotation.x * .017453292F);
		float a = Mth.cos((-rotation.x + 90) * .017453292F);
		float b = Mth.sin((-rotation.x + 90) * .017453292F);
		Vec3 vec3d2 = new Vec3(yCos * xCos, xSin, ySin * xCos);
		Vec3 vec3d3 = new Vec3(yCos * a, b, ySin * a);
		Vec3 vec3d4 = vec3d2.cross(vec3d3).scale(-1);
		double xOff = vec3d2.x * localPos.z + vec3d3.x * localPos.y + vec3d4.x * localPos.x;
		double yOff = vec3d2.y * localPos.z + vec3d3.y * localPos.y + vec3d4.y * localPos.x;
		double zOff = vec3d2.z * localPos.z + vec3d3.z * localPos.y + vec3d4.z * localPos.x;
		return new Vec3(worldPos.x + xOff, worldPos.y + yOff, worldPos.z + zOff);
	}
	
	// Converts from direction to pitch/yaw immediately.
	// TODO: better impl
	public static Vec3 toAbsolutePos(Vec3 localPos, Vec3 facing, Vec3 worldPos) {
		float pitch = (float)Math.asin(-facing.y);
		float yaw = (float)Math.atan2(facing.x, facing.z);
		return toAbsolutePos(localPos, new Vec2(pitch, yaw), worldPos);
	}
}