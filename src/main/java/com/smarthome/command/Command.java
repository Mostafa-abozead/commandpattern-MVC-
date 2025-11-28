package com.smarthome.command;

/**
 * Command Interface - The core of the Command Design Pattern.
 * 
 * This interface defines the contract for all concrete commands.
 * Each command encapsulates an action and its parameters.
 * 
 * DECOUPLING BENEFIT: The Invoker (Controller) only knows about this interface,
 * not the specific implementation details. This allows for:
 * - Easy addition of new commands without modifying existing code
 * - Commands can be stored, queued, or logged
 * - Supports undo/redo operations if needed
 */
public interface Command {
    
    /**
     * Executes the encapsulated command action.
     * 
     * @return A status message indicating the result of the command execution
     */
    String execute();
}
