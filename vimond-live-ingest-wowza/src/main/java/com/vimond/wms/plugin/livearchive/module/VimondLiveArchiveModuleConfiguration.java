package com.vimond.wms.plugin.livearchive.module;

import com.wowza.wms.application.IApplicationInstance;

import java.time.Duration;

/**
 * .
 *
 * @author Kevin Caballero mailto:kevin@vimond.com
 * @since 2018-08-18
 */
public class VimondLiveArchiveModuleConfiguration {

    private String vimondLiveArchiveApiBaseUrl;
    private String vimondLiveArchiveApiTenant;
    private String vimondLiveArchiveApiUsername;
    private String vimondLiveArchiveApiPassword;

    private String vimondLiveArchiveRegion;
    private String vimondLiveArchiveIngestBucketName;
    private String vimondLiveArchiveIngestBucketAccessKey;
    private String vimondLiveArchiveIngestBucketSecret;
    private String vimondLiveArchiveArchiveBucketName;


    private Boolean vimondLiveArchiveEnabled;
    private Duration vimondLiveArchiveChunkDuration;

    private String wowzaPushTargetApiUsername;
    private String wowzaPushTargetApiPassword;

    private String wowzaPushTargetWorkingDirectory;
    private Boolean wowzaPushTargetLocalHousekeeping;

    public VimondLiveArchiveModuleConfiguration(IApplicationInstance appInstance) {
        this.vimondLiveArchiveApiBaseUrl = appInstance.getProperties().getPropertyStr("liveArchiveBaseUrl");

        String archiveTenant = appInstance.getProperties().getPropertyStr("liveArchiveBaseTenant");
        if (archiveTenant == null || archiveTenant.isEmpty()) {
            archiveTenant = appInstance.getApplication().getName();
        }
        this.vimondLiveArchiveApiTenant = archiveTenant;

        this.vimondLiveArchiveApiUsername = appInstance.getProperties().getPropertyStr("liveArchiveBaseUsername");
        this.vimondLiveArchiveApiPassword = appInstance.getProperties().getPropertyStr("liveArchiveBasePassword");

        this.vimondLiveArchiveRegion = appInstance.getProperties().getPropertyStr("pushPublishS3Region");
        this.vimondLiveArchiveIngestBucketName = appInstance.getProperties().getPropertyStr("pushPublishS3Bucket");
        this.vimondLiveArchiveIngestBucketAccessKey = appInstance.getProperties().getPropertyStr("pushPublishS3AccessKey");
        this.vimondLiveArchiveIngestBucketSecret = appInstance.getProperties().getPropertyStr("pushPublishS3AccessSecret");
        this.vimondLiveArchiveArchiveBucketName = appInstance.getProperties().getPropertyStr("pushPublishArchiveS3Bucket");

        this.vimondLiveArchiveEnabled = appInstance.getProperties().getPropertyBoolean("liveArchiveEnabled", false);
        this.vimondLiveArchiveChunkDuration = Duration.parse(appInstance.getProperties().getPropertyStr("liveArchiveDuration"));

        this.wowzaPushTargetApiUsername = appInstance.getProperties().getPropertyStr("pushPublishApiUsername");
        this.wowzaPushTargetApiPassword = appInstance.getProperties().getPropertyStr("pushPublishApiPassword");

        this.wowzaPushTargetWorkingDirectory = appInstance.getProperties().getPropertyStr("pushPublishTempDir", "/tmp/");
        this.wowzaPushTargetLocalHousekeeping = appInstance.getProperties().getPropertyBoolean("housekeepingEnabled", false);
    }

    public String getVimondLiveArchiveApiBaseUrl() {
        return vimondLiveArchiveApiBaseUrl;
    }

    public String getVimondLiveArchiveApiTenant() {
        return vimondLiveArchiveApiTenant;
    }

    public String getVimondLiveArchiveApiUsername() {
        return vimondLiveArchiveApiUsername;
    }

    public String getVimondLiveArchiveApiPassword() {
        return vimondLiveArchiveApiPassword;
    }

    public String getVimondLiveArchiveRegion() {
        return vimondLiveArchiveRegion;
    }

    public String getVimondLiveArchiveIngestBucketName() {
        return vimondLiveArchiveIngestBucketName;
    }

    public String getVimondLiveArchiveIngestBucketAccessKey() {
        return vimondLiveArchiveIngestBucketAccessKey;
    }

    public String getVimondLiveArchiveIngestBucketSecret() {
        return vimondLiveArchiveIngestBucketSecret;
    }

    public String getVimondLiveArchiveArchiveBucketName() {
        return vimondLiveArchiveArchiveBucketName;
    }

    public Boolean getVimondLiveArchiveEnabled() {
        return vimondLiveArchiveEnabled;
    }

    public Duration getVimondLiveArchiveChunkDuration() {
        return vimondLiveArchiveChunkDuration;
    }

    public String getWowzaPushTargetApiUsername() {
        return wowzaPushTargetApiUsername;
    }

    public String getWowzaPushTargetApiPassword() {
        return wowzaPushTargetApiPassword;
    }

    public String getWowzaPushTargetWorkingDirectory() {
        return wowzaPushTargetWorkingDirectory;
    }

    public Boolean getWowzaPushTargetLocalHousekeeping() {
        return wowzaPushTargetLocalHousekeeping;
    }
}
