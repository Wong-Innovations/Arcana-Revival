package com.wonginnovations.arcana.commands;

import com.wonginnovations.arcana.Arcana;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ArcanaArguementTypes {

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, Arcana.MODID);

    public static final RegistryObject<ArgumentTypeInfo<?, ?>> NODE = ARGUMENT_TYPES.register("node_argument", () -> ArgumentTypeInfos.registerByClass(NodeArgument.class, SingletonArgumentInfo.contextFree(NodeArgument::nodes)));

}
