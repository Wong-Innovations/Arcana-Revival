package com.wonginnovations.arcana.client.fx;

import com.wonginnovations.arcana.client.fx.particles.FXGeneric;
import com.wonginnovations.arcana.common.ModSounds;
import com.wonginnovations.arcana.common.utils.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class FXDispatcher {

    public static FXDispatcher INSTANCE = new FXDispatcher();

    public ClientLevel getLevel() {
        return (Minecraft.getInstance().player != null)? (ClientLevel) Minecraft.getInstance().player.level() : null;
    }

    public void drawSimpleSparkle(RandomSource rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) {
        boolean sp = (double)rand.nextFloat() < 0.2;
        FXGeneric fb = new FXGeneric(this.getLevel(), x, y, z, x2, y2, z2);
        int age = baseAge * 4 + this.getLevel().random.nextInt(baseAge);
        fb.setMaxAge(age);
        fb.setRBGColorF(r, g, b);
        float[] alphas = new float[6 + rand.nextInt(age / 3)];

        for(int a = 1; a < alphas.length - 1; ++a) {
            alphas[a] = rand.nextFloat();
        }

        fb.setAlphaF(alphas);
        fb.setParticles(sp ? 320 : 512, 16, 1);
        fb.setLoop(true);
        fb.setGravity(grav);
        fb.setScale(scale, scale * 2.0F);
        fb.setSlowDown(decay);
        fb.setRandomMovementScale(5.0E-4F, 0.001F, 5.0E-4F);
        fb.setWind(5.0E-4);
        ParticleEngine.addEffectWithDelay(this.getLevel(), fb, delay);
    }

    public void drawBlockSparkles(BlockPos p, Vec3 start) {
        AABB bb = this.getLevel().getBlockState(p).getShape(this.getLevel(), p).bounds();
        bb.inflate(0.1, 0.1, 0.1);
        int num = (int)(MathUtils.getAverageEdgeLength(bb) * 20.0);

        for (Direction face : Direction.values()) {
            BlockPos offsetPos = p.offset(face.getStepX(), face.getStepY(), face.getStepZ());
            BlockState state = this.getLevel().getBlockState(offsetPos);
            if (!state.isSolidRender(this.getLevel(), p) && !state.isFaceSturdy(this.getLevel(), offsetPos, face.getOpposite())) {
                boolean rx = face.getStepX() == 0;
                boolean ry = face.getStepY() == 0;
                boolean rz = face.getStepZ() == 0;
                double mx = 0.5 + (double) face.getStepX() * 0.51;
                double my = 0.5 + (double) face.getStepY() * 0.51;
                double mz = 0.5 + (double) face.getStepZ() * 0.51;

                for (int a = 0; a < num * 2; ++a) {
                    double x = mx;
                    double y = my;
                    double z = mz;
                    if (rx) {
                        x += this.getLevel().random.nextGaussian() * 0.6;
                    }

                    if (ry) {
                        y += this.getLevel().random.nextGaussian() * 0.6;
                    }

                    if (rz) {
                        z += this.getLevel().random.nextGaussian() * 0.6;
                    }

                    x = Mth.clamp(x, bb.minX, bb.maxX);
                    y = Mth.clamp(y, bb.minY, bb.maxY);
                    z = Mth.clamp(z, bb.minZ, bb.maxZ);
                    float r = (float) MathUtils.getRandomInt(this.getLevel().random, 255, 255) / 255.0F;
                    float g = (float) MathUtils.getRandomInt(this.getLevel().random, 189, 255) / 255.0F;
                    float b = (float) MathUtils.getRandomInt(this.getLevel().random, 64, 255) / 255.0F;
                    Vec3 v1 = new Vec3((double) p.getX() + x, (double) p.getY() + y, (double) p.getZ() + z);
                    double delay = (double) this.getLevel().random.nextInt(5) + v1.distanceTo(start) * 16.0;
                    this.drawSimpleSparkle(this.getLevel().random, (double) p.getX() + x, (double) p.getY() + y, (double) p.getZ() + z, 0.0, 0.0025, 0.0, 0.4F + (float) this.getLevel().random.nextGaussian() * 0.1F, r, g, b, (int) delay, 1.0F, 0.01F, 16);
                }
            }
        }

    }

    public void drawWispyMotes(double x, double y, double z, double vx, double vy, double vz, int age, float grav) {
        this.drawWispyMotes(x, y, z, vx, vy, vz, age, 0.25F + this.getLevel().random.nextFloat() * 0.75F, 0.25F + this.getLevel().random.nextFloat() * 0.75F, 0.25F + this.getLevel().random.nextFloat() * 0.75F, grav);
    }

    public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) {
        FXGeneric fb = new FXGeneric(this.getLevel(), d, e, f, vx, vy, vz);
        fb.setMaxAge((int)((float)age + (float)(age / 2) * this.getLevel().random.nextFloat()));
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(0.0F, 0.6F, 0.6F, 0.0F);
        fb.setGridSize(64);
        fb.setParticles(512, 16, 1);
        fb.setScale(1.0F, 0.5F);
        fb.setLoop(true);
        fb.setWind(0.001);
        fb.setGravity(grav);
        fb.setRandomMovementScale(0.0025F, 0.0F, 0.0025F);
        ParticleEngine.addEffect(this.getLevel().dimension(), fb);
    }

    public void drawBamf(BlockPos p, boolean sound, boolean flair, Direction side) {
        this.drawBamf((double)p.getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5, sound, flair, side);
    }

    public void drawBamf(BlockPos p, float r, float g, float b, boolean sound, boolean flair, Direction side) {
        this.drawBamf((double)p.getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5, r, g, b, sound, flair, side);
    }

    public void drawBamf(BlockPos p, int color, boolean sound, boolean flair, Direction side) {
        this.drawBamf((double)p.getX() + 0.5, (double)p.getY() + 0.5, (double)p.getZ() + 0.5, color, sound, flair, side);
    }

    public void drawBamf(double x, double y, double z, int color, boolean sound, boolean flair, Direction side) {
        Color c = new Color(color);
        float r = (float)c.getRed() / 255.0F;
        float g = (float)c.getGreen() / 255.0F;
        float b = (float)c.getBlue() / 255.0F;
        this.drawBamf(x, y, z, r, g, b, sound, flair, side);
    }

    public void drawBamf(double x, double y, double z, boolean sound, boolean flair, Direction side) {
        this.drawBamf(x, y, z, 0.5F, 0.1F, 0.6F, sound, flair, side);
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
            fb2.setMaxAge(20 + this.getLevel().random.nextInt(15));
            fb2.setRBGColorF(Mth.clamp(r * (1.0F + (float)this.getLevel().random.nextGaussian() * 0.1F), 0.0F, 1.0F), Mth.clamp(g * (1.0F + (float)this.getLevel().random.nextGaussian() * 0.1F), 0.0F, 1.0F), Mth.clamp(b * (1.0F + (float)this.getLevel().random.nextGaussian() * 0.1F), 0.0F, 1.0F));
            fb2.setAlphaF(1.0F, 0.1F);
            fb2.setGridSize(16);
            fb2.setParticles(123, 5, 1);
            fb2.setScale(3.0F, 4.0F + this.getLevel().random.nextFloat() * 3.0F);
            fb2.setSlowDown(0.7);
            fb2.setRotationSpeed(this.getLevel().random.nextFloat(), this.getLevel().random.nextBoolean() ? -1.0F : 1.0F);
            ParticleEngine.addEffect(this.getLevel().dimension(), fb2);
        }

        if (flair) {
            for(a = 0; a < 2 + this.getLevel().random.nextInt(3); ++a) {
                vx = (0.025F + this.getLevel().random.nextFloat() * 0.025F) * (float)(this.getLevel().random.nextBoolean() ? -1 : 1);
                vy = (0.025F + this.getLevel().random.nextFloat() * 0.025F) * (float)(this.getLevel().random.nextBoolean() ? -1 : 1);
                vz = (0.025F + this.getLevel().random.nextFloat() * 0.025F) * (float)(this.getLevel().random.nextBoolean() ? -1 : 1);
                this.drawWispyMotes(x + vx * 2.0, y + vy * 2.0, z + vz * 2.0, vx, vy, vz, 15 + this.getLevel().random.nextInt(10), -0.01F);
            }

            FXGeneric fb = new FXGeneric(this.getLevel(), x, y, z, 0.0, 0.0, 0.0);
            fb.setMaxAge(10 + this.getLevel().random.nextInt(5));
            fb.setRBGColorF(1.0F, 0.9F, 1.0F);
            fb.setAlphaF(1.0F, 0.0F);
            fb.setGridSize(16);
            fb.setParticles(77, 1, 1);
            fb.setScale(10.0F + this.getLevel().random.nextFloat() * 2.0F, 0.0F);
            fb.setRotationSpeed(this.getLevel().random.nextFloat(), (float)this.getLevel().random.nextGaussian());
            ParticleEngine.addEffect(this.getLevel().dimension(), fb);
        }

        for(a = 0; a < (flair ? 2 : 0) + this.getLevel().random.nextInt(3); ++a) {
            this.drawCurlyWisp(x, y, z, 0.0, 0.0, 0.0, 1.0F, (0.9F + this.getLevel().random.nextFloat() * 0.1F + r) / 2.0F, (0.1F + g) / 2.0F, (0.5F + this.getLevel().random.nextFloat() * 0.1F + b) / 2.0F, 0.75F, side, a, 0);
        }

    }

    public void drawCurlyWisp(double x, double y, double z, double vx, double vy, double vz, float scale, float r, float g, float b, float a, Direction side, int seed, int delay) {
        if (this.getLevel() != null) {
            vx += (0.0025F + this.getLevel().random.nextFloat() * 0.005F) * (float)(this.getLevel().random.nextBoolean() ? -1 : 1);
            vy += (0.0025F + this.getLevel().random.nextFloat() * 0.005F) * (float)(this.getLevel().random.nextBoolean() ? -1 : 1);
            vz += (0.0025F + this.getLevel().random.nextFloat() * 0.005F) * (float)(this.getLevel().random.nextBoolean() ? -1 : 1);
            if (side != null) {
                vx += (float)side.getStepX() * 0.025F;
                vy += (float)side.getStepY() * 0.025F;
                vz += (float)side.getStepZ() * 0.025F;
            }

            FXGeneric fb2 = new FXGeneric(this.getLevel(), x + vx * 5.0, y + vy * 5.0, z + vz * 5.0, vx, vy, vz);
            if (seed > 0 && this.getLevel().random.nextBoolean()) {
                fb2.setAngles(90.0F * (float)this.getLevel().random.nextGaussian(), 90.0F * (float)this.getLevel().random.nextGaussian());
            }

            fb2.setMaxAge(25 + this.getLevel().random.nextInt(20 + 20 * seed));
            fb2.setRBGColorF(r, g, b, 0.1F, 0.0F, 0.1F);
            fb2.setAlphaF(a, 0.0F);
            fb2.setGridSize(16);
            fb2.setParticles(60 + this.getLevel().random.nextInt(4), 1, 1);
            fb2.setScale(5.0F * scale, (10.0F + this.getLevel().random.nextFloat() * 4.0F) * scale);
            fb2.setRotationSpeed(this.getLevel().random.nextFloat(), this.getLevel().random.nextBoolean() ? -2.0F - this.getLevel().random.nextFloat() * 2.0F : 2.0F + this.getLevel().random.nextFloat() * 2.0F);
            ParticleEngine.addEffectWithDelay(this.getLevel(), fb2, delay);
        }
    }

}
