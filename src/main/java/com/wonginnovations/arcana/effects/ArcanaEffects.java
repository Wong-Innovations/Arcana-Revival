package com.wonginnovations.arcana.effects;

import com.wonginnovations.arcana.Arcana;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ArcanaEffects extends MobEffect {
	
	public ArcanaEffects(MobEffectCategory category, int color) {
		super(category, color);
	}
	
	// Effect UUIDs should be generated in advance
	
	public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Arcana.MODID);
	
	public static final RegistryObject<MobEffect> TAINTED = MOB_EFFECTS.register("tainted", TaintedEffect::new);
	
	public static final RegistryObject<MobEffect> FROZEN = MOB_EFFECTS.register("frozen", () -> new FrozenEffect().addAttributeModifier(
			Attributes.MOVEMENT_SPEED,
			"4617a65e-47f6-4f2f-ac4f-eef0a46517fa", -.45,
			AttributeModifier.Operation.MULTIPLY_BASE
	));
	
	public static final RegistryObject<MobEffect> WARDING = MOB_EFFECTS.register("warding", () -> new WardingEffect().addAttributeModifier(
			Attributes.ARMOR,
			"c429f8cd-3490-498a-ad98-21cd68e8476e", 1.5,
			AttributeModifier.Operation.MULTIPLY_BASE
	));
	
	public static final RegistryObject<MobEffect> VICTUS = MOB_EFFECTS.register("victus", VictusEffect::new);
}