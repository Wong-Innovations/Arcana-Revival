package com.wonginnovations.arcana.client.model.tainted;

import com.wonginnovations.arcana.entities.tainted.TaintedEntity;
import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TaintedZombieModel<T extends TaintedEntity> extends AbstractZombieModel<T> {

	public TaintedZombieModel(ModelPart root) {
		super(root);
	}
	
	public boolean isAggressive(T entity) {
		return entity.isAggressive();
	}

}