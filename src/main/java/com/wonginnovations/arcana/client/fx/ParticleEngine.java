package com.wonginnovations.arcana.client.fx;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.client.fx.particles.FXGeneric;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber
public class ParticleEngine {
    public static final ResourceLocation particleTexture = new ResourceLocation(Arcana.MODID, "textures/misc/particles.png");
//    private static final ArrayList<HashMap<ResourceKey<Level>, ArrayList<Particle>>> particles = new ArrayList<>(List.of(
//            new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>()));
    private static final HashMap<ResourceKey<Level>, ArrayList<FXGeneric>> particles = new HashMap<>();
    private static final ArrayList<ParticleDelay> particlesDelayed = new ArrayList<>();

//    @SubscribeEvent
//    public static void renderParticlesEvent(RenderLevelStageEvent event) {
//        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
//            Minecraft minecraft = Minecraft.getInstance();
//            if (minecraft.level == null) return;
//            Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(MyRenderType.PARTICLE);
//        }
//    }

    @SubscribeEvent
    public static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            float frame = event.getPartialTick();
//        LocalPlayer entity = Minecraft.getInstance().player;
            ResourceKey<Level> dim = Minecraft.getInstance().level.dimension();
            event.getPoseStack().pushPose();
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
//        GlStateManager.alphaFunc(516, 0.003921569F);
            RenderSystem.setShaderTexture(0, particleTexture);
            RenderSystem.depthMask(false);

//            for(int layer = 3; layer >= 0; --layer) {
                if (particles.containsKey(dim)) {
                    ArrayList<FXGeneric> parts = particles.get(dim);
                    if (!parts.isEmpty()) {
                        for (FXGeneric particle : parts) {
                            if (particle != null) {
                                try {
                                    Tesselator tesselator = Tesselator.getInstance();
                                    BufferBuilder buffer = tesselator.getBuilder();
                                    buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR);
                                    particle.renderParticle(event.getPoseStack(), buffer, event.getCamera(), frame);
                                    BufferUploader.drawWithShader(buffer.end());
                                } catch (Throwable exception) {
                                    CrashReport crashreport = CrashReport.forThrowable(exception, "Rendering Particle");
                                    CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being rendered");
                                    crashreportcategory.setDetail("Particle", particle::toString);
                                    crashreportcategory.setDetail("Particle Type", () -> "ARCANA_PARTICLE_TEXTURE");
                                    throw new ReportedException(crashreport);
                                }
                            }
                        }
//                        tesselator.end();
//                        switch (layer) {
//                            case 2:
//                            case 3:
//                                RenderSystem.enableDepthTest();
//                        }
                    }
                }
//            }

            RenderSystem.depthMask(true);
            RenderSystem.blendFunc(770, 771);
            RenderSystem.disableBlend();
            // GlStateManager.alphaFunc(516, 0.1F);
            event.getPoseStack().popPose();
        }
        // pretty sure the code below is for custom GUI particles, which will be worried about later.

//        else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
//            float frame = event.getPartialTick();
//            LocalPlayer entity = Minecraft.getInstance().player;
//            ResourceKey<Level> dim = Minecraft.getInstance().level.dimension();
//            GL11.glPushMatrix();
//            Window window = Minecraft.getInstance().getWindow();
//            GL11.glClear(256);
//            GL11.glMatrixMode(5889);
//            GL11.glLoadIdentity();
//            GL11.glOrtho(0.0, window.getGuiScaledWidth(), window.getGuiScaledHeight(), 0.0, 1000.0, 3000.0);
//            GL11.glMatrixMode(5888);
//            GL11.glLoadIdentity();
//            GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
//            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//            RenderSystem.enableBlend();
//            // GlStateManager.glAlphaFunc(516, 0.003921569F);
//            RenderSystem.setShaderTexture(0, particleTexture);
//            RenderSystem.depthMask(false);
//
//            for(int layer = 5; layer >= 4; --layer) {
//                if (particles.get(layer).containsKey(dim)) {
//                    ArrayList<Particle> parts = particles.get(layer).get(dim);
//                    if (!parts.isEmpty()) {
//                        switch (layer) {
//                            case 4:
//                                RenderSystem.blendFunc(770, 1);
//                                break;
//                            case 5:
//                                RenderSystem.blendFunc(770, 771);
//                        }
//
//                        Tesselator tesselator = Tesselator.getInstance();
//                        BufferBuilder buffer = tesselator.getBuilder();
//
//                        for(int j = 0; j < parts.size(); ++j) {
//                            final Particle Particle = parts.get(j);
//                            if (Particle != null) {
//                                try {
//                                    Particle.render(buffer, event.getCamera(), frame);
//                                } catch (Throwable exception) {
//                                    CrashReport crashreport = CrashReport.forThrowable(exception, "Rendering Particle");
//                                    CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being rendered");
//                                    crashreportcategory.setDetail("Particle", Particle::toString);
//                                    crashreportcategory.setDetail("Particle Type", () -> "ARCANA_PARTICLE_TEXTURE");
//                                    throw new ReportedException(crashreport);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            RenderSystem.depthMask(true);
//            RenderSystem.blendFunc(770, 771);
//            RenderSystem.disableBlend();
////                GlStateManager.alphaFunc(516, 0.1F);
//            GL11.glPopMatrix();
//        }
    }

    public static void addEffect(ResourceKey<Level> dim, FXGeneric fx) {
        if (Minecraft.getInstance().level != null) {
            int particleStatus = Minecraft.getInstance().options.particles().get().getId();
            if (Minecraft.getInstance().getFps() < 30) {
                ++particleStatus;
            }

            if (Minecraft.getInstance().level.random.nextInt(3) >= particleStatus) {
                if (!particles.containsKey(dim)) {
                    particles.put(dim, new ArrayList<>());
                }

                ArrayList<FXGeneric> parts = particles.get(dim);
                if (parts.size() >= getParticleLimit()) {
                    parts.remove(0);
                }

                parts.add(fx);
                particles.put(dim, parts);
            }
        }
    }

    private static int getParticleLimit() {
        ParticleStatus particleStatus = Minecraft.getInstance().options.particles().get();
        return particleStatus == ParticleStatus.MINIMAL ? 500 : (particleStatus == ParticleStatus.DECREASED ? 1000 : 2000);
    }

    public static void addEffectWithDelay(Level level, FXGeneric fx, int delay) {
        particlesDelayed.add(new ParticleDelay(fx, level.dimension(), delay));
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void updateParticles(TickEvent.ClientTickEvent event) {
        if (event.side != LogicalSide.SERVER) {
            Level level = Minecraft.getInstance().level;
            if (level != null) {
                ResourceKey<Level> dim = level.dimension();
                if (event.phase == TickEvent.Phase.START) {
                    try {
                        Iterator<ParticleDelay> i = particlesDelayed.iterator();

                        while(i.hasNext()) {
                            ParticleDelay pd = i.next();
                            --pd.delay;
                            if (pd.delay <= 0) {
                                if (pd.dim == dim) {
                                    addEffect(pd.dim, pd.particle);
                                }

                                i.remove();
                            }
                        }
                    } catch (Exception ignored) {}

//                    for(int layer = 0; layer < 6; ++layer) {
                        if (particles.containsKey(dim)) {
                            ArrayList<FXGeneric> parts = particles.get(dim);

                            for(int j = 0; j < parts.size(); ++j) {
                                final FXGeneric particle = parts.get(j);

                                try {
                                    if (particle != null) {
                                        particle.onUpdate();
                                    }
                                } catch (Exception exception) {
                                    try {
                                        CrashReport crashreport = CrashReport.forThrowable(exception, "Ticking Particle");
                                        CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being ticked");
                                        crashreportcategory.setDetail("Particle", particle::toString);
                                        crashreportcategory.setDetail("Particle Type", () -> "ENTITY_PARTICLE_TEXTURE");
                                        particle.setExpired();
                                    } catch (Exception ignored) {}
                                }

                                if (particle == null || !particle.isAlive()) {
                                    parts.remove(j--);
                                    particles.put(dim, parts);
                                }
                            }
                        }
//                    }
                }

            }
        }
    }

    private static class ParticleDelay {
        FXGeneric particle;
        ResourceKey<Level> dim;
        int delay;

        public ParticleDelay(FXGeneric particle, ResourceKey<Level> dim, int delay) {
            this.dim = dim;
            this.particle = particle;
            this.delay = delay;
        }
    }
}
