package com.smarthome;

import com.smarthome.command.Command;
import com.smarthome.command.LightOnCommand;
import com.smarthome.command.LightOffCommand;
import com.smarthome.command.GetStatusCommand;
import com.smarthome.invoker.CommandInvoker;
import com.smarthome.model.LightState;
import com.smarthome.service.LightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CommandInvoker class.
 * 
 * These tests verify:
 * 1. CommandInvoker maintains a FIFO queue of commands
 * 2. Commands can be pushed to the queue
 * 3. Commands are executed sequentially in FIFO order
 * 4. ExecuteCommands returns the result of the last command
 * 5. Queue management (size, clear) works correctly
 */
class CommandInvokerTest {

    private CommandInvoker commandInvoker;
    private LightService lightService;

    @BeforeEach
    void setUp() {
        commandInvoker = new CommandInvoker();
        lightService = new LightService();
    }

    // ==================== Queue Management Tests ====================

    @Test
    @DisplayName("CommandInvoker should start with empty queue")
    void commandInvoker_initialState_shouldHaveEmptyQueue() {
        assertEquals(0, commandInvoker.getQueueSize());
    }

    @Test
    @DisplayName("Push should add command to queue")
    void push_shouldAddCommandToQueue() {
        Command command = new LightOnCommand(lightService);
        
        commandInvoker.push(command);
        
        assertEquals(1, commandInvoker.getQueueSize());
    }

    @Test
    @DisplayName("Push null command should not add to queue")
    void push_nullCommand_shouldNotAddToQueue() {
        commandInvoker.push(null);
        
        assertEquals(0, commandInvoker.getQueueSize());
    }

    @Test
    @DisplayName("Multiple pushes should increase queue size")
    void multiplePushes_shouldIncreaseQueueSize() {
        Command onCommand = new LightOnCommand(lightService);
        Command offCommand = new LightOffCommand(lightService);
        
        commandInvoker.push(onCommand);
        commandInvoker.push(offCommand);
        
        assertEquals(2, commandInvoker.getQueueSize());
    }

    @Test
    @DisplayName("ClearQueue should remove all commands from queue")
    void clearQueue_shouldRemoveAllCommands() {
        commandInvoker.push(new LightOnCommand(lightService));
        commandInvoker.push(new LightOffCommand(lightService));
        
        commandInvoker.clearQueue();
        
        assertEquals(0, commandInvoker.getQueueSize());
    }

    // ==================== Execution Tests ====================

    @Test
    @DisplayName("ExecuteCommands on empty queue should return null")
    void executeCommands_emptyQueue_shouldReturnNull() {
        LightState result = commandInvoker.executeCommands();
        
        assertNull(result);
    }

    @Test
    @DisplayName("ExecuteCommands should execute command and return result")
    void executeCommands_singleCommand_shouldExecuteAndReturnResult() {
        commandInvoker.push(new LightOnCommand(lightService));
        
        LightState result = commandInvoker.executeCommands();
        
        assertNotNull(result);
        assertTrue(result.isOn());
        assertEquals("Light is ON", result.getStatusMessage());
    }

    @Test
    @DisplayName("ExecuteCommands should clear queue after execution")
    void executeCommands_shouldClearQueue() {
        commandInvoker.push(new LightOnCommand(lightService));
        
        commandInvoker.executeCommands();
        
        assertEquals(0, commandInvoker.getQueueSize());
    }

    @Test
    @DisplayName("ExecuteCommands should execute commands in FIFO order")
    void executeCommands_shouldExecuteInFIFOOrder() {
        // Push ON first, then OFF
        commandInvoker.push(new LightOnCommand(lightService));
        commandInvoker.push(new LightOffCommand(lightService));
        
        LightState result = commandInvoker.executeCommands();
        
        // Should return result of last command (OFF)
        assertNotNull(result);
        assertFalse(result.isOn());
        assertEquals("Light is OFF", result.getStatusMessage());
        // Light should be OFF (last command was OFF)
        assertFalse(lightService.isLightOn());
    }

    @Test
    @DisplayName("ExecuteCommands should execute all commands in order")
    void executeCommands_shouldExecuteAllCommandsInOrder() {
        // Push OFF first, then ON
        commandInvoker.push(new LightOffCommand(lightService));
        commandInvoker.push(new LightOnCommand(lightService));
        
        LightState result = commandInvoker.executeCommands();
        
        // Should return result of last command (ON)
        assertNotNull(result);
        assertTrue(result.isOn());
        assertEquals("Light is ON", result.getStatusMessage());
        // Light should be ON (last command was ON)
        assertTrue(lightService.isLightOn());
    }

    // ==================== PushAndExecute Tests ====================

    @Test
    @DisplayName("PushAndExecute should add command and execute immediately")
    void pushAndExecute_shouldAddAndExecuteImmediately() {
        LightState result = commandInvoker.pushAndExecute(new LightOnCommand(lightService));
        
        assertNotNull(result);
        assertTrue(result.isOn());
        assertEquals(0, commandInvoker.getQueueSize()); // Queue should be empty after execution
    }

    @Test
    @DisplayName("PushAndExecute should execute all pending commands")
    void pushAndExecute_withPendingCommands_shouldExecuteAll() {
        // First push a command without executing
        commandInvoker.push(new LightOnCommand(lightService));
        
        // Then push and execute (this should execute both)
        LightState result = commandInvoker.pushAndExecute(new LightOffCommand(lightService));
        
        // Result should be from the last command (OFF)
        assertNotNull(result);
        assertFalse(result.isOn());
        assertEquals(0, commandInvoker.getQueueSize());
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("CommandInvoker should properly delegate to commands")
    void commandInvoker_shouldProperlyDelegateToCommands() {
        // Initial state
        assertFalse(lightService.isLightOn());
        
        // Turn on using invoker
        commandInvoker.push(new LightOnCommand(lightService));
        LightState onState = commandInvoker.executeCommands();
        assertTrue(onState.isOn());
        assertTrue(lightService.isLightOn());
        
        // Turn off using invoker
        commandInvoker.push(new LightOffCommand(lightService));
        LightState offState = commandInvoker.executeCommands();
        assertFalse(offState.isOn());
        assertFalse(lightService.isLightOn());
    }

    @Test
    @DisplayName("GetStatusCommand should work through CommandInvoker")
    void getStatusCommand_throughInvoker_shouldWork() {
        // Get status when light is off
        LightState offStatus = commandInvoker.pushAndExecute(new GetStatusCommand(lightService));
        assertFalse(offStatus.isOn());
        assertEquals("Light is currently OFF", offStatus.getStatusMessage());
        
        // Turn on and get status
        commandInvoker.pushAndExecute(new LightOnCommand(lightService));
        LightState onStatus = commandInvoker.pushAndExecute(new GetStatusCommand(lightService));
        assertTrue(onStatus.isOn());
        assertEquals("Light is currently ON", onStatus.getStatusMessage());
    }

    @Test
    @DisplayName("Multiple command sequences should work correctly")
    void multipleCommandSequences_shouldWorkCorrectly() {
        // First sequence: ON
        commandInvoker.push(new LightOnCommand(lightService));
        LightState state1 = commandInvoker.executeCommands();
        assertTrue(state1.isOn());
        
        // Second sequence: OFF
        commandInvoker.push(new LightOffCommand(lightService));
        LightState state2 = commandInvoker.executeCommands();
        assertFalse(state2.isOn());
        
        // Third sequence: Multiple commands
        commandInvoker.push(new LightOnCommand(lightService));
        commandInvoker.push(new LightOffCommand(lightService));
        commandInvoker.push(new LightOnCommand(lightService));
        LightState state3 = commandInvoker.executeCommands();
        assertTrue(state3.isOn());
    }
}
