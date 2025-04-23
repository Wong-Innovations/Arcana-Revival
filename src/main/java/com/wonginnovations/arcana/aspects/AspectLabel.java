package com.wonginnovations.arcana.aspects;

import net.minecraft.core.Direction;

import java.util.Random;

public class AspectLabel {
    public Direction direction;
    public Aspect seal;
    public float renderRotation;

    public AspectLabel(Direction direction) {
        this.seal = Aspects.EMPTY;
        this.direction = direction;

        Random rand = new Random();
        this.renderRotation = (rand.nextInt(300) - 150) / 10f;
    }
}
