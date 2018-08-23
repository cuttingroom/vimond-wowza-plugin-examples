package com.wowza.wms.plugin.test2.hlsprogramdatetime;

import com.wowza.util.ElapsedTimer;
import com.wowza.util.SystemUtils;
import com.wowza.wms.amf.AMFPacket;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.application.WMSProperties;
import com.wowza.wms.httpstreamer.cupertinostreaming.livestreampacketizer.CupertinoPacketHolder;
import com.wowza.wms.httpstreamer.cupertinostreaming.livestreampacketizer.IHTTPStreamerCupertinoLivePacketizerDataHandler2;
import com.wowza.wms.httpstreamer.cupertinostreaming.livestreampacketizer.LiveStreamPacketizerCupertino;
import com.wowza.wms.httpstreamer.cupertinostreaming.livestreampacketizer.LiveStreamPacketizerCupertinoChunk;
import com.wowza.wms.media.mp3.model.idtags.ID3Frames;
import com.wowza.wms.media.mp3.model.idtags.ID3V2FrameTextInformationUserDefined;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.stream.IMediaStream;
import com.wowza.wms.stream.livepacketizer.ILiveStreamPacketizer;
import com.wowza.wms.stream.livepacketizer.LiveStreamPacketizerActionNotifyBase;
import org.apache.commons.lang.time.FastDateFormat;

import java.util.Date;
import java.util.Locale;

/*

This plugin is based on the information available at https://www.wowza.com/docs/how-to-control-display-of-program-date-and-time-headers-in-apple-hls-chunklists-for-live-streams-ext-x-program-date-time

 */

public class ModuleCupertinoProgramDateTime extends ModuleBase
{
    private static final Class<ModuleCupertinoProgramDateTime> CLASS = ModuleCupertinoProgramDateTime.class;
    private static final String CLASSNAME = "ModuleCupertinoProgramDateTime";

    public static final String PROPNAME_TRACKER = "ModuleCupertinoProgramDateTime.ProgramDateTimeTracker";
    public static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'+00:00'"; // The date/time representation is ISO/IEC 8601:2004 - 2010-02-19T14:54:23.031+08:00

    private FastDateFormat fastDateFormat = FastDateFormat.getInstance(DATEFORMAT, SystemUtils.gmtTimeZone, Locale.US);

    private IApplicationInstance appInstance = null;

    class ProgramDateTimeTracker
    {
        long timeOffset = 0;
        IMediaStream stream = null;

        public ProgramDateTimeTracker(IMediaStream stream)
        {
            this.stream = stream;
        }
    }

    class LiveStreamPacketizerDataHandler implements IHTTPStreamerCupertinoLivePacketizerDataHandler2
    {
        private LiveStreamPacketizerCupertino liveStreamPacketizer = null;
        private int textId = 1;
        private String streamName = null;

        public LiveStreamPacketizerDataHandler(LiveStreamPacketizerCupertino liveStreamPacketizer, String streamName)
        {
            this.liveStreamPacketizer = liveStreamPacketizer;
            this.streamName = streamName;
        }

        public ProgramDateTimeTracker getTracker()
        {
            ProgramDateTimeTracker tracker = null;
            while(true)
            {
                IMediaStream stream = appInstance.getStreams().getStream(this.streamName);
                if (stream == null)
                    break;

                WMSProperties props = stream.getProperties();

                synchronized(props)
                {
                    tracker = (ProgramDateTimeTracker)props.getProperty(PROPNAME_TRACKER);
                    if (tracker == null)
                    {
                        tracker = new ProgramDateTimeTracker(stream);
                        props.put(PROPNAME_TRACKER, tracker);
                    }
                }
                break;
            }

            return tracker;
        }

        @Override
        public void onFillChunkStart(LiveStreamPacketizerCupertinoChunk chunk)
        {
            ProgramDateTimeTracker tracker = getTracker();
            if (tracker != null)
            {
                ElapsedTimer elapsedTime = tracker.stream.getElapsedTime();

                long createTime = elapsedTime.getDate().getTime()+tracker.timeOffset;

                String programDateTimeStr = fastDateFormat.format(new Date(createTime));

                chunk.setProgramDateTime(programDateTimeStr);

                ID3Frames id3Header = liveStreamPacketizer.getID3FramesHeader(chunk.getRendition());
                if (id3Header != null)
                {
                    ID3V2FrameTextInformationUserDefined comment = new ID3V2FrameTextInformationUserDefined();

                    comment.setDescription("programDateTime");
                    comment.setValue(programDateTimeStr);

                    id3Header.clear();
                    id3Header.putFrame(comment);
                }
            }
        }

        @Override
        public void onFillChunkEnd(LiveStreamPacketizerCupertinoChunk chunk, long timecode)
        {
            ProgramDateTimeTracker tracker = getTracker();
            if (tracker != null)
                tracker.timeOffset += chunk.getDuration();
        }

        @Override
        public void onFillChunkDataPacket(LiveStreamPacketizerCupertinoChunk chunk, CupertinoPacketHolder holder, AMFPacket packet, ID3Frames id3Frames)
        {
        }

        @Override
        public void onFillChunkMediaPacket(LiveStreamPacketizerCupertinoChunk chunk, CupertinoPacketHolder holder, AMFPacket packet)
        {
        }
    }

    class LiveStreamPacketizerListener extends LiveStreamPacketizerActionNotifyBase
    {
        public void onLiveStreamPacketizerCreate(ILiveStreamPacketizer liveStreamPacketizer, String streamName)
        {
            if (liveStreamPacketizer instanceof LiveStreamPacketizerCupertino)
            {
                getLogger().info(CLASSNAME+"#MyLiveListener.onLiveStreamPacketizerCreate["+((LiveStreamPacketizerCupertino)liveStreamPacketizer).getContextStr()+"]");
                ((LiveStreamPacketizerCupertino)liveStreamPacketizer).setDataHandler(new LiveStreamPacketizerDataHandler((LiveStreamPacketizerCupertino)liveStreamPacketizer, streamName));
            }
        }
    }

    public void onAppStart(IApplicationInstance appInstance)
    {
        this.appInstance = appInstance;

        appInstance.addLiveStreamPacketizerListener(new LiveStreamPacketizerListener());

        getLogger().info(CLASSNAME+".onAppStart["+appInstance.getContextStr()+"]");
    }
}