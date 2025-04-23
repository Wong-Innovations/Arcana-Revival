package com.wonginnovations.arcana.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.wonginnovations.arcana.world.Node;
import com.wonginnovations.arcana.world.NodeType;
import com.wonginnovations.arcana.world.ServerAuraView;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;

import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.Commands.argument;

public class NodeCommand {
	
	private static final SuggestionProvider<CommandSourceStack> SUGGEST_TYPES = (ctx, builder) -> SharedSuggestionProvider.suggestResource(NodeType.TYPES.keySet().stream(), builder);
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		// arcana-nodes <add|remove|modify>
		// arcana-nodes add <type> <x> <y> <z>
		// arcana-nodes remove <nodes>
		// arcana-nodes modify <nodes> <new type>
		// arcana-nodes info <nodes>
		// <nodes> = nearest | in[x1,y1,z1,x2,y2,z2] | nearest[max]
		// nearest is limited to 300x300 blocks centred on caller
		dispatcher.register(
				literal("arcana-nodes").requires(source -> source.hasPermission(2))
				.then(literal("add")
						.then(argument("type", ResourceLocationArgument.id())
								.then(argument("position", Vec3Argument.vec3())
										.executes(NodeCommand::add)
								)
								.suggests(SUGGEST_TYPES)
						)
				)
				.then(literal("remove")
						.then(argument("nodes", NodeArgument.nodes())
								.executes(NodeCommand::remove)
						)
				)
				.then(literal("modify")
						.then(argument("nodes", NodeArgument.nodes())
								.then(argument("type", ResourceLocationArgument.id())
										.executes(NodeCommand::modify)
										.suggests(SUGGEST_TYPES)
								)
						)
				)
				.then(literal("info")
						.then(argument("nodes", NodeArgument.nodes())
								.executes(NodeCommand::info)
						)
				)
		);
	}
	
	public static int add(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		ResourceLocation type = ResourceLocationArgument.getId(ctx, "type");
		NodeType nt;
		if (!NodeType.TYPES.containsKey(type)) {
			// throw exception "nonexistent type"
			Message noSuchEntry = Component.translatable("commands.arcana.nodes.no_type", type.toString());
			throw new CommandSyntaxException(new SimpleCommandExceptionType(noSuchEntry), noSuchEntry);
		} else
			nt = NodeType.TYPES.get(type);
		Vec3 loc = Vec3Argument.getVec3(ctx, "position");
		BlockPos pos = BlockPos.containing(loc);
		Node node = new Node(nt.genBattery(pos, ctx.getSource().getLevel(), ctx.getSource().getLevel().random), nt, pos.getX(), pos.getY(), pos.getZ(), 0);
		ServerAuraView view = new ServerAuraView(ctx.getSource().getLevel());
		boolean added = view.addNode(node);
		// Send PkSyncChunkNodes and feedback
		if (added) {
			view.sendChunkToClients(node);
			ctx.getSource().sendSuccess(() -> Component.translatable("commands.arcana.nodes.add_success"), true);
			return 1;
		}
		ctx.getSource().sendFailure(Component.translatable("commands.arcana.nodes.add_fail"));
		return 0;
	}
	
	public static int remove(CommandContext<CommandSourceStack> ctx) {
		Collection<Node> nodes = NodeArgument.getNodes(ctx, "nodes");
		if (nodes.isEmpty()) {
			// send feedback
			ctx.getSource().sendFailure(Component.translatable("commands.arcana.nodes.empty_selection"));
			return 0;
		}
		
		ServerAuraView view = new ServerAuraView(ctx.getSource().getLevel());
		Collection<Node> removed = new ArrayList<>();
		for (Node node : nodes)
			if (view.removeNode(node))
				removed.add(node);
			
		if (removed.isEmpty())
			ctx.getSource().sendFailure(Component.translatable("commands.arcana.nodes.remove_fail"));
		else if (removed.size() == 1)
			ctx.getSource().sendSuccess(() -> Component.translatable("commands.arcana.nodes.remove_success.single"), true);
		else
			ctx.getSource().sendSuccess(() -> Component.translatable("commands.arcana.nodes.remove_success.many", removed.size()), true);
		
		view.sendAllChunksToClients(removed);
		return removed.size();
	}
	
	public static int modify(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
		Collection<Node> nodes = NodeArgument.getNodes(ctx, "nodes");
		if (nodes.isEmpty()) {
			// send feedback
			ctx.getSource().sendFailure(Component.translatable("commands.arcana.nodes.empty_selection"));
			return 0;
		}
		
		ResourceLocation type = ResourceLocationArgument.getId(ctx, "type");
		NodeType nt;
		if (!NodeType.TYPES.containsKey(type)) {
			// throw exception "nonexistent type"
			Message noSuchEntry = Component.translatable("commands.arcana.nodes.no_type", type.toString());
			throw new CommandSyntaxException(new SimpleCommandExceptionType(noSuchEntry), noSuchEntry);
		} else
			nt = NodeType.TYPES.get(type);
		
		ServerAuraView view = new ServerAuraView(ctx.getSource().getLevel());
		for (Node node : nodes)
			node.setType(nt);
		
		if (nodes.size() == 1)
			ctx.getSource().sendSuccess(() -> Component.translatable("commands.arcana.nodes.modify_success.single"), true);
		else
			ctx.getSource().sendSuccess(() -> Component.translatable("commands.arcana.nodes.modify_success.many", nodes.size()), true);
		
		view.sendAllChunksToClients(nodes);
		return nodes.size();
	}
	
	public static int info(CommandContext<CommandSourceStack> ctx) {
		Collection<Node> nodes = NodeArgument.getNodes(ctx, "nodes");
		ctx.getSource().sendSuccess(() -> Component.translatable("commands.arcana.nodes.info", nodes), true);
		return nodes.size();
	}
}