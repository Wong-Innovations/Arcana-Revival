package com.wonginnovations.arcana.client;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.blocks.entities.ArcanaBlockEntities;
import com.wonginnovations.arcana.client.model.tainted.TaintedFoxModel;
import com.wonginnovations.arcana.client.model.tainted.TaintedWolfModel;
import com.wonginnovations.arcana.client.model.tainted.TaintedZombieModel;
import com.wonginnovations.arcana.client.render.*;
import com.wonginnovations.arcana.client.render.entities.*;
import com.wonginnovations.arcana.client.render.tainted.TaintedCaveSpiderRender;
import com.wonginnovations.arcana.client.render.tainted.TaintedEntityRender;
import com.wonginnovations.arcana.client.render.tainted.TaintedSlimeRender;
import com.wonginnovations.arcana.client.render.tainted.TaintedSquidRenderer;
import com.wonginnovations.arcana.entities.ArcanaEntities;
import com.wonginnovations.arcana.entities.tainted.TaintedEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Arcana.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RendererRegisterEvents {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // BERs
        event.registerBlockEntityRenderer(ArcanaBlockEntities.RESEARCH_TABLE.get(), ResearchTableBlockEntityRender::new);
        event.registerBlockEntityRenderer(ArcanaBlockEntities.JAR.get(), JarBlockEntityRender::new);
        event.registerBlockEntityRenderer(ArcanaBlockEntities.ASPECT_SHELF.get(), AspectBookshelfBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ArcanaBlockEntities.VACUUM.get(), VacuumBlockEntityRender::new);
        event.registerBlockEntityRenderer(ArcanaBlockEntities.PEDESTAL.get(), PedestalBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ArcanaBlockEntities.ASPECT_VALVE.get(), AspectValveBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ArcanaBlockEntities.ASPECT_WINDOW.get(), PipeBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ArcanaBlockEntities.ASPECT_PUMP.get(), PipeBlockEntityRenderer::new);

        // Entities
        event.registerEntityRenderer(ArcanaEntities.KOALA_ENTITY.get(), KoalaEntityRenderer::new);
        event.registerEntityRenderer(ArcanaEntities.DAIR_SPIRIT.get(), DairSpiritRenderer::new);
        event.registerEntityRenderer(ArcanaEntities.WILLOW_SPIRIT.get(), WillowSpiritRenderer::new);
        event.registerEntityRenderer(ArcanaEntities.SPELL_CLOUD.get(), SpellCloudEntityRenderer::new);
        event.registerEntityRenderer(ArcanaEntities.BLAST_EMITTER.get(), BlastEmitterEntityRenderer::new);

        event.registerEntityRenderer(ArcanaEntities.SPELL_EGG.get(), SpellEggEntityRenderer::new);
        event.registerEntityRenderer(ArcanaEntities.TAINT_BOTTLE.get(), TaintBottleEntityRenderer::new);
        event.registerEntityRenderer(ArcanaEntities.TAINTED_CAVE_SPIDER.get(), TaintedCaveSpiderRender::new);
        event.registerEntityRenderer(ArcanaEntities.TAINTED_DONKEY.get(), manager -> new TaintedEntityRender(manager, new HorseModel<>(manager.bakeLayer(ModelLayers.HORSE))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_FOX.get(), manager -> new TaintedEntityRender(manager, new TaintedFoxModel(TaintedFoxModel.createBodyLayer().bakeRoot())));
        //event.registerEntityRenderer(ArcanaEntities.TAINTED_ILLUSIONER.get(), manager -> new TaintedEntityRender(manager, new IllagerModel(manager.bakeLayer(ModelLayers.ILLAGER))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_PANDA.get(), manager -> new TaintedEntityRender(manager, new PandaModel(manager.bakeLayer(ModelLayers.PANDA))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_POLAR_BEAR.get(), manager -> new TaintedEntityRender(manager, new PolarBearModel(manager.bakeLayer(ModelLayers.POLAR_BEAR))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_RABBIT.get(), manager -> new TaintedEntityRender(manager, new RabbitModel(manager.bakeLayer(ModelLayers.RABBIT))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_SKELETON.get(), manager -> new TaintedEntityRender(manager, new SkeletonModel(manager.bakeLayer(ModelLayers.SKELETON))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_SLIME.get(), TaintedSlimeRender::new);
        event.registerEntityRenderer(ArcanaEntities.TAINTED_SNOW_GOLEM.get(), manager -> new TaintedEntityRender(manager, new SnowGolemModel(manager.bakeLayer(ModelLayers.SNOW_GOLEM))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_WOLF.get(), manager -> new TaintedEntityRender(manager, new TaintedWolfModel(TaintedWolfModel.createBodyLayer().bakeRoot())));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_ZOMBIE.get(), manager -> new TaintedEntityRender(manager, new TaintedZombieModel(manager.bakeLayer(ModelLayers.ZOMBIE))));

        event.registerEntityRenderer(ArcanaEntities.TAINTED_COW.get(), manager -> new TaintedEntityRender(manager, new CowModel<TaintedEntity>(manager.bakeLayer(ModelLayers.COW))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_PIG.get(), manager -> new TaintedEntityRender(manager, new PigModel<TaintedEntity>(manager.bakeLayer(ModelLayers.PIG))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_SPIDER.get(), manager -> new TaintedEntityRender(manager, new SpiderModel<TaintedEntity>(manager.bakeLayer(ModelLayers.SPIDER))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_BLAZE.get(), manager -> new TaintedEntityRender(manager, new BlazeModel<TaintedEntity>(manager.bakeLayer(ModelLayers.BLAZE))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_CHICKEN.get(), manager -> new TaintedEntityRender(manager, new ChickenModel<TaintedEntity>(manager.bakeLayer(ModelLayers.CHICKEN))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_MOOSHROOM.get(), manager -> new TaintedEntityRender(manager, new CowModel<TaintedEntity>(manager.bakeLayer(ModelLayers.COW)))); // No tainted_warts on top
        event.registerEntityRenderer(ArcanaEntities.TAINTED_OCELOT.get(), manager -> new TaintedEntityRender(manager, new OcelotModel<TaintedEntity>(manager.bakeLayer(ModelLayers.OCELOT))));
        //event.registerEntityRenderer(ArcanaEntities.TAINTED_SALMON.get(), manager -> new TaintedEntityRender(manager, new SalmonModel<TaintedEntity>(manager.bakeLayer(ModelLayers.SALMON))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_SQUID.get(), TaintedSquidRenderer::new);
        event.registerEntityRenderer(ArcanaEntities.TAINTED_VILLAGER.get(), manager -> new TaintedEntityRender(manager, new VillagerModel<TaintedEntity>(manager.bakeLayer(ModelLayers.VILLAGER))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_WANDERING_TRADER.get(), manager -> new TaintedEntityRender(manager, new VillagerModel<TaintedEntity>(manager.bakeLayer(ModelLayers.VILLAGER))));
        event.registerEntityRenderer(ArcanaEntities.TAINTED_WITCH.get(), manager -> new TaintedEntityRender(manager, new WitchModel<TaintedEntity>(manager.bakeLayer(ModelLayers.WITCH))));
    }

}
