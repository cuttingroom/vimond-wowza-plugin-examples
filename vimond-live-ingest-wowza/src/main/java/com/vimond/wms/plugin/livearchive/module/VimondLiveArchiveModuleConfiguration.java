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

    private String vimondLiveArchiveAuth0Tenant;
    private String vimondLiveArchiveAuth0Region;
    private String vimondLiveArchiveAuth0Audience;
    private String vimondLiveArchiveAuth0ClientId;
    private String vimondLiveArchiveAuth0ClientSecret;

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

        this.vimondLiveArchiveAuth0Tenant = appInstance.getProperties().getPropertyStr("auth0Tenant");
        this.vimondLiveArchiveAuth0Region = appInstance.getProperties().getPropertyStr("auth0Region");
        this.vimondLiveArchiveAuth0Audience = appInstance.getProperties().getPropertyStr("auth0Audience");
        this.vimondLiveArchiveAuth0ClientId = appInstance.getProperties().getPropertyStr("auth0ClientId");
        this.vimondLiveArchiveAuth0ClientSecret = appInstance.getProperties().getPropertyStr("auth0ClientSecret");

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

    public String getVimondLiveArchiveAuth0Tenant() {
        return vimondLiveArchiveAuth0Tenant;
    }

    public String getVimondLiveArchiveAuth0Region() {
        return vimondLiveArchiveAuth0Region;
    }

    public String getVimondLiveArchiveAuth0Audience() {
        return vimondLiveArchiveAuth0Audience;
    }

    public String getVimondLiveArchiveAuth0ClientId() {
        return vimondLiveArchiveAuth0ClientId;
    }

    public String getVimondLiveArchiveAuth0ClientSecret() {
        return vimondLiveArchiveAuth0ClientSecret;
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
