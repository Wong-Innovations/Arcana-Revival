package com.wonginnovations.arcana.client.event;

import com.wonginnovations.arcana.client.render.particles.*;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ParticleFactoryEvent{
	@SubscribeEvent
	public static void onParticleFactoryRegister(final RegisterParticleProvidersEvent event) {
		// TODO: these could just be json based?
		event.registerSpecial(ArcanaParticles.NODE_PARTICLE.get(), new NodeParticle.Factory());
		event.registerSpecial(ArcanaParticles.ASPECT_PARTICLE.get(), new AspectParticle.Factory());
		event.registerSpecial(ArcanaParticles.NUMBER_PARTICLE.get(), new NumberParticle.Factory());
		event.registerSpecial(ArcanaParticles.HUNGRY_NODE_BLOCK_PARTICLE.get(), new HungryNodeBlockParticle.Factory());
		event.registerSpecial(ArcanaParticles.HUNGRY_NODE_DISC_PARTICLE.get(), new HungryNodeDiscParticle.Factory());

		event.registerSpriteSet(ArcanaParticles.ASPECT_HELIX_PARTICLE.get(), AspectHelixParticle.Factory::new);
	}
}