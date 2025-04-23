package com.wonginnovations.arcana.fluids;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.items.ArcanaItems;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ArcanaFluids {
	
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, Arcana.MODID);
	public static final DeferredRegister<FluidType> TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Arcana.MODID);

	/* Fluid Types */

	public static final RegistryObject<FluidType> TAINT_FLUID_TYPE = TYPES.register("tained_goo_type", TaintedFluidType::new);

	/* Fluids */

	public static RegistryObject<FlowingFluid> TAINT_FLUID = FLUIDS.register("tainted_goo", () ->
			new ForgeFlowingFluid.Source(ArcanaFluids.TAINT_FLUID_PROPERTIES)
	);
	public static RegistryObject<FlowingFluid> TAINT_FLUID_FLOWING = FLUIDS.register("tainted_goo_flowing", () ->
			new ForgeFlowingFluid.Flowing(ArcanaFluids.TAINT_FLUID_PROPERTIES)
	);

	/* Fluid Properties */

	public static final ForgeFlowingFluid.Properties TAINT_FLUID_PROPERTIES =
			new ForgeFlowingFluid.Properties(TAINT_FLUID_TYPE, TAINT_FLUID, TAINT_FLUID_FLOWING)
					.bucket(ArcanaItems.TAINT_FLUID_BUCKET).block(ArcanaBlocks.TAINT_FLUID_BLOCK);

}