package com.example.sw_planet_api.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static com.example.sw_planet_api.common.PlanetConstants.PLANET;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = PlanetService.class)
public class PlanetServiceTest {
    @Autowired
    private PlanetService planetService;

    // operação_estado_retorno
    @Test
    public void createPlanet_WithValidDate_ReturnsPlanet(){
        // sut - system under test
        Planet sut = planetService.create(PLANET);

        assertEquals(PLANET, sut);    }
}
