package com.wonginnovations.arcana.client.fx;

import com.wonginnovations.arcana.client.fx.particles.FXGeneric;
import com.wonginnovations.arcana.common.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;

import java.awt.*;

public class FXDispatcher {

    public static FXDispatcher INSTANCE = new FXDispatcher();

    public ClientLevel getLevel() {
        return (Minecraft.getInstance().player != null)? (ClientLevel) Minecraft.getInstance().player.level() : null;
    }

    public void drawBamf(double x, double y, double z, int color, boolean sound, boolean flair, Direction side) {
        Color c = new Color(color);
        float r = (float)c.getRed() / 255.0F;
        float g = (float)c.getGreen() / 255.0F;
        float b = (float)c.getBlue() / 255.0F;
        this.drawBamf(x, y, z, r, g, b, sound, flair, side);
    }

    public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, Direction side) {
        if (sound) {
            this.getLevel().playLocalSound(x, y, z, ModSounds.POOF.get(), SoundSource.BLOCKS, 0.4F, 1.0F + (float)this.getLevel().random.nextGaussian() * 0.05F, false);
        }

        int a;
        double vx;
        double vy;
        double vz;
        for(a = 0; a < 6 + this.getLevel().random.nextInt(3) + 2; ++a) {
            vx = (0.05F + this.getLevel().random.nextFloat() * 0.05F) * (float)(this.getLevel().random.nextBoolean() ? -1 : 1);
            vy = (0.05F + this.getLevel().random.nextFloat() * 0.05F) * (float)(this.getLevel().random.nextBoolean() ? -1 : 1);
            vz = (0.05F + this.getLevel().random.nextFloat() * 0.05F) * (float)(this.getLevel().random.nextBoolean() ? -1 : 1);
            if (side != null) {
                vx += (float)side.getStepX() * 0.1F;
                vy += (float)side.getStepY() * 0.1F;
                vz += (float)side.getStepZ() * 0.1F;
            }

            FXGeneric fb2 = new FXGeneric(this.getLevel(), x + vx * 2.0, y + vy * 2.0, z + vz * 2.0, vx / 2.0, vy / 2.0, vz / 2.0);
            fb2.setLifetime(20 + this.getLevel().random.nextInt(15));
            fb2.setColor(Mth.clamp(r * (1.0F + (float)this.getLevel().random.nextGaussian() * 0.1F), 0.0F, 1.0F), Mth.clamp(g * (1.0F + (float)this.getLevel().random.nextGaussian() * 0.1F), 0.0F, 1.0F), Mth.clamp(b * (1.0F + (float)this.getLevel().random.nextGaussian() * 0.1F), 0.0F, 1.0F));
            fb2.setAlphaF(new float[]{1.0F, 0.1F});
            fb2.setGridSize(16);
            fb2.setParticles(123, 5, 1);
            fb2.setScale(new float[]{3.0F, 4.0F + this.getLevel().random.nextFloat() * 3.0F});
            fb2.setLayer(1);
            fb2.setFriction(0.7F);
            fb2.setRotationSpeed(this.getLevel().random.nextFloat(), this.getLevel().random.nextBoolean() ? -1.0F : 1.0F);
            ParticleEngine.addEffect(this.getLevel(), fb2);
        }

        if (flair) {
            for(a = 0; a < 2 + this.getLevel().random.nextInt(3); ++a) {
                vx = (0.025F + this.getLevel().random.nextFloat() * 0.025F) * (float)(this.getLevel().random.nextBoolean() ? -1 : 1);
                vy = (0.025F + this.getLevel().random.nextFloat() * 0.025F) * (float)(this.getLevel().random.nextBoolean() ? -1 : 1);
                vz = (0.025F + this.getLevel().random.nextFloat() * 0.025F) * (float)(this.getLevel().random.nextBoolean() ? -1 : 1);
                this.drawWispyMotes(x + vx * 2.0, y + vy * 2.0, z + vz * 2.0, vx, vy, vz, 15 + this.getLevel().random.nextInt(10), -0.01F);
            }

            FXGeneric fb = new FXGeneric(this.getLevel(), x, y, z, 0.0, 0.0, 0.0);
            fb.setLifetime(10 + this.getLevel().random.nextInt(5));
            fb.setColor(1.0F, 0.9F, 1.0F);
            fb.setAlphaF(new float[]{1.0F, 0.0F});
            fb.setGridSize(16);
            fb.setParticles(77, 1, 1);
            fb.setScale(new float[]{10.0F + this.getLevel().random.nextFloat() * 2.0F, 0.0F});
            fb.setLayer(0);
            fb.setRotationSpeed(this.getLevel().random.nextFloat(), (float)this.getLevel().random.nextGaussian());
            ParticleEngine.addEffect(this.getLevel(), fb);
        }

        for(a = 0; a < (flair ? 2 : 0) + this.getLevel().random.nextInt(3); ++a) {
            this.drawCurlyWisp(x, y, z, 0.0, 0.0, 0.0, 1.0F, (0.9F + this.getLevel().random.nextFloat() * 0.1F + r) / 2.0F, (0.1F + g) / 2.0F, (0.5F + this.getLevel().random.nextFloat() * 0.1F + b) / 2.0F, 0.75F, side, a, 0, 0);
        }

    }

}
