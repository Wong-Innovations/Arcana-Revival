package com.wonginnovations.arcana.client;

import com.wonginnovations.arcana.items.WandItem;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class PoseHandler {
	
	public static void applyPose(PlayerModel<?> model, Entity entity) {
		if (entity instanceof Player player) {
            // If we're using a wand,
			if (player.getUseItem().getItem() instanceof WandItem) {
				// point where we're facing.
				// TODO: better draining pose
				ModelPart arm = player.getUsedItemHand() == InteractionHand.MAIN_HAND ? model.rightArm : model.leftArm;
				Vec3 facing = player.getLookAngle();
				arm.xRot = -((float)facing.y) - 1.5f;
			}
		}
	}
}