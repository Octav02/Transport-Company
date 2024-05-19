package ro.mpp2024;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class StartREST {
    public static void main(String[] args) {
        SpringApplication.run(StartREST.class, args);
    }
}