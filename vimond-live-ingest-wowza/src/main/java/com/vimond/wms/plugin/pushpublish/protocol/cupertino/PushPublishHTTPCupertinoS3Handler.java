package com.vimond.wms.plugin.pushpublish.protocol.cupertino;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.wowza.util.IPacketFragment;
import com.wowza.util.PacketFragmentList;
import com.wowza.wms.manifest.model.m3u8.MediaSegmentModel;
import com.wowza.wms.manifest.model.m3u8.PlaylistModel;
import com.wowza.wms.manifest.model.m3u8.tag.TagModel;
import com.wowza.wms.manifest.writer.m3u8.PlaylistWriter;
import com.wowza.wms.pushpublish.protocol.cupertino.PushPublishHTTPCupertino;
import com.wowza.wms.server.LicensingException;
import com.wowza.wms.util.PushPublishUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;

/**
 * This class implements a new Push target capable of pushing an HLS stream over S3.
 * It is inspired from open source examples from Wowza available at available at
 * https://github.com/WowzaMediaSystems/wse-example-pushpublish-hls
 *
 * This push target sends the HLS stream into an S3 bucket using the following template
 * <streamName>/index.m3u8
 * <streamName>/index/<sequenceNr>_media.m3u8
 * <streamName>/index/chunklist/_<sequenceNr>.ts
 *
 * @author Vimond Media Solution AS
 *
 */
public class PushPublishHTTPCupertinoS3Handler extends PushPublishHTTPCupertino {

	/*
	 * Directory layout is as follow:
	 * <root-dir> is the directory in the map file
	 * <dstStreamName> is gotten from the base implementation
	 * <sessionId> is gotten from the base implementation, this is here in case the incoming stream comes and goes, we don't overwrite media segments
	 *
	 *
	 * <root-dir>/<dstStreamName>/playlist.m3u8 (master playlist)
	 * <root-dir>/<dstStreamName>/chunklist.m3u8 (media playlist)
	 * <root-dir>/<groupName>/playlist.m3u8 (group master playlist)
	 * <root-dir>/<dstStreamName>/<sessionId>/media_x.ts (media segments)
	 *
	 * We reference the media playlists and the media segements using a preceeding "../" such that when the group master playlist references the different
	 * media playlists that represent renditions, it can pick them up from different implementations without modification.
	 *
	 * If this is sending to a backup server, the <dstStreamName> has "-b" appended to the end
	 *
	 */

    private static final String MEDIA_SEQUENCE_TAG = "EXT-X-MEDIA-SEQUENCE";

    private File tempDir = null;
    private Boolean housekeeping = Boolean.TRUE;
    private boolean backup = false;
    private String groupName = null;

    private Optional<String> s3Bucket;
    private Optional<AmazonS3> s3Client;

    public PushPublishHTTPCupertinoS3Handler() throws LicensingException
    {
        super();
    }

    @Override
    public void load(HashMap<String, String> dataMap)
    {
        super.load(dataMap);


        if (parameterStringIsEmpty(this.getEntryName())) {
            this.setEntryName(this.getSrcStreamName());
        } else if (parameterStringIsEmpty(this.getSrcStreamName())) {
            this.setSrcStreamName(this.getEntryName());
        }

        this.setDestinationName("s3");
        this.setDstStreamName("s3-" + this.getSrcStreamName());

        this.s3Bucket = findConfigParameter(dataMap, "bucketName");
        this.s3Client = createS3Client(dataMap);


        Optional<String> housekeepingStr = findConfigParameter(dataMap, "housekeeping");
        if (housekeepingStr.isPresent()) {
            housekeeping = Boolean.valueOf(housekeepingStr.get());
        }

        Optional<String> tempDir = findConfigParameter(dataMap, "workDir");
        if (tempDir.isPresent()) {
            this.tempDir = createTempDir(tempDir.get());
        } else {
            this.tempDir = createTempDir("/tmp/");
        }
    }

    @Override
    public boolean updateGroupMasterPlaylistPlaybackURI(String groupName, PlaylistModel masterPlaylist)
    {
        boolean retVal = true;
        String newPath = "../" + groupName + "/" + masterPlaylist.getUri().getPath();

        try
        {
            masterPlaylist.setUri(new URI(newPath));
            this.groupName = groupName;
        }
        catch (Exception e)
        {
            logError("updateGroupMasterPlaylistPlaybackURI", "Invalid path " + newPath, e);
            retVal = false;
        }
        return retVal;
    }

    @Override
    public boolean updateMasterPlaylistPlaybackURI(PlaylistModel playlist)
    {
        boolean retVal = true;

        String path = (this.backup ? "-b/":"/") + playlist.getUri().toString();

        try
        {
            playlist.setUri(new URI(path));
        }
        catch (URISyntaxException e)
        {
            logError("updateMasterPlaylistPlaybackURI", "Failed to update master playlist to " + path);
            retVal = false;
        }
        return retVal;
    }

    @Override
    public boolean updateMediaPlaylistPlaybackURI(PlaylistModel playlist)
    {
        boolean retVal = true;

        String path = "index/" + playlist.getUri().toString();

        try
        {
            playlist.setUri(new URI(path));
        }
        catch (URISyntaxException e)
        {
            logError("updateMediaPlaylistPlaybackURI", "Failed to update media playlist to " + path);
            retVal = false;
        }
        return retVal;
    }

    @Override
    public boolean updateMediaSegmentPlaybackURI(MediaSegmentModel mediaSegment)
    {
        boolean retVal = true;
        String newPath = mediaSegment.getUri().getPath();

        // to prevent overriding prior segments if the stream were to reset,
        // we'll use the sessionStr to create a sub directory to keep the
        // media segments in.

        try
        {
            String temp = "chunklist/" + newPath.replace("media", "");
            mediaSegment.setUri(new URI(temp));
        }
        catch (Exception e)
        {
            retVal = false;
            logError("updateMediaSegmentPlaybackURI", "Invalid path " + newPath, e);
        }
        return retVal;
    }

    @Override
    public int sendGroupMasterPlaylist(String groupName, PlaylistModel playlist)
    {
        int retVal = 0;
        FileOutputStream output = null;
        try
        {
            String path = getDestionationGroupDir() + "/" + playlist.getUri();
            Path pathToFile = Paths.get(path);
            Files.createDirectories(pathToFile.getParent());

            File playlistFile = new File(path);
            if (!playlistFile.exists())
                playlistFile.createNewFile();

            output = new FileOutputStream(playlistFile, false);  // don't append
            retVal = writePlaylist(playlist, output);
        }
        catch (Exception e)
        {
            logError("sendGroupMasterPlaylist", "Failed to send master playlist to: " + playlist.getUri(), e);
        }
        finally
        {
            if (output != null)
                try {
                    output.flush();
                    output.close();
                } catch (Exception e2)
                {

                };
        }
        return retVal;
    }

    @Override
    public int sendMasterPlaylist(PlaylistModel playlist)
    {
        int retVal = 0;
        FileOutputStream output = null;
        try
        {
            String path = getDestionationDir() + "/" + playlist.getUri();
            Path pathToFile = Paths.get(path);
            Files.createDirectories(pathToFile.getParent());

            File playlistFile = new File(path);
            if (!playlistFile.exists())
                playlistFile.createNewFile();

            output = new FileOutputStream(playlistFile, false);  // don't append
            retVal = writePlaylist(playlist, output);



            if (this.s3Client.isPresent() && this.s3Bucket.isPresent()) {
                String key = getDstStreamName() + "/index.m3u8";
                this.s3Client.get().putObject(this.s3Bucket.get(), key, playlistFile);
            }

            if (housekeeping) {
                playlistFile.delete();
            }

        }
        catch (Exception e)
        {
            logError("sendMasterPlaylist", "Failed to send master playlist to: " + playlist.getUri(), e);
        }
        finally
        {
            if (output != null)
                try {
                    output.flush();
                    output.close();
                } catch (Exception e2)
                {

                };
        }
        return retVal;
    }

    @Override
    public int sendMediaPlaylist(PlaylistModel playlist)
    {
        int retVal = 0;
        FileOutputStream output = null;
        try
        {
            String path = getDestionationDir() + "/" + playlist.getUri();
            Path pathToFile = Paths.get(path);
            Files.createDirectories(pathToFile.getParent());

            File playlistFile = new File(path);
            if (!playlistFile.exists())
                playlistFile.createNewFile();

            output = new FileOutputStream(playlistFile, false);  // don't append
            retVal = writePlaylist(playlist, output);

            if(this.s3Client.isPresent() && this.s3Bucket.isPresent()) {
                String key = getDstStreamName() + "/" + playlist.getUri();
                this.s3Client.get().putObject(this.s3Bucket.get(), key, playlistFile);
            }


            // Now creating the timing file
            Optional<Integer> sequence = extractSequenceNumberFromMediaPlaylist(playlist);
            if (sequence.isPresent()) {
                if(this.s3Client.isPresent() && this.s3Bucket.isPresent()) {
                    String playlist_path = playlist.getUri().getPath();
                    String timing_key = getDstStreamName() + "/" + playlist_path.substring(0, playlist_path.lastIndexOf("/") + 1) + sequence.get() + "_media.m3u8";
                    this.s3Client.get().putObject(this.s3Bucket.get(), timing_key, playlistFile);
                }
            }


            if (housekeeping) {
                playlistFile.delete();
            }


        }
        catch (Exception e)
        {
            logError("sendMediaPlaylist", "Failed to send media playlist to: " + playlist.getUri(), e);
        }
        finally
        {
            if (output != null)
                try {
                    output.flush();
                    output.close();
                } catch (Exception e2)
                {

                };
        }
        return retVal;
    }

    @Override
    public int sendMediaSegment(MediaSegmentModel mediaSegment)
    {
        int retVal = 0;
        FileOutputStream output = null;
        try
        {
            String path = getDestionationDir() + "/" + mediaSegment.getUri();
            Path pathToFile = Paths.get(path);
            Files.createDirectories(pathToFile.getParent());

            File file = new File(path);

            File dir = file.getParentFile();
            if (dir != null && !dir.exists())
                dir.mkdirs();

            if (!file.exists())
                file.createNewFile();

            PacketFragmentList list = mediaSegment.getFragmentList();
            if (list != null)
            {
                output = new FileOutputStream(file, false);

                Iterator<IPacketFragment> itr = list.getFragments().iterator();
                while (itr.hasNext())
                {
                    IPacketFragment fragment = itr.next();
                    if (fragment.getLen() <= 0)
                        continue;
                    byte[] data = fragment.getBuffer();


                    output.write(data);
                    retVal += data.length;
                }
            }
            else
                retVal = 1;  // empty fragment list.

            if (this.s3Client.isPresent() && this.s3Bucket.isPresent()) {
                String key = getDstStreamName() + "/index/" +  mediaSegment.getUri();
                this.s3Client.get().putObject(this.s3Bucket.get(), key, file);
            }

            if (housekeeping) {
                file.delete();
            }

        }
        catch (Exception e)
        {
            logError("sendMediaSegment", "Failed to send media segment data to " + mediaSegment.getUri(), e);
        }
        finally
        {
            if (output != null)
            {
                try
                {
                    output.flush();
                    output.close();
                }
                catch (Exception e)
                {
                }
            }
        }

        return retVal;
    }

    @Override
    public int deleteMediaSegment(MediaSegmentModel mediaSegment)
    {
        int retVal = 0;

        File segment = new File(getDestionationDir() + "/" + mediaSegment.getUri());
        if (segment.exists())
            if (segment.delete())
                retVal = 1;

        return retVal;
    }

    @Override
    public void setSendToBackupServer(boolean backup)
    {
        this.backup = backup;
    }

    @Override
    public boolean isSendToBackupServer()
    {
        return this.backup;
    }

    @Override
    public boolean outputOpen()
    {
        return true;
    }

    @Override
    public boolean outputClose()
    {
        return true;
    }

    @Override
    public String getDestionationLogData()
    {
        File destinationDir = getDestionationDir();
        String retVal = "Invalid Destination " + destinationDir.toString();
        try
        {
            retVal = destinationDir.toURI().toURL().toString();
        }
        catch (MalformedURLException e)
        {
            logError("getDestionationLogData", "Unable to convert " + destinationDir + " to valid path" ,e);
        }

        return retVal;
    }

    private int writePlaylist(PlaylistModel playlist, FileOutputStream output) throws IOException
    {
        int retVal = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PlaylistWriter writer = new PlaylistWriter(out, getContextStr());
        if (writer.write(playlist))
        {
            String outStr = out.toString();
            byte[] bytes = outStr.getBytes();
            output.write(bytes);
            retVal = bytes.length;
        }

        return retVal;
    }

    private File getDestionationDir()
    {
        if (!this.backup)
            return new File(this.tempDir + "/" + getDstStreamName());
        return new File(this.tempDir + "/" + "/" + getDstStreamName()+"-b");
    }

    private File getDestionationGroupDir()
    {
        if (!this.backup)
            return new File(this.tempDir + "/" + this.groupName);
        return new File(this.tempDir + "/" + getDstStreamName()+"-b");
    }

    private Optional<AmazonS3> createS3Client(HashMap<String, String> dataMap) {

        Optional<String> s3Region = findConfigParameter(dataMap, "bucketRegion");
        Optional<String> s3AccessKey = findConfigParameter(dataMap, "bucketKey");
        Optional<String> s3AccessSecret = findConfigParameter(dataMap, "bucketSecret");

        return createS3Client(s3Region, s3AccessKey, s3AccessSecret);
    }

    private Optional<AmazonS3> createS3Client(Optional<String> s3Region, Optional<String> s3AccessKey, Optional<String> s3Secret) {

        if (s3Region.isPresent()) {
            if (s3AccessKey.isPresent() && s3Secret.isPresent()) {
                return Optional.of(AmazonS3ClientBuilder.standard()
                        .withRegion(s3Region.get())
                        .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(s3AccessKey.get(), s3Secret.get())))
                        .build());
            } else {
                return Optional.of(AmazonS3ClientBuilder.standard()
                        .withRegion(s3Region.get())
                        .build());
            }
        }

        return Optional.empty();
    }


    private File createTempDir(String tmpDir){
        logWarn("createTempDir", "createTempDir: "+tmpDir);
        File tempDir = new File(tmpDir);
        logInfo("load", "Using: " + tempDir);
        if (!tempDir.exists())
        {
            tempDir.mkdir();
            logInfo("load", "Created destination folder: " + tempDir);
        }
        return tempDir;
    }

    private Optional<String> findConfigParameter(HashMap<String, String> dataMap, String mapValue) {
        Optional<String> param = Optional.ofNullable(PushPublishUtils.removeMapString(dataMap, mapValue));
        return param;
    }

    private boolean parameterStringIsEmpty(String value) {

        if (value == null || value.isEmpty() || value == "null") {
            return true;
        }
        return false;
    }

    private Optional<Integer> extractSequenceNumberFromMediaPlaylist(PlaylistModel playlist) {

        for (TagModel tag: playlist.tags){
            if (tag.getTag().equals(MEDIA_SEQUENCE_TAG)) {
                return Optional.of(Integer.valueOf(tag.toString().split("=")[1]));
            }
        }

        return Optional.empty();
    }

}
