package com.smarthome.controller;

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
 * It follows the same refactored command pattern as DashboardController.
 * 
 * REFACTORED ARCHITECTURE:
 * - Controller acts as a CLIENT, not an Invoker
 * - Controller delegates command creation and execution to the CommandInvoker
 * - Controller has NO direct dependency on LightService (only CommandInvoker does)
 * - All communication with Receiver happens through Commands via the Invoker
 * 
 * EXECUTION FLOW:
 * Controller -> Creates Command via Invoker -> Pushes Command to Invoker -> Invoker processes Queue 
 * -> Command calls Receiver -> Receiver updates Model
 */
@RestController
@RequestMapping("/api/light")
public class SmartHomeController {

    /**
     * The CommandInvoker manages command creation, queue, and execution.
     * Controller creates commands via the Invoker and triggers execution.
     */
    private final CommandInvoker commandInvoker;

    /**
     * Constructor - Controller only needs CommandInvoker.
     * Controller NEVER uses LightService directly.
     * 
     * @param commandInvoker The dedicated Invoker for command creation and execution
     */
    @Autowired
    public SmartHomeController(CommandInvoker commandInvoker) {
        this.commandInvoker = commandInvoker;
    }

    /**
     * Endpoint to turn ON the smart light bulb.
     * 
     * @return LightState containing the result
     */
    @GetMapping("/on")
    public LightState turnLightOn() {
        // Create command via Invoker, push, and execute
        commandInvoker.createCommand(CommandInvoker.CommandType.LIGHT_ON);
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
        // Create command via Invoker, push, and execute
        commandInvoker.createCommand(CommandInvoker.CommandType.LIGHT_OFF);
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
        // Create command via Invoker, push, and execute
        commandInvoker.createCommand(CommandInvoker.CommandType.GET_STATUS);
        commandInvoker.pushCurrentCommand();
        return commandInvoker.executeCommands();
    }
}
