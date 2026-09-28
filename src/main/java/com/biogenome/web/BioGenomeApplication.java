package com.biogenome.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * BioGenome Web Platform – Spring Boot Application Entry Point.
 * Serves the interactive genomics research dashboard at http://localhost:8080
 *
 * @author BioGenome Team
 */
@SpringBootApplication(scanBasePackages = "com.biogenome")
public class BioGenomeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BioGenomeApplication.class, args);
        System.out.println("\n=================================================");
        System.out.println("  BioGenome Web Dashboard running at:");
        System.out.println("  http://localhost:8080");
        System.out.println("  REST API Base: http://localhost:8080/api/v1");
        System.out.println("=================================================\n");
    }
}
