package com.example.sw_planet_api.domain;

import static com.example.sw_planet_api.common.PlanetConstants.TATOOINE;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.EntityExistsException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.example.sw_planet_api.common.PlanetConstants.PLANET;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Carrega os componentes que interagem com o jpa (repositorys)
@DataJpaTest
public class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository planetRepository;

    //Permite interagir com o bd sem ser o repository
    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    public void afterEach(){
        PLANET.setId(null);
    }
    @Test
    public void createPlanet_WithValidDate_ReturnsPlanet(){
        // Arrange
        Planet planet = planetRepository.save(PLANET);

        // Act
        Planet sut = testEntityManager.find(Planet.class, planet.getId());

        //Assert
        assertNotNull(sut);
        assertEquals(planet.getName(), sut.getName());
        assertEquals(planet.getClimate(), sut.getClimate());
        assertEquals(planet.getTerrain(), sut.getTerrain());
    }

    @ParameterizedTest
    @MethodSource("providesInvalidPlanets")
    public void createPlanet_WithInvalidDate_Thorws(Planet planet){
        assertThatThrownBy(() -> planetRepository.save(planet)).isInstanceOf(RuntimeException.class);
    }

    private static Stream<Arguments> providesInvalidPlanets(){
        return Stream.of(
                Arguments.of(new Planet(null, "climate", "terrain")),
                Arguments.of(new Planet("name", null, "terrain")),
                Arguments.of(new Planet("name", "climate", null))
        );
    }
    @Test
    public void createPlanet_WithExistingName_ThorwsExcption(){
        Planet planet = testEntityManager.persistAndFlush(PLANET);
        // Planeta não é mais gerenciado pelo entity manager
        testEntityManager.detach(planet);
        planet.setId(null);

        assertThatThrownBy(() -> planetRepository.save(planet))
                .isInstanceOf(RuntimeException.class);
    }
    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() throws Exception{
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        Optional<Planet> planetOpt = planetRepository.findById(planet.getId());

        assertThat(planetOpt).isNotEmpty();
        assertThat(planetOpt.get()).isEqualTo(planet);
    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnsNotFound() {
        Optional<Planet> planetOpt = planetRepository.findById(999L);
        assertThat(planetOpt).isEmpty();
    }


    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() throws Exception{
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        Optional<Planet> planetOpt = planetRepository.findByName(planet.getName());

        assertThat(planetOpt).isNotEmpty();
        assertThat(planetOpt.get()).isEqualTo(planet);
    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnsNotFound() {
        Optional<Planet> planetOpt = planetRepository.findByName("INVALID_NAME");
        assertThat(planetOpt).isEmpty();
    }

    @Sql(scripts = "/import_planets.sql")
    @Test
    public void listPlanets_ReturnsFilteredPlanets() throws Exception{
        Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(new Planet());
        Example<Planet> queryWithFilters = QueryBuilder.makeQuery(new Planet(TATOOINE.getClimate(), TATOOINE.getTerrain()));

        List<Planet> responseWithoutFilters = planetRepository.findAll(queryWithoutFilters);

        List<Planet> responseWithFilters = planetRepository.findAll(queryWithFilters);

        assertThat(responseWithoutFilters).isNotEmpty();
        assertThat(responseWithoutFilters).hasSize(3);

        assertThat(responseWithFilters).isNotEmpty();
        assertThat(responseWithFilters).hasSize(1);
        assertThat(responseWithFilters.getFirst()).isEqualTo(TATOOINE);

    }

    @Test
    public void listPlanets_ReturnsNotPlanets() throws Exception{
        Example<Planet> query = QueryBuilder.makeQuery(new Planet());

        List<Planet> response = planetRepository.findAll(query);

        assertThat(response).isEmpty();

    }

    @Test
    public void removePlanet_WithExistingId_RemovesPlanetFromDatabase() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        planetRepository.deleteById(planet.getId());

        Planet removedPlanet =  testEntityManager.find(Planet.class, planet.getId());
        assertThat(removedPlanet).isNull();

    }

//    implementação do jpa não lança exceção mais
    @Test
    void removePlanet_WithUnexistingId_DoesNotThrowException() {
        assertThatCode(() -> planetRepository.deleteById(999L))
                .doesNotThrowAnyException();
    }
}
