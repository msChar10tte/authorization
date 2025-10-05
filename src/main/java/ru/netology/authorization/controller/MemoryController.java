package ru.netology.authorization.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RestController
public class MemoryController {

    private final Counter requestCounter;
    private List<byte[]> memoryHog = new ArrayList<>();
    private final Random random = new Random();

    public MemoryController(MeterRegistry registry) {
        this.requestCounter = Counter.builder("app_requests_total")
                .description("Total number of requests to MemoryController")
                .register(registry);
    }

    // Метод для потребления памяти
    @GetMapping("/consume-memory")
    public ResponseEntity<String> consumeMemory(@RequestParam(defaultValue = "10") int mb) {
        requestCounter.increment(); // Увеличиваем кастомную метрику
        long bytesToConsume = (long) mb * 1024 * 1024; // MB to bytes
        try {
            for (long i = 0; i < bytesToConsume / (1024 * 1024); i++) { // Consume 1MB at a time
                memoryHog.add(new byte[1024 * 1024]); // Add 1MB byte array
            }
            return ResponseEntity.ok("Consumed " + mb + " MB of memory. Current total consumed: " + memoryHog.size() + " MB");
        } catch (OutOfMemoryError e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("OutOfMemoryError encountered. Failed to consume " + mb + " MB.");
        }
    }

    // Метод для освобождения памяти
    @GetMapping("/free-memory")
    public ResponseEntity<String> freeMemory() {
        requestCounter.increment(); // Увеличиваем кастомную метрику
        memoryHog.clear();
        System.gc(); // Запрос на сборку мусора (не гарантирует немедленное освобождение)
        return ResponseEntity.ok("Memory freed.");
    }

    // Метод, возвращающий 200 OK
    @GetMapping("/status/ok")
    public ResponseEntity<String> getOkStatus() {
        requestCounter.increment(); // Увеличиваем кастомную метрику
        return ResponseEntity.ok("Status 200: OK");
    }

    // Метод, возвращающий 404 Not Found
    @GetMapping("/status/not-found")
    public ResponseEntity<String> getNotFoundStatus() {
        requestCounter.increment(); // Увеличиваем кастомную метрику
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Status 404: Not Found");
    }

    // Метод, возвращающий 500 Internal Server Error (имитация ошибки)
    @GetMapping("/status/internal-error")
    public ResponseEntity<String> getInternalErrorStatus() {
        requestCounter.increment(); // Увеличиваем кастомную метрику
        if (random.nextBoolean()) { // Имитация случайной ошибки
            throw new RuntimeException("Simulated internal server error!");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Status 200: All good (this time)");
    }
}