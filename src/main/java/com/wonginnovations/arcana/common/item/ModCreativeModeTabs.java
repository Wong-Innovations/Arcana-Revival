package com.wonginnovations.arcana.common.item;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.api.aspects.Aspect;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Arcana.MODID);

    public static final RegistryObject<CreativeModeTab> ARCANA_GENERAL = CREATIVE_MODE_TABS.register("arcana_general",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SALIS_MUNDUS.get()))
                    .title(Component.translatable("creativetab.arcana_general"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.SALIS_MUNDUS.get());
                        pOutput.accept(ModItems.PHIAL_SMALL.get());
                        pOutput.accept(ModItems.PHIAL.get());
                        pOutput.accept(ModItems.PHIAL_LARGE.get());
                        for (Aspect aspect : Aspect.aspects.values()) {
                            ItemStack stack = new ItemStack(ModItems.VIS.get());
                            stack.setTag(aspect.toCompoundTag());
                            pOutput.accept(stack);

                            stack = new ItemStack(ModItems.PHIAL_SMALL.get());
                            stack.setTag(aspect.toCompoundTag());
                            pOutput.accept(stack);

                            stack = new ItemStack(ModItems.PHIAL.get());
                            stack.setTag(aspect.toCompoundTag());
                            pOutput.accept(stack);

                            stack = new ItemStack(ModItems.PHIAL_LARGE.get());
                            stack.setTag(aspect.toCompoundTag());
                            pOutput.accept(stack);
                        }
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
