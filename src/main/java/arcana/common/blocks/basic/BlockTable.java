package arcana.common.blocks.basic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.ForgeEventFactory;
import arcana.common.blocks.ModBlocks;
import arcana.api.items.IScribeTools;
import arcana.common.InventoryFake;
import arcana.common.blockentities.crafting.BlockEntityResearchTable;
import arcana.common.blocks.IBlockFacingHorizontal;
import org.jetbrains.annotations.NotNull;

public class BlockTable extends Block {
    public BlockTable(Properties pProperties) {
        super(pProperties.noOcclusion());
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.CONSUME;
        }

        if (this == ModBlocks.tableWood.get() && pPlayer.getItemInHand(pHand).getItem() instanceof IScribeTools) {
            BlockState bs = ModBlocks.researchTable.get().defaultBlockState();
            bs = bs.setValue(IBlockFacingHorizontal.FACING, pPlayer.getDirection());
            pLevel.setBlock(pPos, bs, 1 | 2);
            BlockEntityResearchTable blockEntity = (BlockEntityResearchTable) pLevel.getBlockEntity(pPos);
            blockEntity.setItem(0, pPlayer.getItemInHand(pHand).copy());
            pPlayer.setItemInHand(pHand, ItemStack.EMPTY);
            pPlayer.getInventory().setChanged();
            blockEntity.setChanged();
            pLevel.markAndNotifyBlock(pPos, pLevel.getChunkAt(pPos), bs, bs, 1 | 2, 512);
            ForgeEventFactory.firePlayerCraftingEvent(pPlayer, new ItemStack(ModBlocks.researchTable.get()), new InventoryFake(1));
        }

        return InteractionResult.CONSUME;
    }
}
