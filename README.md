# Vimond Wowza Streaming Engine Examples

## Getting started

Clone the project:

```
git clone git@github.com:vimond/vimond-wowza-plugin-examples.git
```

### Build the project

You can build the project using gradle commands. Note that the build.gradle
configuration file contains reference to Vimond own maven repository. It is simply used as an example, the project in
itself does not have any dependencies to any Vimond library so any public repository may be used.

```
gradle clean
gradle build
```

Once the custom Wowza modules are built and available in folder `vimond-live-ingest-wowza/build/libs`

## Plugins

This repositories contains plugins and extensions for the Wowza Streaming Server in order to integrate it seamlessly with
Vimond IO Live. It contains the following utilities:


| Name | Type | Description |
|---|---|---|
| PushPublishHTTPCupertinoS3Handler | Push Target | Custom Push Target capable of pushing an HLS stream on S3 |
| ModuleCupertinoProgramDateTime | Module | Wowza module used to insert EXT-X-PROGRAM-DATE-TIME headers in the HLS media playlist |
| ModuleVimondLiveArchiver | Module | Module which automatically setup & clean newly detected live streams into Vimond Live Archive |

More information about how to manage modules is available at https://www.wowza.com/docs/How-to-extend-Wowza-Streaming-Engine-using-Java


For more information on how to install and configure Wowza with the custom module please see https://docs.vimond.io/docs/wowza-streaming-engine
