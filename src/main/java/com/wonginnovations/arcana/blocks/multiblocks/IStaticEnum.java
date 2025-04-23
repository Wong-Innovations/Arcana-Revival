package com.wonginnovations.arcana.blocks.multiblocks;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.StringRepresentable;

@MethodsReturnNonnullByDefault
public interface IStaticEnum extends StringRepresentable {

    Vec3i getOffset(Direction direction);

    Vec3i getInvert(Direction direction);
}
