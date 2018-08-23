package com.vimond.wms.plugin.livearchive.client.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/**
 * .
 * @author Vimond Media Solution AS
 * @since 2018-04-26
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClipRanges {

    // ---------------------------------------------------------------
    // STATE
    // ---------------------------------------------------------------


    private Collection<ClipRange> ranges;


    // ---------------------------------------------------------------
    // CONSTRUCTOR AND FACTORY METHODS
    // ---------------------------------------------------------------

    @JsonCreator
    public ClipRanges(
            @JsonProperty("ranges") Collection<ClipRange> ranges
    ) {
        this.ranges = ranges;
    }


    @Override
    public String toString() {
        return "ClipRanges{" +
                "ranges=" + ranges +
                '}';
    }

    public Collection<ClipRange> getRanges() {
        return ranges;
    }

    public Optional<Instant> getEnd() {

        if (this.ranges != null && !this.ranges.isEmpty()) {
            Iterator<ClipRange> it = this.ranges.iterator();
            ClipRange range = it.next();
            while(it.hasNext()) {
                range = it.next();
            }

            Instant endTime = Instant.ofEpochSecond(Double.valueOf(Math.ceil(range.getEnd())).longValue());

            return Optional.of(endTime);
        }
        return Optional.empty();
    }

}
