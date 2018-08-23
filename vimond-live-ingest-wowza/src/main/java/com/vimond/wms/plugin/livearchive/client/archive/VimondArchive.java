package com.vimond.wms.plugin.livearchive.client.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * .
 * @author Vimond Media Solution AS
 * @since 2018-03-13
 */
public class VimondArchive {

    // ---------------------------------------------------------------
    // STATE
    // ---------------------------------------------------------------

    private final Bucket bucket;

    // ---------------------------------------------------------------
    // CONSTRUCTOR AND FACTORY METHODS
    // ---------------------------------------------------------------

    @JsonCreator
    public VimondArchive(
            @JsonProperty("bucket") Bucket bucket
    ) {
        this.bucket = bucket;
    }

    public static VimondArchive of(Bucket bucket) {
        return new VimondArchive(bucket);
    }

    // ---------------------------------------------------------------
    // CORE METHODS
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return bucket.toString();
    }

    // ---------------------------------------------------------------
    // GETTERS AND SETTERS
    // ---------------------------------------------------------------

    public Bucket getBucket() {
        return bucket;
    }

}

