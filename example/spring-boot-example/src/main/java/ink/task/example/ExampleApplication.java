package ink.task.example;

import ink.task.spring.EnableGoTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: lisang
 * @DateTime: 2023-11-28 13:00:49
 * @Description:
 */
@SpringBootApplication
@EnableGoTask
public class ExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExampleApplication.class, args);
    }
}
