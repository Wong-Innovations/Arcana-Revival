package com.wonginnovations.arcana.fluids;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

import static com.wonginnovations.arcana.ArcanaVariables.arcLoc;

public class TaintedFluidType extends FluidType {

    public static final ResourceLocation FLUID_STILL = arcLoc("fluid/tainted_goo");
    public static final ResourceLocation FLUID_FLOWING = arcLoc("fluid/tainted_goo_flowing");

    public TaintedFluidType() {
        super(Properties.create()
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
        );
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture() {
                return FLUID_STILL;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLUID_FLOWING;
            }
        });
    }

}
