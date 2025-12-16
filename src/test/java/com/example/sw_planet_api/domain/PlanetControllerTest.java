package com.example.sw_planet_api.domain;

import com.example.sw_planet_api.web.PlanetController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static com.example.sw_planet_api.common.PlanetConstants.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PlanetService planetService;

    @Test
    public void createPlanet_WithValidData_ReturnsCreated() throws Exception {

        when(planetService.create(PLANET)).thenReturn(PLANET);

        mockMvc
                .perform(
                    post("/planets").content(objectMapper.writeValueAsString(PLANET)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    public void createPlanet_WithInvalidDate_ReturnsBadRequest() throws Exception {
        Planet emmptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");

        mockMvc
                .perform(
                        post("/planets").content(objectMapper.writeValueAsString(emmptyPlanet)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        mockMvc
                .perform(
                        post("/planets").content(objectMapper.writeValueAsString(invalidPlanet)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

    }

    @Test
    public void createPlanet_WithExistingName_ReturnsConflict() throws Exception {
        when(planetService
                .create(any())
        ).thenThrow(DataIntegrityViolationException.class);

        mockMvc
                .perform(
                        post("/planets").content(objectMapper.writeValueAsString(PLANET))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() throws Exception{
        when(planetService.get(anyLong())).thenReturn(Optional.of(PLANET));

        mockMvc.perform(
                        get("/planets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));

    }

    @Test
    public void getPlanet_ByUnexistinId_ReturnsNotFound() throws Exception {
        when(planetService.get(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/planets/1"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() throws Exception{
        when(planetService.getByName(PLANET.getName())).thenReturn(Optional.of(PLANET));

        mockMvc.perform(
                get("/planets/name/" + PLANET.getName())
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(PLANET)
        );
    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnsNotFound() throws Exception {
        when(planetService.getByName("1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/planets/name/1"))
                .andExpect(status().isNotFound());
    }


    @Test
    public void listPlanets_ReturnsFilteredPlanets() throws Exception{
        when(planetService.list(null, null)).thenReturn(PLANETS);
        when(planetService.list(TATOOINE.getTerrain(), TATOOINE.getClimate())).thenReturn(List.of(TATOOINE));


        mockMvc
            .perform(
                get("/planets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3))
        );

        mockMvc
            .perform(
                    get("/planets?" + String.format("terrain=%s&climate=%s", TATOOINE.getTerrain(), TATOOINE.getClimate())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("[0]").value(TATOOINE)
        );

    }

    @Test
    public void listPlanets_ReturnsNotPlanets() throws Exception{
        when(planetService.list(null, null)).thenReturn(Collections.emptyList());

        mockMvc
            .perform(
                    get("/planets" ))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0))
        );
    }

    @Test
    public void removePlanet_WithExistingId_ReturnsNoContent() throws Exception {

        mockMvc
            .perform(
                delete("/planets/1")
            )
            .andExpect(status().isNoContent());
    }

    @Test
    public void removePlanet_WithUnexistingId_ReturnsNotFound() throws Exception {
        final Long id = 1L;
        doThrow(new EmptyResultDataAccessException(1)).when(planetService).remove(id);

        mockMvc
            .perform(
                delete("/planets/" + id)
            )
            .andExpect(status().isNotFound());
    }
}
