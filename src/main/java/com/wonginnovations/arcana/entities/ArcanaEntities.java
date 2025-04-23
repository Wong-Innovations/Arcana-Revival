package com.wonginnovations.arcana.entities;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.systems.taint.Taint;
import com.wonginnovations.arcana.entities.tainted.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.wonginnovations.arcana.Arcana.arcLoc;

/**
 * Initialize Entities here
 *
 * @author Mozaran
 */
@SuppressWarnings("unchecked")
public class ArcanaEntities{
	public static final DeferredRegister<EntityType<?>> T_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
			Arcana.MODID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
			Arcana.MODID);

	public static final RegistryObject<EntityType<KoalaEntity>> KOALA_ENTITY = ENTITY_TYPES
			.register("koala_entity", () -> EntityType.Builder.of(KoalaEntity::new, MobCategory.CREATURE)
				.sized(.6f, .6f).build(arcLoc("koala_entity").toString()));

	public static final RegistryObject<EntityType<SpiritEntity>> DAIR_SPIRIT = ENTITY_TYPES
			.register("dair_spirit_entity", () -> EntityType.Builder.of(SpiritEntity::new, MobCategory.CREATURE)
					.sized(.6f, .6f).build(arcLoc("dair_spirit_entity").toString()));

	public static final RegistryObject<EntityType<SpiritEntity>> WILLOW_SPIRIT = ENTITY_TYPES
			.register("willow_spirit_entity", () -> EntityType.Builder.of(SpiritEntity::new, MobCategory.CREATURE)
					.sized(.6f, .6f).build(arcLoc("willow_spirit_entity").toString()));

	public static final RegistryObject<EntityType<SpellCloudEntity>> SPELL_CLOUD = ENTITY_TYPES
			.register("spell_cloud", () -> EntityType.Builder.<SpellCloudEntity>of(SpellCloudEntity::new, MobCategory.MISC)
					.fireImmune().sized(6, .5f).build(arcLoc("spell_cloud").toString()));

	public static final RegistryObject<EntityType<SpellEggEntity>> SPELL_EGG = ENTITY_TYPES
			.register("spell_egg", () -> EntityType.Builder.<SpellEggEntity>of(SpellEggEntity::new, MobCategory.MISC)
					.fireImmune().sized(.4f, .4f).build(arcLoc("spell_egg").toString()));
	public static final RegistryObject<EntityType<BlastEmitterEntity>> BLAST_EMITTER = ENTITY_TYPES
			.register("spell_core", () -> EntityType.Builder.<BlastEmitterEntity>of(BlastEmitterEntity::new, MobCategory.MISC)
					.fireImmune().sized(.6f, .6f).build(arcLoc("spell_core").toString()));

	public static final RegistryObject<EntityType<TaintBottleEntity>> TAINT_BOTTLE = ENTITY_TYPES
			.register("taint_in_a_bottle", () -> EntityType.Builder.<TaintBottleEntity>of(TaintBottleEntity::new, MobCategory.MISC)
					.fireImmune().sized(.5f, .5f).build(arcLoc("taint_in_a_bottle").toString()));
	
	public static final RegistryObject<EntityType<TaintedSlimeEntity>> TAINTED_SLIME = T_ENTITY_TYPES.register("tainted_slime", () -> Taint.taintedEntityOf(EntityType.SLIME));
	
	public static final RegistryObject<EntityType<TaintedDonkeyEntity>> TAINTED_DONKEY = T_ENTITY_TYPES.register("tainted_donkey", () -> Taint.taintedEntityOf(EntityType.DONKEY));
	public static final RegistryObject<EntityType<TaintedPolarBearEntity>> TAINTED_POLAR_BEAR = T_ENTITY_TYPES.register("tainted_polar_bear", () -> Taint.taintedEntityOf(EntityType.POLAR_BEAR));
	public static final RegistryObject<EntityType<TaintedRabbitEntity>> TAINTED_RABBIT = T_ENTITY_TYPES.register("tainted_rabbit", () -> Taint.taintedEntityOf(EntityType.RABBIT));
	public static final RegistryObject<EntityType<TaintedSnowGolemEntity>> TAINTED_SNOW_GOLEM = T_ENTITY_TYPES.register("tainted_snow_golem", () -> Taint.taintedEntityOf(EntityType.SNOW_GOLEM));
	public static final RegistryObject<EntityType<TaintedCaveSpiderEntity>> TAINTED_CAVE_SPIDER = T_ENTITY_TYPES.register("tainted_cave_spider", () -> Taint.taintedEntityOf(EntityType.CAVE_SPIDER));
	//public static final RegistryObject<EntityType<TaintedIllagerEntity>> TAINTED_ILLUSIONER = T_ENTITY_TYPES.register("tainted_illusioner", () -> Taint.taintedEntityOf(EntityType.ILLUSIONER));
	public static final RegistryObject<EntityType<TaintedPandaEntity>> TAINTED_PANDA = T_ENTITY_TYPES.register("tainted_panda", () -> Taint.taintedEntityOf(EntityType.PANDA));
	public static final RegistryObject<EntityType<TaintedEntity>> TAINTED_FOX = T_ENTITY_TYPES.register("tainted_fox", () -> Taint.taintedEntityOf(EntityType.FOX));
	public static final RegistryObject<EntityType<TaintedSkeletonEntity>> TAINTED_SKELETON = T_ENTITY_TYPES.register("tainted_skeleton", () -> Taint.taintedEntityOf(EntityType.SKELETON));
	public static final RegistryObject<EntityType<TaintedEntity>> TAINTED_WOLF = T_ENTITY_TYPES.register("tainted_wolf", () -> Taint.taintedEntityOf(EntityType.WOLF));
	public static final RegistryObject<EntityType<TaintedEntity>> TAINTED_ZOMBIE = T_ENTITY_TYPES.register("tainted_zombie", () -> Taint.taintedEntityOf(EntityType.ZOMBIE));

	public static final RegistryObject<EntityType<TaintedEntity>> TAINTED_COW = T_ENTITY_TYPES.register("tainted_cow", () -> Taint.taintedEntityOf(EntityType.COW));
	public static final RegistryObject<EntityType<TaintedEntity>> TAINTED_PIG = T_ENTITY_TYPES.register("tainted_pig", () -> Taint.taintedEntityOf(EntityType.PIG));
	public static final RegistryObject<EntityType<TaintedEntity>> TAINTED_SPIDER = T_ENTITY_TYPES.register("tainted_spider", () -> Taint.taintedEntityOf(EntityType.SPIDER));
	public static final RegistryObject<EntityType<TaintedEntity>> TAINTED_BLAZE = T_ENTITY_TYPES.register("tainted_blaze", () -> Taint.taintedEntityOf(EntityType.BLAZE));
	public static final RegistryObject<EntityType<TaintedEntity>> TAINTED_CHICKEN = T_ENTITY_TYPES.register("tainted_chicken", () -> Taint.taintedEntityOf(EntityType.CHICKEN));
	public static final RegistryObject<EntityType<TaintedEntity>> TAINTED_MOOSHROOM = T_ENTITY_TYPES.register("tainted_mooshroom", () -> Taint.taintedEntityOf(EntityType.MOOSHROOM));
	public static final RegistryObject<EntityType<TaintedEntity>> TAINTED_OCELOT = T_ENTITY_TYPES.register("tainted_ocelot", () -> Taint.taintedEntityOf(EntityType.OCELOT));
	public static final RegistryObject<EntityType<TaintedSquidEntity>> TAINTED_SQUID = T_ENTITY_TYPES.register("tainted_squid", () -> Taint.taintedEntityOf(EntityType.SQUID));
	public static final RegistryObject<EntityType<TaintedEntity>> TAINTED_VILLAGER = T_ENTITY_TYPES.register("tainted_villager", () -> Taint.taintedEntityOf(EntityType.VILLAGER));
	public static final RegistryObject<EntityType<TaintedEntity>> TAINTED_WANDERING_TRADER = T_ENTITY_TYPES.register("tainted_wandering_trader", () -> Taint.taintedEntityOf(EntityType.WANDERING_TRADER));
	public static final RegistryObject<EntityType<TaintedEntity>> TAINTED_WITCH = T_ENTITY_TYPES.register("tainted_witch", () -> Taint.taintedEntityOf(EntityType.WITCH));

	public static final RegistryObject<EntityType<SpellTrapEntity>> SPELL_TRAP = ENTITY_TYPES
			.register("spell_trap", () -> EntityType.Builder.<SpellTrapEntity>of(SpellTrapEntity::new, MobCategory.MISC)
					.fireImmune().sized(.4f, .4f).build(arcLoc("spell_trap").toString()));
}