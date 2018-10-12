package com.vimond.wms.plugin.livearchive.client.auth0;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wowza.wms.logging.WMSLoggerFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.Optional;

/**
 * .
 *
 * @author Vimond Media Solution AS
 * @since 2018-10-11
 */
public class Auth0Client {

    private String tenant;
    private String region;
    private CloseableHttpClient client;
    private ObjectMapper mapper = new ObjectMapper();



    public Auth0Client(String tenant, String region) {
        this.tenant = tenant;
        this.region = region;
        this.client = HttpClients.custom().build();
        this.mapper = new ObjectMapper();
    }


    public Optional<Auth0AccessTokens> login(Auth0Credentials credentials) {
        try {
            HttpPost request = new HttpPost("https://" + this.tenant + "." + this.region + ".auth0.com/oauth/token");
            request.addHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(credentials)));

            HttpResponse response = this.client.execute(request);

            Auth0AccessTokens auth0Token = this.mapper.readValue(response.getEntity().getContent(), Auth0AccessTokens.class);

            return Optional.of(auth0Token);
        } catch (Exception e) {
            WMSLoggerFactory.getLogger(null).error(e);
            return Optional.empty();
        }
    }

}
