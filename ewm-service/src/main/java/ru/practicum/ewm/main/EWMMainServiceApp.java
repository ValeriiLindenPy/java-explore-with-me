package ru.practicum.ewm.main;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import ru.practicum.stats.client.config.ClientConfig;

@SpringBootApplication
@Import(ClientConfig.class)
public class EWMMainServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EWMMainServiceApp.class, args);
    }
}
