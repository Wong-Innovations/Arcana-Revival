package arcana.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import arcana.common.blocks.ModBlocks;
import arcana.api.items.ItemGenericEssentiaContainer;
import arcana.common.items.ModItems;
import arcana.common.blocks.world.ore.BlockCrystal;

@OnlyIn(Dist.CLIENT)
public class ColorHandler {
    public static void registerColourHandlers() {
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        ItemColors itemColors = Minecraft.getInstance().getItemColors();
        registerBlockColourHandlers(blockColors);
        registerItemColourHandlers(blockColors, itemColors);
    }

    private static void registerBlockColourHandlers(BlockColors blockColors) {
        BlockColor crystalColourHandler = (state, blockAccess, pos, tintIndex) -> {
            if (state.getBlock() instanceof BlockCrystal) {
                return ((BlockCrystal)state.getBlock()).aspect.getColor();
            }
            return 0xFFFFFF;
        };
        blockColors.register(crystalColourHandler, ModBlocks.crystalAir.get(), ModBlocks.crystalEarth.get(), ModBlocks.crystalFire.get(), ModBlocks.crystalWater.get(), ModBlocks.crystalEntropy.get(), ModBlocks.crystalOrder.get(), ModBlocks.crystalTaint.get());
    }

    private static void registerItemColourHandlers(BlockColors blockColors, ItemColors itemColors) {
        ItemColor itemEssentiaColourHandler = (stack, tintIndex) -> {
            ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer)stack.getItem();
            try {
                if (item.getAspects(stack) != null) {
                    return item.getAspects(stack).getAspects()[0].getColor();
                }
            }
            catch (Exception ignored) {}
            return 0XFFFFFF;
        };
        itemColors.register(itemEssentiaColourHandler, new Item[] { ModItems.crystalEssence.get() });

        ItemColor itemCrystalPlanterColourHandler = (stack, tintIndex) -> {
            Item item = stack.getItem();
            if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof BlockCrystal) {
                return ((BlockCrystal)((BlockItem)item).getBlock()).aspect.getColor();
            }
            return 0XFFFFFF;
        };
        itemColors.register(itemCrystalPlanterColourHandler, new Block[] { ModBlocks.crystalAir.get() });
        itemColors.register(itemCrystalPlanterColourHandler, new Block[] { ModBlocks.crystalEarth.get() });
        itemColors.register(itemCrystalPlanterColourHandler, new Block[] { ModBlocks.crystalFire.get() });
        itemColors.register(itemCrystalPlanterColourHandler, new Block[] { ModBlocks.crystalWater.get() });
        itemColors.register(itemCrystalPlanterColourHandler, new Block[] { ModBlocks.crystalEntropy.get() });
        itemColors.register(itemCrystalPlanterColourHandler, new Block[] { ModBlocks.crystalOrder.get() });
        itemColors.register(itemCrystalPlanterColourHandler, new Block[] { ModBlocks.crystalTaint.get() });

        ItemColor itemEssentiaAltColourHandler = (stack, tintIndex) -> {
            ItemGenericEssentiaContainer item = (ItemGenericEssentiaContainer)stack.getItem();
            if (item.getAspects(stack) != null && tintIndex == 1) {
                return item.getAspects(stack).getAspects()[0].getColor();
            }
            return 0xFFFFFF;
        };
        itemColors.register(itemEssentiaAltColourHandler, ModItems.phial.get());
    }
}
