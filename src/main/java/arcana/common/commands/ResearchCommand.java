package arcana.common.commands;

import arcana.api.ArcanaApi;
import arcana.api.capabilities.IPlayerKnowledge;
import arcana.api.capabilities.ModCapabilities;
import arcana.api.research.ResearchCategories;
import arcana.api.research.ResearchCategory;
import arcana.api.research.ResearchEntry;
import arcana.api.research.ResearchStage;
import arcana.common.lib.research.ResearchManager;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;
import java.util.Collection;

public class ResearchCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> registerLearn() {
        return Commands.literal("learn")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("research", StringArgumentType.string()))
                .executes(context -> {
//                    CommandSourceStack source = context.getSource();
//                    String research = StringArgumentType.getString(context, "research");
//                    String[] keys = research.split("\\s+");

//                    for (String key : keys) {
//                        ArcanaApi.internalMethods.progressResearch(source.getPlayer(), key);
//                    }
                    CommandSourceStack source = context.getSource();
                    String research = StringArgumentType.getString(context, "research");
                    giveResearch(source.getPlayer(), research);

                    return 1;
                });
    }

    public static ArgumentBuilder<CommandSourceStack, ?> registerLearnAll() {
        return Commands.literal("learn-all")
                .requires(cs -> cs.hasPermission(2))
                .executes(context -> {
                    Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
                    for (ResearchCategory cat : rc) {
                        Collection<ResearchEntry> rl = cat.research.values();
                        for (ResearchEntry ri : rl) {
                            giveRecursiveResearch(context.getSource().getPlayer(), ri.getKey());
                        }
                    }

                    return 1;
                });
    }

    public static void giveResearch(ServerPlayer player, String research) {
        if (ResearchCategories.getResearch(research) != null) {
            giveRecursiveResearch(player, research);
            ModCapabilities.getKnowledge(player).sync(player);
            player.sendSystemMessage(Component.literal("§5Arcana gave you " + research + " research and its requisites."));
            player.sendSystemMessage(Component.literal("§5Success!"));
        } else {
            player.sendSystemMessage(Component.literal("§cResearch does not exist."));
        }
    }

    public static void giveRecursiveResearch(ServerPlayer player, String research) {
        if (research.contains("@")) {
            int i = research.indexOf("@");
            research = research.substring(0, i);
        }

        ResearchEntry res = ResearchCategories.getResearch(research);
        IPlayerKnowledge knowledge = ModCapabilities.getKnowledge(player);
        if (!knowledge.isResearchComplete(research)) {
            if (res != null && res.getParents() != null) {
                for (String rsi : res.getParentsStripped()) {
                    giveRecursiveResearch(player, rsi);
                }
            }

            if (res != null && res.getStages() != null) {
                for (ResearchStage page : res.getStages()) {
                    if (page.getResearch() != null) {
                        for (String gr : page.getResearch()) {
                            ResearchManager.completeResearch(player, gr);
                        }
                    }
                }
            }

            ResearchManager.completeResearch(player, research);

            for (String rc : ResearchCategories.researchCategories.keySet()) {
                for (ResearchEntry ri : ResearchCategories.getResearchCategory(rc).research.values()) {
                    if (ri.getStages() != null) {
                        for (ResearchStage stage : ri.getStages()) {
                            if (stage.getResearch() != null && Arrays.asList(stage.getResearch()).contains(research)) {
                                ModCapabilities.getKnowledge(player).setResearchFlag(ri.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
//                                break;
                            }
                        }
                    }
                }
            }

//            Iterator<String> var14 = ResearchCategories.researchCategories.keySet().iterator();

//            label70:
//            while(var14.hasNext()) {
//                String rc = var14.next();
//                Iterator<ResearchEntry> researchEntries = ResearchCategories.getResearchCategory(rc).research.values().iterator();
//
//                while(true) {
//                    while(true) {
//                        ResearchEntry ri;
//                        do {
//                            if (!researchEntries.hasNext()) {
//                                continue label70;
//                            }
//
//                            ri = researchEntries.next();
//                        } while(ri.getStages() == null);
//
//                        for (ResearchStage stage : ri.getStages()) {
//                            if (stage.getResearch() != null && Arrays.asList(stage.getResearch()).contains(research)) {
//                                ModCapabilities.getKnowledge(player).setResearchFlag(ri.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
//                                break;
//                            }
//                        }
//                    }
//                }
//            }

            if (res != null && res.getSiblings() != null) {
                for (String rsi : res.getSiblings()) {
                    giveRecursiveResearch(player, rsi);
                }
            }
        }

    }
    
}
