package com.istatistik;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IstatistikApplication {

    public static void main(String[] args) {
        SpringApplication.run(IstatistikApplication.class, args);
    }

    @Bean
    CommandLineRunner openBrowser() {
        return args -> {
            try {
                String url = "http://localhost:8080";
                Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start", url });
            } catch (Exception e) {
                System.out.println("Tarayıcı otomatik açılamadı: " + e.getMessage());
            }
        };
    }
}
