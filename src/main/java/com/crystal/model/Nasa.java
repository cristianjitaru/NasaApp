package com.crystal.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Nasa {
    private Integer neo_reference_id;
    private String name;
    private String nasa_jpl_url;
    private Double absolute_magnitude_h;
    private boolean is_potentially_hazardous_asteroid;
    private boolean is_sentry_object;

    public Nasa() {
    }

    public Nasa(Integer neo_reference_id, String name, String nasa_jpl_url, Double absolute_magnitude_h, boolean is_potentially_hazardous_asteroid, boolean is_sentry_object) {
        this.neo_reference_id = neo_reference_id;
        this.name = name;
        this.nasa_jpl_url = nasa_jpl_url;
        this.absolute_magnitude_h = absolute_magnitude_h;
        this.is_potentially_hazardous_asteroid = is_potentially_hazardous_asteroid;
        this.is_sentry_object = is_sentry_object;
    }

    public Integer getNeo_reference_id() {
        return neo_reference_id;
    }

    public void setNeo_reference_id(Integer neo_reference_id) {
        this.neo_reference_id = neo_reference_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNasa_jpl_url() {
        return nasa_jpl_url;
    }

    public void setNasa_jpl_url(String nasa_jpl_url) {
        this.nasa_jpl_url = nasa_jpl_url;
    }

    public Double getAbsolute_magnitude_h() {
        return absolute_magnitude_h;
    }

    public void setAbsolute_magnitude_h(Double absolute_magnitude_h) {
        this.absolute_magnitude_h = absolute_magnitude_h;
    }

    public boolean isIs_potentially_hazardous_asteroid() {
        return is_potentially_hazardous_asteroid;
    }

    public void setIs_potentially_hazardous_asteroid(boolean is_potentially_hazardous_asteroid) {
        this.is_potentially_hazardous_asteroid = is_potentially_hazardous_asteroid;
    }

    public boolean isIs_sentry_object() {
        return is_sentry_object;
    }

    public void setIs_sentry_object(boolean is_sentry_object) {
        this.is_sentry_object = is_sentry_object;
    }
}
