package com.smarthome;

import com.smarthome.command.Command;
import com.smarthome.command.LightOnCommand;
import com.smarthome.command.LightOffCommand;
import com.smarthome.service.LightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Command Pattern implementation.
 * 
 * These tests verify:
 * 1. LightService (Receiver) works correctly
 * 2. LightOnCommand and LightOffCommand execute properly
 * 3. Commands properly delegate to the LightService
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
    @DisplayName("LightOnCommand execute should turn light ON via LightService")
    void lightOnCommand_execute_shouldTurnLightOn() {
        Command command = new LightOnCommand(lightService);
        
        String result = command.execute();
        
        assertEquals("Light is ON", result);
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
    @DisplayName("LightOffCommand execute should turn light OFF via LightService")
    void lightOffCommand_execute_shouldTurnLightOff() {
        lightService.turnOn(); // First turn on
        Command command = new LightOffCommand(lightService);
        
        String result = command.execute();
        
        assertEquals("Light is OFF", result);
        assertFalse(lightService.isLightOn());
    }

    // ==================== Command Pattern Integration Tests ====================

    @Test
    @DisplayName("Commands should properly delegate to the Receiver")
    void commands_shouldDelegateToReceiver() {
        // Initial state
        assertFalse(lightService.isLightOn());
        
        // Turn on using command
        Command onCommand = new LightOnCommand(lightService);
        onCommand.execute();
        assertTrue(lightService.isLightOn());
        
        // Turn off using command
        Command offCommand = new LightOffCommand(lightService);
        offCommand.execute();
        assertFalse(lightService.isLightOn());
    }

    @Test
    @DisplayName("Multiple command executions should work correctly")
    void multipleCommands_shouldWorkCorrectly() {
        Command onCommand = new LightOnCommand(lightService);
        Command offCommand = new LightOffCommand(lightService);
        
        // Execute on, off, on, off sequence
        assertEquals("Light is ON", onCommand.execute());
        assertTrue(lightService.isLightOn());
        
        assertEquals("Light is OFF", offCommand.execute());
        assertFalse(lightService.isLightOn());
        
        assertEquals("Light is ON", onCommand.execute());
        assertTrue(lightService.isLightOn());
        
        assertEquals("Light is OFF", offCommand.execute());
        assertFalse(lightService.isLightOn());
    }
}
