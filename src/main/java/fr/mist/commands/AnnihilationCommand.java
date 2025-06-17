package fr.mist.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.mist.MistMod;
import fr.mist.core.components.Models;
import fr.mist.core.consumers.commands.Command;
import fr.mist.utils.DateFormatter;
import fr.mist.utils.MathUtils;
import fr.mist.utils.mc.McUtils;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.concurrent.CompletableFuture;

public class AnnihilationCommand extends Command {
    private static final DateFormatter formatter = new DateFormatter(false);

    private static Instant addHours(Instant base, Double hours) {
        return base.plus(Duration.ofMillis((long) (hours * 3600_000)));
    }

    @Override
    public String getCommandName() {
        return "annihilation";
    }

    @Override
    protected List<String> getAliases() {
        return List.of("anni");
    }

    @Override
    protected LiteralArgumentBuilder<FabricClientCommandSource> getCommandBuilder(LiteralArgumentBuilder<FabricClientCommandSource> base) {
        LiteralArgumentBuilder<FabricClientCommandSource> estimateBuilder =
                ClientCommandManager.literal("estimate")
                        .executes(this::estimate);

        return base.then(estimateBuilder).executes(this::syntaxError);
    }

    private int estimate(CommandContext<FabricClientCommandSource> ctx) {
        CompletableFuture<List<Instant>> completableFuture = Models.AnnihilationEvent.getLastEvents();

        completableFuture.whenComplete((lastEvents, throwable) -> {
            if (throwable != null) {
                McUtils.sendMessageToClient(Text.literal("§cUnable to estimate the next annihilation"));
                MistMod.error("Error trying to parse annihilation event", throwable);
            } else {
                if (lastEvents == null) {
                    McUtils.sendMessageStyledToClient("§cAny last events :(");
                    return;
                }

                List<Double> intervals = new ArrayList<>();
                for (int i = 1; i < lastEvents.size(); i++) {
                    Duration duration = Duration.between(lastEvents.get(i - 1), lastEvents.get(i));
                    double hours = duration.toMinutes() / 60.0;
                    intervals.add(hours);
                }

                OptionalDouble q1 = MathUtils.percentile(intervals, 25);
                OptionalDouble q2 = MathUtils.median(intervals);
                OptionalDouble q3 = MathUtils.percentile(intervals, 75);

                Instant latestEvent = lastEvents.getLast();

                McUtils.sendMessageStyledToClient("§c§lEstimation for Annihilation");
                McUtils.sendMessageStyledToClient("§7Estimations are §lpredictions §r§7based on historical data from past Annihilations to only give an estimate of the next Annihilation.\nWorld Events are scheduled within a random period of time.\n");

                if (q1.isPresent()) {
                    Instant estimateQ1 = addHours(latestEvent, q1.getAsDouble());
                    long durationMillisQ1 = Duration.between(Instant.now(), estimateQ1).toMillis();
                    String formattedDate = formatter.format(durationMillisQ1);
                    McUtils.sendMessageStyledToClient("§eLow Estimate (Q1): §r" + formattedDate);
                }

                if (q3.isPresent()) {
                    Instant estimateQ3 = addHours(latestEvent, q3.getAsDouble());
                    long durationMillisQ3 = Duration.between(Instant.now(), estimateQ3).toMillis();
                    String formattedDate = formatter.format(durationMillisQ3);
                    McUtils.sendMessageStyledToClient("§eUpper Estimate (Q3): §r" + formattedDate);
                }

                if (q2.isPresent()) {
                    Instant estimateQ2 = addHours(latestEvent, q2.getAsDouble());
                    long durationMillisQ2 = Duration.between(Instant.now(), estimateQ2).toMillis();
                    String formattedDate = formatter.format(durationMillisQ2);
                    McUtils.sendMessageStyledToClient("§eAverage (Q2): §r" + formattedDate);
                }
            }
        });

        ctx.getSource().sendFeedback(Text.literal("§aEstimates in progress..."));

        return 1;
    }

    private int syntaxError(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendError(Text.literal("Missing argument"));
        return 0;
    }
}
