package com.example.sw_planet_api;

import static com.example.sw_planet_api.common.PlanetConstants.PLANET;
import com.example.sw_planet_api.domain.Planet;

import static com.example.sw_planet_api.common.PlanetConstants.TATOOINE;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("it")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/import_planets.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/remove_planets.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PlanetIT {
    @Autowired
    private TestRestTemplate restTemplate;
    @Test
    public void createPlanet_ReturnsCreated(){
        ResponseEntity<Planet> sut = restTemplate
                .postForEntity("/planets", PLANET, Planet.class);

        assertEquals(HttpStatus.CREATED, sut.getStatusCode());
        assertNotNull(sut.getBody());
        assertNotNull(sut.getBody().getId());
        assertEquals(PLANET.getClimate(), sut.getBody().getClimate());
        assertEquals(PLANET.getName(), sut.getBody().getName());
        assertEquals(PLANET.getTerrain(), sut.getBody().getTerrain());

   }

   @Test
   public void getPlanet_ReturnsPlanet(){
        ResponseEntity<Planet> sut = restTemplate.getForEntity("/planets/1", Planet.class);

        assertEquals(HttpStatus.OK, sut.getStatusCode());
        assertNotNull(sut.getBody());
        assertNotNull(sut.getBody().getId());
        assertEquals(TATOOINE, sut.getBody());
   }

   @Test
   public void getPlanetsByName_ReturnsPlanet(){
        ResponseEntity<Planet> sut = restTemplate.getForEntity("/planets/name/" + TATOOINE.getName(), Planet.class);

        assertEquals(HttpStatus.OK, sut.getStatusCode());
        assertNotNull(sut.getBody());
        assertNotNull(sut.getBody().getId());
   }

   @Test
   public void listPlanets_ReturnsAllPlanets(){
        ResponseEntity<Planet[]> sut = restTemplate.getForEntity("/planets", Planet[].class);

        assertEquals(HttpStatus.OK, sut.getStatusCode());
        assertNotNull(sut.getBody());
        assertEquals(3, sut.getBody().length);
        assertEquals(TATOOINE, sut.getBody()[0]);
   }

    @Test
    public void listPlanets_ByClimate_ReturnsPlanets(){
        ResponseEntity<Planet[]> sut =
                restTemplate.getForEntity(
                        "/planets?climate=" + TATOOINE.getClimate(),
                        Planet[].class
                );

        assertEquals(HttpStatus.OK, sut.getStatusCode());
        assertNotNull(sut.getBody());
        assertEquals(1, sut.getBody().length);
        assertEquals(TATOOINE, sut.getBody()[0]);
    }


    @Test
    public void listPlanets_ByTerrain_ReturnsPlanets(){
        ResponseEntity<Planet[]> sut = restTemplate.getForEntity("/planets?terrain=" + TATOOINE.getTerrain(), Planet[].class);

        assertEquals(HttpStatus.OK, sut.getStatusCode());
        assertNotNull(sut.getBody());
        assertEquals(1, sut.getBody().length);
        assertEquals(TATOOINE, sut.getBody()[0]);
    }

    @Test
    public void removePlanet_ReturnsNoContent(){
        ResponseEntity<Void> sut = restTemplate.exchange("/planets/" + TATOOINE.getId(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, sut.getStatusCode());
    }
}
