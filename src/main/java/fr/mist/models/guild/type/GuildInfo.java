package fr.mist.models.guild.type;

import java.time.Instant;
import java.util.Map;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.*;

public record GuildInfo(
        String uuid,
        String name,
        String prefix,
        int level,
        int xpPercent,
        int territories,
        long wars,
        Instant created,
        int online,
        Banner banner,
        List<GuildMemberInfo> guildMembers,
        Map<String, SeasonRank> seasonRanks
) {
    public static class GuildDeserializer implements JsonDeserializer<GuildInfo> {
        @Override
        public GuildInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            String uuid = jsonObject.get("uuid").getAsString();
            String name = jsonObject.get("name").getAsString();
            String prefix = jsonObject.get("prefix").getAsString();
            int level = jsonObject.get("level").getAsInt();
            int xpPercent = jsonObject.get("xpPercent").getAsInt();
            int territories = jsonObject.get("territories").getAsInt();
            long wars = jsonObject.get("wars").getAsLong();
            Instant created = Instant.parse(jsonObject.get("created").getAsString());
            int online = jsonObject.get("online").getAsInt();

            JsonObject bannerJson = jsonObject.getAsJsonObject("banner");
            Banner banner = context.deserialize(bannerJson, Banner.class);

            JsonObject seasonRanksJson = jsonObject.getAsJsonObject("seasonRanks");
            Map<String, SeasonRank> seasonRanks = new HashMap<>();
            for (String key : seasonRanksJson.keySet()) {
                JsonObject rankObj = seasonRanksJson.getAsJsonObject(key);
                seasonRanks.put(key, new SeasonRank(
                        rankObj.get("rating").getAsInt(),
                        rankObj.get("finalTerritories").getAsInt()
                ));
            }

            JsonObject membersJson = jsonObject.getAsJsonObject("members");
            List<GuildMemberInfo> guildMembers = new ArrayList<>();

            for (GuildRank rank : GuildRank.values()) {
                String rankKey = rank.getName().toLowerCase();

                if (!membersJson.has(rankKey)) continue;

                JsonObject membersPerRank = membersJson.getAsJsonObject(rankKey);
                for (String username : membersPerRank.keySet()) {
                    JsonObject memberInfo = membersPerRank.getAsJsonObject(username);

                    boolean isOnline = memberInfo.get("online").getAsBoolean();
                    String server = memberInfo.get("server").isJsonNull() ? null : memberInfo.get("server").getAsString();
                    long contributed = memberInfo.get("contributed").getAsLong();
                    int contributionRank = memberInfo.get("contributionRank").getAsInt();
                    Instant joined = Instant.parse(memberInfo.get("joined").getAsString());
                    String memberUuid = memberInfo.has("uuid") ? memberInfo.get("uuid").getAsString() : null;

                    guildMembers.add(new GuildMemberInfo(
                            memberUuid,
                            username,
                            rank,
                            isOnline,
                            server,
                            contributed,
                            contributionRank,
                            joined
                    ));
                }
            }

            return new GuildInfo(
                    uuid,
                    name,
                    prefix,
                    level,
                    xpPercent,
                    territories,
                    wars,
                    created,
                    online,
                    banner,
                    guildMembers,
                    seasonRanks
            );
        }
    }

    public record Banner(
            String base,
            int tier,
            String structure,
            List<Layer> layers
    ) {
        public record Layer(String colour, String pattern) {}
    }

    public record SeasonRank(int rating, int finalTerritories) {}
}
