package com.vimond.wms.plugin.livearchive.module;

import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.logging.WMSLoggerFactory;

import java.time.Duration;

/**
 * .
 *
 * @author Kevin Caballero mailto:kevin@vimond.com
 * @since 2018-08-18
 */
public class VimondLiveArchiveModuleConfiguration {

    private String ioDomain;
    private String ioClientId;
    private Duration ioArchiveChunkDuration;

    private String auth0Domain;
    private String auth0Audience;
    private String auth0ClientId;
    private String auth0ClientSecret;

    private String s3Region;
    private String s3IngestBucketName;
    private String s3AccessKey;
    private String s3SecretKey;
    private String s3ArchiveBucketName;


    private String wowzaPushTargetApiUsername;
    private String wowzaPushTargetApiPassword;

    private String wowzaPushTargetWorkingDirectory;
    private Boolean wowzaPushTargetLocalHousekeeping;

    public VimondLiveArchiveModuleConfiguration(IApplicationInstance appInstance) {
        WMSLogger logger = WMSLoggerFactory.getLogger(null);
        logger.info("VimondLiveArchiveModuleConfiguration.Loading");

        this.ioDomain = appInstance.getProperties().getPropertyStr("ioDomain", null);
        this.ioClientId = appInstance.getProperties().getPropertyStr("ioClientId", null);
        this.ioArchiveChunkDuration = Duration.parse(appInstance.getProperties().getPropertyStr("ioArchiveDuration", "PT3H"));

        this.auth0Domain = appInstance.getProperties().getPropertyStr("auth0Domain");
        this.auth0Audience = appInstance.getProperties().getPropertyStr("auth0Audience");
        this.auth0ClientId = appInstance.getProperties().getPropertyStr("auth0ClientId");
        this.auth0ClientSecret = appInstance.getProperties().getPropertyStr("auth0ClientSecret");

        this.s3Region = appInstance.getProperties().getPropertyStr("s3Region");
        this.s3IngestBucketName = appInstance.getProperties().getPropertyStr("s3IngestBucketName");
        this.s3ArchiveBucketName = appInstance.getProperties().getPropertyStr("s3ArchiveBucketName");
        this.s3AccessKey = appInstance.getProperties().getPropertyStr("s3AccessKey");
        this.s3SecretKey = appInstance.getProperties().getPropertyStr("s3SecretKey");

        this.wowzaPushTargetApiUsername = appInstance.getProperties().getPropertyStr("wowzaPushTargetApiUsername");
        this.wowzaPushTargetApiPassword = appInstance.getProperties().getPropertyStr("wowzaPushTargetApiPassword");
        this.wowzaPushTargetWorkingDirectory = appInstance.getProperties().getPropertyStr("wowzaPushTargetWorkingDirectory", "/tmp/");
        this.wowzaPushTargetLocalHousekeeping = appInstance.getProperties().getPropertyBoolean("wowzaPushTargetLocalHousekeeping", true);

        logger.info("VimondLiveArchiveModuleConfiguration.Loading.complete");
    }

    public String getIoDomain() {
        return ioDomain;
    }

    public String getIoClientId() {
        return ioClientId;
    }

    public String getAuth0Domain() {
        return auth0Domain;
    }

    public String getAuth0Audience() {
        return auth0Audience;
    }

    public String getAuth0ClientId() {
        return auth0ClientId;
    }

    public String getAuth0ClientSecret() {
        return auth0ClientSecret;
    }

    public String getPublishRegion() {
        return s3Region;
    }

    public String getS3IngestBucketName() {
        return s3IngestBucketName;
    }

    public String getS3AccessKey() {
        return s3AccessKey;
    }

    public String getS3SecretKey() {
        return s3SecretKey;
    }

    public String getS3ArchiveBucketName() {
        return s3ArchiveBucketName;
    }

    public Duration getIoArchiveChunkDuration() {
        return ioArchiveChunkDuration;
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
