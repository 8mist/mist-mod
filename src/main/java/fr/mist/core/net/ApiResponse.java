package fr.mist.core.net;

import fr.mist.core.components.Managers;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class ApiResponse extends NetResult {
    public ApiResponse(String desc, HttpRequest request) {
        super("API:" + desc, request);
    }

    @Override
    protected CompletableFuture<InputStream> getInputStreamFuture() {
        return Managers.Net.HTTP_CLIENT
                .sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(HttpResponse::body);
    }
}
