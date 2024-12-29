package arcana.common.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public interface IBlockFacingHorizontal {
    DirectionProperty FACING = DirectionProperty.create("facing", facing -> facing != Direction.UP && facing != Direction.DOWN);
}
