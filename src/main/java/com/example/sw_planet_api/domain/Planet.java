package com.example.sw_planet_api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.Objects;

@Entity
@Table(name = "planets")
public class Planet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty()
    @Column(nullable = false, unique = true)
    private String name;

    @NotEmpty()
    @Column(nullable = false)
    private String climate;

    @NotEmpty()
    @Column(nullable = false)
    private String terrain;

    public Planet(String climate, String terrain) {
        this.climate = climate;
        this.terrain = terrain;
    }
    public Planet(String name, String climate, String terrain) {
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
    }

    public Planet(Long id, String name, String climate, String terrain) {
        this.id = id;
        this.name = name;
        this.climate = climate;
        this.terrain = terrain;
    }


    public Planet() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClimate() {
        return climate;
    }

    public void setClimate(String climate) {
        this.climate = climate;
    }

    public String getTerrain() {
        return terrain;
    }

    public void setTerrain(String terrain) {
        this.terrain = terrain;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Planet)) return false;
        Planet p = (Planet) o;

        return Objects.equals(name, p.name)
                && Objects.equals(climate, p.climate)
                && Objects.equals(terrain, p.terrain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, climate, terrain);
    }
}