package com.wonginnovations.arcana.common.utils;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MathUtils {

    public static int getRandomInt(RandomSource random, int minimum, int maximum)
    {
        return minimum >= maximum ? minimum : random.nextInt(maximum - minimum + 1) + minimum;
    }

    public static Vec3 rotateAroundY(Vec3 vec, float angle) {
        float var2 = Mth.cos(angle);
        float var3 = Mth.sin(angle);
        double var4 = vec.x * (double)var2 + vec.z * (double)var3;
        double var6 = vec.y;
        double var8 = vec.z * (double)var2 - vec.x * (double)var3;
        return new Vec3(var4, var6, var8);
    }

    public static double getAverageEdgeLength(AABB bb)
    {
        double d0 = bb.maxX - bb.minX;
        double d1 = bb.maxY - bb.minY;
        double d2 = bb.maxZ - bb.minZ;
        return (d0 + d1 + d2) / 3.0D;
    }

}
