package com.smarthome;

import com.smarthome.controller.DashboardController;
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
 * Integration tests for the DashboardController (MVC Controller).
 * 
 * These tests verify that the MVC flow works correctly:
 * - Controller receives requests
 * - Controller updates the Model
 * - Controller returns the correct View name
 */
@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LightService lightService;

    @Test
    @DisplayName("GET /dashboard should return dashboard view with light state")
    void showDashboard_shouldReturnDashboardView() throws Exception {
        when(lightService.isLightOn()).thenReturn(false);
        when(lightService.getStatus()).thenReturn("Light is currently OFF");

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("lightState"));

        verify(lightService, times(1)).isLightOn();
        verify(lightService, times(1)).getStatus();
    }

    @Test
    @DisplayName("GET /light/on should turn on light and return dashboard view")
    void turnLightOn_shouldReturnDashboardViewWithLightOn() throws Exception {
        when(lightService.turnOn()).thenReturn("Light is ON");
        when(lightService.isLightOn()).thenReturn(true);

        mockMvc.perform(get("/light/on"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("lightState"));

        verify(lightService, times(1)).turnOn();
    }

    @Test
    @DisplayName("GET /light/off should turn off light and return dashboard view")
    void turnLightOff_shouldReturnDashboardViewWithLightOff() throws Exception {
        when(lightService.turnOff()).thenReturn("Light is OFF");
        when(lightService.isLightOn()).thenReturn(false);

        mockMvc.perform(get("/light/off"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("lightState"));

        verify(lightService, times(1)).turnOff();
    }
}
