package fr.mist.models.raid.type;

import fr.mist.models.player.type.PlayerInfo;

import java.util.function.Function;

public record RaidStats(
		String displayName,
		Function<PlayerInfo.PlayerRanking, Integer> srExtractor,
		Function<PlayerInfo.PlayerRanking, Integer> completionExtractor
) {}