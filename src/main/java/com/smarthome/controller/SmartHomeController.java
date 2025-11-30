package com.smarthome.controller;

import com.smarthome.command.Command;
import com.smarthome.command.GetStatusCommand;
import com.smarthome.command.LightOffCommand;
import com.smarthome.command.LightOnCommand;
import com.smarthome.invoker.CommandInvoker;
import com.smarthome.model.LightState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SmartHomeController - REST API Client in the Strict Command Design Pattern.
 * 
 * This REST controller provides API endpoints for controlling the light.
 * It follows the same command pattern as DashboardController.
 * 
 * REFACTORED ARCHITECTURE (Per UML Class Diagram):
 * - Controller acts as a CLIENT, not an Invoker
 * - Controller has reference to Command beans (injected by Spring)
 * - Controller delegates command queuing and execution to the CommandInvoker
 * - Controller has NO direct dependency on LightService
 * - All communication with Receiver happens through Commands via the Invoker
 * 
 * EXECUTION FLOW (Per UML Sequence Diagram):
 * Controller -> Sets Command via createCommand() -> Pushes Command via pushCurrentCommand()
 * -> Invoker queues Command -> Controller triggers executeCommands()
 * -> Invoker processes Queue (FIFO) -> Command calls Receiver -> Returns LightState
 */
@RestController
@RequestMapping("/api/light")
public class SmartHomeController {

    /**
     * The CommandInvoker manages command queue and execution.
     * Controller sets commands on the Invoker and triggers execution.
     */
    private final CommandInvoker commandInvoker;

    /**
     * Command bean reference for getting light status.
     */
    private final Command getStatusCommand;

    /**
     * Command bean reference for turning light ON.
     */
    private final Command lightOnCommand;

    /**
     * Command bean reference for turning light OFF.
     */
    private final Command lightOffCommand;

    /**
     * Constructor - Controller receives CommandInvoker and Command beans.
     * Controller NEVER receives LightService directly.
     * 
     * @param commandInvoker The dedicated Invoker for command queuing and execution
     * @param getStatusCommand The command bean for getting light status
     * @param lightOnCommand The command bean for turning light ON
     * @param lightOffCommand The command bean for turning light OFF
     */
    @Autowired
    public SmartHomeController(CommandInvoker commandInvoker,
                               GetStatusCommand getStatusCommand,
                               LightOnCommand lightOnCommand,
                               LightOffCommand lightOffCommand) {
        this.commandInvoker = commandInvoker;
        this.getStatusCommand = getStatusCommand;
        this.lightOnCommand = lightOnCommand;
        this.lightOffCommand = lightOffCommand;
    }

    /**
     * Endpoint to turn ON the smart light bulb.
     * 
     * @return LightState containing the result
     */
    @GetMapping("/on")
    public LightState turnLightOn() {
        // Create command via Invoker (per UML: createCommand)
        commandInvoker.createCommand(lightOnCommand);
        // Push command to queue (per UML: pushCurrentCommand)
        commandInvoker.pushCurrentCommand();
        // Execute commands and get result (per UML: executeCommands)
        return commandInvoker.executeCommands();
    }

    /**
     * Endpoint to turn OFF the smart light bulb.
     * 
     * @return LightState containing the result
     */
    @GetMapping("/off")
    public LightState turnLightOff() {
        // Create command via Invoker (per UML: createCommand)
        commandInvoker.createCommand(lightOffCommand);
        // Push command to queue (per UML: pushCurrentCommand)
        commandInvoker.pushCurrentCommand();
        // Execute commands and get result (per UML: executeCommands)
        return commandInvoker.executeCommands();
    }

    /**
     * Endpoint to get the current status of the smart light bulb.
     * 
     * @return LightState containing current status
     */
    @GetMapping("/status")
    public LightState getLightStatus() {
        // Create command via Invoker (per UML: createCommand)
        commandInvoker.createCommand(getStatusCommand);
        // Push command to queue (per UML: pushCurrentCommand)
        commandInvoker.pushCurrentCommand();
        // Execute commands and get result (per UML: executeCommands)
        return commandInvoker.executeCommands();
    }
}
