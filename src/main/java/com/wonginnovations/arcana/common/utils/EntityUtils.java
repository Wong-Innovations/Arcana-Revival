package com.wonginnovations.arcana.common.utils;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityUtils {

    public static Vec3 posToHand(Entity e, InteractionHand hand) {
        double px = e.getX();
        double py = e.getBoundingBox().minY + (double)(e.getBbHeight() / 2.0F) + 0.25; // i think this is bbheight not eyeHeight but we'll see
        double pz = e.getZ();
        float m = hand == InteractionHand.MAIN_HAND ? 0.0F : 180.0F;
        px += -Mth.cos((e.getYRot() + m) / 180.0F * 3.141593F) * 0.3F;
        pz += -Mth.sin((e.getYRot() + m) / 180.0F * 3.141593F) * 0.3F;
        Vec3 Vec3 = e.getLookAngle();
        px += Vec3.x * 0.3;
        py += Vec3.y * 0.3;
        pz += Vec3.z * 0.3;
        return new Vec3(px, py, pz);
    }
    
}
