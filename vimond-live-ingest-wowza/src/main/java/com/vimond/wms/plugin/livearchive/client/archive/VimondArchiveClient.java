package com.vimond.wms.plugin.livearchive.client.archive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wowza.wms.logging.WMSLoggerFactory;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * .
 * @author Vimond Media Solution AS
 * @since 2018-03-08
 */
public class VimondArchiveClient {

    private String baseUrl;
    private CloseableHttpClient client;
    private ObjectMapper mapper = new ObjectMapper();
    private HttpHost target;
    private String tenant;



    public VimondArchiveClient(String baseUrl, String username, String password, String tenant) {
        this.tenant = tenant;
        this.baseUrl = baseUrl;

        this.target = HttpHost.create(baseUrl);


        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(this.target.getHostName(), this.target.getPort()),
                new UsernamePasswordCredentials(username, password));
        this.client = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        this.mapper = new ObjectMapper();
    }


    public Optional<String> createEvent(Bucket ingestBucket, Bucket archiveBucket, String key, String streamName) {
        try {

            VimondArchiveInput input = new VimondArchiveInput(VimondArchiveInput.INPUT_TYPE, ingestBucket, key, "index.m3u8");
            VimondArchive archive = new VimondArchive(archiveBucket);
            VimondSource source = new VimondSource(streamName, input, archive);

            HttpPost request = new HttpPost(baseUrl + "/tenants/" + this.tenant + "/sources");
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Accept", "application/json");


            request.setEntity(new StringEntity(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(source)));

            HttpResponse response = this.client.execute(this.target, request);

            Header location = response.getFirstHeader("location");
            URI uri = new URI(location.getValue());

            return Optional.of(uri.getPath());
        } catch (Exception e) {
            WMSLoggerFactory.getLogger(null).error(e);
            return Optional.empty();
        }
    }

    public Optional<ClipStatus> createClip(String resource, String clipName, Instant startTime, Duration archiveDuration) {
        try {

            Clip clip = new Clip(clipName, startTime.toEpochMilli(), archiveDuration.toString());

            URIBuilder builder = new URIBuilder(baseUrl + resource + "/clip");

            HttpPost request = new HttpPost(builder.build());
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Accept", "application/json");
            request.setEntity(new StringEntity(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(clip)));

            HttpResponse response = this.client.execute(this.target, request);

            ClipStatus clipStatus = this.mapper.readValue(response.getEntity().getContent(), ClipStatus.class);


            return Optional.of(clipStatus);
        } catch (Exception e) {
            WMSLoggerFactory.getLogger(null).error(e);
            return Optional.empty();
        }
    }


    public boolean deleteEvent(String resource) {
        try {


            URIBuilder builder = new URIBuilder(resource);

            HttpDelete request = new HttpDelete(builder.build());
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Accept", "application/json");

            this.client.execute(this.target, request);

            return true;
        } catch (Exception e) {
            WMSLoggerFactory.getLogger(null).error(e);
            return false;
        }
    }


}
