package com.smarthome;

import com.smarthome.controller.SmartHomeController;
import com.smarthome.service.LightService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the SmartHomeController.
 * 
 * These tests verify that the REST endpoints work correctly
 * and properly delegate to the Command Pattern implementation.
 */
@WebMvcTest(SmartHomeController.class)
class SmartHomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LightService lightService;

    @Test
    @DisplayName("GET /api/light/on should turn on the light and return correct message")
    void turnLightOn_shouldReturnLightIsOn() throws Exception {
        when(lightService.turnOn()).thenReturn("Light is ON");

        mockMvc.perform(get("/api/light/on"))
                .andExpect(status().isOk())
                .andExpect(content().string("Light is ON"));

        verify(lightService, times(1)).turnOn();
    }

    @Test
    @DisplayName("GET /api/light/off should turn off the light and return correct message")
    void turnLightOff_shouldReturnLightIsOff() throws Exception {
        when(lightService.turnOff()).thenReturn("Light is OFF");

        mockMvc.perform(get("/api/light/off"))
                .andExpect(status().isOk())
                .andExpect(content().string("Light is OFF"));

        verify(lightService, times(1)).turnOff();
    }

    @Test
    @DisplayName("GET /api/light/status should return current light status")
    void getLightStatus_shouldReturnCurrentStatus() throws Exception {
        when(lightService.getStatus()).thenReturn("Light is currently OFF");

        mockMvc.perform(get("/api/light/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("Light is currently OFF"));

        verify(lightService, times(1)).getStatus();
    }

    @Test
    @DisplayName("GET /api/light/status when light is ON should return ON status")
    void getLightStatus_whenOn_shouldReturnOnStatus() throws Exception {
        when(lightService.getStatus()).thenReturn("Light is currently ON");

        mockMvc.perform(get("/api/light/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("Light is currently ON"));
    }
}
