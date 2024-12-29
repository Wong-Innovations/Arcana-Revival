package arcana.client.gui;

import arcana.Arcana;
import arcana.common.container.ContainerResearchTable;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Arcana.MODID);

    public static final RegistryObject<MenuType<ContainerArcaneWorkbench>> ARCANE_WORKBENCH_MENU = MENU_TYPES.register("arcane_workbench",
            () -> IForgeMenuType.create(ContainerArcaneWorkbench::new));
    public static final RegistryObject<MenuType<ContainerResearchTable>> RESEARCH_TABLE_MENU = MENU_TYPES.register("research_table",
            () -> IForgeMenuType.create(ContainerResearchTable::new));

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}
