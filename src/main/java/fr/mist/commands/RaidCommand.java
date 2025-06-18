package fr.mist.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.mist.MistMod;
import fr.mist.core.components.Models;
import fr.mist.core.consumers.commands.Command;
import fr.mist.models.player.type.PlayerInfo;
import fr.mist.models.raid.type.RaidStats;
import fr.mist.utils.mc.McUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RaidCommand extends Command {
    private static final List<RaidStats> RAID_STATS = List.of(
            new RaidStats("The Canyon Colossus", PlayerInfo.PlayerRanking::colossusSrPlayers, PlayerInfo.PlayerRanking::colossusCompletion),
            new RaidStats("The Nameless Anomaly", PlayerInfo.PlayerRanking::namelessSrPlayers, PlayerInfo.PlayerRanking::namelessCompletion),
            new RaidStats("Nest of the Grootslangs", PlayerInfo.PlayerRanking::grootslangSrPlayers, PlayerInfo.PlayerRanking::grootslangCompletion),
            new RaidStats("Orphion's Nexus of Light", PlayerInfo.PlayerRanking::orphionSrPlayers, PlayerInfo.PlayerRanking::orphionCompletion)
    );

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
                .then(createRaidSubCommand("tcc", RAID_STATS.getFirst()))
                .then(createRaidSubCommand("tna", RAID_STATS.get(1)))
                .then(createRaidSubCommand("notg", RAID_STATS.get(2)))
                .then(createRaidSubCommand("nol", RAID_STATS.getLast()))
                .then(ClientCommandManager
                        .literal("overall")
                        .then(ClientCommandManager.argument("username", StringArgumentType.word())
                                .executes(this::raidOverallInfos)))
                .executes(this::syntaxError);
    }

    private LiteralArgumentBuilder<FabricClientCommandSource> createRaidSubCommand(String alias, RaidStats raidStats) {
        return ClientCommandManager
                .literal(alias)
                .then(ClientCommandManager.argument("username", StringArgumentType.word())
                        .executes(ctx -> handleSingleRaidCommand(ctx, raidStats)));
    }

    private int handleSingleRaidCommand(CommandContext<FabricClientCommandSource> ctx, RaidStats raidStats) {
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

                int total = player.globalData().raids().list().getOrDefault(raidStats.displayName(), 0);
                int sr = raidStats.srExtractor().apply(player.ranking());
                int completion = raidStats.completionExtractor().apply(player.ranking());
                McUtils.sendMessageStyledToClient("§c§l" + username + " §f- §c" + raidStats.displayName() + " §f(§c"+total+"§f)");
                McUtils.sendMessageStyledToClient("§fSR: #§c" + sr);
                McUtils.sendMessageStyledToClient("§fCompletions: #§c" + completion);
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

                McUtils.mc().execute(() -> {
                    McUtils.sendMessageStyledToClient("§c§l" + username + " §f- Raid completions overall");
                    for (RaidStats stats : RAID_STATS) {
                        int total = player.globalData().raids().list().getOrDefault(stats.displayName(), 0);
                        int sr = stats.srExtractor().apply(player.ranking());
                        int completion = stats.completionExtractor().apply(player.ranking());
                        McUtils.sendMessageStyledToClient("§c" + stats.displayName() + " §f(§c"+total+"§f)");
                        McUtils.sendMessageStyledToClient("§fSR: #§c" + sr);
                        McUtils.sendMessageStyledToClient("§fCompletions: #§c" + completion);
                    }
                });
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
