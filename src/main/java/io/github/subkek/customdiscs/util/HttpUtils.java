package io.github.subkek.customdiscs.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.function.Consumer;

public class HttpUtils {
    public static void GET(String url, Consumer<String> onSuccess, Consumer<Throwable> onError) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(30))
                .header("Accept", "application/json")
                .GET()
                .build();

        client.sendAsync(request, BodyHandlers.ofString())
                .thenApply(response -> {
                    int statusCode = response.statusCode();
                    if (statusCode >= 200 && statusCode < 300) {
                        return response.body();
                    } else {
                        throw new RuntimeException("HTTP error: " + statusCode + " - " + response.body());
                    }
                })
                .thenAccept(onSuccess)
                .exceptionally(ex -> {
                    onError.accept(ex);
                    return null;
                });
    }
}
