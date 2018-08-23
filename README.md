# Vimond Wowza Streaming Engine

## Prerequisites

The following tools and libraries must be installed:

* [`docker`](https://www.docker.com/)

For Mac OS users, `docker` can be installed via [`homebrew`](http://brew.sh/):

```
brew update
brew install docker docker-compose docker-machine
```

## Getting started

Clone the project:

```
git clone git@github.com:vimond/vimond-wowza-plugin-examples.git
```

### Build the project

In order to use the docker image, you will need to first build the projet using gradle commands. Note that the build.gradle
configuration file contains reference to Vimond own maven repository. It is simply used as an example, the project in
itself does not have any dependancies to any Vimond library so any public repository may be used.

```
gradle clean
gradle build
```

Once the application itself is built, you can now build the docker image using the command below:

```
docker-compose build
```

### Run the image

The examples here are provided with a Wowza docker image running all the plugins to facilitate tests and discoveries. In
order to start the docker image properly, make sure you update the docker-compose.yml file with valid settings and run:

```
docker-compose up
```

### Start streaming

In order to administrate the wowza streaming server, you can access the administration interface at:

```
http://localhost:8088/enginemanager/Home.htm
```

The live application is configured by default so you can also start streaming by pushing an RTMP stream towards:

```
rtmp://localhost:1935/live/event
```

## Plugins

This repositories contains plugins and extensions for the Wowza Streaming Server in order to integrate it seamlessly with
Vimond IO Live. It contains the following utilities:


| Name | Type | Description |
|---|---|---|
| PushPublishHTTPCupertinoS3Handler | Push Target | Custom Push Target capable of pushing an HLS stream on S3 |
| ModuleCupertinoProgramDateTime | Module | Wowza module used to insert EXT-X-PROGRAM-DATE-TIME headers in the HLS media playlist |
| ModuleVimondLiveArchiver | Module | Module which automatically setup & clean newly detected live streams into Vimond Live Archive |

More information about how to manage modules is available at https://www.wowza.com/docs/How-to-extend-Wowza-Streaming-Engine-using-Java

### Custom S3 Push Target

The class PushPublishHTTPCupertinoS3Handler is a custom Push Target implementation inspired from example providedd by Wowza
at https://github.com/WowzaMediaSystems/wse-example-pushpublish-hls.

It is used to push an incoming stream into HLS format towards S3 in a format compatible with Vimond IO Live ingest format.

The custom Push target may be activated by adding the following information inside the Push Publish Profiles configuration
on the Wowza server available at: /usr/local/WowzaStreamingEngine/conf/PushPublishProfilesCustom.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<Root>
	<PushPublishProfiles>
        <PushPublishProfile>
            <Name>cupertino-s3</Name>
            <Protocol>HTTP</Protocol>
            <BaseClass>com.vimond.wms.plugin.pushpublish.protocol.cupertino.PushPublishHTTPCupertinoS3Handler</BaseClass>
            <UtilClass></UtilClass>
            <Implementation>
                <Name>cupertinos3</Name>
            </Implementation>
            <HTTPConfiguration>
            </HTTPConfiguration
        </PushPublishProfile>
	</PushPublishProfiles>
</Root>
```

In order to activate a new push target, the Push Publish API on the wowza server must be called including the following information:

POST http://localhost:8087/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/live/pushpublish/mapentries/stream

```
{
    "sourceStreamName": "stream name",
    "entryName": "stream name",
    "profile": "cupertino-s3",
    "extraOptions": {
        "bucketName": "s3-bucket-name",
        "bucketRegion": "eu-west-1",
        "bucketKey": "s3-bucket-key",
        "bucketSecret": "s3-bucket-secret",
        "workDir": "/tmp/",
        "housekeeping": false
    }
}
```

While the Push Target is activated through the PushPublishProfilesCustom.xml configuration file, the following wowza module
shall also be activated at the application level for this to work:

```
com.wowza.wms.pushpublish.module.ModulePushPublish
```

### Program Date Time

The program date time module is based on an implementation from Wowza https://www.wowza.com/docs/how-to-control-display-of-program-date-and-time-headers-in-apple-hls-chunklists-for-live-streams-ext-x-program-date-time.
It is used to automatically insert EXT-X-PROGRAM-DATE-TIME information inside the HLS media playlist so that it can be
used as an absolute time refenrece for every segment.

The module must be activated at the application level using the folloing class:

```
com.wowza.wms.plugin.test2.hlsprogramdatetime.ModuleCupertinoProgramDateTime
```

### Vimond Live Archiver

In addition to the Push Publish, the repository also contains an example module called VimondLiveArchiver. This module can
be used to automatically push a live stream to Vimond IO Live as soon as it comes up. The same stream will be automatically
deregistered and archived once it stops.

The module is configured through Wowza properties which are available i the docker template configuration. The module must be
activated at the application level by registering the following class:

```
com.vimond.wms.plugin.livearchive.module.ModuleVimondLiveArchiver
```