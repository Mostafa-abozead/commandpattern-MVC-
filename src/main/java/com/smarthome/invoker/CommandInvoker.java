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
 * - Maintains a Queue<Command> (FIFO) using LinkedList
 * - Allows setting a command reference via createCommand()
 * - Exposes pushCurrentCommand() to add the current command to the queue
 * - Exposes push() to directly add commands to the queue
 * - executeCommands() processes queue in FIFO order and returns last result
 * - Controller (Client) creates/pushes commands; Invoker queues and executes them
 * 
 * EXECUTION FLOW:
 * Controller -> Sets Command via createCommand() -> Pushes Command via pushCurrentCommand() 
 * -> Invoker queues Command -> Controller triggers executeCommands() 
 * -> Invoker processes Queue (FIFO) -> Command calls Receiver -> Returns LightState
 */
@Component
public class CommandInvoker {

    /**
     * FIFO queue to hold pending commands.
     * Commands are executed in the order they are added (First-In, First-Out).
     */
    private final Queue<Command> commandQueue;

    /**
     * The currently set command that can be pushed to the queue.
     * This allows setting a command before pushing it to the queue.
     */
    private Command command;

    /**
     * Default constructor.
     * Initializes the command queue as a LinkedList for FIFO behavior.
     */
    public CommandInvoker() {
        this.commandQueue = new LinkedList<>();
    }

    /**
     * Creates (sets) the current command reference.
     * This command can then be pushed to the queue using pushCurrentCommand().
     * 
     * Per UML: createCommand(command: Command): void
     * 
     * @param command The command to set as the current command
     */
    public void createCommand(Command command) {
        this.command = command;
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
     * Pushes the currently set command to the queue.
     * The command must be set using createCommand() before calling this method.
     * 
     * Per UML: pushCurrentCommand(): void - Adds the currently set command to the queue.
     * 
     * @throws IllegalStateException if no command has been set
     */
    public void pushCurrentCommand() {
        if (this.command == null) {
            throw new IllegalStateException("No command has been set. Call createCommand() first.");
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
     * 
     * QUEUE PROCESSING LOGIC (FIFO - First In, First Out):
     * 1. Check if the queue has pending commands
     * 2. Poll (remove) the first command from the queue
     * 3. Execute the command by calling its execute() method
     * 4. Store the result (LightState) from the command
     * 5. Repeat steps 1-4 until the queue is empty
     * 6. Return the LightState from the LAST executed command
     * 
     * NOTE: The queue is automatically cleared as each command is polled and executed.
     * This ensures commands are only executed once.
     * 
     * @return LightState from the last executed command, or null if queue was empty
     */
    public LightState executeCommands() {
        // Track the result of the last executed command
        LightState lastState = null;
        
        // Process the queue in FIFO order - poll removes and returns the head of the queue
        while (!commandQueue.isEmpty()) {
            // Poll the next command (FIFO - first added is first executed)
            Command command = commandQueue.poll();
            // Execute the command and store the result
            lastState = command.execute();
        }
        
        // Return the result of the last command (for Controller to update the View)
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
