package com.smarthome;

import com.smarthome.command.Command;
import com.smarthome.command.LightOnCommand;
import com.smarthome.command.LightOffCommand;
import com.smarthome.command.GetStatusCommand;
import com.smarthome.model.LightState;
import com.smarthome.service.LightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Strict Command Pattern implementation.
 * 
 * These tests verify:
 * 1. LightService (Receiver) works correctly
 * 2. Commands return LightState (Model)
 * 3. Commands properly delegate to the LightService
 * 4. Strict decoupling - Commands are the only way to access Receiver
 */
class CommandPatternTest {

    private LightService lightService;

    @BeforeEach
    void setUp() {
        lightService = new LightService();
    }

    // ==================== LightService (Receiver) Tests ====================

    @Test
    @DisplayName("LightService should start with light OFF")
    void lightService_initialState_shouldBeOff() {
        assertFalse(lightService.isLightOn());
    }

    @Test
    @DisplayName("LightService turnOn should turn the light ON")
    void lightService_turnOn_shouldTurnLightOn() {
        String result = lightService.turnOn();
        
        assertEquals("Light is ON", result);
        assertTrue(lightService.isLightOn());
    }

    @Test
    @DisplayName("LightService turnOff should turn the light OFF")
    void lightService_turnOff_shouldTurnLightOff() {
        lightService.turnOn(); // First turn on
        String result = lightService.turnOff();
        
        assertEquals("Light is OFF", result);
        assertFalse(lightService.isLightOn());
    }

    @Test
    @DisplayName("LightService getStatus should return correct status when OFF")
    void lightService_getStatus_whenOff_shouldReturnOffStatus() {
        String status = lightService.getStatus();
        
        assertEquals("Light is currently OFF", status);
    }

    @Test
    @DisplayName("LightService getStatus should return correct status when ON")
    void lightService_getStatus_whenOn_shouldReturnOnStatus() {
        lightService.turnOn();
        String status = lightService.getStatus();
        
        assertEquals("Light is currently ON", status);
    }

    // ==================== LightOnCommand Tests ====================

    @Test
    @DisplayName("LightOnCommand should implement Command interface")
    void lightOnCommand_shouldImplementCommandInterface() {
        Command command = new LightOnCommand(lightService);
        
        assertNotNull(command);
        assertTrue(command instanceof Command);
    }

    @Test
    @DisplayName("LightOnCommand execute should return LightState with ON status")
    void lightOnCommand_execute_shouldReturnLightStateOn() {
        Command command = new LightOnCommand(lightService);
        
        LightState result = command.execute();
        
        assertTrue(result.isOn());
        assertEquals("Light is ON", result.getStatusMessage());
        assertTrue(lightService.isLightOn());
    }

    // ==================== LightOffCommand Tests ====================

    @Test
    @DisplayName("LightOffCommand should implement Command interface")
    void lightOffCommand_shouldImplementCommandInterface() {
        Command command = new LightOffCommand(lightService);
        
        assertNotNull(command);
        assertTrue(command instanceof Command);
    }

    @Test
    @DisplayName("LightOffCommand execute should return LightState with OFF status")
    void lightOffCommand_execute_shouldReturnLightStateOff() {
        lightService.turnOn(); // First turn on
        Command command = new LightOffCommand(lightService);
        
        LightState result = command.execute();
        
        assertFalse(result.isOn());
        assertEquals("Light is OFF", result.getStatusMessage());
        assertFalse(lightService.isLightOn());
    }

    // ==================== GetStatusCommand Tests ====================

    @Test
    @DisplayName("GetStatusCommand should return current LightState")
    void getStatusCommand_shouldReturnCurrentState() {
        Command command = new GetStatusCommand(lightService);
        
        LightState result = command.execute();
        
        assertFalse(result.isOn());
        assertEquals("Light is currently OFF", result.getStatusMessage());
    }

    @Test
    @DisplayName("GetStatusCommand should return ON state after light is turned on")
    void getStatusCommand_afterTurnOn_shouldReturnOnState() {
        lightService.turnOn();
        Command command = new GetStatusCommand(lightService);
        
        LightState result = command.execute();
        
        assertTrue(result.isOn());
        assertEquals("Light is currently ON", result.getStatusMessage());
    }

    // ==================== Command Pattern Integration Tests ====================

    @Test
    @DisplayName("Commands should properly delegate to the Receiver and return LightState")
    void commands_shouldDelegateToReceiver() {
        // Initial state
        assertFalse(lightService.isLightOn());
        
        // Turn on using command
        Command onCommand = new LightOnCommand(lightService);
        LightState onState = onCommand.execute();
        assertTrue(onState.isOn());
        assertTrue(lightService.isLightOn());
        
        // Turn off using command
        Command offCommand = new LightOffCommand(lightService);
        LightState offState = offCommand.execute();
        assertFalse(offState.isOn());
        assertFalse(lightService.isLightOn());
    }

    @Test
    @DisplayName("Multiple command executions should work correctly")
    void multipleCommands_shouldWorkCorrectly() {
        Command onCommand = new LightOnCommand(lightService);
        Command offCommand = new LightOffCommand(lightService);
        
        // Execute on, off, on, off sequence
        LightState state1 = onCommand.execute();
        assertTrue(state1.isOn());
        
        LightState state2 = offCommand.execute();
        assertFalse(state2.isOn());
        
        LightState state3 = onCommand.execute();
        assertTrue(state3.isOn());
        
        LightState state4 = offCommand.execute();
        assertFalse(state4.isOn());
    }
}
