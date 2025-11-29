package com.smarthome.command;

import com.smarthome.model.LightState;
import com.smarthome.service.LightService;

/**
 * LightOffCommand - A Concrete Command in the Command Design Pattern.
 * 
 * This command encapsulates the action of turning OFF the smart light bulb.
 * It holds a reference to the Receiver (LightService) and delegates
 * the actual work to it.
 * 
 * STRICT DECOUPLING: The Controller (Invoker) doesn't need to know about
 * the LightService. It only knows about the Command interface.
 * This command handles all interaction with the Receiver internally.
 */
public class LightOffCommand implements Command {

    /**
     * The Receiver - the service that performs the actual hardware logic
     */
    private final LightService lightService;

    /**
     * Constructor that receives the Receiver (LightService) as a dependency.
     * 
     * @param lightService The service that will perform the actual operation
     */
    public LightOffCommand(LightService lightService) {
        this.lightService = lightService;
    }

    /**
     * Executes the "Turn Off" command by delegating to the LightService.
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
        String result = lightService.turnOff();
        return new LightState(lightService.isLightOn(), result);
    }
}
