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
 * - Allows setting a command before pushing it to the queue
 * - Exposes a method to push (add) commands to the queue
 * - Has a mechanism to execute pending commands sequentially
 * - Controller (Client) sets and pushes commands here; Invoker executes them
 * 
 * EXECUTION FLOW:
 * Controller -> Sets Command -> Pushes Command to Invoker -> Invoker processes Queue 
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
     * The currently set command that can be pushed to the queue.
     * This allows setting a command before pushing it.
     */
    private Command command;

    /**
     * Default constructor initializes an empty command queue.
     */
    public CommandInvoker() {
        this.commandQueue = new LinkedList<>();
    }

    /**
     * Sets the command to be used.
     * This command can then be pushed to the queue using pushCurrentCommand().
     * 
     * @param command The command to set
     */
    public void setCommand(Command command) {
        this.command = command;
    }

    /**
     * Gets the currently set command.
     * 
     * @return The currently set command, or null if not set
     */
    public Command getCommand() {
        return this.command;
    }

    /**
     * Pushes the currently set command to the queue.
     * The command must be set using setCommand() before calling this method.
     * 
     * @throws IllegalStateException if no command has been set
     */
    public void pushCurrentCommand() {
        if (this.command == null) {
            throw new IllegalStateException("No command has been set. Call setCommand() first.");
        }
        commandQueue.add(this.command);
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
