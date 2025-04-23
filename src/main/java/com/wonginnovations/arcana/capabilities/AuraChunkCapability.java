package com.wonginnovations.arcana.capabilities;

import com.wonginnovations.arcana.world.Node;
import com.wonginnovations.arcana.world.NodeType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;

import static com.wonginnovations.arcana.Arcana.arcLoc;

public class AuraChunkCapability implements AuraChunk {

	Collection<Node> nodes = new ArrayList<>();
	float taint;

	public void addNode(Node node) {
		nodes.add(node);
	}

	public void removeNode(Node node) {
		nodes.remove(node);
	}

	public void setNodes(Collection<Node> nodes) {
		this.nodes = nodes;
	}

	public Collection<Node> getNodes() {
		return new ArrayList<>(nodes);
	}

	public Collection<Node> getNodesWithinAABB(AABB bounds) {
		Collection<Node> set = new ArrayList<>();
		for (Node node : getNodes())
			if (bounds.contains(node.getX(), node.getY(), node.getZ()))
				set.add(node);
		return set;
	}

	public Collection<Node> getNodesOfType(NodeType type) {
		Collection<Node> set = new ArrayList<>();
		for (Node node : getNodes())
			if (node.type().equals(type))
				set.add(node);
		return set;
	}

	public Collection<Node> getNodesOfTypeWithinAABB(NodeType type, AABB bounds) {
		Collection<Node> set = new ArrayList<>();
		for (Node node : getNodes())
			if (node.type().equals(type))
				if (bounds.contains(node.getX(), node.getY(), node.getZ()))
					set.add(node);
		return set;
	}

	public float getFluxLevel() {
		return taint;
	}

	public void addFlux(float amount) {
		taint += amount;
		taint = Math.max(taint, 0);
	}

	public void setFlux(float newTaint) {
		taint = Math.max(newTaint, 0);
	}

	public CompoundTag serializeNBT() {
		// Just make a list of CompoundTags from each node.
		CompoundTag compound = new CompoundTag();
		ListTag data = new ListTag();
		for (Node node : nodes)
			data.add(node.serializeNBT());
		compound.put("nodes", data);
		compound.putFloat("flux", taint);
		return compound;
	}

	public void deserializeNBT(@Nonnull CompoundTag data) {
		// Go through the list and deserialize each entry
		ListTag list = data.getList("nodes", Tag.TAG_COMPOUND);
		Collection<Node> nodeSet = new ArrayList<>(list.size());
		for (Tag nodeNBT : list)
			if (nodeNBT instanceof CompoundTag)
				nodeSet.add(Node.fromNBT((CompoundTag)nodeNBT));
		nodes = nodeSet;
		taint = data.getFloat("flux");
		// load old integer taint
		if (data.contains("taint"))
			taint += data.getInt("taint");
	}

	/* Capability management */

	public static Capability<AuraChunk> AURA_CHUNK_CAPABILITY =
			CapabilityManager.get(new CapabilityToken<>() {
			});
	
	public static final ResourceLocation KEY = arcLoc("node_chunk_capability");

	/* Capability provider */

	public static class Provider implements ICapabilitySerializable<CompoundTag>{
		
		private final AuraChunk cap = new AuraChunkCapability();
		
		public CompoundTag serializeNBT() {
			return cap.serializeNBT();
		}
		
		public void deserializeNBT(CompoundTag nbt) {
			cap.deserializeNBT(nbt);
		}
		
		@Nonnull
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
			return capability == AURA_CHUNK_CAPABILITY ? LazyOptional.of(() -> (T)cap) : LazyOptional.empty();
		}

	}

}