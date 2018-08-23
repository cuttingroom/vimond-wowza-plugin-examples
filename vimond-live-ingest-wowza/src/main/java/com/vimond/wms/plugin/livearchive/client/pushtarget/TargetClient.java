package com.vimond.wms.plugin.livearchive.client.pushtarget;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * .
 *
 * @author Vimond Media Solution AS
 * @since 2018-03-07
 */
public class TargetClient {

    private static String baseUrl = "http://localhost:8087/v2/servers/_defaultServer_/vhosts/_defaultVHost_/applications/";

    private CloseableHttpClient client;
    private HttpHost target;
    private ObjectMapper mapper = new ObjectMapper();


    public TargetClient(String username, String password) {
        this.target = new HttpHost("localhost", 8087, "http");

        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(this.target.getHostName(), this.target.getPort()),
                new UsernamePasswordCredentials(username, password));
        this.client = HttpClients.custom()
                .setDefaultCredentialsProvider(credsProvider)
                .build();
    }

    public boolean createStreamTarget(
            String applicationName,
            String streamName,
            String s3Bucket,
            String s3Region,
            String s3AccessKey,
            String s3Secret,
            String workingDir,
            Boolean localHousekeeping
    ) {
        try {

            AuthCache authCache = new BasicAuthCache();
            // Generate DIGEST scheme object, initialize it and add it to the local
            // auth cache
            DigestScheme digestAuth = new DigestScheme();
            authCache.put(this.target, digestAuth);

            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);

            HttpPost request = new HttpPost(baseUrl + applicationName + "/pushpublish/mapentries/" + streamName);
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Accept", "application/json");

            TargetOptions options = new TargetOptions(
                    s3Bucket,
                    s3Region,
                    s3AccessKey,
                    s3Secret,
                    workingDir,
                    localHousekeeping
            );

            TargetEntry entry = new TargetEntry(
                    streamName,
                    streamName,
                    "cupertino-s3",
                    options
            );

            request.setEntity(new StringEntity(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(entry)));

            this.client.execute(this.target, request, localContext);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteStreamTarget(String applicationName, String streamName) {
        try {

            AuthCache authCache = new BasicAuthCache();
            // Generate DIGEST scheme object, initialize it and add it to the local
            // auth cache
            DigestScheme digestAuth = new DigestScheme();
            authCache.put(this.target, digestAuth);

            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);

            HttpDelete request = new HttpDelete(baseUrl + applicationName + "/pushpublish/mapentries/" + streamName);
            request.addHeader("Content-Type", "application/json");
            request.addHeader("Accept", "application/json");

            this.client.execute(this.target, request, localContext);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
