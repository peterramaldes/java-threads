import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.*;

public class IOBound {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        List<String> urls = List.of(
            "https://httpbin.org/delay/2", // waits 2s
            "https://httpbin.org/delay/3", // waits 3s
            "https://httpbin.org/delay/1"  // waits 1s
        );

        // Each task runs in its own virtual thread
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = urls.stream()
                .map(url -> executor.submit(() -> {
                    HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .build();
                    HttpResponse<String> response =
                        client.send(request, HttpResponse.BodyHandlers.ofString());
                    return "Got response from " + url + ": " + response.statusCode();
                }))
                .toList();

            // Collect results
            for (Future<String> f : futures) {
                System.out.println(f.get());
            }
        }
    }
}
