package com.wonginnovations.arcana.mixin;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(BlockModel.class)
public interface BlockModelMixin {

    // Literally only needed because BlockModel::getElements lies
    @Accessor("elements")
    List<BlockElement> getModelElements();

}
