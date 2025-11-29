package com.smarthome.service;

import org.springframework.stereotype.Service;

/**
 * LightService - The Receiver in the Command Design Pattern.
 * 
 * This service contains the actual business logic for controlling the smart light bulb.
 * It encapsulates the "hardware logic" (simulated) for IoT device operations.
 * 
 * DECOUPLING BENEFIT: The Receiver knows nothing about Commands or the Controller.
 * It only knows how to perform specific actions (turn on/off the light).
 * This makes the service reusable across different contexts and easy to test.
 */
@Service
public class LightService {

    // Simulated state of the light bulb
    private boolean isOn = false;

    /**
     * Turns on the smart light bulb.
     * In a real IoT scenario, this would communicate with the hardware device.
     * 
     * @return Status message confirming the light is on
     */
    public String turnOn() {
        this.isOn = true;
        // Simulate hardware communication delay
        System.out.println("[Hardware] Sending signal to light bulb: TURN ON");
        return "Light is ON";
    }

    /**
     * Turns off the smart light bulb.
     * In a real IoT scenario, this would communicate with the hardware device.
     * 
     * @return Status message confirming the light is off
     */
    public String turnOff() {
        this.isOn = false;
        // Simulate hardware communication delay
        System.out.println("[Hardware] Sending signal to light bulb: TURN OFF");
        return "Light is OFF";
    }

    /**
     * Gets the current state of the light bulb.
     * 
     * @return true if the light is on, false otherwise
     */
    public boolean isLightOn() {
        return this.isOn;
    }

    /**
     * Gets the current status of the light as a string.
     * 
     * @return Status message indicating current light state
     */
    public String getStatus() {
        return isOn ? "Light is currently ON" : "Light is currently OFF";
    }
}
