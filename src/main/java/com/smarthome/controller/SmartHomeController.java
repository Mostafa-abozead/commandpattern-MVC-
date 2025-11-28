package com.smarthome.controller;

import com.smarthome.command.Command;
import com.smarthome.command.LightOnCommand;
import com.smarthome.command.LightOffCommand;
import com.smarthome.service.LightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SmartHomeController - The Invoker in the Command Design Pattern.
 * 
 * This REST controller acts as the Invoker that receives HTTP requests
 * from the user's web dashboard and creates/executes the appropriate commands.
 * 
 * DECOUPLING BENEFIT: The Controller does NOT know the details of how to
 * turn on or off the light. It only knows:
 * 1. How to create Command objects
 * 2. How to call execute() on them
 * 
 * This separation of concerns allows:
 * - Easy addition of new commands (e.g., DimLightCommand) without changing the controller logic
 * - Better testability - commands can be mocked
 * - Command history/logging can be added without modifying existing code
 */
@RestController
@RequestMapping("/api/light")
public class SmartHomeController {

    /**
     * The Receiver service - injected via Spring's dependency injection.
     * The Controller holds a reference to the Receiver to pass to Commands.
     */
    private final LightService lightService;

    /**
     * Constructor-based dependency injection for the LightService.
     * 
     * @param lightService The Receiver service that will be passed to commands
     */
    @Autowired
    public SmartHomeController(LightService lightService) {
        this.lightService = lightService;
    }

    /**
     * Endpoint to turn ON the smart light bulb.
     * 
     * Flow:
     * 1. User sends GET /light/on
     * 2. Controller creates a new LightOnCommand with the lightService
     * 3. Controller calls command.execute()
     * 4. Command delegates to lightService.turnOn()
     * 5. Controller returns the result to the User
     * 
     * DECOUPLING: The controller doesn't know HOW to turn on the light,
     * only the Command and Service know that. The controller just
     * creates the command and tells it to execute.
     * 
     * @return Status message indicating the light is ON
     */
    @GetMapping("/on")
    public String turnLightOn() {
        // Create the concrete command with the receiver
        Command command = new LightOnCommand(lightService);
        
        // Execute the command - the Invoker doesn't know the implementation details
        return command.execute();
    }

    /**
     * Endpoint to turn OFF the smart light bulb.
     * 
     * Flow:
     * 1. User sends GET /light/off
     * 2. Controller creates a new LightOffCommand with the lightService
     * 3. Controller calls command.execute()
     * 4. Command delegates to lightService.turnOff()
     * 5. Controller returns the result to the User
     * 
     * DECOUPLING: The controller doesn't know HOW to turn off the light,
     * only the Command and Service know that. The controller just
     * creates the command and tells it to execute.
     * 
     * @return Status message indicating the light is OFF
     */
    @GetMapping("/off")
    public String turnLightOff() {
        // Create the concrete command with the receiver
        Command command = new LightOffCommand(lightService);
        
        // Execute the command - the Invoker doesn't know the implementation details
        return command.execute();
    }

    /**
     * Endpoint to get the current status of the smart light bulb.
     * 
     * This endpoint demonstrates that the service maintains state
     * and can be queried independently of the command pattern.
     * 
     * @return Current status of the light
     */
    @GetMapping("/status")
    public String getLightStatus() {
        return lightService.getStatus();
    }
}
