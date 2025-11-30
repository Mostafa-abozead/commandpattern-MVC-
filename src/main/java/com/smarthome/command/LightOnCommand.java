package com.smarthome.command;

import com.smarthome.model.LightState;
import com.smarthome.service.LightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * LightOnCommand - A Concrete Command in the Command Design Pattern.
 * 
 * This command encapsulates the action of turning ON the smart light bulb.
 * It holds a reference to the Receiver (LightService) and delegates
 * the actual work to it.
 * 
 * STRICT DECOUPLING: The Controller (Client) doesn't need to know about
 * the LightService. It only knows about the Command interface.
 * This command handles all interaction with the Receiver internally.
 * 
 * SPRING BEAN: This command is a Spring-managed bean (@Component) that
 * is injected with the LightService (Receiver) dependency.
 */
@Component
public class LightOnCommand implements Command {

    /**
     * The Receiver - the service that performs the actual hardware logic
     */
    private final LightService lightService;

    /**
     * Constructor that receives the Receiver (LightService) as a dependency.
     * Spring will inject the LightService bean automatically.
     * 
     * @param lightService The service that will perform the actual operation
     */
    @Autowired
    public LightOnCommand(LightService lightService) {
        this.lightService = lightService;
    }

    /**
     * Executes the "Turn On" command by delegating to the LightService.
     * Returns a LightState object so the Controller doesn't need to
     * access the Service directly.
     * 
     * STRICT DECOUPLING: The command knows WHAT action to perform,
     * and also returns the state - Controller never touches the Receiver.
     * 
     * @return LightState containing the result of the operation
     */
    @Override
    public LightState execute() {
        String result = lightService.turnOn();
        return new LightState(lightService.isLightOn(), result);
    }
}
