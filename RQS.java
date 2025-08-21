import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.*;

public class VirtualThreadRpsExample {
    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        int requestsPerSecond = 5;
        int totalRequests = 20;

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        for (int i = 0; i < totalRequests; i++) {
            int taskId = i;
            long delayMs = (long) ((1000.0 / requestsPerSecond) * i);

            scheduler.schedule(() -> {
                executor.submit(() -> {
                    try {
                        HttpRequest req = HttpRequest.newBuilder()
                                .uri(URI.create("https://httpbin.org/get?id=" + taskId))
                                .timeout(Duration.ofSeconds(10))
                                .build();

                        HttpResponse<String> res =
                                client.send(req, HttpResponse.BodyHandlers.ofString());

                        System.out.println("Task " + taskId + " → " + res.statusCode());
                    } catch (Exception e) {
                        System.err.println("Task " + taskId + " failed: " + e.getMessage());
                    }
                });
            }, delayMs, TimeUnit.MILLISECONDS);
        }

        scheduler.shutdown();
        scheduler.awaitTermination(1, TimeUnit.MINUTES);

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        System.out.println("All tasks completed.");
    }
}
