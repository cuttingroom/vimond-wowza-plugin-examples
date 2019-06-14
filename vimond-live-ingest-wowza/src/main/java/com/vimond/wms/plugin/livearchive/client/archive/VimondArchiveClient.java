package com.vimond.wms.plugin.livearchive.client.archive;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vimond.wms.plugin.livearchive.client.auth0.Auth0AccessTokens;
import com.vimond.wms.plugin.livearchive.client.auth0.Auth0Client;
import com.vimond.wms.plugin.livearchive.client.auth0.Auth0Credentials;
import com.wowza.wms.logging.WMSLoggerFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * .
 * @author Vimond Media Solution AS
 * @since 2018-03-08
 */
public class VimondArchiveClient {

    private CloseableHttpClient client;
    private ObjectMapper mapper = new ObjectMapper();
    private HttpHost target;
    private String baseUrl;
    private String tenant;
    private Optional<Auth0AccessTokens> auth0Token = Optional.empty();



    public VimondArchiveClient(String baseUrl, String tenant, String auth0Domain, Auth0Credentials auth0Credentials) {
        this.tenant = tenant;
        this.baseUrl = baseUrl;

        this.target = HttpHost.create(baseUrl);

        Auth0Client auth0Client = new Auth0Client(auth0Domain);
        this.auth0Token = auth0Client.login(auth0Credentials);

        this.client = HttpClients.custom().build();

        this.mapper = new ObjectMapper();
    }


    public Optional<String> createEvent(Bucket ingestBucket, Bucket archiveBucket, String key, String streamName) {
        try {

            VimondArchiveInput input = new VimondArchiveInput(VimondArchiveInput.INPUT_TYPE, ingestBucket, key, "index.m3u8");
            VimondArchive archive = new VimondArchive(archiveBucket);
            VimondSource source = new VimondSource(streamName, input, archive);

            HttpPost request = new HttpPost(baseUrl + "/tenants/" + this.tenant + "/sources");
            HttpResponse response = getHttpResponse(source, request);
            Header location = response.getFirstHeader("location");
            URI uri = new URI(location.getValue());

            WMSLoggerFactory.getLogger(null).warn("ModuleVimondLiveArchiver.VimondArchiveClient.createEvent: [" + uri.getPath() + "]");
            return Optional.of(uri.getPath());
        } catch (Exception e) {
            WMSLoggerFactory.getLogger(null).error("ModuleVimondLiveArchiver.VimondArchiveClient.createEvent", e);
            return Optional.empty();
        }
    }

    public Optional<ClipStatus> createClip(String resource, String clipName, Instant startTime, Duration archiveDuration) {
        try {

            Clip clip = new Clip(clipName, startTime.toEpochMilli(), archiveDuration.toString());

            URIBuilder builder = new URIBuilder(baseUrl + resource + "/clip");

            HttpPost request = new HttpPost(builder.build());
            HttpResponse response = getHttpResponse(clip, request);
            ClipStatus clipStatus = this.mapper.readValue(response.getEntity().getContent(), ClipStatus.class);

            return Optional.of(clipStatus);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse getHttpResponse(Object object, HttpPost request) throws IOException {

        setHeaders(request);
        request.setEntity(new StringEntity(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object)));

        HttpResponse response = this.client.execute(this.target, request);
        if(response.getStatusLine().getStatusCode() == 401) {
            String error = "ModuleVimondLiveArchiver.VimondArchiveClient Authentication failed. Please check Vimond IO and Auth0 configuration. ";
            throw new RuntimeException(error);
        }
        return response;
    }


    public boolean deleteEvent(String resource) {
        try {
            URIBuilder builder = new URIBuilder(resource);

            HttpDelete request = new HttpDelete(builder.build());
            setHeaders(request);

            HttpResponse response = this.client.execute(this.target, request);
            if(response.getStatusLine().getStatusCode() == 401) {
                WMSLoggerFactory.getLogger(null).error("ModuleVimondLiveArchiver.VimondArchiveClient Authentication failed. Please check Auth0 configuration.");
                return false;
            }

            WMSLoggerFactory.getLogger(null).warn("ModuleVimondLiveArchiver.VimondArchiveClient.deleteEvent: [" + request.getURI().getPath() + "]");

            return true;
        } catch (Exception e) {
            WMSLoggerFactory.getLogger(null).error("ModuleVimondLiveArchiver.VimondArchiveClient.deleteEvent", e);
            return false;
        }
    }

    private void setHeaders(HttpRequestBase request) {
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Accept", "application/json");
        request.addHeader("Authorization", auth0Token.get().getAuthorizationHeader());
    }


}
