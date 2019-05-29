package com.vimond.wms.plugin.livearchive.client.auth0;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wowza.wms.logging.WMSLoggerFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.Optional;

/**
 * .
 *
 * @author Vimond Media Solution AS
 * @since 2018-10-11
 */
public class Auth0Client {

    private String domain;
    private CloseableHttpClient client;
    private ObjectMapper mapper = new ObjectMapper();



    public Auth0Client(String domain) {
        this.domain = domain;
        this.client = HttpClients.custom().build();
        this.mapper = new ObjectMapper();
    }


    public Optional<Auth0AccessTokens> login(Auth0Credentials credentials) {
        try {
            HttpPost request = new HttpPost(this.domain + "/oauth/token");
            request.addHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(credentials)));

            HttpResponse response = this.client.execute(request);
            if(response.getStatusLine().getStatusCode() == 401) {
                throw new RuntimeException("Authentication failed. Please check Auth0 configuration.");
            }

            Auth0AccessTokens auth0Token = this.mapper.readValue(response.getEntity().getContent(), Auth0AccessTokens.class);
            if(auth0Token.getAuthorizationHeader() == null) {
                throw new RuntimeException("Auth0Client Authentication failed. Please check Auth0 configuration.");
            }
             return Optional.of(auth0Token);
        } catch (Exception e) {
            String errorMessage = "ModuleVimondLiveArchiver.Auth0Client auth failed for [ " +
                    "domain: " + this.domain + ", " +
                    "audience: " + credentials.getAudience() + ", " +
                    "client_id: " + credentials.getClient_id() + ", " +
                    "client_secret: XXXXXXXXXX" + credentials.getClient_secret().substring(4) + ", " +
                    "grant_type" + credentials.getGrant_type() + ", " +
                    "]";
            WMSLoggerFactory.getLogger(null).error(errorMessage, e);
            throw new RuntimeException(errorMessage);
        }
    }

}
