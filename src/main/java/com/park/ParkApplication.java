package com.park;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class ParkApplication {

    public static void main(String[] args) {
        // Ensure the data directory exists before the datasource tries to create the H2 file there
        File dataDir = new File("./data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        SpringApplication.run(ParkApplication.class, args);
    }
}
