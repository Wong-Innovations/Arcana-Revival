package com.wonginnovations.arcana.mixin;

import com.wonginnovations.arcana.client.PoseHandler;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> extends HumanoidModel<T> {

	public HumanoidModelMixin(ModelPart pRoot) {
		super(pRoot);
	}

	@SuppressWarnings("ConstantConditions")
	@Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
	        at = @At("TAIL"))
	private void applyPose(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
		// yes I assure you
		// (PlayerModel<?>)((Object)this) is completely necessary
		// and there is no better way
		PoseHandler.applyPose((PlayerModel<?>)((Object)this), entity);
	}

}