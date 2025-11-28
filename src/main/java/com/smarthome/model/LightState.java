package com.smarthome.model;

/**
 * LightState - The Model in MVC Architecture.
 * 
 * This POJO (Plain Old Java Object) carries data between the Controller and the View.
 * It represents the current state of the smart light that will be displayed on the dashboard.
 * 
 * MVC BENEFIT: The Model is a simple data carrier with no business logic.
 * It decouples the View from the Service layer - the View only needs to know
 * about LightState, not about LightService or Commands.
 */
public class LightState {

    /**
     * Whether the light is currently on or off
     */
    private boolean on;

    /**
     * Human-readable status message for display
     */
    private String statusMessage;

    /**
     * Default constructor
     */
    public LightState() {
    }

    /**
     * Parameterized constructor
     * 
     * @param on Whether the light is on
     * @param statusMessage The status message to display
     */
    public LightState(boolean on, String statusMessage) {
        this.on = on;
        this.statusMessage = statusMessage;
    }

    /**
     * Gets whether the light is on
     * 
     * @return true if the light is on, false otherwise
     */
    public boolean isOn() {
        return on;
    }

    /**
     * Sets whether the light is on
     * 
     * @param on true to indicate light is on, false otherwise
     */
    public void setOn(boolean on) {
        this.on = on;
    }

    /**
     * Gets the status message
     * 
     * @return The human-readable status message
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the status message
     * 
     * @param statusMessage The status message to set
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
