package com.smarthome;

import com.smarthome.model.LightState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the LightState Model.
 * 
 * These tests verify that the Model POJO works correctly
 * as a data carrier between Controller and View.
 */
class LightStateTest {

    @Test
    @DisplayName("LightState should be created with default constructor")
    void lightState_defaultConstructor_shouldWork() {
        LightState lightState = new LightState();
        
        assertNotNull(lightState);
        assertFalse(lightState.isOn());
        assertNull(lightState.getStatusMessage());
    }

    @Test
    @DisplayName("LightState should be created with parameterized constructor")
    void lightState_parameterizedConstructor_shouldSetValues() {
        LightState lightState = new LightState(true, "Light is ON");
        
        assertTrue(lightState.isOn());
        assertEquals("Light is ON", lightState.getStatusMessage());
    }

    @Test
    @DisplayName("LightState setters should update values")
    void lightState_setters_shouldUpdateValues() {
        LightState lightState = new LightState();
        
        lightState.setOn(true);
        lightState.setStatusMessage("Test status");
        
        assertTrue(lightState.isOn());
        assertEquals("Test status", lightState.getStatusMessage());
    }

    @Test
    @DisplayName("LightState should represent OFF state correctly")
    void lightState_offState_shouldBeRepresentedCorrectly() {
        LightState lightState = new LightState(false, "Light is OFF");
        
        assertFalse(lightState.isOn());
        assertEquals("Light is OFF", lightState.getStatusMessage());
    }
}
