package com.smarthome.controller;

import com.smarthome.command.Command;
import com.smarthome.command.GetStatusCommand;
import com.smarthome.command.LightOffCommand;
import com.smarthome.command.LightOnCommand;
import com.smarthome.invoker.CommandInvoker;
import com.smarthome.model.LightState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * DashboardController - The Client in the Strict Command Design Pattern.
 * 
 * REFACTORED ARCHITECTURE (Per UML Class Diagram):
 * - The Controller acts as a CLIENT, not an Invoker
 * - The Controller has reference to Command beans (injected by Spring)
 * - The Controller delegates command queuing and execution to the CommandInvoker
 * - The Controller has NO direct dependency on the Receiver (LightService)
 * - All communication with the Receiver happens through Commands via the Invoker
 * 
 * MVC ARCHITECTURE:
 * - @Controller annotation (NOT @RestController)
 * - Returns View names, not raw data
 * - Uses Spring's Model to pass data to the View
 * 
 * EXECUTION FLOW (Per UML Sequence Diagram):
 * Controller -> Sets Command via setCommand() -> Pushes Command via pushCurrentCommand()
 * -> Invoker queues Command -> Controller triggers executeCommands()
 * -> Invoker processes Queue (FIFO) -> Command calls Receiver -> Returns LightState
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
     * The CommandInvoker manages command queue and execution.
     * Controller sets commands on the Invoker and triggers execution.
     */
    private final CommandInvoker commandInvoker;

    /**
     * Command bean reference for getting light status (per UML: -command: Command).
     * Injected by Spring as a managed bean.
     */
    private final Command getStatusCommand;

    /**
     * Command bean reference for turning light ON (per UML: -command: Command).
     * Injected by Spring as a managed bean.
     */
    private final Command lightOnCommand;

    /**
     * Command bean reference for turning light OFF (per UML: -command: Command).
     * Injected by Spring as a managed bean.
     */
    private final Command lightOffCommand;

    /**
     * Constructor-based dependency injection.
     * The Controller receives the CommandInvoker and Command beans.
     * NOTE: The Controller does NOT receive LightService - strict decoupling.
     * 
     * @param commandInvoker The dedicated Invoker for command queuing and execution
     * @param getStatusCommand The command bean for getting light status
     * @param lightOnCommand The command bean for turning light ON
     * @param lightOffCommand The command bean for turning light OFF
     */
    @Autowired
    public DashboardController(CommandInvoker commandInvoker,
                               GetStatusCommand getStatusCommand,
                               LightOnCommand lightOnCommand,
                               LightOffCommand lightOffCommand) {
        this.commandInvoker = commandInvoker;
        this.getStatusCommand = getStatusCommand;
        this.lightOnCommand = lightOnCommand;
        this.lightOffCommand = lightOffCommand;
    }

    /**
     * Displays the Smart Home Dashboard.
     * 
     * COMMAND PATTERN FLOW (Per UML Sequence Diagram):
     * 1. User navigates to /dashboard
     * 2. Controller sets the GET_STATUS command via invoker.setCommand()
     * 3. Controller pushes Command to queue via invoker.pushCurrentCommand()
     * 4. Controller triggers execution via invoker.executeCommands()
     * 5. Invoker processes queue (FIFO) and executes Command
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
        // Set command via Invoker (per UML: setCommand)
        commandInvoker.setCommand(getStatusCommand);
        // Push command to queue (per UML: pushCurrentCommand)
        commandInvoker.pushCurrentCommand();
        // Execute commands and get result (per UML: executeCommands)
        LightState lightState = commandInvoker.executeCommands();
        
        // Add Model to Spring's Model for the View
        model.addAttribute("lightState", lightState);
        
        // Return View name
        return "dashboard";
    }

    /**
     * Turns ON the light and returns to dashboard.
     * 
     * COMMAND PATTERN FLOW (Per UML Sequence Diagram):
     * 1. User clicks "Turn ON" button on View
     * 2. Controller sets the LIGHT_ON command via invoker.setCommand()
     * 3. Controller pushes Command to queue via invoker.pushCurrentCommand()
     * 4. Controller triggers execution via invoker.executeCommands()
     * 5. Invoker processes queue (FIFO) and executes Command
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
        // Set command via Invoker (per UML: setCommand)
        commandInvoker.setCommand(lightOnCommand);
        // Push command to queue (per UML: pushCurrentCommand)
        commandInvoker.pushCurrentCommand();
        // Execute commands and get result (per UML: executeCommands)
        LightState lightState = commandInvoker.executeCommands();
        
        // Add Model to Spring's Model for the View
        model.addAttribute("lightState", lightState);
        
        // Return View name
        return "dashboard";
    }

    /**
     * Turns OFF the light and returns to dashboard.
     * 
     * COMMAND PATTERN FLOW (Per UML Sequence Diagram):
     * 1. User clicks "Turn OFF" button on View
     * 2. Controller sets the LIGHT_OFF command via invoker.setCommand()
     * 3. Controller pushes Command to queue via invoker.pushCurrentCommand()
     * 4. Controller triggers execution via invoker.executeCommands()
     * 5. Invoker processes queue (FIFO) and executes Command
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
        // Set command via Invoker (per UML: setCommand)
        commandInvoker.setCommand(lightOffCommand);
        // Push command to queue (per UML: pushCurrentCommand)
        commandInvoker.pushCurrentCommand();
        // Execute commands and get result (per UML: executeCommands)
        LightState lightState = commandInvoker.executeCommands();
        
        // Add Model to Spring's Model for the View
        model.addAttribute("lightState", lightState);
        
        // Return View name
        return "dashboard";
    }
}
