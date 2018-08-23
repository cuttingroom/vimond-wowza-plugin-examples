package com.vimond.wms.plugin.livearchive.client.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;

/**
 * .
 * @author Vimond Media Solution AS
 * @since 2018-03-08
 */
public class Clip {

    private static Duration DEFAULT_DURATION = Duration.ofHours(3);

    // ---------------------------------------------------------------
    // STATE
    // ---------------------------------------------------------------


    private String name;
    private Long start;
    private String duration = DEFAULT_DURATION.toString();


    // ---------------------------------------------------------------
    // CONSTRUCTOR AND FACTORY METHODS
    // ---------------------------------------------------------------

    @JsonCreator
    public Clip(
            @JsonProperty("name") String name,
            @JsonProperty("start") Long start,
            @JsonProperty("duration") String duration
    ) {
        this.name = name;
        this.start = start;
        if (duration == null) {
            this.duration = DEFAULT_DURATION.toString();
        } else {
            this.duration = duration;
        }
    }


    // ---------------------------------------------------------------
    // CORE METHODS
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return "Clip{" +
                "name='" + name + '\'' +
                ", start=" + start +
                ", duration='" + duration + '\'' +
                '}';
    }


    // ---------------------------------------------------------------
    // GETTERS AND SETTERS
    // ---------------------------------------------------------------


    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public Long getStart() {
        return start;
    }

}
