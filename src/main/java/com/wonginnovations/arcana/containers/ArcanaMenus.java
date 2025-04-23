package com.wonginnovations.arcana.containers;

import com.wonginnovations.arcana.Arcana;
import com.wonginnovations.arcana.blocks.pipes.PumpBlockEntity;
import com.wonginnovations.arcana.blocks.entities.AlembicBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ArcanaMenus {
	
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Arcana.MODID);

	public static final RegistryObject<MenuType<FociForgeMenu>> FOCI_FORGE = MENUS.register("foci_forge", () -> IForgeMenuType.create(FociForgeMenu::new));
	public static final RegistryObject<MenuType<ResearchTableMenu>> RESEARCH_TABLE = MENUS.register("research_table", () -> IForgeMenuType.create(ResearchTableMenu::new));
	public static final RegistryObject<MenuType<ArcaneCraftingTableMenu>> ARCANE_CRAFTING_TABLE = MENUS.register("arcane_crafting_table", () -> IForgeMenuType.create((id, inventory, buffer) -> new ArcaneCraftingTableMenu(id, inventory, (Container) inventory.player.level().getBlockEntity(buffer.readBlockPos()))));
	public static final RegistryObject<MenuType<AspectCrystallizerMenu>> ASPECT_CRYSTALLIZER = MENUS.register("aspect_crystallizer", () -> IForgeMenuType.create((id, inventory, buffer) -> new AspectCrystallizerMenu(id, (Container) inventory.player.level().getBlockEntity(buffer.readBlockPos()), inventory)));
	public static final RegistryObject<MenuType<AlembicMenu>> ALEMBIC = MENUS.register("alembic", () -> IForgeMenuType.create((id, inventory, buffer) -> new AlembicMenu(id, (AlembicBlockEntity)inventory.player.level().getBlockEntity(buffer.readBlockPos()), inventory)));
	public static final RegistryObject<MenuType<PumpMenu>> PUMP = MENUS.register("essentia_pump", () -> IForgeMenuType.create((id, inventory, buffer) -> new PumpMenu(id, (PumpBlockEntity)inventory.player.level().getBlockEntity(buffer.readBlockPos()), inventory)));
}