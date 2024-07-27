package br.com.samueltorga;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Starting application...");

        // Using executor service to create virtual threads
        ThreadFactory threadFactory = Thread.ofVirtual().name("virtual-thread").factory();
        try (ExecutorService executorService = Executors.newThreadPerTaskExecutor(threadFactory)) {
            // Iterate 10 times to execute the task
            for (int i = 0; i < 10; i++) {
                executorService.submit(() -> {
                    try {
                        execute();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                });
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        log.info("Application finished.");
    }

    private static void execute() throws Exception {
        log.info("Executing task... {}", Thread.currentThread());
        Thread.sleep(Duration.ofSeconds(2).toMillis());
        log.info("Task executed. {}", Thread.currentThread());
    }
}