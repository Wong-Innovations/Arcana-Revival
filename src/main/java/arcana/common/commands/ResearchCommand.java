package arcana.common.commands;

import arcana.api.ArcanaApi;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ResearchCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("learn")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.argument("research", StringArgumentType.greedyString()))
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    String research = StringArgumentType.getString(context, "research");
                    String[] keys = research.split("\\s+");

                    for (String key : keys) {
                        ArcanaApi.internalMethods.progressResearch(source.getPlayer(), key);
                    }

                    return 1;
                });
    }
}
