package com.wonginnovations.arcana.client.render.particles;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

//public class SubSprite extends TextureAtlasSprite {
//    private final TextureAtlasSprite original;
//    private final float minU, maxU, minV, maxV;
//
//    public SubSprite(TextureAtlasSprite original, int frameIndex, int frameHeight) {
//        super(original.contents()); // Forge requires super call, this is placeholder
//        this.original = original;
//
//        float totalHeight = original.getHeight();
//        float frameCount = totalHeight / frameHeight;
//        float vSize = 1.0f / frameCount;
//
//        this.minU = original.getU0();
//        this.maxU = original.getU1();
//
//        this.minV = original.getV0() + vSize * frameIndex;
//        this.maxV = original.getV0() + vSize * (frameIndex + 1);
//    }
//
//    @Override public float getU0() { return minU; }
//    @Override public float getU1() { return maxU; }
//    @Override public float getV0() { return minV; }
//    @Override public float getV1() { return maxV; }
//}
