package com.wonginnovations.arcana.systems.research.impls;

import com.wonginnovations.arcana.capabilities.Researcher;
import com.wonginnovations.arcana.systems.research.Parent;
import com.wonginnovations.arcana.systems.research.Requirement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import static com.wonginnovations.arcana.Arcana.arcLoc;

public class ResearchCompletedRequirement extends Requirement{
	
	public static final ResourceLocation TYPE = arcLoc("research_completed");
	
	protected Parent req;
	
	public ResearchCompletedRequirement(String req) {
		this.req = Parent.parse(req);
	}
	
	public boolean satisfied(Player player) {
		return req.satisfiedBy(Researcher.getFrom(player));
	}
	
	public void take(Player player) {
		// no-op
	}
	
	public ResourceLocation type() {
		return TYPE;
	}
	
	public CompoundTag data() {
		CompoundTag compound = new CompoundTag();
		compound.putString("requirement", req.asString());
		return compound;
	}
}