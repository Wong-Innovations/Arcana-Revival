package com.wonginnovations.arcana.client;

import com.wonginnovations.arcana.client.fx.ModParticles;
import com.wonginnovations.arcana.client.fx.MyRenderType;
import com.wonginnovations.arcana.client.fx.particles.SparkleParticle;
import com.wonginnovations.arcana.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    public ClientProxy() {
        super();
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SPARKLE.get(), SparkleParticle.Provider::new);
    }

    @SubscribeEvent
    public static void renderParticlesEvent(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.level == null) return;
            Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(MyRenderType.PARTICLE);
        }
    }

}
