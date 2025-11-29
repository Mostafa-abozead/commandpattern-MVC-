package com.smarthome.controller;

import com.smarthome.command.Command;
import com.smarthome.command.LightOnCommand;
import com.smarthome.command.LightOffCommand;
import com.smarthome.command.GetStatusCommand;
import com.smarthome.model.LightState;
import com.smarthome.service.LightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SmartHomeController - REST API Invoker in the Strict Command Design Pattern.
 * 
 * This REST controller provides API endpoints for controlling the light.
 * It follows the same strict command pattern as DashboardController.
 * 
 * STRICT COMMAND PATTERN:
 * - Controller holds Command reference (Aggregation)
 * - Controller has NO direct dependency on LightService (only Commands do)
 * - All communication with Receiver happens ONLY through Commands
 */
@RestController
@RequestMapping("/api/light")
public class SmartHomeController {

    /**
     * Command reference - Aggregation relationship with Command interface.
     */
    private Command command;

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
     */
    @Autowired
    public SmartHomeController(LightService lightService) {
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
        this.command = lightOnCommand;
        return command.execute();
    }

    /**
     * Endpoint to turn OFF the smart light bulb.
     * 
     * @return LightState containing the result
     */
    @GetMapping("/off")
    public LightState turnLightOff() {
        this.command = lightOffCommand;
        return command.execute();
    }

    /**
     * Endpoint to get the current status of the smart light bulb.
     * 
     * @return LightState containing current status
     */
    @GetMapping("/status")
    public LightState getLightStatus() {
        this.command = getStatusCommand;
        return command.execute();
    }
}
