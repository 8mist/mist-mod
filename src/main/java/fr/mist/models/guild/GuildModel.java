package fr.mist.models.guild;

import com.google.common.reflect.TypeToken;
import fr.mist.core.components.Managers;
import fr.mist.core.net.ApiResponse;

import java.net.URI;

import com.google.gson.*;
import fr.mist.models.guild.type.GuildInfo;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;

public class GuildModel {
    private static final Gson GUILD_PROFILE_GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(GuildInfo.class, new GuildInfo.GuildDeserializer())
            .create();

    public CompletableFuture<GuildInfo> getGuild(String guildToSearch) {
        CompletableFuture<GuildInfo> future = new CompletableFuture<>();

        URI uri = URI.create("https://api.wynncraft.com/v3/guild/prefix/" + guildToSearch);

        ApiResponse apiResponse = Managers.Net.callApi(uri);
        apiResponse.handleJsonObject(
                json -> {
                    Type type = new TypeToken<GuildInfo>() {}.getType();
                    future.complete(GUILD_PROFILE_GSON.fromJson(json, type));
                },
                onError -> future.complete(null));

        return future;
    }
}
