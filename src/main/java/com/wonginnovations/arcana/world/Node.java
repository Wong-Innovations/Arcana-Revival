package com.wonginnovations.arcana.world;

import com.wonginnovations.arcana.aspects.handlers.AspectBattery;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

// implements Vec3i for BlockPos constructor convenience
public class Node extends Vec3i {
	
	/** The aspects contained in the node. */
	AspectHandler aspects;
	/** The type of node this is. */
	NodeType type;
	/** The unique ID of this node. */
	UUID nodeUniqueId = UUID.randomUUID();
	/** The time, in ticks, until the node gains some aspects. */
	int timeUntilRecharge;
	/** Any extra data, used by the node type (e.g. hungry nodes). */
	CompoundTag data = new CompoundTag();
	/** Client only, no serialization needed. */
	public boolean isEmittingNoise = false;
	
	public Node(AspectHandler aspects, NodeType type, int x, int y, int z, int timeUntilRecharge) {
		super(x, y, z);
		this.aspects = aspects;
		this.type = type;
		this.timeUntilRecharge = timeUntilRecharge;
	}
	
	public Node(AspectHandler aspects, NodeType type, int x, int y, int z, int timeUntilRecharge, CompoundTag data) {
		super(x, y, z);
		this.aspects = aspects;
		this.type = type;
		this.timeUntilRecharge = timeUntilRecharge;
		this.data = data;
	}
	
	public Node(AspectHandler aspects, NodeType type, int x, int y, int z, int timeUntilRecharge, UUID nodeUniqueId, CompoundTag data) {
		super(x, y, z);
		this.aspects = aspects;
		this.type = type;
		this.timeUntilRecharge = timeUntilRecharge;
		this.nodeUniqueId = nodeUniqueId;
		this.data = data;
	}
	
	public NodeType type() {
		return type;
	}
	
	public CompoundTag serializeNBT() {
		CompoundTag nbt = new CompoundTag();
		nbt.putString("type", NodeType.TYPES.inverse().get(type()).toString());
		nbt.put("aspects", aspects.serializeNBT());
		nbt.putInt("x", getX());
		nbt.putInt("y", getY());
		nbt.putInt("z", getZ());
		nbt.putUUID("nodeUniqueId", nodeUniqueId);
		nbt.putInt("timeUntilRecharge", timeUntilRecharge);
		nbt.put("data", data);
		return nbt;
	}
	
	public static Node fromNBT(CompoundTag nbt) {
		AspectHandler aspects = new AspectBattery();
		aspects.deserializeNBT(nbt.getCompound("aspects"));
		NodeType type = NodeType.TYPES.get(new ResourceLocation(nbt.getString("type")));
		int x = nbt.getInt("x"), y = nbt.getInt("y"), z = nbt.getInt("z");
		int timeUntilRecharge = nbt.getInt("timeUntilRecharge");
		CompoundTag data = nbt.getCompound("data");
		return nbt.hasUUID("nodeUniqueId") ? new Node(aspects, type, x, y, z, timeUntilRecharge, nbt.getUUID("nodeUniqueId"), data) : new Node(aspects, type, x, y, z, timeUntilRecharge, data);
	}

	public Vec3 getPosition(){
		return new Vec3(getX(), getY(), getZ());
	}
	
	public AspectHandler getAspects() {
		return aspects;
	}
	
	public int getTimeUntilRecharge() {
		return timeUntilRecharge;
	}
	
	public void setType(NodeType type) {
		this.type = type;
	}
	
	public void setTimeUntilRecharge(int timeUntilRecharge) {
		this.timeUntilRecharge = timeUntilRecharge;
	}
	
	public UUID nodeUniqueId() {
		return nodeUniqueId;
	}
	
	public CompoundTag getData() {
		return data;
	}
	
	public String toString() {
		return "Node{" +
				"aspects=" + aspects +
				", type=" + type +
				", x=" + getX() +
				", y=" + getY() +
				", z=" + getZ() +
				", nodeUniqueId=" + nodeUniqueId +
				", timeUntilRecharge=" + timeUntilRecharge +
				", data=" + data +
				'}';
	}
}