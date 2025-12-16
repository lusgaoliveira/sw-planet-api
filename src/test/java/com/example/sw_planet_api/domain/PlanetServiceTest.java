package com.example.sw_planet_api.domain;

import static com.example.sw_planet_api.common.PlanetConstants.INVALID_PLANET;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static com.example.sw_planet_api.common.PlanetConstants.PLANET;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
//@SpringBootTest(classes = PlanetService.class) -> Mais pesado/menor perfomance
public class PlanetServiceTest {
//    @Autowired

    @InjectMocks //Cria instância real e todas depedências são injetadas com mocks
    private PlanetService planetService;

    //    @MockitoBean
    @Mock
    private PlanetRepository planetRepository;

    // operação_estado_retorno
    @Test
    public void createPlanet_WithValidDate_ReturnsPlanet() {
        //AAA
        // Arrange - Preparar/arrumar dados para teste
        //mocando comportamento da depedência (resultado) -> o create vai retornar isso
        when(planetRepository.save(PLANET)).thenReturn(PLANET);
        // sut - system under test

        // Act - Realizar a operação teste
        Planet sut = planetService.create(PLANET);

        // Assert - Aferir o resultado
        assertEquals(PLANET, sut);
    }

    @Test
    public void createPlanet_WithInvalidDate_ThrowsException() {
        when(planetRepository.save(INVALID_PLANET)).thenThrow(RuntimeException.class);

        assertThatThrownBy(
                () -> planetService
                        .create(INVALID_PLANET)
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getPlanet_ByExistingId_ReturnsPlanet() {
        when(planetRepository.findById(anyLong())).thenReturn(Optional.of(PLANET));

        Optional<Planet> sut = planetService.get(1L);

        assertThat(sut).isNotEmpty();
        assertEquals(PLANET, sut.get());
    }

    @Test
    public void getPlanet_ByUnexistingId_ReturnsEmpty() {
        when(planetRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Planet> sut = planetService.get(1L);

        assertEquals(Optional.empty(), sut);
    }

    @Test
    public void getPlanet_ByExistingName_ReturnsPlanet() {
        //AAA - Arrange -> Act -> Assert
        when(planetRepository.findByName(anyString())).thenReturn(Optional.of(PLANET));

        Optional<Planet> sut = planetService.getByName(anyString());

        assertThat(sut).isNotEmpty();
        assertEquals(PLANET, sut.get());
    }

    @Test
    public void getPlanet_ByUnexistingName_ReturnsEmpty() {
        when(planetRepository.findByName(anyString())).thenReturn(Optional.empty());

        Optional<Planet> sut = planetService.getByName(anyString());

        assertEquals(Optional.empty(), sut);
    }

    @Test
    void listPlanets_UsesCorrectExample() {
        when(planetRepository.findAll(any()))
                .thenReturn(List.of(PLANET));

        ArgumentCaptor<Example<Planet>> captor =
                ArgumentCaptor.forClass(Example.class);

        planetService.list(PLANET.getTerrain(), PLANET.getClimate());

        verify(planetRepository).findAll(captor.capture());

        Planet probe = captor.getValue().getProbe();
        assertEquals(PLANET.getTerrain(), probe.getTerrain());
        assertEquals(PLANET.getClimate(), probe.getClimate());
    }


    @Test
    public void listPlanets_ReturnsNoPlanets() {
        when(planetRepository.findAll(any())).thenReturn(Collections.emptyList());

        List<Planet> sut = planetService.list(PLANET.getTerrain(), PLANET.getClimate());

        assertThat(sut).isEmpty();
    }

    public void removePlanet_ByExistingId_doesNotThrowAnyException() {
        assertThatCode(() -> planetService.remove(1L)).doesNotThrowAnyException();
    }

    public void removePlanet_ByUnexistingId_doesNotThrowAnyException() {
        doThrow(new RuntimeException()).when(planetRepository).deleteById(anyLong());

        assertThatThrownBy(() -> planetService.remove(anyLong())).isInstanceOf(RuntimeException.class);
    }
}
