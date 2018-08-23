package com.vimond.wms.plugin.livearchive.client.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Optional;

/**
 * .
 * @author Vimond Media Solution AS
 * @since 2018-04-24
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClipStatus {

    // ---------------------------------------------------------------
    // STATE
    // ---------------------------------------------------------------


    private Boolean created;
    private String clipId;
    private ClipRanges ranges;


    // ---------------------------------------------------------------
    // CONSTRUCTOR AND FACTORY METHODS
    // ---------------------------------------------------------------

    @JsonCreator
    public ClipStatus(
            @JsonProperty("created") Boolean created,
            @JsonProperty("clipId") String clipId,
            @JsonProperty("status") ClipRanges ranges
    ) {
        this.created = created;
        this.clipId = clipId;
        this.ranges = ranges;
    }

    @Override
    public String toString() {
        return "ClipStatus{" +
                "created=" + created +
                ", clipId='" + clipId + '\'' +
                ", ranges=" + ranges +
                '}';
    }

    public Boolean getCreated() {
        return created;
    }

    public String getClipId() {
        return clipId;
    }

    public ClipRanges getRanges() {
        return ranges;
    }

    public Optional<Instant> getEnd() {

        if (this.created) {
            return this.ranges.getEnd();
        } else {
            return Optional.empty();
        }

    }

}
