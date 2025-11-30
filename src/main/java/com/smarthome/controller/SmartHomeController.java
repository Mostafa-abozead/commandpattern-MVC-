package com.smarthome.controller;

import com.smarthome.command.Command;
import com.smarthome.command.LightOnCommand;
import com.smarthome.command.LightOffCommand;
import com.smarthome.command.GetStatusCommand;
import com.smarthome.invoker.CommandInvoker;
import com.smarthome.model.LightState;
import com.smarthome.service.LightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SmartHomeController - REST API Client in the Strict Command Design Pattern.
 * 
 * This REST controller provides API endpoints for controlling the light.
 * It follows the same refactored command pattern as DashboardController.
 * 
 * REFACTORED ARCHITECTURE:
 * - Controller acts as a CLIENT, not an Invoker
 * - Controller delegates command execution to the CommandInvoker
 * - Controller has NO direct dependency on LightService (only Commands do)
 * - All communication with Receiver happens through Commands via the Invoker
 * 
 * EXECUTION FLOW:
 * Controller -> Pushes Command to Invoker -> Invoker processes Queue 
 * -> Command calls Receiver -> Receiver updates Model
 */
@RestController
@RequestMapping("/api/light")
public class SmartHomeController {

    /**
     * The CommandInvoker manages the command queue and execution.
     * Controller pushes commands to the Invoker and triggers execution.
     */
    private final CommandInvoker commandInvoker;

    /**
     * Pre-configured commands.
     */
    private final Command lightOnCommand;
    private final Command lightOffCommand;
    private final Command getStatusCommand;

    /**
     * Constructor - Commands are created with the Receiver.
     * Controller NEVER uses LightService directly.
     * 
     * @param lightService Injected by Spring, passed to Commands only
     * @param commandInvoker The dedicated Invoker for command execution
     */
    @Autowired
    public SmartHomeController(LightService lightService, CommandInvoker commandInvoker) {
        this.commandInvoker = commandInvoker;
        this.lightOnCommand = new LightOnCommand(lightService);
        this.lightOffCommand = new LightOffCommand(lightService);
        this.getStatusCommand = new GetStatusCommand(lightService);
    }

    /**
     * Endpoint to turn ON the smart light bulb.
     * 
     * @return LightState containing the result
     */
    @GetMapping("/on")
    public LightState turnLightOn() {
        // Set the command first, then push to Invoker and trigger execution
        commandInvoker.setCommand(lightOnCommand);
        commandInvoker.pushCurrentCommand();
        return commandInvoker.executeCommands();
    }

    /**
     * Endpoint to turn OFF the smart light bulb.
     * 
     * @return LightState containing the result
     */
    @GetMapping("/off")
    public LightState turnLightOff() {
        // Set the command first, then push to Invoker and trigger execution
        commandInvoker.setCommand(lightOffCommand);
        commandInvoker.pushCurrentCommand();
        return commandInvoker.executeCommands();
    }

    /**
     * Endpoint to get the current status of the smart light bulb.
     * 
     * @return LightState containing current status
     */
    @GetMapping("/status")
    public LightState getLightStatus() {
        // Set the command first, then push to Invoker and trigger execution
        commandInvoker.setCommand(getStatusCommand);
        commandInvoker.pushCurrentCommand();
        return commandInvoker.executeCommands();
    }
}
