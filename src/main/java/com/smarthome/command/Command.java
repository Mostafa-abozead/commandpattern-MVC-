package com.smarthome.command;

import com.smarthome.model.LightState;

/**
 * Command Interface - The core of the Command Design Pattern.
 * 
 * This interface defines the contract for all concrete commands.
 * Each command encapsulates an action and its parameters.
 * 
 * STRICT DECOUPLING: The Invoker (Controller) only knows about this interface,
 * not the Receiver (LightService). The Controller has NO direct reference
 * to the Service - all communication happens through Commands.
 * 
 * DECOUPLING BENEFIT:
 * - Controller doesn't know about LightService
 * - Easy addition of new commands without modifying Controller
 * - Commands can be stored, queued, or logged
 * - Supports undo/redo operations if needed
 */
public interface Command {
    
    /**
     * Executes the encapsulated command action.
     * 
     * @return LightState containing the result of the command execution
     */
    LightState execute();
}
