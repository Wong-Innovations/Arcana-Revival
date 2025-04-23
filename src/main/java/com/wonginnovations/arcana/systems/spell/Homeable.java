package com.wonginnovations.arcana.systems.spell;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface Homeable {
    static <T extends Entity & Homeable> void startHoming(T toHome) {
        int s = 15; // size of box
        if (!toHome.getHomeables().isEmpty()) {
            AABB box = new AABB(
                    toHome.getX()-s,toHome.getY()-s,toHome.getZ()-s,
                    toHome.getX()+s,toHome.getY()+s,toHome.getZ()+s
            );
            for (Class<? extends Entity> homeTarget : toHome.getHomeables()) {
                List<? extends Entity> entitiesWithinBox = toHome.level().getEntitiesOfClass((Class<? extends Entity>) homeTarget, box, Entity::isAlive);
                if (!entitiesWithinBox.isEmpty()) {
                    for (Entity entity : entitiesWithinBox) {
                        toHome.move(MoverType.PLAYER,getVecDistance(entity, toHome,10f));
                    }
                }
            }
        }
    }

    // JAVA 11 pls
    /*private */static <E extends Entity> Vec3 getVecDistance(E homing, E entity, float pDM) {
        return new Vec3(
                (homing.getX()-entity.getX())/pDM,
                0,
                (homing.getZ()-entity.getZ())/pDM
        );
    }

    List<Class<? extends Entity>> getHomeables();
}
