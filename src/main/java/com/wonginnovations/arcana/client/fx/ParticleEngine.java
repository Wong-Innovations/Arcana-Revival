package com.wonginnovations.arcana.client.fx;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wonginnovations.arcana.Arcana;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.MixinEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.Callable;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber
public class ParticleEngine {
    public static final ResourceLocation particleTexture = new ResourceLocation(Arcana.MODID, "textures/misc/particles.png");
    protected Level level;
    private static HashMap<Integer, ArrayList<Particle>>[] particles = new HashMap[]{new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap(), new HashMap()};
    private static ArrayList<ParticleDelay> particlesDelayed = new ArrayList();
    private Random rand = new Random();

    @SubscribeEvent
    public static void renderTick(TickEvent.RenderTickEvent event) {
        if (Minecraft.getInstance().level != null) {
            if (event.phase == TickEvent.Phase.END) {
                float frame = event.renderTickTime;
                Entity entity = Minecraft.getInstance().player;
                TextureManager renderer = Minecraft.getInstance().renderEngine;
                int dim = Minecraft.getInstance().level.provider.getDimension();
                GL11.glPushMatrix();
                ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
                GL11.glClear(256);
                GL11.glMatrixMode(5889);
                GL11.glLoadIdentity();
                GL11.glOrtho(0.0, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
                GL11.glMatrixMode(5888);
                GL11.glLoadIdentity();
                GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableBlend();
                GL11.glEnable(3042);
                GL11.glAlphaFunc(516, 0.003921569F);
                renderer.bindTexture(particleTexture);
                GlStateManager.depthMask(false);

                for(int layer = 5; layer >= 4; --layer) {
                    if (particles[layer].containsKey(dim)) {
                        ArrayList<Particle> parts = particles[layer].get(dim);
                        if (!parts.isEmpty()) {
                            switch (layer) {
                                case 4:
                                    GlStateManager.blendFunc(770, 1);
                                    break;
                                case 5:
                                    GlStateManager.blendFunc(770, 771);
                            }

                            Tessellator tessellator = Tessellator.getInstance();
                            BufferBuilder buffer = tessellator.getBuffer();

                            for(int j = 0; j < parts.size(); ++j) {
                                final Particle Particle = (Particle)parts.get(j);
                                if (Particle != null) {
                                    try {
                                        Particle.renderParticle(buffer, entity, frame, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
                                    } catch (Throwable exception) {
                                        CrashReport crashreport = CrashReport.forThrowable(exception, "Rendering Particle");
                                        CrashReportCategory crashreportcategory = crashreport.addCategory("Particle being rendered");
                                        crashreportcategory.setDetail("Particle", new ICrashReportDetail<String>() {
                                            public String call() {
                                                return Particle.toString();
                                            }
                                        });
                                        crashreportcategory.addDetail("Particle Type", new ICrashReportDetail<String>() {
                                            public String call() {
                                                return "ENTITY_PARTICLE_TEXTURE";
                                            }
                                        });
                                        throw new ReportedException(crashreport);
                                    }
                                }
                            }
                        }
                    }
                }

                RenderSystem.depthMask(true);
                RenderSystem.blendFunc(770, 771);
                RenderSystem.disableBlend();
                RenderSystem.blen(516, 0.1F);
                RenderSystem.
                GL11.glPopMatrix();
            }

        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        float frame = event.getPartialTicks();
        Entity entity = Minecraft.getMinecraft().player;
        TextureManager renderer = Minecraft.getMinecraft().renderEngine;
        int dim = Minecraft.getMinecraft().world.provider.getDimension();
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GL11.glEnable(3042);
        GL11.glAlphaFunc(516, 0.003921569F);
        renderer.bindTexture(particleTexture);
        GlStateManager.depthMask(false);

        for(int layer = 3; layer >= 0; --layer) {
            if (particles[layer].containsKey(dim)) {
                ArrayList<Particle> parts = (ArrayList)particles[layer].get(dim);
                if (parts.size() != 0) {
                    switch (layer) {
                        case 0:
                            GlStateManager.blendFunc(770, 1);
                            break;
                        case 1:
                            GlStateManager.blendFunc(770, 771);
                            break;
                        case 2:
                            GlStateManager.blendFunc(770, 1);
                            GlStateManager.disableDepth();
                            break;
                        case 3:
                            GlStateManager.blendFunc(770, 771);
                            GlStateManager.disableDepth();
                    }

                    float f1 = ActiveRenderInfo.getRotationX();
                    float f2 = ActiveRenderInfo.getRotationZ();
                    float f3 = ActiveRenderInfo.getRotationYZ();
                    float f4 = ActiveRenderInfo.getRotationXY();
                    float f5 = ActiveRenderInfo.getRotationXZ();
                    Particle.interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)frame;
                    Particle.interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)frame;
                    Particle.interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)frame;
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder buffer = tessellator.getBuffer();
                    buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

                    for(int j = 0; j < parts.size(); ++j) {
                        final Particle Particle = (Particle)parts.get(j);
                        if (Particle != null) {
                            try {
                                Particle.renderParticle(buffer, entity, frame, f1, f5, f2, f3, f4);
                            } catch (Throwable var19) {
                                Throwable throwable = var19;
                                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
                                CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
                                crashreportcategory.addDetail("Particle", new ICrashReportDetail<String>() {
                                    public String call() {
                                        return Particle.toString();
                                    }
                                });
                                crashreportcategory.addDetail("Particle Type", new ICrashReportDetail<String>() {
                                    public String call() {
                                        return "ENTITY_PARTICLE_TEXTURE";
                                    }
                                });
                                throw new ReportedException(crashreport);
                            }
                        }
                    }

                    tessellator.draw();
                    switch (layer) {
                        case 2:
                        case 3:
                            GlStateManager.enableDepth();
                    }
                }
            }
        }

        GlStateManager.depthMask(true);
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableBlend();
        GlStateManager.alphaFunc(516, 0.1F);
        GL11.glPopMatrix();
    }

    public static void addEffect(World world, Particle fx) {
        addEffect(world.provider.getDimension(), fx);
    }

    private static int getParticleLimit() {
        return FMLClientHandler.instance().getClient().gameSettings.particleSetting == 2 ? 500 : (FMLClientHandler.instance().getClient().gameSettings.particleSetting == 1 ? 1000 : 2000);
    }

    public static void addEffect(int dim, Particle fx) {
        if (Minecraft.getMinecraft().world != null) {
            int ps = FMLClientHandler.instance().getClient().gameSettings.particleSetting;
            Minecraft.getMinecraft();
            if (Minecraft.getDebugFPS() < 30) {
                ++ps;
            }

            if (Minecraft.getMinecraft().world.rand.nextInt(3) >= ps) {
                if (!particles[fx.getFXLayer()].containsKey(dim)) {
                    particles[fx.getFXLayer()].put(dim, new ArrayList());
                }

                ArrayList<Particle> parts = (ArrayList)particles[fx.getFXLayer()].get(dim);
                if (parts.size() >= getParticleLimit()) {
                    parts.remove(0);
                }

                parts.add(fx);
                particles[fx.getFXLayer()].put(dim, parts);
            }
        }
    }

    public static void addEffectWithDelay(World world, Particle fx, int delay) {
        particlesDelayed.add(new ParticleDelay(fx, world.provider.getDimension(), delay));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void updateParticles(TickEvent.ClientTickEvent event) {
        if (event.side != Side.SERVER) {
            Minecraft mc = FMLClientHandler.instance().getClient();
            World world = mc.world;
            if (mc.world != null) {
                int dim = world.provider.getDimension();
                if (event.phase == Phase.START) {
                    try {
                        Iterator<ParticleDelay> i = particlesDelayed.iterator();

                        while(i.hasNext()) {
                            ParticleDelay pd = (ParticleDelay)i.next();
                            --pd.delay;
                            if (pd.delay <= 0) {
                                if (pd.dim == dim) {
                                    addEffect(pd.dim, pd.particle);
                                }

                                i.remove();
                            }
                        }
                    } catch (Exception var13) {
                    }

                    for(int layer = 0; layer < 6; ++layer) {
                        if (particles[layer].containsKey(dim)) {
                            ArrayList<Particle> parts = (ArrayList)particles[layer].get(dim);

                            for(int j = 0; j < parts.size(); ++j) {
                                final Particle Particle = (Particle)parts.get(j);

                                try {
                                    if (Particle != null) {
                                        Particle.onUpdate();
                                    }
                                } catch (Exception var12) {
                                    Exception e = var12;

                                    try {
                                        CrashReport crashreport = CrashReport.makeCrashReport(e, "Ticking Particle");
                                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being ticked");
                                        crashreportcategory.addCrashSection("Particle", new Callable() {
                                            public String call() {
                                                return Particle.toString();
                                            }
                                        });
                                        crashreportcategory.addCrashSection("Particle Type", new Callable() {
                                            public String call() {
                                                return "ENTITY_PARTICLE_TEXTURE";
                                            }
                                        });
                                        Particle.setExpired();
                                    } catch (Exception var11) {
                                    }
                                }

                                if (Particle == null || !Particle.isAlive()) {
                                    parts.remove(j--);
                                    particles[layer].put(dim, parts);
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    private static class ParticleDelay {
        Particle particle;
        int dim;
        int level;
        int delay;

        public ParticleDelay(Particle particle, int dim, int delay) {
            this.dim = dim;
            this.particle = particle;
            this.delay = delay;
        }
    }
}
