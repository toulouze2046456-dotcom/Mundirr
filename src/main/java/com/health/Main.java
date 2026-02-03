package com.health;

import com.health.entity.User;
import com.health.repository.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Vellbeing OS - Main Application
 * A scientifically-grounded personal wellness operating system.
 */
@SpringBootApplication
@EnableAsync  // Enable async for email sending
@EnableScheduling  // Enable scheduled tasks for daily market data fetch
public class Main {
    
    public static void main(String[] args) {
        // Load .env file and set as system properties for Spring to pick up
        try {
            Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .load();
            
            dotenv.entries().forEach(entry -> {
                if (System.getProperty(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });
            System.out.println("✅ Environment variables loaded from .env file");
        } catch (Exception e) {
            System.out.println("⚠️ No .env file found, using system environment");
        }
        
        SpringApplication.run(Main.class, args);
    }
    
    /**
     * Initialize demo user on first run
     */
    @Bean
    CommandLineRunner init(UserRepository userRepo, BCryptPasswordEncoder encoder) {
        return args -> {
            if (userRepo.count() == 0) {
                User demo = new User("demo@vellbeing.app", encoder.encode("password"), "Demo User");
                userRepo.save(demo);
                System.out.println("✅ Demo user created: demo@vellbeing.app / password");
            }
        };
    }
}
