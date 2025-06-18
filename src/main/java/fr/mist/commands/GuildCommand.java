package fr.mist.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.mist.MistMod;
import fr.mist.core.components.Models;
import fr.mist.core.consumers.commands.Command;
import fr.mist.models.guild.type.GuildInfo;
import fr.mist.models.guild.type.GuildMemberInfo;
import fr.mist.models.guild.type.GuildRank;
import fr.mist.utils.mc.McUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GuildCommand extends Command {
    @Override
    public String getCommandName() {
        return "guildx";
    }

    @Override
    protected List<String> getAliases() {
        return List.of("gx");
    }

    @Override
    protected LiteralArgumentBuilder<FabricClientCommandSource> getCommandBuilder(LiteralArgumentBuilder<FabricClientCommandSource> base) {
        LiteralArgumentBuilder<FabricClientCommandSource> onlineMembersBuilder =
                ClientCommandManager
                        .literal("om")
                        .then(ClientCommandManager
                                .argument("guild_prefix", StringArgumentType.word())
                                .executes(this::onlineMembers));

        return base.then(onlineMembersBuilder).executes(this::syntaxError);
    }

    private int onlineMembers(CommandContext<FabricClientCommandSource> ctx) {
        String guildName = ctx.getArgument("guild_prefix", String.class).toUpperCase();

        CompletableFuture<GuildInfo> completableFuture = Models.Guild.getGuild(guildName);

        completableFuture.whenComplete((guild, throwable) -> {
            if (throwable != null) {
                McUtils.sendMessageToClient(Text.literal("§cUnable to view online members guild for " + guildName));
                MistMod.error("Error trying to parse player guild", throwable);
            } else {
                if (guild == null) {
                    McUtils.sendMessageStyledToClient("§cGuild not found: " + guildName);
                    return;
                }

                List<GuildMemberInfo> onlineMembers = guild.guildMembers()
                        .stream()
                        .filter(GuildMemberInfo::online)
                        .toList();

                if (onlineMembers.isEmpty()) {
                    McUtils.sendMessageStyledToClient("§c§lNo members online.");
                    return;
                }

                McUtils.mc().execute(() -> {
                    McUtils.sendMessageStyledToClient("§3§l" + guild.name() + " §f- Online members §f(§b" + onlineMembers.size() + "§f):");

                    Map<GuildRank, List<GuildMemberInfo>> membersByRank = onlineMembers.stream()
                            .collect(Collectors.groupingBy(GuildMemberInfo::rank));

                    for (GuildRank rank : GuildRank.values()) {
                        List<GuildMemberInfo> members = membersByRank.get(rank);
                        if (members == null || members.isEmpty()) {
                            continue;
                        }

                        McUtils.sendMessageStyledToClient("§7" + rank.getGuildDescription() + ":");
                        String joinedNames = members.stream()
                                .map(GuildMemberInfo::username)
                                .collect(Collectors.joining("§f, §b"));

                        McUtils.sendMessageStyledToClient("§b" + joinedNames);
                    }
                });
            }
        });

        ctx.getSource().sendFeedback(Text.literal("§aLooking up guild online members..."));

        return 1;
    }

    private int syntaxError(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendError(Text.literal("Missing argument"));
        return 0;
    }
}
