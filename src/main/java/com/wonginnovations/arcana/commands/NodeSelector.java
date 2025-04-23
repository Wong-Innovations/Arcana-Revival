package com.wonginnovations.arcana.commands;

import com.wonginnovations.arcana.world.AuraView;
import com.wonginnovations.arcana.world.Node;
import com.wonginnovations.arcana.world.ServerAuraView;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

public class NodeSelector {
	
	/** false = nearest, true = in */
	private final boolean type;
	private final AABB aabb;
	private final int max;
	
	public NodeSelector(boolean type, AABB aabb, int max) {
		this.type = type;
		this.aabb = aabb;
		this.max = max;
	}
	
	public int getMax() {
		return max;
	}
	
	public Collection<Node> select(CommandSourceStack source) {
		if (type) {
			// in aabb
			AuraView view = new ServerAuraView(source.getLevel());
			return view.getNodesWithinAABB(aabb);
		} else {
			// nearest to caller
			Vec3 caller = source.getPosition();
			// get all nodes in a 300x300 chunk area
			AuraView view = new ServerAuraView(source.getLevel());
			Collection<Node> ranged = new ArrayList<>(view.getNodesWithinAABB(new AABB(caller.x - 150, 0, caller.z - 150, caller.x + 150, 256, caller.z + 150)));
			// sort by distance
			return ranged.stream()
					.sorted(Comparator.comparingDouble(node -> new Vec3(node.getX(), node.getY(), node.getZ()).distanceTo(caller)))
					.limit(max)
					.collect(Collectors.toList());
		}
	}
}