package fr.mist.models.annihilation;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.mist.core.components.Managers;
import fr.mist.core.net.ApiResponse;
import fr.mist.models.annihilation.type.AnnihilationInfo;

import java.lang.reflect.Type;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public record AnnihilationEventModel() {
    private static final Gson ANNIHILATION_INFO_GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(AnnihilationInfo.class, new AnnihilationInfo.AnnihilationDeserializer())
            .create();

    public CompletableFuture<List<Instant>> getLastEvents() {
        CompletableFuture<List<Instant>> future = new CompletableFuture<>();

        URI uri = URI.create("https://script.google.com/macros/s/AKfycbzEbl1hTjwPNmYPLbBfQ6rddVNMioHBwRBhDuJv6Xuq0aLvTVi0E7w1ndCGBieU5281jQ/exec");

        ApiResponse apiResponse = Managers.Net.callApi(uri);
        apiResponse.handleJsonObject(
                json -> {
                    Type type = new TypeToken<AnnihilationInfo>() {}.getType();
                    AnnihilationInfo annihilationInfo = ANNIHILATION_INFO_GSON.fromJson(json, type);
                    future.complete(annihilationInfo.lastEvents());
                },
                onError -> future.complete(null));

        return future;
    }
}
