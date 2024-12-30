package arcana.common.lib;

import arcana.Arcana;
import arcana.api.aspects.Aspect;
import arcana.api.aspects.AspectList;
import arcana.common.items.ModItems;
import arcana.common.items.consumables.ItemPhial;
import arcana.common.items.resources.ItemCrystalEssence;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs  {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Arcana.MODID);

    public static final RegistryObject<CreativeModeTab> ARCANA_GENERAL = CREATIVE_MODE_TABS.register("arcana_general",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.salisMundus.get()))
                    .title(Component.translatable("creativetab.arcana_general"))
                    .displayItems((pParameters, pOutput) -> {
                        for (RegistryObject<Item> itemHolder : ModItems.ITEMS.getEntries()) {
                            if (itemHolder.get() instanceof ItemCrystalEssence) {
                                for (Aspect aspect : Aspect.aspects.values()) {
                                    ItemStack stack = new ItemStack(ModItems.crystalEssence.get());
                                    ((ItemCrystalEssence) stack.getItem()).setAspects(stack, new AspectList().add(aspect, ((ItemCrystalEssence) stack.getItem()).getBase()));
                                    pOutput.accept(stack);
                                }
                            } else if (itemHolder.get() instanceof ItemPhial) {
                                pOutput.accept(new ItemStack(ModItems.phial.get()));
                                for (Aspect aspect : Aspect.aspects.values()) {
                                    ItemStack stack = new ItemStack(ModItems.phial.get());
                                    ((ItemPhial) stack.getItem()).setAspects(stack, new AspectList().add(aspect, ((ItemPhial) stack.getItem()).getBase()));
                                    pOutput.accept(stack);
                                }
                            } else {
                                pOutput.accept(itemHolder.get());
                            }
                        }
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
