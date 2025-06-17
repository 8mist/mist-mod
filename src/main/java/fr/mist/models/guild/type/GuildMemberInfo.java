package fr.mist.models.guild.type;

import java.time.Instant;

public record GuildMemberInfo(
        String uuid,
        String username,
        GuildRank rank,
        boolean online,
        String server,
        long contributed,
        int contributionRank,
        Instant joined
) {}
