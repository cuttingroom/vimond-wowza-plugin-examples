package com.vimond.wms.plugin.livearchive.client.archive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Vimond Media Solution AS
 * @since 2017-12-15
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VimondArchiveInput {

    public static final String INPUT_TYPE = "WOWZA";

    // ---------------------------------------------------------------
    // STATE
    // ---------------------------------------------------------------

    private final String type;
    private final Bucket bucket;
    private final String rootKey;
    private final String masterPlaylistFilename;

    // ---------------------------------------------------------------
    // CONSTRUCTOR AND FACTORY METHODS
    // ---------------------------------------------------------------

    @JsonCreator
    public VimondArchiveInput(
            @JsonProperty("type") String type,
            @JsonProperty("bucket") Bucket bucket,
            @JsonProperty("rootKey") String rootKey,
            @JsonProperty("masterPlaylistFilename") String masterPlaylistFilename
    ) {
        this.type = type;
        this.bucket = bucket;
        this.rootKey = rootKey;
        this.masterPlaylistFilename = masterPlaylistFilename;
    }

    // ---------------------------------------------------------------
    // CORE METHODS
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return "Input{" +
                "type=" + type +
                ", bucket=" + bucket +
                ", rootKey='" + rootKey + '\'' +
                ", masterPlaylistFilename='" + masterPlaylistFilename + '\'' +
                '}';
    }


    // ---------------------------------------------------------------
    // GETTERS AND SETTERS
    // ---------------------------------------------------------------

    public String getType() {
        return type;
    }

    public Bucket getBucket() {
        return bucket;
    }

    public String getRootKey() {
        return rootKey;
    }

    public String getMasterPlaylistFilename() {
        return masterPlaylistFilename;
    }

}
