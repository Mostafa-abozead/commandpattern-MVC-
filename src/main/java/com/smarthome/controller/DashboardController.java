package com.smarthome.controller;

import com.smarthome.command.Command;
import com.smarthome.command.LightOnCommand;
import com.smarthome.command.LightOffCommand;
import com.smarthome.command.GetStatusCommand;
import com.smarthome.invoker.CommandInvoker;
import com.smarthome.model.LightState;
import com.smarthome.service.LightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * DashboardController - The Client in the Strict Command Design Pattern.
 * 
 * REFACTORED ARCHITECTURE:
 * - The Controller acts as a CLIENT, not an Invoker
 * - The Controller delegates command execution to the CommandInvoker
 * - The Controller has NO direct dependency on the Receiver (LightService)
 * - All communication with the Receiver happens through Commands via the Invoker
 * 
 * MVC ARCHITECTURE:
 * - @Controller annotation (NOT @RestController)
 * - Returns View names, not raw data
 * - Uses Spring's Model to pass data to the View
 * 
 * EXECUTION FLOW:
 * Controller -> Pushes Command to Invoker -> Invoker processes Queue 
 * -> Command calls Receiver -> Receiver updates Model
 * 
 * DECOUPLING BENEFITS:
 * - Controller doesn't know HOW to control the light
 * - Controller doesn't know about LightService at all
 * - Controller doesn't call execute() directly on commands
 * - All execution logic is delegated to CommandInvoker
 */
@Controller
public class DashboardController {

    /**
     * The CommandInvoker manages the command queue and execution.
     * Controller pushes commands to the Invoker and triggers execution.
     */
    private final CommandInvoker commandInvoker;

    /**
     * Pre-configured commands injected via Spring DI.
     * These are ready to use - Controller identifies the correct command
     * and pushes it to the Invoker.
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
     * @param commandInvoker The dedicated Invoker for command execution
     */
    @Autowired
    public DashboardController(LightService lightService, CommandInvoker commandInvoker) {
        this.commandInvoker = commandInvoker;
        // Commands are created with the Receiver - Controller doesn't use it directly
        this.lightOnCommand = new LightOnCommand(lightService);
        this.lightOffCommand = new LightOffCommand(lightService);
        this.getStatusCommand = new GetStatusCommand(lightService);
    }

    /**
     * Displays the Smart Home Dashboard.
     * 
     * REFACTORED COMMAND PATTERN FLOW:
     * 1. User navigates to /dashboard
     * 2. Controller identifies the correct Command (getStatusCommand)
     * 3. Controller pushes Command to CommandInvoker
     * 4. Controller triggers execution via the Invoker
     * 5. Invoker executes Command from queue
     * 6. Command returns LightState (Model)
     * 7. Controller adds Model to Spring's Model
     * 8. Controller returns "dashboard" (View name)
     * 
     * NOTE: Controller NEVER calls execute() directly - only the Invoker does
     * 
     * @param model Spring's Model to pass data to the View
     * @return The view name "dashboard"
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Push command to Invoker and trigger execution
        commandInvoker.push(getStatusCommand);
        LightState lightState = commandInvoker.executeCommands();
        
        // Add Model to Spring's Model for the View
        model.addAttribute("lightState", lightState);
        
        // Return View name
        return "dashboard";
    }

    /**
     * Turns ON the light and returns to dashboard.
     * 
     * REFACTORED COMMAND PATTERN FLOW:
     * 1. User clicks "Turn ON" button on View
     * 2. Controller identifies the correct Command (lightOnCommand)
     * 3. Controller pushes Command to CommandInvoker
     * 4. Controller triggers execution via the Invoker
     * 5. Invoker executes Command from queue
     * 6. Command delegates to Receiver (internally)
     * 7. Command returns LightState (Model)
     * 8. Controller returns "dashboard" (View name)
     * 
     * STRICT DECOUPLING: Controller doesn't know about LightService,
     * doesn't know HOW to turn on the light, and doesn't call execute() directly.
     * 
     * @param model Spring's Model to pass data to the View
     * @return The view name "dashboard"
     */
    @GetMapping("/light/on")
    public String turnLightOn(Model model) {
        // Push command to Invoker and trigger execution
        commandInvoker.push(lightOnCommand);
        LightState lightState = commandInvoker.executeCommands();
        
        // Add Model to Spring's Model for the View
        model.addAttribute("lightState", lightState);
        
        // Return View name
        return "dashboard";
    }

    /**
     * Turns OFF the light and returns to dashboard.
     * 
     * REFACTORED COMMAND PATTERN FLOW:
     * 1. User clicks "Turn OFF" button on View
     * 2. Controller identifies the correct Command (lightOffCommand)
     * 3. Controller pushes Command to CommandInvoker
     * 4. Controller triggers execution via the Invoker
     * 5. Invoker executes Command from queue
     * 6. Command delegates to Receiver (internally)
     * 7. Command returns LightState (Model)
     * 8. Controller returns "dashboard" (View name)
     * 
     * STRICT DECOUPLING: Controller doesn't know about LightService,
     * doesn't know HOW to turn off the light, and doesn't call execute() directly.
     * 
     * @param model Spring's Model to pass data to the View
     * @return The view name "dashboard"
     */
    @GetMapping("/light/off")
    public String turnLightOff(Model model) {
        // Push command to Invoker and trigger execution
        commandInvoker.push(lightOffCommand);
        LightState lightState = commandInvoker.executeCommands();
        
        // Add Model to Spring's Model for the View
        model.addAttribute("lightState", lightState);
        
        // Return View name
        return "dashboard";
    }
}
