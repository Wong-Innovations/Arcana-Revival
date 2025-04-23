package com.wonginnovations.arcana.entities.tainted;

import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.mixin.BiomeAccessor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class TaintedSnowGolemEntity extends SnowGolem {
	
	public TaintedSnowGolemEntity(EntityType<? extends SnowGolem> entityType, Level level) {
		super(entityType, level);
	}

	/**
	 * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
	 * use this to react to sunlight and start to burn.
	 */
	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide) {
			int i = Mth.floor(this.getX());
			int j = Mth.floor(this.getY());
			int k = Mth.floor(this.getZ());
			if (this.isInWaterRainOrBubble()) {
				this.hurt(level().damageSources().drown(), 1.0F);
			}
			// TODO: fix now private method
			Biome biome = this.level().getBiome(new BlockPos(i, 0, k)).get();
			if (((BiomeAccessor) (Object) biome).getTemperatureAt(new BlockPos(i, j, k)) > 1.0F) {
				this.hurt(level().damageSources().onFire(), 1.0F);
			}

			if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
				return;
			}

			BlockState blockstate = ArcanaBlocks.TAINTED_SNOW.get().defaultBlockState();

			for (int l = 0; l < 4; ++l) {
				i = Mth.floor(this.getX() + (double)((float)(l % 2 * 2 - 1) * 0.25F));
				j = Mth.floor(this.getY());
				k = Mth.floor(this.getZ() + (double)((float)(l / 2 % 2 * 2 - 1) * 0.25F));
				BlockPos blockpos = new BlockPos(i, j, k);
				biome = this.level().getBiome(blockpos).get(); // TODO: suppress if no crash?
				if (this.level().getBlockState(blockpos).isAir() && ((BiomeAccessor) (Object) biome).getTemperatureAt(blockpos) < 0.8F && blockstate.canSurvive(this.level(), blockpos)) {
					this.level().setBlockAndUpdate(blockpos, blockstate);
				}
			}
		}

	}
}
