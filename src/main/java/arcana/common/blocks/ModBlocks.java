package arcana.common.blocks;

import arcana.Arcana;
import arcana.api.aspects.Aspect;
import arcana.common.blocks.basic.BlockStoneTC;
import arcana.common.blocks.basic.BlockTable;
import arcana.common.blocks.crafting.BlockArcaneWorkbench;
import arcana.common.blocks.crafting.BlockCrucible;
import arcana.common.blocks.crafting.BlockResearchTable;
import arcana.common.blocks.world.ore.BlockCrystal;
import arcana.common.blocks.world.ore.ShardType;
import arcana.common.items.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Arcana.MODID);

    public static final RegistryObject<Block> crystalAir = registerBlock("crystal_aer", () -> new BlockCrystal(Aspect.AIR));
    public static final RegistryObject<Block> crystalFire = registerBlock("crystal_ignis", () -> new BlockCrystal(Aspect.FIRE));
    public static final RegistryObject<Block> crystalWater = registerBlock("crystal_aqua", () -> new BlockCrystal(Aspect.WATER));
    public static final RegistryObject<Block> crystalEarth = registerBlock("crystal_terra", () -> new BlockCrystal(Aspect.EARTH));
    public static final RegistryObject<Block> crystalOrder = registerBlock("crystal_ordo", () -> new BlockCrystal(Aspect.ORDER));
    public static final RegistryObject<Block> crystalEntropy = registerBlock("crystal_perditio", () -> new BlockCrystal(Aspect.ENTROPY));
    public static final RegistryObject<Block> crystalTaint = registerBlock("crystal_vitium", () -> new BlockCrystal(Aspect.FLUX));

    public static final RegistryObject<Block> stoneArcane = registerBlock("stone_arcane", BlockStoneTC::new);
    public static final RegistryObject<Block> stoneArcaneBrick = registerBlock("stone_arcane_brick", BlockStoneTC::new);
    public static final RegistryObject<Block> tableWood = registerBlock("table_wood", () -> new BlockTable(BlockBehaviour.Properties.of().sound(SoundType.WOOD).destroyTime(2.0f)));
    public static final RegistryObject<Block> arcaneWorkbench = registerBlock("arcane_workbench", BlockArcaneWorkbench::new);
    public static final RegistryObject<Block> researchTable = registerBlock("research_table", () -> new BlockResearchTable(BlockBehaviour.Properties.of().sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<Block> crucible = registerBlock("crucible", BlockCrucible::new);
    public static final RegistryObject<Block> bratusLog = registerBlock("bratus_log", () -> log(MapColor.COLOR_PURPLE, MapColor.PODZOL));
    public static final RegistryObject<Block> bratusWood = registerBlock("bratus_wood", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava()));
    public static final RegistryObject<Block> strippedBratusLog = registerBlock("stripped_bratus_log", () -> log(MapColor.COLOR_PURPLE, MapColor.COLOR_PURPLE));
    public static final RegistryObject<Block> strippedBratusWood = registerBlock("stripped_bratus_wood", () -> log(MapColor.COLOR_PURPLE, MapColor.COLOR_PURPLE));
    public static final RegistryObject<Block> bratusPlanks = registerBlock("bratus_planks", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).instrument(NoteBlockInstrument.BASS).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()));

    private static RegistryObject<Block> registerBlock(String name, Supplier<Block> supplier) {
        RegistryObject<Block> toReturn = BLOCKS.register(name, supplier);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static RegistryObject<Item> registerBlockItem(String name, RegistryObject<Block> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);

        ShardType.AIR.setOre(crystalAir);
        ShardType.FIRE.setOre(crystalFire);
        ShardType.WATER.setOre(crystalWater);
        ShardType.EARTH.setOre(crystalEarth);
        ShardType.ORDER.setOre(crystalOrder);
        ShardType.ENTROPY.setOre(crystalEntropy);
        ShardType.FLUX.setOre(crystalTaint);
    }

    private static Block log(MapColor top, MapColor side) {
        return new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor((direction) -> direction.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? top : side).instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
    }
}
