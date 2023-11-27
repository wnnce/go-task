package ink.task.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: lisang
 * @DateTime: 2023-11-26 20:26:53
 * @Description:
 */
@SpringBootApplication
@EnableGoTask
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
