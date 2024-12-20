package com.wonginnovations.arcana.client.fx.particles;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class OldParticleBase
{
    private static final AABB EMPTY_AABB = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    protected Level level;
    protected double prevPosX;
    protected double prevPosY;
    protected double prevPosZ;
    protected double posX;
    protected double posY;
    protected double posZ;
    protected double motionX;
    protected double motionY;
    protected double motionZ;
    private AABB boundingBox;
    protected boolean onGround;
    /** Determines if particle to block collision is to be used */
    protected boolean canCollide;
    protected boolean isExpired;
    protected float width;
    protected float height;
    protected Random rand;
    protected int particleTextureIndexX;
    protected int particleTextureIndexY;
    protected float particleTextureJitterX;
    protected float particleTextureJitterY;
    protected int particleAge;
    protected int particleMaxAge;
    protected float particleScale;
    protected float particleGravity;
    /** The red amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0. */
    protected float particleRed;
    /** The green amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0. */
    protected float particleGreen;
    /** The blue amount of color. Used as a percentage, 1.0 = 255 and 0.0 = 0. */
    protected float particleBlue;
    /** Particle alpha */
    protected float particleAlpha;
    protected TextureAtlasSprite particleTexture;
    /** The amount the particle will be rotated in rendering. */
    protected float particleAngle;
    /** The particle angle from the last tick. Appears to be used for calculating the rendered angle with partial ticks. */
    protected float prevParticleAngle;
    public static double interpPosX;
    public static double interpPosY;
    public static double interpPosZ;
    public static Vec3 cameraViewDir;

    protected OldParticleBase(Level level, double posXIn, double posYIn, double posZIn)
    {
        this.boundingBox = EMPTY_AABB;
        this.width = 0.6F;
        this.height = 1.8F;
        this.rand = new Random();
        this.particleAlpha = 1.0F;
        this.level = level;
        this.setSize(0.2F, 0.2F);
        this.setPosition(posXIn, posYIn, posZIn);
        this.prevPosX = posXIn;
        this.prevPosY = posYIn;
        this.prevPosZ = posZIn;
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
        this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
        this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
        this.particleMaxAge = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
        this.particleAge = 0;
        this.canCollide = true;
    }

    public OldParticleBase(Level worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        this(worldIn, xCoordIn, yCoordIn, zCoordIn);
        this.motionX = xSpeedIn + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionY = ySpeedIn + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        this.motionZ = zSpeedIn + (Math.random() * 2.0D - 1.0D) * 0.4000000059604645D;
        float f = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
        float f1 = Mth.sqrt((float) (this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ));
        this.motionX = this.motionX / (double)f1 * (double)f * 0.4000000059604645D;
        this.motionY = this.motionY / (double)f1 * (double)f * 0.4000000059604645D + 0.10000000149011612D;
        this.motionZ = this.motionZ / (double)f1 * (double)f * 0.4000000059604645D;
    }

    public OldParticleBase multiplyVelocity(float multiplier)
    {
        this.motionX *= multiplier;
        this.motionY = (this.motionY - 0.10000000149011612D) * (double) multiplier + 0.10000000149011612D;
        this.motionZ *= multiplier;
        return this;
    }

    public OldParticleBase multipleParticleScaleBy(float scale)
    {
        this.setSize(0.2F * scale, 0.2F * scale);
        this.particleScale *= scale;
        return this;
    }

    public void setRBGColorF(float particleRedIn, float particleGreenIn, float particleBlueIn)
    {
        this.particleRed = particleRedIn;
        this.particleGreen = particleGreenIn;
        this.particleBlue = particleBlueIn;
    }

    /**
     * Sets the particle alpha (float)
     */
    public void setAlphaF(float alpha)
    {
        this.particleAlpha = alpha;
    }

    public boolean shouldDisableDepth()
    {
        return false;
    }

    public float getRedColorF()
    {
        return this.particleRed;
    }

    public float getGreenColorF()
    {
        return this.particleGreen;
    }

    public float getBlueColorF()
    {
        return this.particleBlue;
    }

    public void setMaxAge(int particleLifeTime)
    {
        this.particleMaxAge = particleLifeTime;
    }

    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }

        this.motionY -= 0.04D * (double)this.particleGravity;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.9800000190734863D;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= 0.9800000190734863D;

        if (this.onGround)
        {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
        }
    }

    /**
     * Renders the particle
     */
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        float f = (float)this.particleTextureIndexX / 16.0F;
        float f1 = f + 0.0624375F;
        float f2 = (float)this.particleTextureIndexY / 16.0F;
        float f3 = f2 + 0.0624375F;
        float f4 = 0.1F * this.particleScale;

        if (this.particleTexture != null)
        {
            f = this.particleTexture.getU0();
            f1 = this.particleTexture.getU1();
            f2 = this.particleTexture.getV0();
            f3 = this.particleTexture.getV1();
        }

        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
        int i = this.getBrightnessForRender();
        int j = i >> 16 & 65535;
        int k = i & 65535;
        Vec3[] vec3Array = new Vec3[] {new Vec3(-rotationX * f4 - rotationXY * f4, -rotationZ * f4, -rotationYZ * f4 - rotationXZ * f4), new Vec3(-rotationX * f4 + rotationXY * f4, rotationZ * f4, -rotationYZ * f4 + rotationXZ * f4), new Vec3(rotationX * f4 + rotationXY * f4, rotationZ * f4, rotationYZ * f4 + rotationXZ * f4), new Vec3(rotationX * f4 - rotationXY * f4, -rotationZ * f4, rotationYZ * f4 - rotationXZ * f4)};

        if (this.particleAngle != 0.0F)
        {
            float f8 = this.particleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
            float f9 = Mth.cos(f8 * 0.5F);
            float f10 = Mth.sin(f8 * 0.5F) * (float)cameraViewDir.x;
            float f11 = Mth.sin(f8 * 0.5F) * (float)cameraViewDir.y;
            float f12 = Mth.sin(f8 * 0.5F) * (float)cameraViewDir.z;
            Vec3 vec3 = new Vec3(f10, f11, f12);

            for (int l = 0; l < 4; ++l)
            {
                vec3Array[l] = vec3.scale(2.0D * vec3Array[l].dot(vec3)).add(vec3Array[l].scale((double)(f9 * f9) - vec3.dot(vec3))).add(vec3.cross(vec3Array[l]).scale(2.0F * f9));
            }
        }

        buffer.vertex((double)f5 + vec3Array[0].x, (double)f6 + vec3Array[0].y, (double)f7 + vec3Array[0].z).uv(f1, f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).uv2(j, k).endVertex();
        buffer.vertex((double)f5 + vec3Array[1].x, (double)f6 + vec3Array[1].y, (double)f7 + vec3Array[1].z).uv(f1, f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).uv2(j, k).endVertex();
        buffer.vertex((double)f5 + vec3Array[2].x, (double)f6 + vec3Array[2].y, (double)f7 + vec3Array[2].z).uv(f, f2).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).uv2(j, k).endVertex();
        buffer.vertex((double)f5 + vec3Array[3].x, (double)f6 + vec3Array[3].y, (double)f7 + vec3Array[3].z).uv(f, f3).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).uv2(j, k).endVertex();
    }

    /**
     * Retrieve what effect layer (what texture) the particle should be rendered with. 0 for the particle sprite sheet,
     * 1 for the main Texture atlas, and 3 for a custom texture
     */
    public int getFXLayer()
    {
        return 0;
    }

    /**
     * Sets the texture used by the particle.
     */
    public void setParticleTexture(TextureAtlasSprite texture)
    {
        int i = this.getFXLayer();

        if (i == 1)
        {
            this.particleTexture = texture;
        }
        else
        {
            throw new RuntimeException("Invalid call to Particle.setTex, use coordinate methods");
        }
    }

    /**
     * Public method to set private field particleTextureIndex.
     */
    public void setParticleTextureIndex(int particleTextureIndex)
    {
        if (this.getFXLayer() != 0)
        {
            throw new RuntimeException("Invalid call to Particle.setMiscTex");
        }
        else
        {
            this.particleTextureIndexX = particleTextureIndex % 16;
            this.particleTextureIndexY = particleTextureIndex / 16;
        }
    }

    public void nextTextureIndexX()
    {
        ++this.particleTextureIndexX;
    }

    public String toString()
    {
        return this.getClass().getSimpleName() + ", Pos (" + this.posX + "," + this.posY + "," + this.posZ + "), RGBA (" + this.particleRed + "," + this.particleGreen + "," + this.particleBlue + "," + this.particleAlpha + "), Age " + this.particleAge;
    }

    /**
     * Called to indicate that this particle effect has expired and should be discontinued.
     */
    public void setExpired()
    {
        this.isExpired = true;
    }

    protected void setSize(float particleWidth, float particleHeight)
    {
        if (particleWidth != this.width || particleHeight != this.height)
        {
            this.width = particleWidth;
            this.height = particleHeight;
            // FORGE: Fix MC-12269 - Glitchy movement when setSize is called without setPosition
            setPosition(posX, posY, posZ);
        }
    }

    public void setPosition(double x, double y, double z)
    {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        float f = this.width / 2.0F;
        float f1 = this.height;
        this.setBoundingBox(new AABB(x - (double)f, y, z - (double)f, x + (double)f, y + (double)f1, z + (double)f));
    }

    public void move(double x, double y, double z)
    {
        double d0 = y;
        double origX = x;
        double origZ = z;

        if (this.canCollide)
        {
            Iterable<VoxelShape> list = this.level.getBlockCollisions(null, this.getBoundingBox().inflate(x, y, z));

            for (VoxelShape aabb : list)
            {
                y = aabb.collide(Direction.Axis.Y, this.getBoundingBox(), y);
            }

            this.setBoundingBox(this.getBoundingBox().move(0.0D, y, 0.0D));

            for (VoxelShape aabb1 : list)
            {
                x = aabb1.collide(Direction.Axis.X, this.getBoundingBox(), x);
            }

            this.setBoundingBox(this.getBoundingBox().move(x, 0.0D, 0.0D));

            for (VoxelShape aabb2 : list)
            {
                z = aabb2.collide(Direction.Axis.Z, this.getBoundingBox(), z);
            }

            this.setBoundingBox(this.getBoundingBox().move(0.0D, 0.0D, z));
        }
        else
        {
            this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        }

        this.resetPositionToBB();
        this.onGround = d0 != y && d0 < 0.0D;

        if (origX != x)
        {
            this.motionX = 0.0D;
        }

        if (origZ != z)
        {
            this.motionZ = 0.0D;
        }
    }

    protected void resetPositionToBB()
    {
        AABB AABB = this.getBoundingBox();
        this.posX = (AABB.minX + AABB.maxX) / 2.0D;
        this.posY = AABB.minY;
        this.posZ = (AABB.minZ + AABB.maxZ) / 2.0D;
    }

    public int getBrightnessForRender()
    {
        BlockPos blockpos = new BlockPos((int) this.posX, (int) this.posY, (int) this.posZ);
        return this.level.hasChunkAt(blockpos)
                ? LightTexture.pack(this.level.getBrightness(LightLayer.BLOCK, blockpos), this.level.getBrightness(LightLayer.SKY, blockpos))
                : 0;
    }

    /**
     * Returns true if this effect has not yet expired. "I feel happy! I feel happy!"
     */
    public boolean isAlive()
    {
        return !this.isExpired;
    }

    public AABB getBoundingBox()
    {
        return this.boundingBox;
    }

    public void setBoundingBox(AABB bb)
    {
        this.boundingBox = bb;
    }
}
