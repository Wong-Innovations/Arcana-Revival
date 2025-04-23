package com.wonginnovations.arcana.blocks.entities;

import net.minecraft.MethodsReturnNonnullByDefault;
import com.wonginnovations.arcana.ArcanaConfig;
import com.wonginnovations.arcana.aspects.AspectLabel;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.Aspects;
import com.wonginnovations.arcana.aspects.VisShareable;
import com.wonginnovations.arcana.aspects.handlers.AspectBattery;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.aspects.handlers.AspectHandlerCapability;
import com.wonginnovations.arcana.aspects.handlers.VisUtils;
import com.wonginnovations.arcana.blocks.ArcanaBlocks;
import com.wonginnovations.arcana.blocks.JarBlock;
import com.wonginnovations.arcana.blocks.pipes.TubeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.Collections;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JarBlockEntity extends BlockEntity implements VisShareable {
	private final JarBlock.Type jarType;
	public AspectBattery vis = new AspectBattery(/*1, 100*/);
	public AspectLabel label;
	private double lastVis;
	
	private double clientVis;
	private final double visAnimationSpeed = ArcanaConfig.JAR_ANIMATION_SPEED.get();
	
	private static final int MAX_PUSH = 4;
	
	public JarBlockEntity(JarBlock.Type type, BlockPos pos, BlockState state) {
		super(ArcanaBlockEntities.JAR.get(), pos, state);
		this.jarType = type;
		vis = new AspectBattery();
		vis.initHolders(100, 1);
		vis.getHolder(0).setVoids(type == JarBlock.Type.VOID);
	}
	
	/**
	 * @deprecated This is required when BlockEntity is created. Please use JarBlockEntity(JarBlock.Type).
	 */
	@Deprecated
	public JarBlockEntity(BlockPos pos, BlockState state) {
		this(JarBlock.Type.BASIC, pos, state);
	}
	
	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		vis.deserializeNBT(compound.getCompound("aspects"));
		clientVis = vis.getHolder(0).getStack().getAmount();
		if (compound.contains("label")) {
			if (label == null)
				label = new AspectLabel(Direction.from3DDataValue(compound.getInt("label")));
			else
				label.direction = Direction.from3DDataValue(compound.getInt("label"));
			label.seal = AspectUtils.getAspect(compound, "seal");
		}
	}
	
	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		CompoundTag aspectsNbt = vis.serializeNBT();
		compound.put("aspects", aspectsNbt);
		if (label != null) {
			compound.putInt("label", label.direction.ordinal());
			AspectUtils.putAspect(compound, "seal", label.seal);
		}
	}
	
	public Color getAspectColor() {
		if (this.getLevel().getBlockState(this.getBlockPos().below()).getBlock() == ArcanaBlocks.ASPECT_TESTER.get())
			return getCreativeJarColor();
		else
			return !vis.getHolder(0).getStack().isEmpty() ? new Color(vis.getHolder(0).getStack().getAspect().getColorRange().get(2)) : Color.WHITE;
	}
	
	public int nextColor = 0;
	
	public Color getCreativeJarColor() {
		nextColor++;
		if (nextColor >= 800)
			nextColor = 0;
		
		final int ARRAY_SIZE = 100;
		double jump = 360.0 / (ARRAY_SIZE * 1.0);
		int[] colors = new int[ARRAY_SIZE];
		for (int i = 0; i < colors.length; i++)
			colors[i] = Color.HSBtoRGB((float)(jump * i), 1.0f, 1.0f);
		return new Color(colors[nextColor / 8]);
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
		if (cap == AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY)
			return vis.getCapability(AspectHandlerCapability.ASPECT_HANDLER_CAPABILITY).cast();
		return LazyOptional.empty();
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return getCapability(cap);
	}
	
	public CompoundTag getUpdateTag() {
		CompoundTag tag = new CompoundTag();
		saveAdditional(tag);
		return tag;
	}
	
	@Override
	public boolean isVisShareable() {
		return true;
	}
	
	@Override
	public boolean isManual() {
		return false;
	}
	
	@Override
	public boolean isSecure() {
		return jarType == JarBlock.Type.SECURED;
	}

	public void tick(Level level, BlockPos pos, BlockState state) {
		if (label != null && (label.seal == Aspects.EMPTY && label.seal != vis.getHolder(0).getStack().getAspect())) {
			label.seal = vis.getHolder(0).getStack().getAspect();
			vis.getHolder(0).setWhitelist(Collections.singletonList(label.seal));
		}
		double newVis = vis.getHolder(0).getStack().getAmount();
		if (lastVis != newVis && level != null)
			level.updateNeighbourForOutputSignal(pos, level.getBlockState(pos).getBlock());
		lastVis = newVis;
		if (!getJarType().equals(JarBlock.Type.SECURED))
			vis.getHolder(0).getStack().getAspect().aspectTick(this);
		if (clientVis > newVis)
			clientVis = Math.max(clientVis - visAnimationSpeed, newVis);
		else if (clientVis < newVis)
			clientVis = Math.min(clientVis + visAnimationSpeed, newVis);
		
		BlockEntity entity = level.getBlockEntity(pos.above());
		if (entity instanceof TubeBlockEntity)
			if (getJarType() == JarBlock.Type.VACUUM) // pull in
				VisUtils.moveAllAspects(AspectHandler.getFrom(entity), vis, MAX_PUSH);
			else if (getJarType() == JarBlock.Type.PRESSURE) // push out
				VisUtils.moveAllAspects(vis, AspectHandler.getFrom(entity), MAX_PUSH);
	}
	
	public double getClientVis(float partialTicks) {
		float newVis = vis.getHolder(0).getStack().getAmount();
		if (clientVis > newVis)
			return Math.max(clientVis - (visAnimationSpeed * partialTicks), newVis);
		else if (clientVis < newVis)
			return Math.min(clientVis + (visAnimationSpeed * partialTicks), newVis);
		return clientVis;
	}
	
	public JarBlock.Type getJarType() {
		return jarType;
	}
	
	public ResourceLocation getPaperAspectLocation() {
		return new ResourceLocation((label != null ? label.seal : Aspects.EMPTY).toResourceLocation().toString()
				.replace(":", ":block/models/labels/paper_"));
	}
	
	public @Nullable
	Direction getLabelSide() {
		return label != null ? label.direction : null;
	}
}