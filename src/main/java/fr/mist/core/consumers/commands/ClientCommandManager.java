package fr.mist.core.consumers.commands;

import fr.mist.commands.AnnihilationCommand;
import fr.mist.commands.GuildCommand;
import fr.mist.commands.RaidCommand;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import java.util.List;

public class ClientCommandManager {
    private static final List<Command> COMMANDS = List.of(
            new AnnihilationCommand(),
            new RaidCommand(),
            new GuildCommand()
    );

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            for (Command cmd : COMMANDS) {
                for (var builder : cmd.getCommandBuilders()) {
                    dispatcher.register(builder);
                }
            }
        });
    }
}
