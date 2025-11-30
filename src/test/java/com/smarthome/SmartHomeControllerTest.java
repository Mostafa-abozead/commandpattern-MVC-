package com.smarthome;

import com.smarthome.controller.SmartHomeController;
import com.smarthome.invoker.CommandInvoker;
import com.smarthome.service.LightService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for the SmartHomeController (REST API).
 * 
 * These tests verify that the REST endpoints work correctly
 * and properly delegate to the CommandInvoker which executes
 * the Strict Command Pattern implementation.
 */
@WebMvcTest(SmartHomeController.class)
@Import(CommandInvoker.class)
class SmartHomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LightService lightService;

    @Test
    @DisplayName("GET /api/light/on should turn on the light and return LightState")
    void turnLightOn_shouldReturnLightStateOn() throws Exception {
        when(lightService.turnOn()).thenReturn("Light is ON");
        when(lightService.isLightOn()).thenReturn(true);

        mockMvc.perform(get("/api/light/on"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.on", is(true)))
                .andExpect(jsonPath("$.statusMessage", is("Light is ON")));

        verify(lightService, times(1)).turnOn();
    }

    @Test
    @DisplayName("GET /api/light/off should turn off the light and return LightState")
    void turnLightOff_shouldReturnLightStateOff() throws Exception {
        when(lightService.turnOff()).thenReturn("Light is OFF");
        when(lightService.isLightOn()).thenReturn(false);

        mockMvc.perform(get("/api/light/off"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.on", is(false)))
                .andExpect(jsonPath("$.statusMessage", is("Light is OFF")));

        verify(lightService, times(1)).turnOff();
    }

    @Test
    @DisplayName("GET /api/light/status should return current LightState")
    void getLightStatus_shouldReturnCurrentLightState() throws Exception {
        when(lightService.getStatus()).thenReturn("Light is currently OFF");
        when(lightService.isLightOn()).thenReturn(false);

        mockMvc.perform(get("/api/light/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.on", is(false)))
                .andExpect(jsonPath("$.statusMessage", is("Light is currently OFF")));

        verify(lightService, times(1)).getStatus();
    }

    @Test
    @DisplayName("GET /api/light/status when light is ON should return ON state")
    void getLightStatus_whenOn_shouldReturnOnState() throws Exception {
        when(lightService.getStatus()).thenReturn("Light is currently ON");
        when(lightService.isLightOn()).thenReturn(true);

        mockMvc.perform(get("/api/light/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.on", is(true)))
                .andExpect(jsonPath("$.statusMessage", is("Light is currently ON")));
    }
}
