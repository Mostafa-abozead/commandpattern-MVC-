package com.smarthome.invoker;

import com.smarthome.command.Command;
import com.smarthome.command.LightOnCommand;
import com.smarthome.command.LightOffCommand;
import com.smarthome.command.GetStatusCommand;
import com.smarthome.model.LightState;
import com.smarthome.service.LightService;
import org.springframework.beans.factory.annotation.Autowired;
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
 * - Connects to the Receiver (LightService) to create commands
 * - Allows creating a command by type before pushing it to the queue
 * - Exposes a method to push (add) commands to the queue
 * - Has a mechanism to execute pending commands sequentially
 * - Controller (Client) creates and pushes commands here; Invoker executes them
 * 
 * EXECUTION FLOW:
 * Controller -> Creates Command via Invoker -> Pushes Command to Invoker -> Invoker processes Queue 
 * -> Command calls Receiver -> Receiver updates Model
 */
@Component
public class CommandInvoker {

    /**
     * Enum defining the types of commands that can be created.
     */
    public enum CommandType {
        LIGHT_ON,
        LIGHT_OFF,
        GET_STATUS
    }

    /**
     * FIFO queue to hold pending commands.
     * Commands are executed in the order they are added.
     */
    private final Queue<Command> commandQueue;

    /**
     * The Receiver (LightService) used to create commands.
     */
    private final LightService lightService;

    /**
     * The currently created command that can be pushed to the queue.
     * This allows creating a command before pushing it.
     */
    private Command command;

    /**
     * Constructor with LightService dependency injection.
     * The Invoker connects to the Receiver to create commands.
     * 
     * @param lightService The Receiver used to create commands
     */
    @Autowired
    public CommandInvoker(LightService lightService) {
        this.commandQueue = new LinkedList<>();
        this.lightService = lightService;
    }

    /**
     * Creates a command of the specified type using the Receiver.
     * This command can then be pushed to the queue using pushCurrentCommand().
     * 
     * @param commandType The type of command to create
     */
    public void createCommand(CommandType commandType) {
        switch (commandType) {
            case LIGHT_ON:
                this.command = new LightOnCommand(lightService);
                break;
            case LIGHT_OFF:
                this.command = new LightOffCommand(lightService);
                break;
            case GET_STATUS:
                this.command = new GetStatusCommand(lightService);
                break;
            default:
                throw new IllegalArgumentException("Unknown command type: " + commandType);
        }
    }

    /**
     * Gets the currently created command.
     * 
     * @return The currently created command, or null if not created
     */
    public Command getCommand() {
        return this.command;
    }

    /**
     * Pushes the currently created command to the queue.
     * The command must be created using createCommand() before calling this method.
     * 
     * @throws IllegalStateException if no command has been created
     */
    public void pushCurrentCommand() {
        if (this.command == null) {
            throw new IllegalStateException("No command has been created. Call createCommand() first.");
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
