package arcana.common.blockentities;

import arcana.Arcana;
import arcana.common.blockentities.crafting.BlockEntityArcaneWorkbench;
import arcana.common.blockentities.crafting.BlockEntityResearchTable;
import arcana.common.blocks.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Arcana.MODID);

    public static final RegistryObject<BlockEntityType<?>> ARCANE_WORKBENCH_ENTITY = BLOCK_ENTITIES.register("arcane_workbench",
            () -> BlockEntityType.Builder.of(BlockEntityArcaneWorkbench::new, ModBlocks.arcaneWorkbench.get()).build(null));
    public static final RegistryObject<BlockEntityType<?>> RESEARCH_TABLE_ENTITY = BLOCK_ENTITIES.register("research_table",
            () -> BlockEntityType.Builder.of(BlockEntityResearchTable::new, ModBlocks.researchTable.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
