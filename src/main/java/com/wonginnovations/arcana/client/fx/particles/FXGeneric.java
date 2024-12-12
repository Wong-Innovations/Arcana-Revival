package com.wonginnovations.arcana.client.fx.particles;

import com.mojang.blaze3d.vertex.*;
import com.wonginnovations.arcana.common.utils.MathUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class FXGeneric extends OldParticleBase {

    boolean doneFrames = false;
    boolean flipped = false;
    double windX;
    double windZ;
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
    double slowDown = 0.9800000190734863;
    float randomX;
    float randomY;
    float randomZ;
    int[] finalFrames = null;
    boolean angled = false;
    float angleYaw;
    float anglePitch;
    int gridSize = 64;

    public FXGeneric(Level level, double x, double y, double z, double xx, double yy, double zz) {
        super(level, x, y, z, xx, yy, zz);
        this.setSize(0.1F, 0.1F);
        this.setPosition(x, y, z);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.particleTextureJitterX = 0.0F;
        this.particleTextureJitterY = 0.0F;
        this.motionX = xx;
        this.motionY = yy;
        this.motionZ = zz;
    }

    public FXGeneric(Level level, double x, double y, double z) {
        super(level, x, y, z, 0.0, 0.0, 0.0);
        this.setSize(0.1F, 0.1F);
        this.setPosition(x, y, z);
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.particleTextureJitterX = 0.0F;
        this.particleTextureJitterY = 0.0F;
    }

    void calculateFrames() {
        this.doneFrames = true;
        if (this.alphaKeys == null) {
            this.setAlphaF(1.0F);
        }

        this.alphaFrames = new float[this.particleMaxAge + 1];
        float inc = (float)(this.alphaKeys.length - 1) / (float)this.particleMaxAge;
        float is = 0.0F;

        int a;
        int isF;
        float diff;
        float pa;
        for(a = 0; a <= this.particleMaxAge; ++a) {
            isF = Mth.floor(is);
            diff = isF < this.alphaKeys.length - 1 ? this.alphaKeys[isF + 1] - this.alphaKeys[isF] : 0.0F;
            pa = is - (float)isF;
            this.alphaFrames[a] = this.alphaKeys[isF] + diff * pa;
            is += inc;
        }

        if (this.scaleKeys == null) {
            this.setScale(1.0F);
        }

        this.scaleFrames = new float[this.particleMaxAge + 1];
        inc = (float)(this.scaleKeys.length - 1) / (float)this.particleMaxAge;
        is = 0.0F;

        for(a = 0; a <= this.particleMaxAge; ++a) {
            isF = Mth.floor(is);
            diff = isF < this.scaleKeys.length - 1 ? this.scaleKeys[isF + 1] - this.scaleKeys[isF] : 0.0F;
            pa = is - (float)isF;
            this.scaleFrames[a] = this.scaleKeys[isF] + diff * pa;
            is += inc;
        }

    }

    public void onUpdate() {
        if (!this.doneFrames) {
            this.calculateFrames();
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }

        this.prevParticleAngle = this.particleAngle;
        this.particleAngle += 3.1415927F * this.rotationSpeed * 2.0F;
        this.motionY -= 0.04 * (double)this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= this.slowDown;
        this.motionY *= this.slowDown;
        this.motionZ *= this.slowDown;
        this.motionX += this.level.random.nextGaussian() * (double)this.randomX;
        this.motionY += this.level.random.nextGaussian() * (double)this.randomY;
        this.motionZ += this.level.random.nextGaussian() * (double)this.randomZ;
        this.motionX += this.windX;
        this.motionZ += this.windZ;
        if (this.onGround && this.slowDown != 1.0) {
            this.motionX *= 0.699999988079071;
            this.motionZ *= 0.699999988079071;
        }

    }
    
    public void renderParticle(PoseStack ps, BufferBuilder bb, Camera camera, float partialTicks) {
        int i = (Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_FRONT)? 1 : 0;
        float rotationX = Mth.cos(camera.getEntity().getYRot() * 0.017453292F) * (float)(1 - i * 2);
        float rotationZ = Mth.sin(camera.getEntity().getYRot() * 0.017453292F) * (float)(1 - i * 2);
        float rotationYZ = -rotationZ * Mth.sin(camera.getEntity().getXRot() * 0.017453292F) * (float)(1 - i * 2);
        float rotationXY = rotationX * Mth.sin(camera.getEntity().getXRot() * 0.017453292F) * (float)(1 - i * 2);
        float rotationXZ = Mth.cos(camera.getEntity().getXRot() * 0.017453292F);
        this.renderParticle(ps, bb, camera.getEntity(), partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    public void renderParticle(PoseStack ps, BufferBuilder bb, Entity entity, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (this.loop) {
            this.setParticleTextureIndex(this.startParticle + this.particleAge / this.particleInc % this.numParticles);
        } else {
            float fs = (float)this.particleAge / (float)this.particleMaxAge;
            this.setParticleTextureIndex((int)((float)this.startParticle + Math.min((float)this.numParticles * fs, (float)(this.numParticles - 1))));
        }

        if (this.finalFrames != null && this.finalFrames.length > 0 && this.particleAge > this.particleMaxAge - this.finalFrames.length) {
            int frame = this.particleMaxAge - this.particleAge;
            if (frame < 0) {
                frame = 0;
            }

            this.setParticleTextureIndex(this.finalFrames[frame]);
        }

        this.particleAlpha = this.alphaFrames.length <= 0 ? 0.0F : this.alphaFrames[Math.min(this.particleAge, this.alphaFrames.length - 1)];
        this.particleScale = this.scaleFrames.length <= 0 ? 0.0F : this.scaleFrames[Math.min(this.particleAge, this.scaleFrames.length - 1)];
        this.draw(ps, bb, entity, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
    }

    public boolean isFlipped() {
        return this.flipped;
    }

    public void setFlipped(boolean flip) {
        this.flipped = flip;
    }

    public void draw(PoseStack ps, BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float tx1 = (float)this.particleTextureIndexX / (float)this.gridSize;
        float tx2 = tx1 + 1.0F / (float)this.gridSize;
        float ty1 = (float)this.particleTextureIndexY / (float)this.gridSize;
        float ty2 = ty1 + 1.0F / (float)this.gridSize;
        float ts = 0.1F * this.particleScale;
        if (this.particleTexture != null) {
            tx1 = this.particleTexture.getU0();
            tx2 = this.particleTexture.getU1();
            ty1 = this.particleTexture.getV0();
            ty2 = this.particleTexture.getV1();
        }

        float fs;
        if (this.flipped) {
            fs = tx1;
            tx1 = tx2;
            tx2 = fs;
        }

        fs = Mth.clamp(((float)this.particleAge + partialTicks) / (float)this.particleMaxAge, 0.0F, 1.0F);
        float pr = this.particleRed + (this.dr - this.particleRed) * fs;
        float pg = this.particleGreen + (this.dg - this.particleGreen) * fs;
        float pb = this.particleBlue + (this.db - this.particleBlue) * fs;
        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & '\uffff';
        int k = i & '\uffff';
        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        if (this.angled) {
//            Tesselator.getInstance().end();
            ps.pushPose();
            ps.translate(f5, f6, f7);
            ps.mulPose(new Quaternionf(new AxisAngle4f(-this.angleYaw + 90.0F, 0.0F, 1.0F, 0.0F)));
            ps.mulPose(new Quaternionf(new AxisAngle4f(this.anglePitch + 90.0F, 1.0F, 0.0F, 0.0F)));
            if (this.particleAngle != 0.0F) {
                float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
                ps.mulPose(new Quaternionf(new AxisAngle4f(f8 * 57.29577951308232F, 0.0F, 0.0F, 0.0F)));
            }

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR);
            buffer.vertex(-ts, -ts, 0.0).uv(tx2, ty2).uv2(j, k).color(pr, pg, pb, this.particleAlpha).endVertex();
            buffer.vertex(-ts, ts, 0.0).uv(tx2, ty1).uv2(j, k).color(pr, pg, pb, this.particleAlpha).endVertex();
            buffer.vertex(ts, ts, 0.0).uv(tx1, ty1).uv2(j, k).color(pr, pg, pb, this.particleAlpha).endVertex();
            buffer.vertex(ts, -ts, 0.0).uv(tx1, ty2).uv2(j, k).color(pr, pg, pb, this.particleAlpha).endVertex();
//            Tesselator.getInstance().end();
            ps.popPose();
            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR);
        } else {
            Vec3[] avec3d = new Vec3[]{new Vec3(-rotationX * ts - rotationXY * ts, -rotationZ * ts, -rotationYZ * ts - rotationXZ * ts), new Vec3(-rotationX * ts + rotationXY * ts, rotationZ * ts, -rotationYZ * ts + rotationXZ * ts), new Vec3(rotationX * ts + rotationXY * ts, rotationZ * ts, rotationYZ * ts + rotationXZ * ts), new Vec3(rotationX * ts - rotationXY * ts, -rotationZ * ts, rotationYZ * ts - rotationXZ * ts)};
            if (this.particleAngle != 0.0F) {
                float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
                float f9 = Mth.cos(f8 * 0.5F);
                float f10 = Mth.sin(f8 * 0.5F) * (float)cameraViewDir.x;
                float f11 = Mth.sin(f8 * 0.5F) * (float)cameraViewDir.y;
                float f12 = Mth.sin(f8 * 0.5F) * (float)cameraViewDir.z;
                Vec3 vec3d = new Vec3(f10, f11, f12);

                for(int l = 0; l < 4; ++l) {
                    avec3d[l] = vec3d.scale(2.0 * avec3d[l].dot(vec3d)).add(avec3d[l].scale((double)(f9 * f9) - vec3d.dot(vec3d))).add(vec3d.cross(avec3d[l]).scale(2.0F * f9));
                }
            }

            Matrix4f transformationMatrix = ps.last().pose();

            buffer.vertex(transformationMatrix, f5 + (float) avec3d[0].x, f6 + (float) avec3d[0].y, f7 + (float) avec3d[0].z).uv(tx2, ty2).uv2(j, k).color(pr, pg, pb, this.particleAlpha).endVertex();
            buffer.vertex(transformationMatrix, f5 + (float) avec3d[1].x, f6 + (float) avec3d[1].y, f7 + (float) avec3d[1].z).uv(tx2, ty1).uv2(j, k).color(pr, pg, pb, this.particleAlpha).endVertex();
            buffer.vertex(transformationMatrix, f5 + (float) avec3d[2].x, f6 + (float) avec3d[2].y, f7 + (float) avec3d[2].z).uv(tx1, ty1).uv2(j, k).color(pr, pg, pb, this.particleAlpha).endVertex();
            buffer.vertex(transformationMatrix, f5 + (float) avec3d[3].x, f6 + (float) avec3d[3].y, f7 + (float) avec3d[3].z).uv(tx1, ty2).uv2(j, k).color(pr, pg, pb, this.particleAlpha).endVertex();
        }
    }

    public void setWind(double d) {
        int m = this.level.getMoonPhase();
        Vec3 vsource = new Vec3(0.0, 0.0, 0.0);
        Vec3 vtar = new Vec3(0.1, 0.0, 0.0);
        vtar = MathUtils.rotateAroundY(vtar, (float)(m * (40 + this.level.random.nextInt(10))) / 180.0F * 3.1415927F);
        Vec3 vres = vsource.add(vtar.x, vtar.y, vtar.z);
        this.windX = vres.x * d;
        this.windZ = vres.z * d;
    }

    public void setRBGColorF(float particleRedIn, float particleGreenIn, float particleBlueIn) {
        super.setRBGColorF(particleRedIn, particleGreenIn, particleBlueIn);
        this.dr = particleRedIn;
        this.dg = particleGreenIn;
        this.db = particleBlueIn;
    }

    public void setRBGColorF(float particleRedIn, float particleGreenIn, float particleBlueIn, float r2, float g2, float b2) {
        super.setRBGColorF(particleRedIn, particleGreenIn, particleBlueIn);
        this.dr = r2;
        this.dg = g2;
        this.db = b2;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setRotationSpeed(float rot) {
        this.rotationSpeed = (float)((double)rot * 0.017453292519943);
    }

    public void setRotationSpeed(float start, float rot) {
        this.particleAngle = (float)((double)start * Math.PI * 2.0);
        this.rotationSpeed = (float)((double)rot * 0.017453292519943);
    }

    public void setParticles(int startParticle, int numParticles, int particleInc) {
        this.numParticles = numParticles;
        this.particleInc = particleInc;
        this.startParticle = startParticle;
        this.setParticleTextureIndex(startParticle);
    }

    public void setParticle(int startParticle) {
        this.numParticles = 1;
        this.particleInc = 1;
        this.startParticle = startParticle;
        this.setParticleTextureIndex(startParticle);
    }

    public void setScale(float... scale) {
        this.particleScale = scale[0];
        this.scaleKeys = scale;
    }

    public void setAlphaF(float... a1) {
        super.setAlphaF(a1[0]);
        this.alphaKeys = a1;
    }

    public void setAlphaF(float a1) {
        super.setAlphaF(a1);
        this.alphaKeys = new float[1];
        this.alphaKeys[0] = a1;
    }

    public void setSlowDown(double slowDown) {
        this.slowDown = slowDown;
    }

    public void setRandomMovementScale(float x, float y, float z) {
        this.randomX = x;
        this.randomY = y;
        this.randomZ = z;
    }

    public void setFinalFrames(int... frames) {
        this.finalFrames = frames;
    }

    public void setAngles(float yaw, float pitch) {
        this.angleYaw = yaw;
        this.anglePitch = pitch;
        this.angled = true;
    }

    public void setGravity(float g) {
        this.particleGravity = g;
    }

    public void setParticleTextureIndex(int index) {
        if (index < 0) {
            index = 0;
        }

        this.particleTextureIndexX = index % this.gridSize;
        this.particleTextureIndexY = index / this.gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public void setNoClip(boolean clip) {
        this.canCollide = clip;
    }
}
