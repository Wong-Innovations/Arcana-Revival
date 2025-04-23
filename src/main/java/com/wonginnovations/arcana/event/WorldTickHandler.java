package com.wonginnovations.arcana.event;

import com.google.common.collect.ConcurrentHashMultiset;
import com.wonginnovations.arcana.world.AuraView;
import com.wonginnovations.arcana.world.ServerAuraView;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber
public class WorldTickHandler{
	
	public static Collection<Consumer<Level>> onTick = ConcurrentHashMultiset.create();
	
	@SubscribeEvent
	public static void tickEnd(TickEvent.LevelTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			Level level = event.level;
			
			if (level instanceof ServerLevel serverLevel) {
                AuraView view = new ServerAuraView(serverLevel);
				view.getAllNodes().forEach(node -> node.type().tick(serverLevel, view, node));
				if (event.level.getGameTime() % 6 == 0)
					view.tickTaintLevel();
			}
			
			if (!onTick.isEmpty()) {
				List<Consumer<Level>> temp = new ArrayList<>(onTick);
				temp.forEach(consumer -> consumer.accept(level));
				onTick.removeAll(temp);
			}
		}
	}
}