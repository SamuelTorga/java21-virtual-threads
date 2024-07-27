package br.com.samueltorga;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Slf4j
public class CompletableFutureMain {

    public static void main(String[] args) {
        log.info("Starting application...");

        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.rangeClosed(1, 3)
                    .mapToObj(i -> CompletableFuture.supplyAsync(CompletableFutureMain::execute, executorService))
                    .forEach(future -> future.thenAccept(result -> log.info("Result: {}", result)));
        }

        log.info("Application finished.");
    }

    private static String execute() {
        log.info("Executing task... {}", Thread.currentThread());
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> httpResponse = client.send(HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("https://httpbin.org/delay/3"))
                    .build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            log.info("Task executed. {}", Thread.currentThread());
            return httpResponse.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
