package com.vimond.wms.plugin.livearchive.client.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * .
 * @author Vimond Media Solution AS
 * @since 2018-03-08
 */
public class Bucket {

    // ---------------------------------------------------------------
    // STATE
    // ---------------------------------------------------------------

    private final String name;
    private final String region;

    // ---------------------------------------------------------------
    // CONSTRUCTOR AND FACTORY METHODS
    // ---------------------------------------------------------------

    @JsonCreator
    public Bucket(
            @JsonProperty("name") String name,
            @JsonProperty("region") String region
    ) {
        Objects.requireNonNull(name);
        this.name = name;
        this.region = region;
    }

    // ---------------------------------------------------------------
    // CORE METHODS
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return "Bucket{" +
                "name='" + name + '\'' +
                ", region='" + region + '\'' +
                '}';
    }


    // ---------------------------------------------------------------
    // GETTERS AND SETTERS
    // ---------------------------------------------------------------

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

}
