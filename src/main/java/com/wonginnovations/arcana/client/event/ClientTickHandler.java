package com.wonginnovations.arcana.client.event;

import com.wonginnovations.arcana.ArcanaSounds;
import com.wonginnovations.arcana.aspects.Aspect;
import com.wonginnovations.arcana.aspects.AspectStack;
import com.wonginnovations.arcana.aspects.AspectUtils;
import com.wonginnovations.arcana.aspects.handlers.AspectHandler;
import com.wonginnovations.arcana.aspects.handlers.AspectHolder;
import com.wonginnovations.arcana.blocks.entities.AlembicBlockEntity;
import com.wonginnovations.arcana.blocks.entities.AspectBookshelfBlockEntity;
import com.wonginnovations.arcana.blocks.entities.CrucibleBlockEntity;
import com.wonginnovations.arcana.blocks.entities.JarBlockEntity;
import com.wonginnovations.arcana.client.render.particles.AspectParticleData;
import com.wonginnovations.arcana.client.render.particles.NumberParticleData;
import com.wonginnovations.arcana.items.ArcanaItems;
import com.wonginnovations.arcana.items.settings.GogglePriority;
import com.wonginnovations.arcana.util.LocalAxis;
import com.wonginnovations.arcana.util.Pair;
import com.wonginnovations.arcana.util.RayTraceUtils;
import com.wonginnovations.arcana.world.AuraView;
import com.wonginnovations.arcana.world.Node;
import com.wonginnovations.arcana.world.NodeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientTickHandler {

	private ClientTickHandler() {
	}

	public static int ticksWithLexicaOpen = 0;
	public static int pageFlipTicks = 0;
	public static int ticksInGame = 0;
	public static float partialTicks = 0;
	public static float delta = 0;
	public static float total = 0;

	private static void calcDelta() {
		float oldTotal = total;
		total = ticksInGame + partialTicks;
		delta = total - oldTotal;
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void renderTick(TickEvent.RenderTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (event.phase == TickEvent.Phase.START) {
			partialTicks = event.renderTickTime;

			if (mc.isPaused()) {
				// If game is paused, need to use the saved value. The event is always fired with the "true" value which
				// keeps updating when paused. See RenderTickEvent fire site for details, remove when MinecraftForge#6991 is resolved
//				partialTicks = ((AccessorMinecraft)mc).getRenderPartialTicksPaused(); // TODO: no longer needed, verify
				partialTicks = mc.getPartialTick();
			}
		} else {
			calcDelta();
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public static void clientTickEnd(TickEvent.ClientTickEvent event) {
		Minecraft mc = Minecraft.getInstance();
		Level level = mc.level;
		LocalPlayer player = mc.player;

		if (player != null && level != null) {
			if (event.phase == TickEvent.Phase.END) {
				if (!Minecraft.getInstance().isPaused()) {
					ticksInGame++;
					partialTicks = 0;

					int ticksToOpen = 10;

					InteractionHand hand = null;
					if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == ArcanaItems.ARCANUM.get())
						hand = InteractionHand.MAIN_HAND;
					if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() == ArcanaItems.ARCANUM.get())
						hand = InteractionHand.OFF_HAND;

					if (hand != null)
						if (player.getItemInHand(hand).getOrCreateTag().getBoolean("open")) {
							if (ticksWithLexicaOpen < 0) {
								ticksWithLexicaOpen = 0;
							}
							if (ticksWithLexicaOpen < ticksToOpen) {
								ticksWithLexicaOpen++;
							}
							if (pageFlipTicks > 0) {
								pageFlipTicks--;
							}
						} else {
							pageFlipTicks = 0;
							if (ticksWithLexicaOpen > 0) {
								if (ticksWithLexicaOpen > ticksToOpen) {
									ticksWithLexicaOpen = ticksToOpen;
								}
								ticksWithLexicaOpen--;
							}
						}
				}

				double reach = player.getAttribute(ForgeMod.BLOCK_REACH.get()).getValue();
				BlockPos pos = RayTraceUtils.getTargetBlockPos(player, level, (int)reach);
				BlockEntity te = level.getBlockEntity(pos);
				GogglePriority priority = GogglePriority.getClientGogglePriority();

				AuraView view = AuraView.SIDED_FACTORY.apply(player.level());
				Collection<Node> nodes = view.getNodesWithinAABB(player.getBoundingBox().inflate(8));
				// Play node sounds
				if (!nodes.isEmpty()) {
					for (Node node : nodes) {
						NodeType type = node.type();
						if (type == NodeType.HUNGRY)
							ArcanaSounds.playSoundOnce(player, new BlockPos(node), ArcanaSounds.arcana_hunger_node.get(), SoundSource.AMBIENT, 0.4f, 1.0f);
						else if (type == NodeType.NORMAL || type == NodeType.BRIGHT || type == NodeType.PURE || type == NodeType.PALE)
							ArcanaSounds.playSoundOnce(player, new BlockPos(node), ArcanaSounds.arcananodes.get(), SoundSource.AMBIENT, 0.4f, 1.0f);
						else
							ArcanaSounds.playSoundOnce(player, new BlockPos(node), ArcanaSounds.arcananodesnegative.get(), SoundSource.AMBIENT, 0.4f, 1.0f);
					}
				}

				// Render aspect particle around Node
				if (priority == GogglePriority.SHOW_ASPECTS) {
					Vec3 position = player.getEyePosition(Minecraft.getInstance().getPartialTick());
					view.raycast(position, reach, player).ifPresent(node -> {
						List<AspectHolder> holders = node.getAspects().getHolders();
						ArrayList<Pair<Aspect, Float>> stacks = new ArrayList<>();
						for (AspectHolder holder : holders)
							stacks.add(Pair.of(holder.getStack().getAspect(), holder.getStack().getAmount()));

						for (int i = 0, size = stacks.size(); i < size; i++) {
							Pair<Aspect, Float> stack = stacks.get(i);
							// let's place them on X/Y positions on a local axis, then convert to world co-ords
							// then its as easy as picking positions on a circle
							float angle = (float)(Math.toRadians(360 * i) / size);
							// TODO: ease in/out (multiply by some fraction)
							Vec3 localPos = new Vec3(Mth.sin(angle) * (size / 5f), Mth.cos(angle) * (size / 5f), 0);
							Vec3 wPos = LocalAxis.toAbsolutePos(localPos, new Vec2(player.getXRot(), player.getYRot()), node.getPosition());
							// why?
							level.addParticle(new AspectParticleData(AspectUtils.getAspectParticleLocation(stack.getFirst())), wPos.x, wPos.y, wPos.z, 0, 0, 0);
							// TODO: client reference (UiUtil::tooltipColor)
							Vec3 numberPos = new Vec3(Mth.sin(angle) * ((size / 5f) - .04), Mth.cos(angle) * ((size / 5f) - .04), -.1);
							wPos = LocalAxis.toAbsolutePos(numberPos, new Vec2(player.getXRot(), player.getYRot()), node.getPosition());
							renderNumberParticles(wPos.x, wPos.y, wPos.z, player.getViewYRot(0), stack.getSecond(), level);
						}
					});

					// Render aspect particle around Jar
					if (te instanceof JarBlockEntity) {
						if (level.isClientSide()) {
							JarBlockEntity jte = (JarBlockEntity)te;
							// If player has googles show particle
							if (!jte.vis.getHolder(0).getStack().isEmpty()) {
								// track player head rotation
								double srx = (-Math.sin(Math.toRadians(player.getXRot())));
								double crx = (Math.cos(Math.toRadians(player.getXRot())));
								// Add Aspect Particle
								level.addParticle(new AspectParticleData(new ResourceLocation(AspectUtils.getAspectTextureLocation(jte.vis.getHolder(0).getStack().getAspect()).toString().replace("textures/", "").replace(".png", ""))),
										pos.getX() + 0.5D + ((-srx) / 2), pos.getY() + 0.8D, pos.getZ() + 0.5D + ((-crx) / 2), 0, 0, 0);
								float currVis = jte.vis.getHolder(0).getStack().getAmount();
								// Add Number Particles
								// If you change Y, particle is no more good aligned with particle
								renderNumberParticles(pos.getX() + 0.5D + ((-srx * 1.01) / 2), pos.getY() + 0.8D, pos.getZ() + 0.5D + ((-crx * 1.01) / 2), player.getXRot(), currVis, level);
							}
						}
					}
					// Render aspect particle around Phialshelf
					if (te instanceof AspectBookshelfBlockEntity) {
						if (level.isClientSide()) {
							AspectBookshelfBlockEntity abte = (AspectBookshelfBlockEntity)te;
							// If player has googles show particle
							AspectHandler vis = AspectHandler.getFrom(abte);
							if (vis != null) {
								// Get all stacks from phialshelf
								ArrayList<AspectStack> stacks = new ArrayList<>();
								for (int i = 0; i < vis.countHolders(); i++)
									if (vis.getHolder(i) != null && !vis.getHolder(i).getStack().isEmpty())
										stacks.add(vis.getHolder(i).getStack());
								// Squish aspect stacks in to reducedStacks
								List<AspectStack> reducedStacks = AspectUtils.squish(stacks);
								renderAspectAndNumberParticlesInCircle(level, new Vec3(pos.getX(), pos.getY(), pos.getZ()), player, reducedStacks.stream().map(stack -> Pair.of(stack.getAspect(), stack.getAmount())).collect(Collectors.toList()),2);
							}
						}
					}

					// Render aspect particle around CrucibleBlockEntity and AlembicBlockEntity
					if (te instanceof AlembicBlockEntity abe) {
						if (level.isClientSide()) {
                            // If player has googles show particle
							AspectHandler vis = AspectHandler.getFrom(abe);
							if (vis != null) {
								// Squish aspect stacks in to reducedStacks
								// FIXME: There's no dot particle, and numbers are misaligned with aspects
								//List<AspectStack> reducedStacks = AspectUtils.squish(vis.getHolders().stream().map(AspectHolder::getStack).collect(Collectors.toList()));
								//renderAspectAndNumberParticlesInCircle(world, new Vector3d(pos.getX(), pos.getY(), pos.getZ()), player, reducedStacks.stream().map(stack -> Pair.of(stack.getAspect(), stack.getAmount())).collect(Collectors.toList()), 1.4f);
							}
						}
					}

					// Render aspect particle around CrucibleBlockEntity and AlembicBlockEntity
					if (te instanceof CrucibleBlockEntity) {
						if (level.isClientSide()) {
							CrucibleBlockEntity crte = (CrucibleBlockEntity)te;
							List<AspectStack> reducedStacks = AspectUtils.squish(new ArrayList<>(crte.getAspectStackMap().values()));
							reducedStacks.remove(new AspectStack());
							renderAspectAndNumberParticlesInCircle(level, new Vec3(pos.getX(), pos.getY(), pos.getZ()), player, reducedStacks.stream().map(stack -> Pair.of(stack.getAspect(), stack.getAmount())).collect(Collectors.toList()),1.4f);
						}
					}
				}

				//Spell.updateSpellStatusBar(player);

				calcDelta();
			}
		}
	}

	protected static void renderAspectAndNumberParticlesInCircle(Level level, Vec3 pos, LocalPlayer player, List<Pair<Aspect, Float>> aspectStacks, float ringReduceSize) {
		//aspectStacks.removeIf(x -> x.getFirst() == null || x.getFirst() == Aspects.EMPTY);
		float[] v = spreadVertices(aspectStacks.size(), 32);
		for (int i = 0; i < aspectStacks.size(); i++) {
			double centerSpread = v[i];
			// track player head rotation
			double srx = (-Math.sin(Math.toRadians(player.getXRot() + centerSpread + 10)));
			double crx = (Math.cos(Math.toRadians(player.getXRot() + centerSpread + 10)));
			// Add Aspect Particle
			level.addParticle(new AspectParticleData(AspectUtils.getAspectParticleLocation(aspectStacks.get(i).getFirst())),
					pos.x + 0.5D + (((-srx) / ringReduceSize)), pos.y + 0.8D, pos.z + 0.5D + (((-crx) / ringReduceSize)), 0, 0, 0);
			float currVis = aspectStacks.get(i).getSecond();
			// Add Number Particles
			// If you change Y, particle is no more good aligned with particle
			renderNumberParticles(pos.x + 0.5D + ((-srx * 1.01) / ringReduceSize), pos.y + 0.8D, pos.z + 0.5D + ((-crx * 1.01) / ringReduceSize), player.getXRot(), currVis, level);
		}
	}

	@SuppressWarnings("unused")
	private static void renderNumberParticles(double baseX, double baseY, double baseZ, float rotation, float number, Level level) {
		String numberStr = (number % 1 > 0) ? String.format("%.1f", number) : String.format("%.0f", number);
		char[] array = numberStr.split(",")[0].toCharArray();
		double rotOffsetX = -Math.cos(Math.toRadians(rotation));
		double rotOffsetZ = -Math.sin(Math.toRadians(rotation));
		double size = .1;
		double padding = .8;
		double center = -.66;
		double x = baseX - array.length * rotOffsetX * size * center;
		double z = baseZ - array.length * rotOffsetZ * size * center;
		for (int i = 0, length = array.length; i < length; i++) {
			char c = array[i];
			level.addParticle(new NumberParticleData(c), false, x + rotOffsetX * i * size * padding, baseY - .12, z + rotOffsetZ * i * size * padding, 0, 0, 0);
		}
	}

	private static float[] spreadVertices(float amount, float padding) {
		float[] r = new float[(int)amount];
		for (int i = 0; i < amount; i++) {
			r[i] = i - (amount / 2);
			r[i] *= padding;
		}
		return r;
	}
}
