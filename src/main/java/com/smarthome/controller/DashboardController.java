package com.smarthome.controller;

import com.smarthome.command.Command;
import com.smarthome.command.LightOnCommand;
import com.smarthome.command.LightOffCommand;
import com.smarthome.command.GetStatusCommand;
import com.smarthome.model.LightState;
import com.smarthome.service.LightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * DashboardController - The Invoker in the Strict Command Design Pattern.
 * 
 * STRICT COMMAND PATTERN IMPLEMENTATION:
 * - The Controller holds a reference to Command (Aggregation relationship)
 * - The Controller has NO direct dependency on the Receiver (LightService)
 * - All communication with the Receiver happens ONLY through Commands
 * 
 * MVC ARCHITECTURE:
 * - @Controller annotation (NOT @RestController)
 * - Returns View names, not raw data
 * - Uses Spring's Model to pass data to the View
 * 
 * DECOUPLING BENEFITS:
 * - Controller doesn't know HOW to control the light
 * - Controller doesn't know about LightService at all
 * - All it knows is the Command interface
 */
@Controller
public class DashboardController {

    /**
     * Command reference - The Controller (Invoker) holds a reference to Command.
     * This demonstrates the Aggregation relationship between Invoker and Command.
     * The Controller communicates ONLY via this Command interface.
     */
    private Command command;

    /**
     * Pre-configured commands injected via Spring DI.
     * These are ready to use - Controller just assigns them and calls execute().
     */
    private final Command lightOnCommand;
    private final Command lightOffCommand;
    private final Command getStatusCommand;

    /**
     * Constructor-based dependency injection.
     * Commands are created with the Receiver (LightService) internally.
     * The Controller NEVER sees or interacts with LightService directly.
     * 
     * @param lightService Injected by Spring, passed to Commands only
     */
    @Autowired
    public DashboardController(LightService lightService) {
        // Commands are created with the Receiver - Controller doesn't use it directly
        this.lightOnCommand = new LightOnCommand(lightService);
        this.lightOffCommand = new LightOffCommand(lightService);
        this.getStatusCommand = new GetStatusCommand(lightService);
    }

    /**
     * Displays the Smart Home Dashboard.
     * 
     * STRICT COMMAND PATTERN FLOW:
     * 1. User navigates to /dashboard
     * 2. Controller sets command = getStatusCommand
     * 3. Controller calls command.execute()
     * 4. Command returns LightState (Model)
     * 5. Controller adds Model to Spring's Model
     * 6. Controller returns "dashboard" (View name)
     * 
     * NOTE: Controller NEVER accesses LightService - only uses Command
     * 
     * @param model Spring's Model to pass data to the View
     * @return The view name "dashboard"
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Set the command (Aggregation - Invoker holds reference to Command)
        this.command = getStatusCommand;
        
        // Execute command - returns LightState (Model)
        LightState lightState = command.execute();
        
        // Add Model to Spring's Model for the View
        model.addAttribute("lightState", lightState);
        
        // Return View name
        return "dashboard";
    }

    /**
     * Turns ON the light and returns to dashboard.
     * 
     * STRICT COMMAND PATTERN FLOW:
     * 1. User clicks "Turn ON" button on View
     * 2. Controller sets command = lightOnCommand
     * 3. Controller calls command.execute()
     * 4. Command delegates to Receiver (internally)
     * 5. Command returns LightState (Model)
     * 6. Controller returns "dashboard" (View name)
     * 
     * STRICT DECOUPLING: Controller doesn't know about LightService,
     * doesn't know HOW to turn on the light - only Command knows.
     * 
     * @param model Spring's Model to pass data to the View
     * @return The view name "dashboard"
     */
    @GetMapping("/light/on")
    public String turnLightOn(Model model) {
        // Set the command (Aggregation - Invoker holds reference to Command)
        this.command = lightOnCommand;
        
        // Execute command - returns LightState (Model)
        LightState lightState = command.execute();
        
        // Add Model to Spring's Model for the View
        model.addAttribute("lightState", lightState);
        
        // Return View name
        return "dashboard";
    }

    /**
     * Turns OFF the light and returns to dashboard.
     * 
     * STRICT COMMAND PATTERN FLOW:
     * 1. User clicks "Turn OFF" button on View
     * 2. Controller sets command = lightOffCommand
     * 3. Controller calls command.execute()
     * 4. Command delegates to Receiver (internally)
     * 5. Command returns LightState (Model)
     * 6. Controller returns "dashboard" (View name)
     * 
     * STRICT DECOUPLING: Controller doesn't know about LightService,
     * doesn't know HOW to turn off the light - only Command knows.
     * 
     * @param model Spring's Model to pass data to the View
     * @return The view name "dashboard"
     */
    @GetMapping("/light/off")
    public String turnLightOff(Model model) {
        // Set the command (Aggregation - Invoker holds reference to Command)
        this.command = lightOffCommand;
        
        // Execute command - returns LightState (Model)
        LightState lightState = command.execute();
        
        // Add Model to Spring's Model for the View
        model.addAttribute("lightState", lightState);
        
        // Return View name
        return "dashboard";
    }
}
