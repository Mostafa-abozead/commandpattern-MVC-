package com.smarthome.controller;

import com.smarthome.command.Command;
import com.smarthome.command.LightOnCommand;
import com.smarthome.command.LightOffCommand;
import com.smarthome.model.LightState;
import com.smarthome.service.LightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * DashboardController - The Controller in Strict MVC Architecture (also Invoker in Command Pattern).
 * 
 * This controller demonstrates the classic Spring MVC pattern:
 * 1. Receives HTTP request from the User
 * 2. Creates and executes Command (Command Pattern)
 * 3. Updates the Model with data (MVC Model)
 * 4. Returns the View name (MVC View)
 * 
 * KEY ANNOTATIONS:
 * - @Controller (NOT @RestController): Returns View names, not raw data
 * - Methods receive Spring's Model to pass data to the View
 * 
 * DECOUPLING BENEFITS:
 * - Controller doesn't know HOW to turn on/off the light (Command Pattern)
 * - Controller doesn't know HOW the View renders the data (MVC)
 * - Model (LightState) is a simple data carrier between Controller and View
 */
@Controller
public class DashboardController {

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
    public DashboardController(LightService lightService) {
        this.lightService = lightService;
    }

    /**
     * Displays the Smart Home Dashboard.
     * 
     * MVC Flow:
     * 1. User navigates to /dashboard
     * 2. Controller gets current light state from Service
     * 3. Controller creates LightState Model with current data
     * 4. Controller adds Model to Spring's Model
     * 5. Controller returns "dashboard" (View name)
     * 6. Thymeleaf renders dashboard.html with the Model data
     * 
     * @param model Spring's Model to pass data to the View
     * @return The view name "dashboard"
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Get current state from service
        boolean isOn = lightService.isLightOn();
        String status = lightService.getStatus();
        
        // Create the Model (LightState) with current data
        LightState lightState = new LightState(isOn, status);
        
        // Add Model to Spring's Model for the View
        model.addAttribute("lightState", lightState);
        
        // Return View name - Thymeleaf will render dashboard.html
        return "dashboard";
    }

    /**
     * Turns ON the light and returns to dashboard.
     * 
     * Complete MVC + Command Pattern Flow:
     * 1. User clicks "Turn ON" button on View (dashboard.html)
     * 2. Request goes to Controller (/light/on)
     * 3. Controller creates LightOnCommand with Receiver (Command Pattern)
     * 4. Controller executes Command
     * 5. Command delegates to LightService.turnOn() (Receiver)
     * 6. Controller updates Model (LightState) with new state
     * 7. Controller returns "dashboard" (View name)
     * 8. View displays updated light status
     * 
     * DECOUPLING: The controller doesn't know HOW to turn on the light,
     * only the Command and Service know that.
     * 
     * @param model Spring's Model to pass data to the View
     * @return The view name "dashboard"
     */
    @GetMapping("/light/on")
    public String turnLightOn(Model model) {
        // Create the concrete command with the receiver (Command Pattern)
        Command command = new LightOnCommand(lightService);
        
        // Execute the command - the Invoker doesn't know the implementation details
        String result = command.execute();
        
        // Update the Model with new state
        LightState lightState = new LightState(lightService.isLightOn(), result);
        model.addAttribute("lightState", lightState);
        
        // Return View name - completes the MVC cycle
        return "dashboard";
    }

    /**
     * Turns OFF the light and returns to dashboard.
     * 
     * Complete MVC + Command Pattern Flow:
     * 1. User clicks "Turn OFF" button on View (dashboard.html)
     * 2. Request goes to Controller (/light/off)
     * 3. Controller creates LightOffCommand with Receiver (Command Pattern)
     * 4. Controller executes Command
     * 5. Command delegates to LightService.turnOff() (Receiver)
     * 6. Controller updates Model (LightState) with new state
     * 7. Controller returns "dashboard" (View name)
     * 8. View displays updated light status
     * 
     * DECOUPLING: The controller doesn't know HOW to turn off the light,
     * only the Command and Service know that.
     * 
     * @param model Spring's Model to pass data to the View
     * @return The view name "dashboard"
     */
    @GetMapping("/light/off")
    public String turnLightOff(Model model) {
        // Create the concrete command with the receiver (Command Pattern)
        Command command = new LightOffCommand(lightService);
        
        // Execute the command - the Invoker doesn't know the implementation details
        String result = command.execute();
        
        // Update the Model with new state
        LightState lightState = new LightState(lightService.isLightOn(), result);
        model.addAttribute("lightState", lightState);
        
        // Return View name - completes the MVC cycle
        return "dashboard";
    }
}
