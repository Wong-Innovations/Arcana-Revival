package com.wonginnovations.arcana;

import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.client.ClientUtils;
import com.wonginnovations.arcana.client.event.*;
import com.wonginnovations.arcana.client.gui.*;
import com.wonginnovations.arcana.client.model.WandModel;
import com.wonginnovations.arcana.client.render.particles.ArcanaParticles;
import com.wonginnovations.arcana.client.research.BackgroundLayerRenderers;
import com.wonginnovations.arcana.client.research.EntrySectionRenderer;
import com.wonginnovations.arcana.client.research.PuzzleRenderer;
import com.wonginnovations.arcana.client.research.RequirementRenderer;
import com.wonginnovations.arcana.containers.ArcanaMenus;
import com.wonginnovations.arcana.event.ResearchEvent;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.items.attachment.FocusItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

/**
 * Handles client-side only things (that would otherwise crash dedicated servers).
 */
public class ClientProxy extends CommonProxy {

	// TODO: make an enum for keybinds somewhere
	public static KeyMapping SWAP_FOCUS_BINDING = new KeyMapping("key.arcana.swap_focus", GLFW.GLFW_KEY_G, "key.categories.mod.arcana");
	
	public void construct() {
		super.construct();
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ArcanaConfig.CLIENT_SPEC);
		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
				() -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ConfigScreen(screen)));
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(TextureStitchHandler::onTextureStitch);
		bus.addListener(BakeEventHandler::onModelBake);
		bus.addListener(ParticleFactoryEvent::onParticleFactoryRegister);
		bus.addListener((ModelEvent.RegisterGeometryLoaders event) -> event.register("wand_loader", new WandModel.Loader()));
		
		MinecraftForge.EVENT_BUS.addListener(RenderTooltipHandler::makeTooltip);
		MinecraftForge.EVENT_BUS.addListener(FogHandler::setFogColor);
//		MinecraftForge.EVENT_BUS.addListener(FogHandler::setFogDensity);
		MinecraftForge.EVENT_BUS.addListener(FogHandler::setFogLength);
		MinecraftForge.EVENT_BUS.addListener(InitScreenHandler::onInitGuiEvent);
		MinecraftForge.EVENT_BUS.register(ClientTickHandler.class);
		MinecraftForge.EVENT_BUS.register(ParticleFactoryEvent.class);
		
		ArcanaParticles.PARTICLE_TYPES.register(bus);
	}

	public void preInit(FMLCommonSetupEvent event) {
		super.preInit(event);
		setRenderLayers();
		registerScreens();
		EntrySectionRenderer.init();
		RequirementRenderer.init();
		PuzzleRenderer.init();
		BackgroundLayerRenderers.init();
		
		// there's an event for this, but putting it here seems to affect literally nothing. huh.
		// I'm not at all surprised.
		
		ItemProperties.register(ArcanaItems.PHIAL.get(), new ResourceLocation("aspect"), (itemStack, world, livingEntity, seed) -> {
			AspectHandler vis = AspectHandler.getFrom(itemStack);
			if (vis == null)
				return -1;
			if (vis.countHolders() == 0)
				return -1;
			if (vis.getHolder(0) == null)
				return -1;
			return vis.getHolder(0).getStack().getAspect().getId() - 1;
		});
		ItemProperties.register(ArcanaItems.ARCANUM.get(), new ResourceLocation("open"), (itemStack, world, livingEntity, seed) -> {
			if (!itemStack.getOrCreateTag().contains("open"))
				return 0;
			if (itemStack.getOrCreateTag().getBoolean("open"))
				return 1;
			return 0;
		});
		
		Minecraft inst = Minecraft.getInstance();
		
		inst.getBlockColors().register((state, access, pos, index) ->
						access != null && pos != null ? BiomeColors.getAverageWaterColor(access, pos) : -1,
				ArcanaBlocks.CRUCIBLE.get()
		);
		
		inst.getItemColors().register((stack, layer) ->
						layer == 1 ? FocusItem.getColorAspect(stack) : 0xffffffff,
				ArcanaItems.DEFAULT_FOCUS::get
		);
		
		inst.getBlockColors().register((state, access, pos, index) ->
						access != null && pos != null ? BiomeColors.getAverageFoliageColor(access, pos) : -1,
				ArcanaBlocks.GREATWOOD_LEAVES.get(),
				ArcanaBlocks.WILLOW_LEAVES.get(),
				ArcanaBlocks.EUCALYPTUS_LEAVES.get(),
				ArcanaBlocks.DAIR_LEAVES.get()
		);
		
		inst.getItemColors().register((stack, layer) ->
						0x529c34,
				ArcanaBlocks.GREATWOOD_LEAVES.get(),
				ArcanaBlocks.WILLOW_LEAVES.get(),
				ArcanaBlocks.EUCALYPTUS_LEAVES.get(),
				ArcanaBlocks.DAIR_LEAVES.get()
		);

	}
	
	@SubscribeEvent
	// can't be private
	public static void fireResearchChange(ResearchEvent even) {
		ClientUtils.onResearchChange(even);
	}
	
	public Player getPlayerOnClient() {
		return Minecraft.getInstance().player;
	}
	
	public Level getLevelOnClient() {
		return Minecraft.getInstance().level;
	}
	
	public void scheduleOnClient(Runnable runnable) {
		Minecraft.getInstance().doRunTask(runnable);
	}
	
	public ItemStack getAspectItemStackForDisplay() {
		if (Minecraft.getInstance().player == null)
			return super.getAspectItemStackForDisplay();
		else
			return AspectUtils.getItemStackForAspect((Minecraft.getInstance().player.tickCount / 20) % AspectUtils.aspectItems.size());
	}
	
	protected void registerScreens() {
		//Screens
		MenuScreens.register(ArcanaMenus.FOCI_FORGE.get(), FociForgeScreen::new);
		MenuScreens.register(ArcanaMenus.RESEARCH_TABLE.get(), ResearchTableScreen::new);
		MenuScreens.register(ArcanaMenus.ARCANE_CRAFTING_TABLE.get(), ArcaneCraftingTableScreen::new);
		MenuScreens.register(ArcanaMenus.ASPECT_CRYSTALLIZER.get(), AspectCrystallizerScreen::new);
		MenuScreens.register(ArcanaMenus.ALEMBIC.get(), AlembicScreen::new);
		MenuScreens.register(ArcanaMenus.PUMP.get(), PumpScreen::new);
	}
	
	protected void setRenderLayers() {
		//Render Layers for Blocks
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.WARDENED_BLOCK.get(), RenderType.translucent());

		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.ARCANE_CRAFTING_TABLE.get(), RenderType.cutout());
		
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.JAR.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.SECURE_JAR.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.VOID_JAR.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.VACUUM_JAR.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.PRESSURE_JAR.get(), RenderType.translucent());
		
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.RESEARCH_TABLE.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.RESEARCH_TABLE_COMPONENT.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.ASPECT_WINDOW.get(), RenderType.translucent());
		
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.HARDENED_GLASS.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.SMOKEY_GLASS.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.LUMINIFEROUS_GLASS.get(), RenderType.translucent());
		
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.DEAD_FLOWER.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.DEAD_GRASS.get(), RenderType.cutout());
		
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_GRASS.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_FLOWER.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_MUSHROOM.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_VINE.get(), RenderType.cutout());
		
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.SILVERWOOD_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.DAIR_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.EUCALYPTUS_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.GREATWOOD_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.HAWTHORN_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.WILLOW_SAPLING.get(), RenderType.cutout());
		
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.MAGIC_MUSHROOM.get(), RenderType.cutout());
		
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_DAIR_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_ACACIA_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_BIRCH_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_DARKOAK_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_EUCALYPTUS_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_GREATWOOD_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_HAWTHORN_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_JUNGLE_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_OAK_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_SPRUCE_SAPLING.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_WILLOW_SAPLING.get(), RenderType.cutout());
		
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.DAIR_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.EUCALYPTUS_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.GREATWOOD_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.SILVERWOOD_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.HAWTHORN_DOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.WILLOW_DOOR.get(), RenderType.cutout());
		
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.DAIR_TRAPDOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.SILVERWOOD_TRAPDOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.EUCALYPTUS_TRAPDOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.GREATWOOD_TRAPDOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.HAWTHORN_TRAPDOOR.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.WILLOW_TRAPDOOR.get(), RenderType.cutout());
		
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_DAIR_LEAVES.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_ACACIA_LEAVES.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_BIRCH_LEAVES.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_DARKOAK_LEAVES.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_EUCALYPTUS_LEAVES.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_GREATWOOD_LEAVES.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_HAWTHORN_LEAVES.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_JUNGLE_LEAVES.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_OAK_LEAVES.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_SPRUCE_LEAVES.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINTED_WILLOW_LEAVES.get(), RenderType.cutout());
		
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.TAINT_FLUID_BLOCK.get(), RenderType.translucent());
		
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.FIRE_CLUSTER.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.WATER_CLUSTER.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.AIR_CLUSTER.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.EARTH_CLUSTER.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.ORDER_CLUSTER.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.CHAOS_CLUSTER.get(), RenderType.cutout());

//		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.FIRE_CRYSTAL_FRAGMENTS.get(), RenderType.cutout());
//		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.WATER_CRYSTAL_FRAGMENTS.get(), RenderType.cutout());
//		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.AIR_CRYSTAL_FRAGMENTS.get(), RenderType.cutout());
//		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.EARTH_CRYSTAL_FRAGMENTS.get(), RenderType.cutout());
//		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.ORDER_CRYSTAL_FRAGMENTS.get(), RenderType.cutout());
//		ItemBlockRenderTypes.setRenderLayer(ArcanaBlocks.CHAOS_CRYSTAL_FRAGMENTS.get(), RenderType.cutout());
	}
}