package fr.mist.core.net;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.mist.MistMod;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.http.HttpRequest;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

public abstract class NetResult {
    private static final Consumer<Throwable> DEFAULT_ERROR_HANDLER =
            (exception) -> MistMod.error("Error while processing network request; ignored");

    protected final HttpRequest request;
    private final String desc;

    protected NetResult(String desc, HttpRequest request) {
        this.request = request;
        this.desc = desc;
    }

    public void handleInputStream(Consumer<InputStream> handler, Consumer<Throwable> onError) {
        doHandle(handler, onError);
    }

    public void handleInputStream(Consumer<InputStream> handler) {
        handleInputStream(handler, DEFAULT_ERROR_HANDLER);
    }

    public void handleReader(Consumer<Reader> handler, Consumer<Throwable> onError) {
        handleInputStream(is -> handler.accept(new InputStreamReader(is, StandardCharsets.UTF_8)), onError);
    }

    public void handleReader(Consumer<Reader> handler) {
        handleReader(handler, DEFAULT_ERROR_HANDLER);
    }

    public void handleJsonObject(Consumer<JsonObject> handler, Consumer<Throwable> onError) {
        handleReader(
                reader -> {
                    try {
                        handler.accept(JsonParser.parseReader(reader).getAsJsonObject());
                    } catch (Throwable t) {
                        MistMod.error("Failure in net manager [handleJsonObject], processing " + desc + ": "
                                + t.getMessage());
                        onError.accept(t);
                    }
                },
                onError);
    }

    public void handleJsonObject(Consumer<JsonObject> handler) {
        handleJsonObject(handler, DEFAULT_ERROR_HANDLER);
    }

    public void handleJsonArray(Consumer<JsonArray> handler, Consumer<Throwable> onError) {
        handleReader(
                reader -> {
                    try {
                        handler.accept(JsonParser.parseReader(reader).getAsJsonArray());
                    } catch (Throwable t) {
                        MistMod.error("Failure in net manager [handleJsonArray], processing " + desc, t);
                        onError.accept(t);
                    }
                },
                onError);
    }

    public void handleJsonArray(Consumer<JsonArray> handler) {
        handleJsonArray(handler, DEFAULT_ERROR_HANDLER);
    }

    private void doHandle(Consumer<InputStream> onCompletion, Consumer<Throwable> onError) {
        // The wrappingHandler will make sure we close the input stream
        CompletableFuture<Void> future = getInputStreamFuture()
                .thenAccept(wrappingHandler(onCompletion, onError))
                .exceptionally(t -> {
                    if (t instanceof CompletionException ce && ce.getCause() instanceof HttpTimeoutException hte) {
                        // Don't spam the log with stack traces for timeouts
                        MistMod.error("Failure in net manager [doHandle], processing " + desc
                                + ", HttpTimeoutException: " + hte.getMessage());
                    } else {
                        MistMod.error("Failure in net manager [doHandle], processing " + desc, t);
                    }
                    onError.accept(t);
                    return null;
                });
    }

    private Consumer<InputStream> wrappingHandler(Consumer<InputStream> handler, Consumer<Throwable> onError) {
        return (inputStream) -> {
            try {
                handler.accept(inputStream);
            } catch (Throwable t) {
                // Something went wrong in our handlers, perhaps an NPE?
                MistMod.error("Failure in net manager [wrappingHandler], processing " + desc, t);
                onError.accept(t);
                onHandlingFailed();
            } finally {
                try {
                    // We must always close the input stream
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        };
    }

    protected void onHandlingFailed() {}

    protected abstract CompletableFuture<InputStream> getInputStreamFuture();
}
