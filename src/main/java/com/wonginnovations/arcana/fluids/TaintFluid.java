package com.wonginnovations.arcana.fluids;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.entities.TaintedGooWrapper;
import com.wonginnovations.arcana.systems.taint.Taint;
import com.wonginnovations.arcana.effects.ArcanaEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TaintFluid extends LiquidBlock {
	public TaintFluid(Supplier<? extends FlowingFluid> supplier, Properties properties) {
		super(supplier, properties);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.randomTick(state, level, pos, random);
		Taint.tickTaintedBlock(state, level, pos, random);
	}

	@Override
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity) {
			((TaintedGooWrapper) entity).setGooTicks(((TaintedGooWrapper) entity).getGooTicks() + 1);
			if (((TaintedGooWrapper) entity).getGooTicks() > 6) {
				((LivingEntity) entity).addEffect(new MobEffectInstance(ArcanaEffects.TAINTED.get(), 5 * 20));
			}
		}
	}
}