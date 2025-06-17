package fr.mist.models.player;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.mist.core.components.Managers;
import fr.mist.core.net.ApiResponse;
import fr.mist.models.player.type.PlayerInfo;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class PlayerModel {
    private static final Gson PLAYER_PROFILE_GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(PlayerInfo.class, new PlayerInfo.PlayerDeserializer())
            .create();

    public CompletableFuture<PlayerInfo> getPlayer(String playerToSearch) {
        CompletableFuture<PlayerInfo> future = new CompletableFuture<>();

        URI uri = URI.create("https://api.wynncraft.com/v3/player/" + playerToSearch);

        ApiResponse apiResponse = Managers.Net.callApi(uri);
        apiResponse.handleJsonObject(
                json -> {
                    Type type = new TypeToken<PlayerInfo>() {}.getType();
                    future.complete(PLAYER_PROFILE_GSON.fromJson(json, type));
                },
                onError -> future.complete(null));

        return future;
    }
}
