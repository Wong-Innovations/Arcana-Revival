package arcana.common.lib.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;

public class BlockUtils {
    public static boolean isBlockTouching(BlockGetter world, BlockPos pos, final TagKey<Block> mat, final boolean solid) {
        for (final Direction face : Direction.values()) {
            if (world.getBlockState(pos.relative(face)).is(mat) && (!solid || world.getBlockState(pos.relative(face)).isFaceSturdy(world, pos.relative(face), face.getOpposite()))) {
                return true;
            }
        }
        return false;
    }
}
