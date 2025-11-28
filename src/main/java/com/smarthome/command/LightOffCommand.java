package com.smarthome.command;

import com.smarthome.service.LightService;

/**
 * LightOffCommand - A Concrete Command in the Command Design Pattern.
 * 
 * This command encapsulates the action of turning OFF the smart light bulb.
 * It holds a reference to the Receiver (LightService) and delegates
 * the actual work to it.
 * 
 * DECOUPLING BENEFIT: The Controller (Invoker) doesn't need to know HOW
 * to turn off the light. It simply creates this command and calls execute().
 * All the implementation details are encapsulated within this command class.
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
     * 
     * DECOUPLING BENEFIT: The command knows WHAT action to perform,
     * but the Receiver (LightService) knows HOW to perform it.
     * 
     * @return Status message from the Receiver
     */
    @Override
    public String execute() {
        return lightService.turnOff();
    }
}
