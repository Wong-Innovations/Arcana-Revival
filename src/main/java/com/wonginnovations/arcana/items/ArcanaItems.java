package com.wonginnovations.arcana.items;

import com.wonginnovations.arcana.ArcanaSounds;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.effects.ArcanaEffects;
import com.wonginnovations.arcana.fluids.ArcanaFluids;
import com.wonginnovations.arcana.items.armor.ArcanaArmorMaterials;
import com.wonginnovations.arcana.items.armor.AutoRepairArmorItem;
import com.wonginnovations.arcana.items.armor.GogglesItem;
import com.wonginnovations.arcana.items.attachment.CapItem;
import com.wonginnovations.arcana.items.attachment.Core;
import com.wonginnovations.arcana.items.attachment.CoreItem;
import com.wonginnovations.arcana.items.attachment.FocusItem;
import com.wonginnovations.arcana.items.settings.GogglePriority;
import com.wonginnovations.arcana.items.tools.*;
import com.wonginnovations.arcana.systems.spell.MDModifier;
import com.wonginnovations.arcana.util.annotations.GIM;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.wonginnovations.arcana.Arcana.MODID;
import static com.wonginnovations.arcana.Arcana.arcLoc;
import static com.wonginnovations.arcana.util.annotations.GIM.Type.ITEM;

/**
 * Contains all items.
 *
 * @author Merijn, Tea
 * @see ArcanaItems
 */
@SuppressWarnings({"unused", "RedundantSuppression"})
public class ArcanaItems {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public static final Map<RegistryObject<Block>, RegistryObject<Item>> BLOCK_ITEMS = new HashMap<>();
	public static final Map<Aspect, RegistryObject<Item>> CRYSTAL_ITEMS = new HashMap<>();

	//Music discs
	public static final RegistryObject<Item> MUSIC_DISC_MAIN = ITEMS.register("music_disc_main", () -> new RecordItem(1, ArcanaSounds.music_arcana_theme, new Properties().stacksTo(1).rarity(Rarity.RARE), 3380));
	public static final RegistryObject<Item> MUSIC_DISC_GREEN_SLEEVES = ITEMS.register("music_disc_green_sleeves", () -> new RecordItem(1, ArcanaSounds.music_arcana_green_sleeves, new Properties().stacksTo(1).rarity(Rarity.RARE), 1940));

	// Arcanium
	public static final RegistryObject<Item> ARCANIUM_SWORD = ITEMS.register("arcanium_sword", () -> new SwordItem(ArcanaToolTiers.ARCANIUM, 3, -2.4f, new Properties()));
	public static final RegistryObject<Item> ARCANIUM_SHOVEL = ITEMS.register("arcanium_shovel", () -> new ShovelItem(ArcanaToolTiers.ARCANIUM, 1.5f, -3, new Properties()));
	public static final RegistryObject<Item> ARCANIUM_PICKAXE = ITEMS.register("arcanium_pickaxe", () -> new PickaxeItem(ArcanaToolTiers.ARCANIUM, 1, -2.8f, new Properties()));
	public static final RegistryObject<Item> ARCANIUM_AXE = ITEMS.register("arcanium_axe", () -> new AxeItem(ArcanaToolTiers.ARCANIUM, 5.5f, -3, new Properties()));
	public static final RegistryObject<Item> ARCANIUM_HOE = ITEMS.register("arcanium_hoe", () -> new HoeItem(ArcanaToolTiers.ARCANIUM, 1, -.5f, new Properties()));
	
	public static final RegistryObject<Item> ARCANIUM_HELMET = ITEMS.register("arcanium_helmet", () -> new ArmorItem(ArcanaArmorMaterials.ARCANIUM, ArmorItem.Type.HELMET, new Properties()));
	public static final RegistryObject<Item> ARCANIUM_CHESTPLATE = ITEMS.register("arcanium_chestplate", () -> new ArmorItem(ArcanaArmorMaterials.ARCANIUM, ArmorItem.Type.CHESTPLATE, new Properties()));
	public static final RegistryObject<Item> ARCANIUM_LEGGINGS = ITEMS.register("arcanium_leggings", () -> new ArmorItem(ArcanaArmorMaterials.ARCANIUM, ArmorItem.Type.LEGGINGS, new Properties()));
	public static final RegistryObject<Item> ARCANIUM_BOOTS = ITEMS.register("arcanium_boots", () -> new ArmorItem(ArcanaArmorMaterials.ARCANIUM, ArmorItem.Type.BOOTS, new Properties()));
	
	// Silver Tools
	public static final RegistryObject<Item> SILVER_SWORD = ITEMS.register("silver_sword", () -> new SilverSwordItem(ArcanaToolTiers.SILVER, 3, -2.4f, new Properties()));
	public static final RegistryObject<Item> SILVER_SHOVEL = ITEMS.register("silver_shovel", () -> new SilverShovelItem(ArcanaToolTiers.SILVER, 1.5f, -3, new Properties()));
	public static final RegistryObject<Item> SILVER_PICKAXE = ITEMS.register("silver_pickaxe", () -> new SilverPickaxeItem(ArcanaToolTiers.SILVER, 1, -2.8f, new Properties()));
	public static final RegistryObject<Item> SILVER_AXE = ITEMS.register("silver_axe", () -> new SilverAxeItem(ArcanaToolTiers.SILVER, 5.5f, -3, new Properties()));
	public static final RegistryObject<Item> SILVER_HOE = ITEMS.register("silver_hoe", () -> new SilverHoeItem(ArcanaToolTiers.SILVER, -.5f, new Properties()));
	
	// Void Metal
	public static final RegistryObject<Item> VOID_METAL_SWORD = ITEMS.register("void_metal_sword", () -> new AutoRepairSwordItem(ArcanaToolTiers.VOID_METAL, 3, -2.4f, new Properties()));
	public static final RegistryObject<Item> VOID_METAL_SHOVEL = ITEMS.register("void_metal_shovel", () -> new AutoRepairShovelItem(ArcanaToolTiers.VOID_METAL, 1.5f, -3, new Properties()));
	public static final RegistryObject<Item> VOID_METAL_PICKAXE = ITEMS.register("void_metal_pickaxe", () -> new AutoRepairPickaxeItem(ArcanaToolTiers.VOID_METAL, 1, -2.8f, new Properties()));
	public static final RegistryObject<Item> VOID_METAL_AXE = ITEMS.register("void_metal_axe", () -> new AutoRepairAxeItem(ArcanaToolTiers.VOID_METAL, 5.5f, -3, new Properties()));
	public static final RegistryObject<Item> VOID_METAL_HOE = ITEMS.register("void_metal_hoe", () -> new AutoRepairHoeItem(ArcanaToolTiers.VOID_METAL, -.5f, new Properties()));
	
	public static final RegistryObject<Item> VOID_METAL_HELMET = ITEMS.register("void_metal_helmet", () -> new AutoRepairArmorItem(ArcanaArmorMaterials.VOID_METAL, ArmorItem.Type.HELMET, new Properties()));
	public static final RegistryObject<Item> VOID_METAL_CHESTPLATE = ITEMS.register("void_metal_chestplate", () -> new AutoRepairArmorItem(ArcanaArmorMaterials.VOID_METAL, ArmorItem.Type.CHESTPLATE, new Properties()));
	public static final RegistryObject<Item> VOID_METAL_LEGGINGS = ITEMS.register("void_metal_leggings", () -> new AutoRepairArmorItem(ArcanaArmorMaterials.VOID_METAL, ArmorItem.Type.LEGGINGS, new Properties()));
	public static final RegistryObject<Item> VOID_METAL_BOOTS = ITEMS.register("void_metal_boots", () -> new AutoRepairArmorItem(ArcanaArmorMaterials.VOID_METAL, ArmorItem.Type.BOOTS, new Properties()));
	
	//Items With Function
	public static final RegistryObject<Item> RESEARCH_NOTE_COMPLETE = ITEMS.register("research_note_complete", () -> new ResearchNoteItem(new Item.Properties(), true));
	public static final RegistryObject<Item> RESEARCH_NOTE = ITEMS.register("research_note", () -> new ResearchNoteItem(new Item.Properties(), false));
	public static final RegistryObject<Item> SCRIBING_TOOLS = ITEMS.register("scribing_tools", () -> new Item(new Properties().stacksTo(1).durability(100).setNoRepair()));
	public static final RegistryObject<Item> VIS_MANIPULATION_TOOLS = ITEMS.register("vis_manipulation_tools", () -> new VisManipulatorsItem(new Item.Properties().stacksTo(1)));
	public static final RegistryObject<Item> PHIAL = ITEMS.register("phial", PhialItem::new);
	public static final RegistryObject<Item> SCRIBBLED_NOTES_COMPLETE = ITEMS.register("scribbled_notes_complete", () -> new ScribbledNotesCompleteItem(new Properties().stacksTo(1)));
	public static final RegistryObject<Item> SCRIBBLED_NOTES = ITEMS.register("scribbled_notes", () -> new ScribbledNoteItem(new Properties().stacksTo(1)));
	public static final RegistryObject<Item> TAINT_IN_A_BOTTLE = ITEMS.register("taint_in_a_bottle", () -> new TaintBottleItem(new Properties()));
	public static final RegistryObject<Item> FLUX_METER = ITEMS.register("flux_meter", () -> new Item(new Properties()));
	
	// Books
	public static final RegistryObject<Item> ARCANUM = ITEMS.register("arcanum", () -> new ResearchBookItem(new Properties().stacksTo(1), new ResourceLocation(MODID, "arcanum")));
	public static final RegistryObject<Item> GRIMOIRE = ITEMS.register("illustrious_grimoire", () -> new ResearchBookItem(new Properties().stacksTo(1), new ResourceLocation(MODID, "illustrious_grimoire")));
	public static final RegistryObject<Item> CODEX = ITEMS.register("tainted_codex", () -> new ResearchBookItem(new Properties().stacksTo(1), new ResourceLocation(MODID, "tainted_codex")));
	public static final RegistryObject<Item> RITES = ITEMS.register("crimson_rites", () -> new ResearchBookItem(new Properties().stacksTo(1), new ResourceLocation(MODID, "crimson_rites")));
	
	// Materials
	@GIM(ITEM) public static final RegistryObject<Item> LABEL = ITEMS.register("label", () -> new Item(new Properties()));
	
	public static final RegistryObject<Item> THAUMIUM_INGOT = ITEMS.register("thaumium_ingot", () -> new Item(new Properties()));
	public static final RegistryObject<Item> ARCANIUM_INGOT = ITEMS.register("arcanium_ingot", () -> new Item(new Properties()));
	public static final RegistryObject<Item> VOID_METAL_INGOT = ITEMS.register("void_metal_ingot", () -> new Item(new Properties()));
	@GIM(ITEM) public static final RegistryObject<Item> VOID_METAL_NUGGET = ITEMS.register("void_metal_nugget", () -> new Item(new Properties()));
	@GIM(ITEM) public static final RegistryObject<Item> SILVER_INGOT = ITEMS.register("silver_ingot", () -> new Item(new Properties()));
	@GIM(ITEM) public static final RegistryObject<Item> NETHERITE_NUGGET = ITEMS.register("netherite_nugget", () -> new Item(new Properties()));
	@GIM(ITEM) public static final RegistryObject<Item> SILVER_NUGGET = ITEMS.register("silver_nugget", () -> new Item(new Properties()));
	@GIM(ITEM) public static final RegistryObject<Item> THAUMIUM_NUGGET = ITEMS.register("thaumium_nugget", () -> new Item(new Properties()));
	
	public static final RegistryObject<Item> SILVERWOOD_STICK = ITEMS.register("silverwood_stick", () -> new Item(new Properties()));
	public static final RegistryObject<Item> AMBER = ITEMS.register("amber", () -> new Item(new Item.Properties()));
	@GIM(ITEM) public static final RegistryObject<Item> ELDRITCH_CLOTH = ITEMS.register("eldritch_cloth", () -> new Item(new Item.Properties()));
	
	public static final RegistryObject<Item> PURIFIED_IRON = ITEMS.register("purified_iron", () -> new Item(new Properties()));
	public static final RegistryObject<Item> PURIFIED_GOLD = ITEMS.register("purified_gold", () -> new Item(new Properties()));
	public static final RegistryObject<Item> PURIFIED_SILVER = ITEMS.register("purified_silver", () -> new Item(new Properties()));
	public static final RegistryObject<Item> ALTERED_IRON = ITEMS.register("altered_iron", () -> new Item(new Properties()));
	// copper, tin, lead, etc... clusters
	
	// Goggles
	public static final RegistryObject<Item> GOGGLES_OF_REVEALING = ITEMS.register("goggles_of_revealing", () -> new GogglesItem(ArcanaArmorMaterials.GOGGLES, new Properties(), GogglePriority.SHOW_ASPECTS));
	
	// Crystal Cluster Seeds
	public static final RegistryObject<Item> AIR_CRYSTAL_SEED = ITEMS.register("air_cluster_seed", () -> new CrystalClusterItem(ArcanaBlocks.AIR_CLUSTER.get(), new Properties(), 0));
	public static final RegistryObject<Item> EARTH_CRYSTAL_SEED = ITEMS.register("earth_cluster_seed", () -> new CrystalClusterItem(ArcanaBlocks.EARTH_CLUSTER.get(), new Properties(), 0));
	public static final RegistryObject<Item> FIRE_CRYSTAL_SEED = ITEMS.register("fire_cluster_seed", () -> new CrystalClusterItem(ArcanaBlocks.FIRE_CLUSTER.get(), new Properties(), 0));
	public static final RegistryObject<Item> WATER_CRYSTAL_SEED = ITEMS.register("water_cluster_seed", () -> new CrystalClusterItem(ArcanaBlocks.WATER_CLUSTER.get(), new Properties(), 0));
	public static final RegistryObject<Item> ORDER_CRYSTAL_SEED = ITEMS.register("order_cluster_seed", () -> new CrystalClusterItem(ArcanaBlocks.ORDER_CLUSTER.get(), new Properties(), 0));
	public static final RegistryObject<Item> CHAOS_CRYSTAL_SEED = ITEMS.register("chaos_cluster_seed", () -> new CrystalClusterItem(ArcanaBlocks.CHAOS_CLUSTER.get(), new Properties(), 0));
	
	// Caps
	public static final RegistryObject<CapItem> IRON_CAP = ITEMS.register("iron_cap", () -> new CapItem(new Properties(), 10, 3, 1, arcLoc("iron_cap")));
	public static final RegistryObject<CapItem> GOLD_CAP = ITEMS.register("gold_cap", () -> new CapItem(new Properties(), 25, 15, 2, arcLoc("gold_cap")));
	public static final RegistryObject<CapItem> COPPER_CAP = ITEMS.register("copper_cap", () -> new CapItem(new Properties(), 15, 5, 2, arcLoc("copper_cap")));
	public static final RegistryObject<CapItem> SILVER_CAP = ITEMS.register("silver_cap", () -> new CapItem(new Properties(), 0, 75, 2, arcLoc("silver_cap")));
	public static final RegistryObject<CapItem> THAUMIUM_CAP = ITEMS.register("thaumium_cap", () -> new CapItem(new Properties(), 50, 25, 3, arcLoc("thaumium_cap")));
	public static final RegistryObject<CapItem> VOID_CAP = ITEMS.register("void_cap", () -> new CapItem(new Properties(), 75, 45, 3, arcLoc("void_cap")));
	public static final RegistryObject<CapItem> NETHERITE_CAP = ITEMS.register("netherite_cap", () -> new CapItem(new Properties(), 65, 40, 3, arcLoc("netherite_cap")));
	@GIM(ITEM) public static final RegistryObject<CapItem> PRISMARINE_CAP = ITEMS.register("prismarine_cap", () -> new CapItem(new Properties(), 50, 30, 2, arcLoc("prismarine_cap")));
	@GIM(ITEM) public static final RegistryObject<CapItem> BAMBOO_CAP = ITEMS.register("bamboo_cap", () -> new CapItem(new Properties(), 50, 30, 2, arcLoc("bamboo_cap")));
	@GIM(ITEM) public static final RegistryObject<CapItem> AMBER_CAP = ITEMS.register("amber_cap", () -> new CapItem(new Properties(), 50, 30, 2, arcLoc("amber_cap")));
	@GIM(ITEM) public static final RegistryObject<CapItem> QUARTZ_CAP = ITEMS.register("quartz_cap", () -> new CapItem(new Properties(), 50, 30, 2, arcLoc("quartz_cap")));
	@GIM(ITEM) public static final RegistryObject<CapItem> LEATHER_CAP = ITEMS.register("leather_cap", () -> new CapItem(new Properties(), 50, 30, 2, arcLoc("leather_cap")));
	@GIM(ITEM) public static final RegistryObject<CapItem> HONEY_CAP = ITEMS.register("honey_cap", () -> new CapItem(new Properties(), 50, 30, 2, arcLoc("honey_cap")));
	@GIM(ITEM) public static final RegistryObject<CapItem> SHULKER_CAP = ITEMS.register("shulker_cap", () -> new CapItem(new Properties(), 0, 35, 2, arcLoc("shulker_cap")));
	@GIM(ITEM) public static final RegistryObject<CapItem> MECHANICAL_CAP = ITEMS.register("mechanical_cap", () -> new CapItem(new Properties(), 60, 40, 2, arcLoc("mechanical_cap")));
	@GIM(ITEM) public static final RegistryObject<CapItem> ELDRITCH_CLOTH_CAP = ITEMS.register("eldritch_cloth_cap", () -> new CapItem(new Properties(), 70, 50, 2, arcLoc("eldritch_cloth_cap")));
	@GIM(ITEM) public static final RegistryObject<CapItem> CLAY_CAP = ITEMS.register("clay_cap", () -> new CapItem(new Properties(), Short.MAX_VALUE, Short.MAX_VALUE, -1, arcLoc("clay_cap")));
	
	// Foci
	public static RegistryObject<FocusItem> DEFAULT_FOCUS = ITEMS.register("focus", () -> new FocusItem(new Properties().stacksTo(1), FocusItem.DEFAULT_NUMSTYLES, arcLoc("wand_focus")));
	
	public static final RegistryObject<Item> FOCUS_PARTS = ITEMS.register("focus_parts", () -> new Item(new Item.Properties()));
	
	// Cores
	public static final Core.Impl WOOD_WAND_CORE = new Core.Impl(5,1, 1, new MDModifier.Empty(), arcLoc("wood_wand"));
	public static final RegistryObject<CoreItem> GREATWOOD_WAND_CORE = ITEMS.register("greatwood_wand_core", () -> new CoreItem(new Properties(), 25,3, 2, arcLoc("greatwood_wand"), new MDModifier.ReducedVis(Arrays.asList(AspectUtils.primalAspects).stream().map(aspect -> new AspectStack(aspect,5)).collect(Collectors.toList()))));
	public static final RegistryObject<CoreItem> TAINTED_WAND_CORE = ITEMS.register("tainted_wand_core", () -> new CoreItem(new Properties(), 200,30, 2, arcLoc("tainted_wand"), new MDModifier.Warping()));
	public static final RegistryObject<CoreItem> DAIR_WAND_CORE = ITEMS.register("dair_wand_core", () -> new CoreItem(new Properties(), 10,1, 3, arcLoc("dair_wand"), new MDModifier.Empty()));
	public static final RegistryObject<CoreItem> HAWTHORN_WAND_CORE = ITEMS.register("hawthorn_wand_core", () -> new CoreItem(new Properties(), 15,1, 3, arcLoc("hawthorn_wand"), new MDModifier.Empty()));
	public static final RegistryObject<CoreItem> SILVERWOOD_WAND_CORE = ITEMS.register("silverwood_wand_core", () -> new CoreItem(new Properties(), 100,5, 4, arcLoc("silverwood_wand"), new MDModifier.ReducedVis(Arrays.asList(AspectUtils.primalAspects).stream().map(aspect -> new AspectStack(aspect,10)).collect(Collectors.toList()))));
	public static final RegistryObject<CoreItem> ARCANIUM_WAND_CORE = ITEMS.register("arcanium_wand_core", () -> new CoreItem(new Properties(), 150,10, 4, arcLoc("arcanium_wand"), new MDModifier.Empty()));
	@GIM(ITEM) public static final RegistryObject<CoreItem> BLAZE_WAND_CORE = ITEMS.register("blaze_wand_core", () -> new CoreItem(new Properties(), 199,10, 2, arcLoc("blaze_wand"), new MDModifier.ReducedVis(Collections.singletonList(new AspectStack(Aspects.FIRE,20)))));
	@GIM(ITEM) public static final RegistryObject<CoreItem> ENDROD_WAND_CORE = ITEMS.register("endrod_wand_core", () -> new CoreItem(new Properties(), 225,15, 4, arcLoc("end_rod_wand"), new MDModifier.Empty()));
	@GIM(ITEM) public static final RegistryObject<CoreItem> BONE_WAND_CORE = ITEMS.register("bone_wand_core", () -> new CoreItem(new Properties(), 100,5, 2, arcLoc("bone_wand"), new MDModifier.ReducedVis(Collections.singletonList(new AspectStack(Aspects.CHAOS,20)))));
	@GIM(ITEM) public static final RegistryObject<CoreItem> ICE_WAND_CORE = ITEMS.register("ice_wand_core", () -> new CoreItem(new Properties(), 100,10, 2, arcLoc("ice_wand"), new MDModifier.ReducedVis(Collections.singletonList(new AspectStack(Aspects.WATER,20)))));
	@GIM(ITEM) public static final RegistryObject<CoreItem> ARCANE_STONE_WAND_CORE = ITEMS.register("arcane_stone_wand_core", () -> new CoreItem(new Properties(), 100,10, 2, arcLoc("stone_wand"), new MDModifier.ReducedVis(Collections.singletonList(new AspectStack(Aspects.ORDER,20)))));
	@GIM(ITEM) public static final RegistryObject<CoreItem> OBSIDIAN_WAND_CORE = ITEMS.register("obsidian_wand_core", () -> new CoreItem(new Properties(), 100,5, 2, arcLoc("obsidian_wand"), new MDModifier.ReducedVis(Collections.singletonList(new AspectStack(Aspects.EARTH,20)))));
	@GIM(ITEM) public static final RegistryObject<CoreItem> SUGAR_CANE_WAND_CORE = ITEMS.register("sugar_cane_wand_core", () -> new CoreItem(new Properties(), 100,10, 2, arcLoc("cane_wand"), new MDModifier.ReducedVis(Collections.singletonList(new AspectStack(Aspects.AIR,20)))));
	@GIM(ITEM) public static final RegistryObject<CoreItem> MECHANICAL_WAND_CORE = ITEMS.register("mechanical_wand_core", () -> new CoreItem(new Properties(), 125,15, 2, arcLoc("mechanical_wand"), new MDModifier.Mechanical()));
	@GIM(ITEM) public static final RegistryObject<CoreItem> ELDRITCH_WAND_CORE = ITEMS.register("eldritch_wand_core", () -> new CoreItem(new Properties(), 250,30, 2, arcLoc("eldritch_wand"), new MDModifier.Warping()));
	@GIM(ITEM) public static final RegistryObject<CoreItem> CLAY_WAND_CORE = ITEMS.register("clay_wand_core", () -> new CoreItem(new Properties(), Short.MAX_VALUE,Short.MAX_VALUE, -1, arcLoc("creative_wand"), new MDModifier.Creative()));

	public static final RegistryObject<Item> WAND = ITEMS.register("wand", () -> new WandItem(new Properties().stacksTo(1)));
	public static final RegistryObject<Item> SCEPTER = ITEMS.register("scepter", () -> new ScepterItem(new Properties().stacksTo(1)));
	public static final RegistryObject<Item> STAFF = ITEMS.register("staff", () -> new StaffItem(new Properties().stacksTo(1)));
	public static final RegistryObject<Item> GAUNTLET = ITEMS.register("gauntlet", () -> new GauntletItem(new Properties().stacksTo(1)));
	
	// Tainted Items
	public static final RegistryObject<Item> TAINTED_MELON_SLICE = ITEMS.register("tainted_melon_slice", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.6f).effect(() -> new MobEffectInstance(ArcanaEffects.TAINTED.get(), 600, 0), 1f).build())));
	public static final RegistryObject<Item> TAINTED_POTATO = ITEMS.register("tainted_potato", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.8f).effect(() -> new MobEffectInstance(ArcanaEffects.TAINTED.get(), 600, 0), 1f).build())));
	public static final RegistryObject<Item> BAKED_TAINTED_POTATO = ITEMS.register("baked_tainted_potato", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(3.0f).effect(() -> new MobEffectInstance(ArcanaEffects.TAINTED.get(), 600, 0), 1f).build())));
	public static final RegistryObject<Item> TAINTED_BEETROOT = ITEMS.register("tainted_beetroot", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.6f).effect(() -> new MobEffectInstance(ArcanaEffects.TAINTED.get(), 600, 0), 1f).build())));
	public static final RegistryObject<Item> TAINTED_CARROT = ITEMS.register("tainted_carrot", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(1.8f).effect(() -> new MobEffectInstance(ArcanaEffects.TAINTED.get(), 600, 0), 1f).build())));
	public static final RegistryObject<Item> TAINTED_BERRIES = ITEMS.register("tainted_berries", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).effect(() -> new MobEffectInstance(ArcanaEffects.TAINTED.get(), 600, 0), 1f).build())));
	
	public static final RegistryObject<Item> RAW_TAINTED_COD = ITEMS.register("raw_tainted_cod", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).effect(() -> new MobEffectInstance(ArcanaEffects.TAINTED.get(), 600, 0), 1f).build())));
	public static final RegistryObject<Item> COOKED_TAINTED_COD = ITEMS.register("cooked_tainted_cod", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(3.0f).effect(() -> new MobEffectInstance(ArcanaEffects.TAINTED.get(), 600, 0), 1f).build())));
	public static final RegistryObject<Item> RAW_TAINTED_SALMON = ITEMS.register("raw_tainted_salmon", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).effect(() -> new MobEffectInstance(ArcanaEffects.TAINTED.get(), 600, 0), 1f).build())));
	public static final RegistryObject<Item> COOKED_TAINTED_SALMON = ITEMS.register("cooked_tainted_salmon", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(4.8f).effect(() -> new MobEffectInstance(ArcanaEffects.TAINTED.get(), 600, 0), 1f).build())));
	public static final RegistryObject<Item> TAINTED_TROPICAL_FISH = ITEMS.register("tainted_tropical_fish", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).effect(() -> new MobEffectInstance(ArcanaEffects.TAINTED.get(), 600, 0), 1f).build())));
	public static final RegistryObject<Item> TAINTED_PUFFERFISH = ITEMS.register("tainted_pufferfish", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).effect(() -> new MobEffectInstance(ArcanaEffects.TAINTED.get(), 600, 0), 1f).effect(() -> new MobEffectInstance(MobEffects.HUNGER, 300, 2), 1f).effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 300, 1), 1f).effect(() -> new MobEffectInstance(MobEffects.POISON, 1200, 3), 1f).build())));
	public static final RegistryObject<Item> TAINTED_SUGAR_CANE = ITEMS.register("tainted_sugar_cane", () -> new Item(new Properties()));
	@GIM(ITEM) public static final RegistryObject<Item> TAINTED_SNOWBALL = ITEMS.register("tainted_snowball", () -> new SnowballItem(new Properties()));
	
	//public static final RegistryObject<Item> TAINTED_SALMON_BUCKED = ITEMS.register("tainted_salmon_bucket",() -> new FishBucketItem(ArcanaEntities.TAINTED_SALMON, ()->Fluids.WATER,new Item.Properties().stacksTo(1)));

	// Multi-block placers
	@GIM(ITEM) public static final RegistryObject<Item> RESEARCH_TABLE_ITEM = ITEMS.register("research_table_item", () -> new BlockItem(ArcanaBlocks.RESEARCH_TABLE.get(), new Properties().stacksTo(1)));
	@GIM(ITEM) public static final RegistryObject<Item> FOCI_FORGE_ITEM = ITEMS.register("foci_forge_item", () -> new BlockItem(ArcanaBlocks.FOCI_FORGE.get(), new Properties().stacksTo(1)));

	// Food
	// TODO: Remove?
	public static final RegistryObject<Item> RAW_DOG_MEAT = ITEMS.register("raw_dog_meat", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(7).saturationMod(0.6f).build())));
	public static final RegistryObject<Item> RAW_CAT_MEAT = ITEMS.register("raw_cat_meat", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(5).saturationMod(0.4f).build())));
	public static final RegistryObject<Item> COOKED_DOG_MEAT = ITEMS.register("cooked_dog_meat", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.2f).build())));
	public static final RegistryObject<Item> COOKED_CAT_MEAT = ITEMS.register("cooked_cat_meat", () -> new Item(new Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1f).build())));

	// Filters
	public static final RegistryObject<Item> ARCANIUM_FILTER = ITEMS.register("arcanium_filter", () -> new EnchantedFilterItem(new Item.Properties().stacksTo(1), 1, 1));
	public static final RegistryObject<Item> SILVERWOOD_FILTER = ITEMS.register("silverwood_filter", () -> new EnchantedFilterItem(new Properties().stacksTo(1), 2, 0));
	public static final RegistryObject<Item> GREATWOOD_FILTER = ITEMS.register("greatwood_filter", () -> new EnchantedFilterItem(new Properties().stacksTo(1), 0, 2));
	public static final RegistryObject<Item> HAWTHORN_FILTER = ITEMS.register("hawthorn_filter", () -> new EnchantedFilterItem(new Properties().stacksTo(1), 3, -1));
	public static final RegistryObject<Item> DAIR_FILTER = ITEMS.register("dair_filter", () -> new EnchantedFilterItem(new Properties().stacksTo(1), 3, -1));
	public static final RegistryObject<Item> WILLOW_FILTER = ITEMS.register("willow_filter", () -> new EnchantedFilterItem(new Properties().stacksTo(1), -1, 3));
	public static final RegistryObject<Item> EUCALYPTUS_FILTER = ITEMS.register("eucalyptus_filter", () -> new EnchantedFilterItem(new Properties().stacksTo(1), -1, 3));

	// Creative Only
	public static final RegistryObject<Item> CHEATERS_ARCANUM = ITEMS.register("cheaters_arcanum", () -> new CheatersResearchBookItem(new Properties().stacksTo(1).rarity(Rarity.EPIC), new ResourceLocation(MODID, "arcanum")));

	// Fluid Buckets
	public static RegistryObject<Item> TAINT_FLUID_BUCKET = ITEMS.register("tainted_goo_bucket", () -> new BucketItem(ArcanaFluids.TAINT_FLUID, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

	static {
		AspectUtils.register();
	}
}