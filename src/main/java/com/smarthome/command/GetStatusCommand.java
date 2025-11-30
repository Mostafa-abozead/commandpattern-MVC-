package com.smarthome.command;

import com.smarthome.model.LightState;
import com.smarthome.service.LightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * GetStatusCommand - A Concrete Command to get the current light status.
 * 
 * This command encapsulates the action of querying the current state
 * of the smart light bulb without modifying it.
 * 
 * STRICT DECOUPLING: This allows the Controller to get status
 * without having any direct reference to the LightService.
 * 
 * SPRING BEAN: This command is a Spring-managed bean (@Component) that
 * is injected with the LightService (Receiver) dependency.
 */
@Component
public class GetStatusCommand implements Command {

    /**
     * The Receiver - the service that holds the light state
     */
    private final LightService lightService;

    /**
     * Constructor that receives the Receiver (LightService) as a dependency.
     * Spring will inject the LightService bean automatically.
     * 
     * @param lightService The service that holds the light state
     */
    @Autowired
    public GetStatusCommand(LightService lightService) {
        this.lightService = lightService;
    }

    /**
     * Executes the "Get Status" command by querying the LightService.
     * Returns a LightState object with the current state.
     * 
     * @return LightState containing the current light status
     */
    @Override
    public LightState execute() {
        return new LightState(lightService.isLightOn(), lightService.getStatus());
    }
}
