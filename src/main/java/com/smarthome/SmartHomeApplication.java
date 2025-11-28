package com.smarthome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Smart Home Automation Application.
 * 
 * This application demonstrates the Command Design Pattern within
 * a Spring Boot MVC architecture for controlling IoT devices.
 */
@SpringBootApplication
public class SmartHomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartHomeApplication.class, args);
    }
}
