package com.wonginnovations.arcana.blocks.entities;

import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.aspects.handlers.AspectBattery;
import com.wonginnovations.arcana.aspects.handlers.AspectCell;
import com.wonginnovations.arcana.aspects.handlers.AspectHandlerCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class AspectTesterBlockEntity extends BlockEntity {
	
	public AspectBattery battery = new AspectBattery(/*100,100*/);
	
	public AspectTesterBlockEntity(BlockPos pos, BlockState state) {
		super(ArcanaBlockEntities.ASPECT_TESTER.get(), pos, state);
		init();
	}
	
	private void init() {
		AspectCell cell = new AspectCell(100);
		cell.insert(new AspectStack(Aspects.EXCHANGE, 16), false);
		battery.getHolders().add(cell);
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
		if (cap.equals(AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY))
			return battery.getCapability(cap).cast();
		return null;
	}
}
