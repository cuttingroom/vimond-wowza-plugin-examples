package com.vimond.wms.plugin.livearchive.client.pushtarget;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * .
 *
 * @author Kevin Caballero mailto:kevin@vimond.com
 * @since 2018-08-21
 */
public class TargetOptions {

    // ---------------------------------------------------------------
    // STATE
    // ---------------------------------------------------------------

    private final String bucketName;

    private final String bucketRegion;
    private final String bucketKey;
    private final String bucketSecret;

    private final String workDir;
    private final Boolean housekeeping;

    // ---------------------------------------------------------------
    // CONSTRUCTOR AND FACTORY METHODS
    // ---------------------------------------------------------------

    @JsonCreator
    public TargetOptions(
            @JsonProperty("bucketName") String bucketName,
            @JsonProperty("bucketRegion") String bucketRegion,
            @JsonProperty("bucketKey") String bucketKey,
            @JsonProperty("bucketSecret") String bucketSecret,
            @JsonProperty("workDir") String workDir,
            @JsonProperty("housekeeping") Boolean housekeeping
    ) {
        this.bucketName = bucketName;
        this.bucketRegion = bucketRegion;
        this.bucketKey = bucketKey;
        this.bucketSecret = bucketSecret;
        this.workDir = workDir;
        this.housekeeping = housekeeping;
    }


    // ---------------------------------------------------------------
    // CORE METHODS
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return "TargetOptions{" +
                "bucketName='" + bucketName + '\'' +
                ", bucketRegion='" + bucketRegion + '\'' +
                ", bucketKey='" + bucketKey + '\'' +
                ", bucketSecret='" + bucketSecret + '\'' +
                ", workDir='" + workDir + '\'' +
                ", housekeeping=" + housekeeping +
                '}';
    }


    // ---------------------------------------------------------------
    // GETTERS AND SETTERS
    // ---------------------------------------------------------------


    public String getBucketName() {
        return bucketName;
    }

    public String getBucketRegion() {
        return bucketRegion;
    }

    public String getBucketKey() {
        return bucketKey;
    }

    public String getBucketSecret() {
        return bucketSecret;
    }

    public String getWorkDir() {
        return workDir;
    }

    public Boolean getHousekeeping() {
        return housekeeping;
    }

}
