package com.vimond.wms.plugin.livearchive.client.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * .
 * @author Vimond Media Solution AS
 * @since 2018-04-24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClipRange {

    // ---------------------------------------------------------------
    // STATE
    // ---------------------------------------------------------------


    private Double start;
    private Double end;


    // ---------------------------------------------------------------
    // CONSTRUCTOR AND FACTORY METHODS
    // ---------------------------------------------------------------

    @JsonCreator
    public ClipRange(
            @JsonProperty("start") Double start,
            @JsonProperty("end") Double end
    ) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "ClipRange{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    public Double getStart() {
        return start;
    }

    public Double getEnd() {
        return end;
    }
}
