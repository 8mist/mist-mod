package fr.mist.core.consumers.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.List;
import java.util.stream.Stream;

public abstract class Command {
    public abstract String getCommandName();

    protected List<String> getAliases() {
        return List.of();
    }

    public String getTypeName() {
        return "Command";
    }

    protected abstract LiteralArgumentBuilder<FabricClientCommandSource> getCommandBuilder(
            LiteralArgumentBuilder<FabricClientCommandSource> base);

    public final List<LiteralArgumentBuilder<FabricClientCommandSource>> getCommandBuilders() {
        return Stream.concat(
                        Stream.of(ClientCommandManager.literal(getCommandName())),
                        getAliases().stream().map(ClientCommandManager::literal))
                .map(this::getCommandBuilder)
                .toList();
    }
}
