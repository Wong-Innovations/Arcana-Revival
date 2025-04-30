package com.wonginnovations.arcana.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

public class PositionalSound extends AbstractTickableSoundInstance {

    private final BlockPos source;
    private final Entity listener;
    private final Runnable onStop;
    private final float maxVolume;
    private final float maxDistance;

    public PositionalSound(BlockPos pos, Entity listener, SoundEvent se, SoundSource ss, float maxVolume, float pitch, float maxDistance, Runnable onStop) {
        super(se, ss, SoundInstance.createUnseededRandom());
        this.source = pos;
        this.listener = listener;
        this.onStop = onStop;
        this.pitch = pitch;
        this.maxVolume = maxVolume;
        this.maxDistance = maxDistance;
    }

    @Override
    public void tick() {
        double distance = listener.position().distanceTo(source.getCenter());
        this.volume = Math.max(0f, maxVolume - (float)(distance * maxVolume / maxDistance));
        if (distance > maxDistance) {
            this.stop();
            this.onStop.run();
        }
    }
}
