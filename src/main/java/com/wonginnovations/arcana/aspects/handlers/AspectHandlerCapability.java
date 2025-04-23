package com.wonginnovations.arcana.aspects.handlers;

import com.wonginnovations.arcana.Arcana;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AspectHandlerCapability {

	/* Capability management */

	public static Capability<AspectHandler> ASPECT_HANDLER_CAPABILITY =
			CapabilityManager.get(new CapabilityToken<>() {
			});

	public static final ResourceLocation KEY = new ResourceLocation(Arcana.MODID, "new_aspect_handler_capability");

	/* Capability provider */

	public static class Provider implements ICapabilitySerializable<CompoundTag> {

		protected final AspectHandler cap;

		public Provider(AspectHandler pHandler) {
			cap = pHandler;
		}

		public CompoundTag serializeNBT() {
			return cap.serializeNBT();
		}

		public void deserializeNBT(CompoundTag nbt) {
			cap.deserializeNBT(nbt);
		}

		@SuppressWarnings("unchecked")
		@Nonnull
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
			return capability == ASPECT_HANDLER_CAPABILITY ? LazyOptional.of(() -> (T)cap) : LazyOptional.empty();
		}

	}

}
