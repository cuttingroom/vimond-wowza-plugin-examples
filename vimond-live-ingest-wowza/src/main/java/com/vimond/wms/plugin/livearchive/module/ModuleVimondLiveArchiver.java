package com.vimond.wms.plugin.livearchive.module;

import com.vimond.wms.plugin.livearchive.client.archive.Bucket;
import com.vimond.wms.plugin.livearchive.client.archive.ClipStatus;
import com.vimond.wms.plugin.livearchive.client.archive.VimondArchiveClient;
import com.vimond.wms.plugin.livearchive.client.auth0.Auth0Credentials;
import com.vimond.wms.plugin.livearchive.client.pushtarget.TargetClient;
import com.wowza.wms.amf.AMFPacket;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.stream.IMediaStream;
import com.wowza.wms.stream.IMediaStreamActionNotify2;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * .
 *
 * @author Vimond Media Solution AS
 * @since 2018-03-06
 */
public class ModuleVimondLiveArchiver extends ModuleBase {

    private static final String VIMOND_TARGET_PREFIX = "s3-";

    class StreamInitInfo {

        private String name;
        private Instant startTime;
        private String resource;

        StreamInitInfo(String name, Instant startTime, String resource) {
            this.name = name;
            this.startTime = startTime;
            this.resource = resource;
        }

        public String getName() {
            return name;
        }

        public Instant getStartTime() {
            return startTime;
        }

        public String getResource() {
            return resource;
        }
    }


    Map<IMediaStream, StreamInitInfo> publishers = new HashMap<IMediaStream, StreamInitInfo>();

    class StreamNotify implements IMediaStreamActionNotify2
    {

        public void onPlay(IMediaStream stream, String streamName, double playStart, double playLen, int playReset)
        {
        }

        public void onPause(IMediaStream stream, boolean isPause, double location)
        {
        }

        public void onSeek(IMediaStream stream, double location)
        {
        }

        public void onStop(IMediaStream stream)
        {
        }

        public void onMetaData(IMediaStream stream, AMFPacket metaDataPacket)
        {
        }

        public void onPauseRaw(IMediaStream stream, boolean isPause, double location)
        {
        }

        public void onPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend)
        {
            if (!streamName.startsWith(VIMOND_TARGET_PREFIX)) // this is here to avoid looping pushes
            {
                try
                {

                    IApplicationInstance appInstance = stream.getStreams().getAppInstance();

                    synchronized(publishers)
                    {

                        // Load configuration from application properties
                        VimondLiveArchiveModuleConfiguration config = new VimondLiveArchiveModuleConfiguration(appInstance);

                        // Register new live archive ingest location towards Vimond Live Archive API
                        VimondArchiveClient vimondArchiveClient = createVimondArchiveClient(config);


                        Optional<String> eventResource = vimondArchiveClient.createEvent(
                                new Bucket(config.getVimondLiveArchiveIngestBucketName(), config.getVimondLiveArchiveRegion()),
                                new Bucket(config.getVimondLiveArchiveArchiveBucketName(), config.getVimondLiveArchiveRegion()),
                                VIMOND_TARGET_PREFIX + streamName,
                                streamName
                        );

                        if (eventResource.isPresent()) {

                            // Activate the new push target towards S3
                            TargetClient targetClient = new TargetClient(
                                    config.getWowzaPushTargetApiUsername(),
                                    config.getWowzaPushTargetApiPassword()
                            );
                            targetClient.createStreamTarget(
                                    appInstance.getApplication().getName(),
                                    streamName,
                                    config.getVimondLiveArchiveIngestBucketName(),
                                    config.getVimondLiveArchiveRegion(),
                                    config.getVimondLiveArchiveIngestBucketAccessKey(),
                                    config.getVimondLiveArchiveIngestBucketSecret(),
                                    config.getWowzaPushTargetWorkingDirectory(),
                                    config.getWowzaPushTargetLocalHousekeeping()
                            );

                            // Store startup details
                            StreamInitInfo initInfo = new StreamInitInfo(streamName, Instant.now(), eventResource.get());
                            publishers.put(stream, initInfo);
                        }

                    }


                }
                catch(Exception e)
                {
                    WMSLoggerFactory.getLogger(null).error("ModulePushPublishSimpleExample#StreamNotify.onPublish: "+e.toString());
                }
            }
        }

        public void onUnPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend)
        {
            stopPublisher(stream);
        }
    }

    public void stopPublisher(IMediaStream stream)
    {
        try
        {
            synchronized(publishers) {

                StreamInitInfo streamInitInfo = publishers.remove(stream);

                IApplicationInstance appInstance = stream.getStreams().getAppInstance();

                // Load configuration from application properties
                VimondLiveArchiveModuleConfiguration config = new VimondLiveArchiveModuleConfiguration(appInstance);

                // Delete push target towards S3
                TargetClient targetClient = new TargetClient(
                        config.getWowzaPushTargetApiUsername(),
                        config.getWowzaPushTargetApiPassword()
                );
                targetClient.deleteStreamTarget(appInstance.getApplication().getName(), streamInitInfo.getName());



                // Archiving is activated, we will remove the stream and archive it as VOD
                VimondArchiveClient vimondArchiveClient = createVimondArchiveClient(config);


                if (config.getVimondLiveArchiveEnabled()) {

                    WMSLoggerFactory.getLogger(null).error("Archiving");

                    if (streamInitInfo.getStartTime().isAfter(Instant.now().minus(Duration.ofHours(24)))) {
                        // Do not archive clips longer than 24 hours, those are considered to be linear

                        Integer part = 1;
                        Boolean doneCliping = Boolean.FALSE;
                        Instant clipStartTime = streamInitInfo.getStartTime();

                        while (!doneCliping) {

                            String clipName = streamInitInfo.getName() + ": Part " + part;

                            Optional<ClipStatus> clipStatus = vimondArchiveClient.createClip(
                                    streamInitInfo.getResource(),
                                    clipName,
                                    clipStartTime,
                                    config.getVimondLiveArchiveChunkDuration());

                            doneCliping = Boolean.TRUE;
                            if (clipStatus.isPresent()) {
                                // If the previous clips was not empty, we use the last fragment time as the start of the next chunk for accuracy for accuracy
                                Optional<Instant> accurateEnd = clipStatus.get().getEnd();
                                if(accurateEnd.isPresent()) {
                                    clipStartTime = accurateEnd.get();
                                    part += 1;
                                    doneCliping = Boolean.FALSE;
                                }
                            }

                        }

                    }


                }

                // Unregister event from vimond live archive
                vimondArchiveClient.deleteEvent(streamInitInfo.getResource());


            }

        }
        catch(Exception e)
        {
            WMSLoggerFactory.getLogger(null).error("ModulePushPublishSimpleExample#StreamNotify.onUnPublish: "+e.toString());
        }
    }

    public void onStreamCreate(IMediaStream stream)
    {
        stream.addClientListener(new StreamNotify());
    }

    public void onStreamDestory(IMediaStream stream)
    {
        stopPublisher(stream);
    }

    private static VimondArchiveClient createVimondArchiveClient(VimondLiveArchiveModuleConfiguration config){
        VimondArchiveClient vimondArchiveClient = new VimondArchiveClient(
                config.getVimondLiveArchiveApiBaseUrl(),
                config.getVimondLiveArchiveApiTenant(),
                config.getVimondLiveArchiveAuth0Domain(),
                new Auth0Credentials(
                        config.getVimondLiveArchiveAuth0ClientId(),
                        config.getVimondLiveArchiveAuth0ClientSecret(),
                        config.getVimondLiveArchiveAuth0Audience(),
                        Auth0Credentials.CLIENT_CREDENTIALS_GRANT_TYPE
                )
        );
        return vimondArchiveClient;
    }
}