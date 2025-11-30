package com.smarthome.invoker;

import com.smarthome.command.Command;
import com.smarthome.model.LightState;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

/**
 * CommandInvoker - The dedicated Invoker in the Strict Command Design Pattern.
 * 
 * This class is responsible for maintaining a FIFO queue of commands and 
 * executing them sequentially. It provides strict separation of concerns
 * by extracting the Invoker logic from the Controller.
 * 
 * STRICT COMMAND PATTERN IMPLEMENTATION:
 * - Maintains a Queue (FIFO) of commands
 * - Exposes a method to push (add) commands to the queue
 * - Has a mechanism to execute pending commands sequentially
 * - Controller (Client) pushes commands here; Invoker executes them
 * 
 * EXECUTION FLOW:
 * Controller -> Pushes Command to Invoker -> Invoker processes Queue 
 * -> Command calls Receiver -> Receiver updates Model
 */
@Component
public class CommandInvoker {

    /**
     * FIFO queue to hold pending commands.
     * Commands are executed in the order they are added.
     */
    private final Queue<Command> commandQueue;

    /**
     * Default constructor initializes an empty command queue.
     */
    public CommandInvoker() {
        this.commandQueue = new LinkedList<>();
    }

    /**
     * Pushes a command to the queue.
     * The command will be executed when executeCommands() is called.
     * 
     * @param command The command to add to the queue
     */
    public void push(Command command) {
        if (command != null) {
            commandQueue.add(command);
        }
    }

    /**
     * Executes all pending commands in the queue sequentially (FIFO order).
     * Returns the result of the last executed command.
     * 
     * @return LightState from the last executed command, or null if queue was empty
     */
    public LightState executeCommands() {
        LightState lastState = null;
        while (!commandQueue.isEmpty()) {
            Command command = commandQueue.poll();
            lastState = command.execute();
        }
        return lastState;
    }

    /**
     * Pushes a command to the queue and immediately executes all pending commands.
     * This is a convenience method that combines push() and executeCommands().
     * 
     * @param command The command to add and execute
     * @return LightState from the executed command(s)
     */
    public LightState pushAndExecute(Command command) {
        push(command);
        return executeCommands();
    }

    /**
     * Gets the number of commands currently in the queue.
     * Useful for testing and debugging.
     * 
     * @return The number of pending commands in the queue
     */
    public int getQueueSize() {
        return commandQueue.size();
    }

    /**
     * Clears all pending commands from the queue.
     * Useful for resetting state or handling errors.
     */
    public void clearQueue() {
        commandQueue.clear();
    }
}
