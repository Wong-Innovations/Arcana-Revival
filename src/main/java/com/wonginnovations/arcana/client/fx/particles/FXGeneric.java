package com.wonginnovations.arcana.client.fx.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class FXGeneric extends Particle {

    boolean doneFrames = false;
    boolean flipped = false;
    double windX;
    double windZ;
    int layer = 0;
    float dr = 0.0F;
    float dg = 0.0F;
    float db = 0.0F;
    boolean loop = false;
    float rotationSpeed = 0.0F;
    int startParticle = 0;
    int numParticles = 1;
    int particleInc = 1;
    float[] scaleKeys = new float[]{1.0F};
    float[] scaleFrames = new float[]{0.0F};
    float[] alphaKeys = new float[]{1.0F};
    float[] alphaFrames = new float[]{0.0F};
//    double slowDown = 0.9800000190734863;
    float randomX;
    float randomY;
    float randomZ;
    int[] finalFrames = null;
    boolean angled = false;
    float angleYaw;
    float anglePitch;
    int gridSize = 64;

    public FXGeneric(ClientLevel pLevel, double pX, double pY, double pZ) {
        super(pLevel, pX, pY, pZ);
        this.setSize(0.1F, 0.1F);
        this.setPos(x, y, z);
        this.friction = 0.9800000190734863F;
//        this.xo = pX;
//        this.yo = pY;
//        this.zo = pZ;
    }

    public FXGeneric(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.setSize(0.1F, 0.1F);
        this.setPos(x, y, z);
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        this.friction = 0.9800000190734863F;
//        this.xo = pX;
//        this.yo = pY;
//        this.zo = pZ;
    }

    void calculateFrames() {
        this.doneFrames = true;
        if (this.alphaKeys == null) {
            this.setAlpha(1.0F);
        }

        this.alphaFrames = new float[this.lifetime + 1];
        float inc = (float)(this.alphaKeys.length - 1) / (float)this.lifetime;
        float is = 0.0F;

        int a;
        int isF;
        float diff;
        float pa;
        for(a = 0; a <= this.lifetime; ++a) {
            isF = Mth.floor(is);
            diff = isF < this.alphaKeys.length - 1 ? this.alphaKeys[isF + 1] - this.alphaKeys[isF] : 0.0F;
            pa = is - (float)isF;
            this.alphaFrames[a] = this.alphaKeys[isF] + diff * pa;
            is += inc;
        }

        if (this.scaleKeys == null) {
            this.scale(1.0F);
        }

        this.scaleFrames = new float[this.lifetime + 1];
        inc = (float)(this.scaleKeys.length - 1) / (float)this.lifetime;
        is = 0.0F;

        for(a = 0; a <= this.lifetime; ++a) {
            isF = Mth.floor(is);
            diff = isF < this.scaleKeys.length - 1 ? this.scaleKeys[isF + 1] - this.scaleKeys[isF] : 0.0F;
            pa = is - (float)isF;
            this.scaleFrames[a] = this.scaleKeys[isF] + diff * pa;
            is += inc;
        }
    }

    @Override
    public void tick() {
        if (!this.doneFrames) {
            this.calculateFrames();
        }

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.oRoll = this.roll;
            this.roll += 3.1415927F * this.rotationSpeed * 2.0F;
            this.yd -= 0.04 * (double)this.gravity;
            this.move(this.xd, this.yd, this.zd);
            this.xd *= this.friction;
            this.yd *= this.friction;
            this.zd *= this.friction;
            this.xd += this.level.random.nextGaussian() * (double)this.randomX;
            this.yd += this.level.random.nextGaussian() * (double)this.randomY;
            this.zd += this.level.random.nextGaussian() * (double)this.randomZ;
            this.xd += this.windX;
            this.zd += this.windZ;
            if (this.onGround && this.friction != 1.0) {
                this.xd *= 0.699999988079071;
                this.zd *= 0.699999988079071;
            }
        }
    }

    @Override
    public void render(@NotNull VertexConsumer vertexConsumer, @NotNull Camera camera, float v) {
        if (this.loop) {
            this.setParticleTextureIndex(this.startParticle + this.age / this.particleInc % this.numParticles);
        } else {
            float fs = (float)this.age / (float)this.lifetime;
            this.setParticleTextureIndex((int)((float)this.startParticle + Math.min((float)this.numParticles * fs, (float)(this.numParticles - 1))));
        }

        if (this.finalFrames != null && this.finalFrames.length > 0 && this.age > this.lifetime - this.finalFrames.length) {
            int frame = this.lifetime - this.age;
            if (frame < 0) {
                frame = 0;
            }

            this.setParticleTextureIndex(this.finalFrames[frame]);
        }

        this.alpha = this.alphaFrames.length <= 0 ? 0.0F : this.alphaFrames[Math.min(this.age, this.alphaFrames.length - 1)];
        this.scale(this.scaleFrames.length <= 0 ? 0.0F : this.scaleFrames[Math.min(this.age, this.scaleFrames.length - 1)]);
        this.draw(wr, entity, f, f1, f2, f3, f4, f5);
    }

    public void draw(BufferBuilder wr, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float tx1 = (float)this.particleTextureIndexX / (float)this.gridSize;
        float tx2 = tx1 + 1.0F / (float)this.gridSize;
        float ty1 = (float)this.particleTextureIndexY / (float)this.gridSize;
        float ty2 = ty1 + 1.0F / (float)this.gridSize;
        float ts = 0.1F * this.particleScale;
        if (this.particleTexture != null) {
            tx1 = this.particleTexture.getMinU();
            tx2 = this.particleTexture.getMaxU();
            ty1 = this.particleTexture.getMinV();
            ty2 = this.particleTexture.getMaxV();
        }

        float fs;
        if (this.flipped) {
            fs = tx1;
            tx1 = tx2;
            tx2 = fs;
        }

        fs = Mth.clamp(((float)this.age + partialTicks) / (float)this.lifetime, 0.0F, 1.0F);
        float pr = this.rCol + (this.dr - this.rCol) * fs;
        float pg = this.gCol + (this.dg - this.gCol) * fs;
        float pb = this.bCol + (this.db - this.bCol) * fs;
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & '\uffff';
        int k = i & '\uffff';
        float f5 = (float)(this.xo + (this.x - this.xo) * (double)partialTicks - interpPosX);
        float f6 = (float)(this.yo + (this.y - this.yo) * (double)partialTicks - interpPosY);
        float f7 = (float)(this.zo + (this.z - this.zo) * (double)partialTicks - interpPosZ);
        if (this.angled) {
            Tessellator.getInstance().draw();
            GL11.glPushMatrix();
            GL11.glTranslated(f5, f6, f7);
            GL11.glRotatef(-this.angleYaw + 90.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.anglePitch + 90.0F, 1.0F, 0.0F, 0.0F);
            if (this.particleAngle != 0.0F) {
                float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
                GL11.glRotated((double)f8 * 57.29577951308232, 0.0, 0.0, 1.0);
            }

            wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            wr.pos((double)(-ts), (double)(-ts), 0.0).tex((double)tx2, (double)ty2).color(pr, pg, pb, this.alpha).lightmap(j, k).endVertex();
            wr.pos((double)(-ts), (double)ts, 0.0).tex((double)tx2, (double)ty1).color(pr, pg, pb, this.alpha).lightmap(j, k).endVertex();
            wr.pos((double)ts, (double)ts, 0.0).tex((double)tx1, (double)ty1).color(pr, pg, pb, this.alpha).lightmap(j, k).endVertex();
            wr.pos((double)ts, (double)(-ts), 0.0).tex((double)tx1, (double)ty2).color(pr, pg, pb, this.alpha).lightmap(j, k).endVertex();
            Tessellator.getInstance().draw();
            GL11.glPopMatrix();
            wr.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        } else {
            Vec3d[] avec3d = new Vec3d[]{new Vec3d((double)(-rotationX * ts - rotationXY * ts), (double)(-rotationZ * ts), (double)(-rotationYZ * ts - rotationXZ * ts)), new Vec3d((double)(-rotationX * ts + rotationXY * ts), (double)(rotationZ * ts), (double)(-rotationYZ * ts + rotationXZ * ts)), new Vec3d((double)(rotationX * ts + rotationXY * ts), (double)(rotationZ * ts), (double)(rotationYZ * ts + rotationXZ * ts)), new Vec3d((double)(rotationX * ts - rotationXY * ts), (double)(-rotationZ * ts), (double)(rotationYZ * ts - rotationXZ * ts))};
            if (this.particleAngle != 0.0F) {
                float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
                float f9 = Mth.cos(f8 * 0.5F);
                float f10 = Mth.sin(f8 * 0.5F) * (float)cameraViewDir.x;
                float f11 = Mth.sin(f8 * 0.5F) * (float)cameraViewDir.y;
                float f12 = Mth.sin(f8 * 0.5F) * (float)cameraViewDir.z;
                Vec3d vec3d = new Vec3d((double)f10, (double)f11, (double)f12);

                for(int l = 0; l < 4; ++l) {
                    avec3d[l] = vec3d.scale(2.0 * avec3d[l].dotProduct(vec3d)).add(avec3d[l].scale((double)(f9 * f9) - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(avec3d[l]).scale((double)(2.0F * f9)));
                }
            }

            wr.pos((double)f5 + avec3d[0].x, (double)f6 + avec3d[0].y, (double)f7 + avec3d[0].z).tex((double)tx2, (double)ty2).color(pr, pg, pb, this.alpha).lightmap(j, k).endVertex();
            wr.pos((double)f5 + avec3d[1].x, (double)f6 + avec3d[1].y, (double)f7 + avec3d[1].z).tex((double)tx2, (double)ty1).color(pr, pg, pb, this.alpha).lightmap(j, k).endVertex();
            wr.pos((double)f5 + avec3d[2].x, (double)f6 + avec3d[2].y, (double)f7 + avec3d[2].z).tex((double)tx1, (double)ty1).color(pr, pg, pb, this.alpha).lightmap(j, k).endVertex();
            wr.pos((double)f5 + avec3d[3].x, (double)f6 + avec3d[3].y, (double)f7 + avec3d[3].z).tex((double)tx1, (double)ty2).color(pr, pg, pb, this.alpha).lightmap(j, k).endVertex();
        }

    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public void setFriction(float friction) {
        this.friction = friction;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }
}
