FROM wowzamedia/wowza-streaming-engine-linux:4.7.4

#################################################
# Documentation
#################################################

# https://www.wowza.com/docs/how-to-set-up-wowza-streaming-engine-using-docker


#################################################
# Copy custom libraries / configurations
#################################################

COPY ./docker/lib/*.jar /usr/local/WowzaStreamingEngine/lib/
COPY ./vimond-live-ingest-wowza/build/libs/*.jar /usr/local/WowzaStreamingEngine/lib/

#################################################
# Setup new application
#################################################

COPY ./docker/conf/Application.xml.live /usr/local/WowzaStreamingEngine/conf/live/
COPY ./docker/conf/PushPublishProfilesCustom.xml /usr/local/WowzaStreamingEngine/conf/live/

COPY ./docker/conf/Application.xml.template /usr/local/WowzaStreamingEngine/conf/
COPY ./docker/conf/PushPublishProfilesCustom.xml /usr/local/WowzaStreamingEngine/conf/


#################################################
# Copy custom entrypoint
#################################################
COPY ./docker/scripts/entrypoint.sh /home/wowza/

RUN chmod +x /home/wowza/entrypoint.sh

#################################################
# Port to expose
#################################################
EXPOSE 8088
EXPOSE 1935

#################################################
# Environments variables
#################################################

ENV \
    WSE_IP_PARAM="localhost" \
    WSE_LIC="wowza-license-key" \
    WSE_MGR_USER="admin" \
    WSE_MGR_PASS="admin" \
    INGEST_S3_BUCKET="some-test-bucket" \
    ARCHIVE_S3_BUCKET="some-test-bucket" \
    ARCHIVE_S3_REGION="eu-west-1" \
    ARCHIVE_S3_ACCESS_KEY="s3-access-key" \
    ARCHIVE_S3_ACCESS_SECRET="s3-secret" \
    ARCHIVE_TEMP_DIR="/tmp/" \
    HOUSEKEEPING_ENABLED="true" \
    LIVE_ARCHIVE_ENABLED="true" \
    LIVE_ARCHIVE_TENANT="sandbox" \
    LIVE_ARCHIVE_BASE_URL="" \
    AUTH0_DOMAIN="" \
    AUTH0_AUDIENCE="" \
    AUTH0_CLIENT_ID="" \
    AUTH0_CLIENT_SECRET="" \
    LIVE_ARCHIVE_DURATION="PT3H"

ENTRYPOINT ["/home/wowza/entrypoint.sh"]
