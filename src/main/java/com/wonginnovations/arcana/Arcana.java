package com.wonginnovations.arcana;

import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.aspects.ItemAspectRegistry;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.aspects.handlers.AspectHandlerCapability;
import com.wonginnovations.arcana.aspects.handlers.AspectHolder;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.blocks.entities.ArcanaBlockEntities;
import com.wonginnovations.arcana.capabilities.ResearcherCapability;
import com.wonginnovations.arcana.commands.ArcanaArguementTypes;
import com.wonginnovations.arcana.containers.ArcanaMenus;
import com.wonginnovations.arcana.effects.ArcanaEffects;
import com.wonginnovations.arcana.entities.ArcanaEntities;
import com.wonginnovations.arcana.event.WorldLoadEvent;
import com.wonginnovations.arcana.fluids.ArcanaFluids;
import com.wonginnovations.arcana.init.ArcanaCreativeTabs;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.items.WandItem;
import com.wonginnovations.arcana.items.recipes.ArcanaRecipes;
import com.wonginnovations.arcana.network.Connection;
import com.wonginnovations.arcana.systems.research.*;
import com.wonginnovations.arcana.systems.taint.Taint;
import com.wonginnovations.arcana.util.AuthorisationManager;
import com.wonginnovations.arcana.world.NodeType;
import com.wonginnovations.arcana.world.WorldInteractionsRegistry;
import com.wonginnovations.arcana.worldgen.ArcanaFeatures;
import net.minecraft.core.Position;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

import static net.minecraft.world.level.block.RotatedPillarBlock.AXIS;

/**
 * Base Arcana Class
 */
@Mod(Arcana.MODID)
public class Arcana {
	
	// Main
	public static final String MODID = "arcana";
	public static final Logger LOGGER = LogManager.getLogger("Arcana");
	public static Arcana instance;
	public static AuthorisationManager authManager;
	
	// Json Registry
	public static ResearchLoader researchManager;
	public static ItemAspectRegistry itemAspectRegistry;
	public static WorldInteractionsRegistry levelInteractionsRegistry;
	
	// Item Groups
//	public static SupplierItemGroup ITEMS = new SupplierItemGroup(MODID, () -> new ItemStack(ArcanaBlocks.ARCANE_STONE.get()))
//			.setHasSearchBar(true)
//			.setBackgroundImage(new ResourceLocation("textures/gui/container/creative_inventory/tab_item_search.png"));
//	public static SupplierItemGroup TAINT = new SupplierItemGroup("taint", () -> new ItemStack(ArcanaBlocks.TAINTED_GRASS_BLOCK.get()));
	
	// Proxy
	public static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	
	// Debug Mode
	public static final boolean debug = false;
	
	public Arcana() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::setup);
		bus.addListener(this::enqueueIMC);
		bus.addListener(this::processIMC);
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ArcanaConfig.COMMON_SPEC);
		
		// deferred registry registration
		NodeType.init();
		Aspects.init();
		
		ArcanaBlocks.register(bus);
		ArcanaEntities.ENTITY_TYPES.register(bus);
		ArcanaEntities.T_ENTITY_TYPES.register(bus);
		ArcanaItems.ITEMS.register(bus);
		ArcanaEffects.MOB_EFFECTS.register(bus);
		ArcanaRecipes.Serializers.SERIALIZERS.register(bus);
		ArcanaRecipes.Types.TYPES.register(bus);
		ArcanaBlockEntities.BLOCK_ENTITIES.register(bus);
		ArcanaMenus.MENUS.register(bus);
		ArcanaFeatures.FEATURES.register(bus);
		ArcanaFeatures.FOLAIGE_PLACERS.register(bus);
		ArcanaFeatures.TRUNK_PLACERS.register(bus);
//		ArcanaBiomes.BIOMES.register(bus);
		ArcanaFluids.FLUIDS.register(bus);
		ArcanaFluids.TYPES.register(bus);
		ArcanaSounds.SOUND_EVENTS.register(bus);
		ArcanaCreativeTabs.register(bus);
		ArcanaArguementTypes.ARGUMENT_TYPES.register(bus);

		MinecraftForge.EVENT_BUS.addListener(WorldLoadEvent::serverAboutToStart);
		MinecraftForge.EVENT_BUS.addListener(this::toolInteractionEvent);
		
		proxy.construct();
	}

	public void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(AspectHandlerCapability.class);
		event.register(ResearcherCapability.class);
	}
	
	public void toolInteractionEvent(BlockEvent.BlockToolModificationEvent event) {
		if (event.getToolAction() == ToolActions.AXE_DIG) {
			if (event.getState().getBlock()==ArcanaBlocks.SILVERWOOD_LOG.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_SILVERWOOD_LOG.get().defaultBlockState().setValue(AXIS, event.getState().getValue(AXIS)));
			if (event.getState().getBlock()==ArcanaBlocks.DAIR_LOG.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_DAIR_LOG.get().defaultBlockState().setValue(AXIS, event.getState().getValue(AXIS)));
			if (event.getState().getBlock()==ArcanaBlocks.GREATWOOD_LOG.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_GREATWOOD_LOG.get().defaultBlockState().setValue(AXIS, event.getState().getValue(AXIS)));
			if (event.getState().getBlock()==ArcanaBlocks.EUCALYPTUS_LOG.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_EUCALYPTUS_LOG.get().defaultBlockState().setValue(AXIS, event.getState().getValue(AXIS)));
			if (event.getState().getBlock()==ArcanaBlocks.HAWTHORN_LOG.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_HAWTHORN_LOG.get().defaultBlockState().setValue(AXIS, event.getState().getValue(AXIS)));
			if (event.getState().getBlock()==ArcanaBlocks.WILLOW_LOG.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_WILLOW_LOG.get().defaultBlockState().setValue(AXIS, event.getState().getValue(AXIS)));
			
//			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_DAIR_LOG.get())
//				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_DAIR_LOG.get().defaultBlockState().setValue(AXIS, event.getState().getValue(AXIS)));
//			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_GREATWOOD_LOG.get())
//				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_GREATWOOD_LOG.get().defaultBlockState().setValue(AXIS, event.getState().getValue(AXIS)));
//			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_EUCALYPTUS_LOG.get())
//				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_EUCALYPTUS_LOG.get().defaultBlockState().setValue(AXIS, event.getState().getValue(AXIS)));
//			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_HAWTHORN_LOG.get())
//				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_HAWTHORN_LOG.get().defaultBlockState().setValue(AXIS, event.getState().getValue(AXIS)));
//			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_WILLOW_LOG.get())
//				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_WILLOW_LOG.get().defaultBlockState().setValue(AXIS, event.getState().getValue(AXIS)));
			
			if (event.getState().getBlock()==ArcanaBlocks.SILVERWOOD_WOOD.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_SILVERWOOD_WOOD.get().defaultBlockState());
			if (event.getState().getBlock()==ArcanaBlocks.DAIR_WOOD.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_DAIR_WOOD.get().defaultBlockState());
			if (event.getState().getBlock()==ArcanaBlocks.GREATWOOD_WOOD.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_GREATWOOD_WOOD.get().defaultBlockState());
			if (event.getState().getBlock()==ArcanaBlocks.EUCALYPTUS_WOOD.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_EUCALYPTUS_WOOD.get().defaultBlockState());
			if (event.getState().getBlock()==ArcanaBlocks.HAWTHORN_WOOD.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_HAWTHORN_WOOD.get().defaultBlockState());
			if (event.getState().getBlock()==ArcanaBlocks.WILLOW_WOOD.get())
				event.setFinalState(ArcanaBlocks.STRIPPED_WILLOW_WOOD.get().defaultBlockState());
			
//			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_DAIR_WOOD.get())
//				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_DAIR_WOOD.get().defaultBlockState());
//			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_GREATWOOD_WOOD.get())
//				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_GREATWOOD_WOOD.get().defaultBlockState());
//			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_EUCALYPTUS_WOOD.get())
//				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_EUCALYPTUS_WOOD.get().defaultBlockState());
//			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_HAWTHORN_WOOD.get())
//				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_HAWTHORN_WOOD.get().defaultBlockState());
//			if (event.getState().getBlock()==ArcanaBlocks.TAINTED_WILLOW_WOOD.get())
//				event.setFinalState(ArcanaBlocks.TAINTED_STRIPPED_WILLOW_WOOD.get().defaultBlockState());
		}
	}
	
	public static ResourceLocation arcLoc(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	private void setup(FMLCommonSetupEvent event) {
		authManager = new AuthorisationManager();
		
		// init, init, init, init, init, init, init, init
		EntrySection.init();
		Requirement.init();
		Puzzle.init();
		Taint.init();
		BackgroundLayer.init();
		StartupMessageManager.addModMessage("Arcana: Research registration completed");
		
		proxy.preInit(event);
		
		Connection.init();
		
		// dispenser behaviour for wand conversion
		DispenserBlock.registerBehavior(ArcanaItems.WAND.get(), new OptionalDispenseItemBehavior() {
			@Nonnull
			protected ItemStack execute(@Nonnull BlockSource source, @Nonnull ItemStack stack) {
				Level level = source.getLevel();
				BlockPos blockpos = source.getPos().offset(source.getBlockState().getValue(DispenserBlock.FACING).getNormal());
				InteractionResult convert = WandItem.convert(level, blockpos, null);
				if (convert.consumesAction()) {
					//successful = true;
					return stack;
				} else
					return super.execute(source, stack);
			}
		});
		// and phial usage
		// TODO: replace this all with standard vis transfer code.
		// bleh
		DispenserBlock.registerBehavior(ArcanaItems.PHIAL.get(), new OptionalDispenseItemBehavior() {
			// copypasta from PhialItem
			// needs some reworking for readability...
			@Nonnull
			protected ItemStack dispenseStack(@Nonnull BlockSource source, @Nonnull ItemStack stack) {
				Level level = source.getLevel();
				BlockPos pos = source.getPos().offset(source.getBlockState().getValue(DispenserBlock.FACING).getNormal());
				BlockState state = level.getBlockState(pos);
				BlockEntity tile = level.getBlockEntity(pos);
				DispenserBlockEntity dispenser = source.getEntity();
				if (tile != null) {
					LazyOptional<AspectHandler> cap = tile.getCapability(AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY);
					if (cap.isPresent()) {
						//noinspection ConstantConditions
						AspectHandler tileHandle = cap.orElse(null);
						AspectHolder myHandle = AspectHandler.getFrom(stack).getHolder(0);
						if (myHandle.getStack().getAmount() <= 0) {
							for (AspectHolder holder : tileHandle.getHolders())
								if (holder.getStack().getAmount() > 0) {
									float min = Math.min(holder.getStack().getAmount(), 8);
									Aspect aspect = holder.getStack().getAspect();
									ItemStack cappedItemStack = new ItemStack(ArcanaItems.PHIAL.get());
									AspectHandler.getFrom(cappedItemStack).insert(aspect, min);//.insert(0, new AspectStack(aspect, min), false);
									if (cappedItemStack.getTag() == null)
										cappedItemStack.setTag(cappedItemStack.getShareTag());
									stack.shrink(1);
									if (!stack.isEmpty())
										if (dispenser.addItem(stack) == -1) {
											Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
											Position position = DispenserBlock.getDispensePosition(source);
											spawnItem(source.getLevel(), stack, 6, direction, position);
										}
									holder.drain(min, false);
									//successful = true;
									level.sendBlockUpdated(pos, state, state, 2);
									return cappedItemStack;
								}
						} else {
							for (AspectHolder holder : tileHandle.getHolders())
								if ((holder.getCapacity() - holder.getStack().getAmount() > 0 || holder.voids()) && (holder.getStack().getAspect() == myHandle.getStack().getAspect() || holder.getStack().getAspect() == Aspects.EMPTY)) {
									float inserted = holder.insert(myHandle.getStack().getAmount(), false);
									if (inserted != 0) {
										ItemStack newPhial = new ItemStack(ArcanaItems.PHIAL.get(), 1);
										AspectHolder oldHolder = AspectHandler.getFrom(stack).getHolder(0);
										AspectHolder newHolder = AspectHandler.getFrom(newPhial).getHolder(0);
										newHolder.insert(inserted, false);
										newPhial.setTag(newPhial.getShareTag());
										level.sendBlockUpdated(pos, state, state, 2);
										return newPhial;
									} else {
										level.sendBlockUpdated(pos, state, state, 2);
										stack.shrink(1);
										if (!stack.isEmpty())
											if (dispenser.addItem(stack) == -1) {
												Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
												Position position = DispenserBlock.getDispensePosition(source);
												spawnItem(source.getLevel(), stack, 6, direction, position);
											}
										return new ItemStack(ArcanaItems.PHIAL.get());
									}
								}
						}
					}
				}
				return super.execute(source, stack);
			}
		});
		
		//FeatureGenerator.setupFeatureGeneration();
	}
	
	private void enqueueIMC(InterModEnqueueEvent event) {
		// tell curios or whatever about our baubles
	}
	
	private void processIMC(InterModProcessEvent event) {
		// handle aspect registration from addons?
	}
}