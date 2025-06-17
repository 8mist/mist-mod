package fr.mist.core.net;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Map;

public class NetManager {
    static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

    private static final int REQUEST_TIMEOUT_MILLIS = 10000;

    public ApiResponse callApi(URI uri, Map<String, String> headers) {
        return createApiResponse(uri, headers);
    }

    public ApiResponse callApi(URI uri) {
        return callApi(uri, Map.of());
    }

    private HttpRequest createGetRequest(URI uri, Map<String, String> headers) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofMillis(REQUEST_TIMEOUT_MILLIS))
                .header("Content-Type", "application/json");

        headers.forEach(builder::header);

        return builder.build();
    }

    private ApiResponse createApiResponse(URI uri, Map<String, String> headers) {
        HttpRequest request = createGetRequest(uri, headers);
        return new ApiResponse(uri.toString(), request);
    }
}
