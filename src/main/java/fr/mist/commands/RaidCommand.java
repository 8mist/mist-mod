package fr.mist.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.mist.MistMod;
import fr.mist.core.components.Models;
import fr.mist.core.consumers.commands.Command;
import fr.mist.models.player.type.PlayerInfo;
import fr.mist.utils.mc.McUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RaidCommand extends Command {
    private static final String[] RAID_NAMES = {
            "The Canyon Colossus",
            "The Nameless Anomaly",
            "Nest of the Grootslangs",
            "Orphion's Nexus of Light"
    };

    @Override
    public String getCommandName() {
        return "raid";
    }

    @Override
    protected List<String> getAliases() {
        return List.of("ra");
    }

    @Override
    protected LiteralArgumentBuilder<FabricClientCommandSource> getCommandBuilder(LiteralArgumentBuilder<FabricClientCommandSource> base) {
        return base
                .then(createRaidSubCommand("tcc", RAID_NAMES[0]))
                .then(createRaidSubCommand("tna", RAID_NAMES[1]))
                .then(createRaidSubCommand("notg", RAID_NAMES[2]))
                .then(createRaidSubCommand("nol", RAID_NAMES[3]))
                .then(ClientCommandManager
                        .literal("overall")
                        .then(ClientCommandManager.argument("username", StringArgumentType.word())
                                .executes(this::raidOverallInfos)))
                .executes(this::syntaxError);
    }

    private LiteralArgumentBuilder<FabricClientCommandSource> createRaidSubCommand(String alias, String raidName) {
        return ClientCommandManager
                .literal(alias)
                .then(ClientCommandManager.argument("username", StringArgumentType.word())
                        .executes(ctx -> handleSingleRaidCommand(ctx, raidName)));
    }

    private int handleSingleRaidCommand(CommandContext<FabricClientCommandSource> ctx, String raidName) {
        String username = ctx.getArgument("username", String.class);

        CompletableFuture<PlayerInfo> completableFuture = Models.Player.getPlayer(username);

        completableFuture.whenComplete((player, throwable) -> {
            if (throwable != null) {
                McUtils.sendMessageToClient(Text.literal("§cUnable to view raids stats for " + username));
                MistMod.error("Error trying to parse player guild", throwable);
            } else {
                if (player == null) {
                    McUtils.sendMessageStyledToClient("§cPlayer not found");
                    return;
                }

                Map<String, Integer> raidList = player.globalData().raids().list();
                McUtils.sendMessageStyledToClient("§c§l" + username + " §f- §c" + raidName + " §fcompletions: §c" + raidList.getOrDefault(raidName, 0));
            }
        });

        ctx.getSource().sendFeedback(Text.literal("§aLooking up for user raid stats..."));

        return 1;
    }

    private int raidOverallInfos(CommandContext<FabricClientCommandSource> ctx) {
        String username = ctx.getArgument("username", String.class);

        CompletableFuture<PlayerInfo> completableFuture = Models.Player.getPlayer(username);

        completableFuture.whenComplete((player, throwable) -> {
            if (throwable != null) {
                McUtils.sendMessageToClient(Text.literal("§cUnable to view raids stats for " + username));
                MistMod.error("Error trying to parse player guild", throwable);
            } else {
                if (player == null) {
                    McUtils.sendMessageStyledToClient("§cPlayer not found");
                    return;
                }

                Map<String, Integer> raidList = player.globalData().raids().list();
                McUtils.sendMessageStyledToClient("§c§l" + username + " §f- Raid completions overall");
                for (String raidName : RAID_NAMES) {
                    int completions = raidList.getOrDefault(raidName, 0);
                    McUtils.sendMessageStyledToClient("§f- §c" + raidName + " §fcompletions: §c" + completions);
                }
            }
        });

        ctx.getSource().sendFeedback(Text.literal("§aLooking up for user raid stats..."));

        return 1;
    }

    private int syntaxError(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendError(Text.literal("Missing argument"));
        return 0;
    }
}
