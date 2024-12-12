package com.wonginnovations.arcana.client.fx.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wonginnovations.arcana.client.fx.ModParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class SparkleParticle extends TextureSheetParticle {

    protected final SpriteSet sprites;

    public SparkleParticle(ClientLevel pLevel, double pX, double pY, double pZ, double dx, double dy, double dz, SpriteSet pSprites, float pGravity) {
        super(pLevel, pX, pY, pZ, dx, dy, dz);
        this.gravity = pGravity;
        this.sprites = pSprites;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<Options> {

        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public @Nullable Particle createParticle(@NotNull Options particleOptions, @NotNull ClientLevel clientLevel, double x, double y, double z, double dx, double dy, double dz) {
            return new SparkleParticle(clientLevel, x, y, z, dx, dy, dz, this.sprites, particleOptions.getGravity());
        }
    }

    public static class Options implements ParticleOptions {

        float gravity;


        public Options(float gravity) {
            this.gravity = gravity;
        }

        public float getGravity() {
            return gravity;
        }

        public static final Codec<Options> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("gravity").forGetter(particle -> particle.gravity))
                .apply(instance, Options::new));

        @SuppressWarnings("deprecation")
        public static final Deserializer<Options> DESERIALIZER = new Deserializer<>() {

            @Override
            public @NotNull Options fromCommand(@NotNull ParticleType<Options> arg, StringReader stringReader) throws CommandSyntaxException {
                float gravity = stringReader.readFloat();
                return new Options(gravity);
            }

            public @NotNull Options fromNetwork(@NotNull ParticleType<Options> arg, FriendlyByteBuf arg2) {
                return new Options(arg2.readFloat());
            }
        };

        @Override
        public @NotNull ParticleType<?> getType() {
            return ModParticles.SPARKLE.get();
        }

        @Override
        public void writeToNetwork(@NotNull FriendlyByteBuf friendlyByteBuf) {

        }

        @Override
        public @NotNull String writeToString() {
            return "";
        }
    }

    public static class Type extends ParticleType<Options> {

        @SuppressWarnings("deprecation")
        public Type(boolean pOverrideLimiter, ParticleOptions.Deserializer<Options> pDeserializer) {
            super(pOverrideLimiter, pDeserializer);
        }

        @Override
        public @NotNull Codec<SparkleParticle.Options> codec() {
            return SparkleParticle.Options.CODEC;
        }
    }

}
