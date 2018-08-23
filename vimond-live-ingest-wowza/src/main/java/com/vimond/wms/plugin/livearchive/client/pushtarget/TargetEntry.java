package com.vimond.wms.plugin.livearchive.client.pushtarget;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * .
 *
 * @author Vimond Media Solution AS
 * @since 2018-03-07
 */
public class TargetEntry {

    // ---------------------------------------------------------------
    // STATE
    // ---------------------------------------------------------------

    private final String sourceStreamName;
    private final String entryName;
    private final String profile;

    private final TargetOptions extraOptions;

    // ---------------------------------------------------------------
    // CONSTRUCTOR AND FACTORY METHODS
    // ---------------------------------------------------------------

    @JsonCreator
    public TargetEntry(
            @JsonProperty("sourceStreamName") String sourceStreamName,
            @JsonProperty("entryName") String entryName,
            @JsonProperty("profile") String profile,
            @JsonProperty("extraOptions") TargetOptions extraOptions
            ) {
        this.sourceStreamName = sourceStreamName;
        this.entryName = entryName;
        this.profile = profile;
        this.extraOptions = extraOptions;
    }


    // ---------------------------------------------------------------
    // CORE METHODS
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return "TargetEntry{" +
                "sourceStreamName='" + sourceStreamName + '\'' +
                ", entryName='" + entryName + '\'' +
                ", profile='" + profile + '\'' +
                ", extraOptions=" + extraOptions +
                '}';
    }


    // ---------------------------------------------------------------
    // GETTERS AND SETTERS
    // ---------------------------------------------------------------


    public String getSourceStreamName() {
        return sourceStreamName;
    }

    public String getEntryName() {
        return entryName;
    }

    public String getProfile() {
        return profile;
    }

    public TargetOptions getExtraOptions() {
        return extraOptions;
    }
}
