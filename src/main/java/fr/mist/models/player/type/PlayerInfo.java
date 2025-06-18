package fr.mist.models.player.type;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

public record PlayerInfo(
		String username,
		boolean online,
		String server,
		String activeCharacter,
		String nickname,
		String uuid,
		String rank,
		String rankBadge,
		fr.mist.models.player.type.PlayerInfo.LegacyRankColour legacyRankColour,
		String shortenedRank,
		String supportRank,
		Boolean veteran,
		String firstJoin,
		String lastJoin,
		double playtime,
		fr.mist.models.player.type.PlayerInfo.PlayerGuildInfo guild,
		fr.mist.models.player.type.PlayerInfo.PlayerGlobalData globalData,
		fr.mist.models.player.type.PlayerInfo.PlayerRanking ranking,
		fr.mist.models.player.type.PlayerInfo.PlayerRanking previousRanking,
		boolean publicProfile
) {

	public record LegacyRankColour(
			String main,
			String sub
	) {}

	public record PlayerGuildInfo(
			String uuid,
			String name,
			String prefix,
			String rank,
			String rankStars
	) {}

	public record PlayerGlobalData(
			int wars,
			int totalLevel,
			int killedMobs,
			int chestsFound,
			fr.mist.models.player.type.PlayerInfo.PlayerGlobalData.Dungeons dungeons,
			fr.mist.models.player.type.PlayerInfo.PlayerGlobalData.Raids raids,
			int completedQuests,
			fr.mist.models.player.type.PlayerInfo.PlayerGlobalData.Pvp pvp
	) {
		public record Dungeons(
				int total,
				Map<String, Integer> list
		) {}

		public record Raids(
				int total,
				Map<String, Integer> list
		) {}

		public record Pvp(
				int kills,
				int deaths
		) {}
	}

	public record PlayerRanking(
			int huicContent,
			int colossusSrPlayers,
			int colossusCompletion,
			int hardcoreContent,
			int namelessSrPlayers,
			int warsCompletion,
			int combatSoloLevel,
			int namelessCompletion,
			int grootslangSrPlayers,
			int grootslangCompletion,
			int orphionCompletion,
			int globalPlayerContent,
			int woodcuttingLevel,
			int orphionSrPlayers,
			int playerContent,
			int totalGlobalLevel,
			int professionsGlobalLevel,
			int armouringLevel,
			int miningLevel,
			int scribingLevel,
			int combatGlobalLevel,
			int farmingLevel,
			int fishingLevel,
			int totalSoloLevel,
			int professionsSoloLevel,
			int weaponsmithingLevel,
			int tailoringLevel,
			int alchemismLevel,
			int cookingLevel,
			int jewelingLevel,
			int woodworkingLevel
	) {}

	public static class PlayerDeserializer implements JsonDeserializer<fr.mist.models.player.type.PlayerInfo> {
		@Override
		public fr.mist.models.player.type.PlayerInfo deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject json = jsonElement.getAsJsonObject();

			String username = json.get("username").getAsString();
			boolean online = json.get("online").getAsBoolean();
			String server = json.get("server").getAsString();
			String activeCharacter = json.has("activeCharacter") && !json.get("activeCharacter").isJsonNull()
					? json.get("activeCharacter").getAsString() : null;
			String nickname = json.has("nickname") && !json.get("nickname").isJsonNull()
					? json.get("nickname").getAsString() : null;
			String uuid = json.get("uuid").getAsString();
			String rank = json.get("rank").getAsString();
			String rankBadge = json.has("rankBadge") && !json.get("rankBadge").isJsonNull()
					? json.get("rankBadge").getAsString() : null;

			fr.mist.models.player.type.PlayerInfo.LegacyRankColour legacyRankColour = json.has("legacyRankColour") && !json.get("legacyRankColour").isJsonNull()
					? context.deserialize(json.get("legacyRankColour"), fr.mist.models.player.type.PlayerInfo.LegacyRankColour.class)
					: null;

			String shortenedRank = json.has("shortenedRank") && !json.get("shortenedRank").isJsonNull()
					? json.get("shortenedRank").getAsString() : null;

			String supportRank = json.has("supportRank") && !json.get("supportRank").isJsonNull()
					? json.get("supportRank").getAsString() : null;

			Boolean veteran = json.has("veteran") && !json.get("veteran").isJsonNull()
					? json.get("veteran").getAsBoolean() : null;

			String firstJoin = json.has("firstJoin") && !json.get("firstJoin").isJsonNull()
					? json.get("firstJoin").getAsString() : null;

			String lastJoin = json.has("lastJoin") && !json.get("lastJoin").isJsonNull()
					? json.get("lastJoin").getAsString() : null;

			double playtime = json.has("playtime") && !json.get("playtime").isJsonNull()
					? json.get("playtime").getAsDouble() : 0.0;

			fr.mist.models.player.type.PlayerInfo.PlayerGuildInfo guild = json.has("guild") && !json.get("guild").isJsonNull()
					? context.deserialize(json.get("guild"), fr.mist.models.player.type.PlayerInfo.PlayerGuildInfo.class)
					: null;

			fr.mist.models.player.type.PlayerInfo.PlayerGlobalData globalData = json.has("globalData") && !json.get("globalData").isJsonNull()
					? context.deserialize(json.get("globalData"), fr.mist.models.player.type.PlayerInfo.PlayerGlobalData.class)
					: null;

			fr.mist.models.player.type.PlayerInfo.PlayerRanking ranking = json.has("ranking") && !json.get("ranking").isJsonNull()
					? context.deserialize(json.get("ranking"), fr.mist.models.player.type.PlayerInfo.PlayerRanking.class)
					: null;

			fr.mist.models.player.type.PlayerInfo.PlayerRanking previousRanking = json.has("previousRanking") && !json.get("previousRanking").isJsonNull()
					? context.deserialize(json.get("previousRanking"), fr.mist.models.player.type.PlayerInfo.PlayerRanking.class)
					: null;

			boolean publicProfile = json.has("publicProfile") && !json.get("publicProfile").isJsonNull() && json.get("publicProfile").getAsBoolean();

			return new fr.mist.models.player.type.PlayerInfo(
					username,
					online,
					server,
					activeCharacter,
					nickname,
					uuid,
					rank,
					rankBadge,
					legacyRankColour,
					shortenedRank,
					supportRank,
					veteran,
					firstJoin,
					lastJoin,
					playtime,
					guild,
					globalData,
					ranking,
					previousRanking,
					publicProfile
			);
		}
	}
}
