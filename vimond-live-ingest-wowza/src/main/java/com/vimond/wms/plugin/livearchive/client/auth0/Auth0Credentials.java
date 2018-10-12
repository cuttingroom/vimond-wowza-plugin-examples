package com.vimond.wms.plugin.livearchive.client.auth0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * .
 *
 * @author Vimond Media Solution AS
 * @since 2018-10-11
 */
public class Auth0Credentials {

    // ---------------------------------------------------------------
    // STATIC
    // ---------------------------------------------------------------
    public static final String CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";

    // ---------------------------------------------------------------
    // STATE
    // ---------------------------------------------------------------

    private final String client_id;
    private final String client_secret;
    private final String audience;
    private final String grant_type;

    // ---------------------------------------------------------------
    // CONSTRUCTOR AND FACTORY METHODS
    // ---------------------------------------------------------------

    @JsonCreator
    public Auth0Credentials(
            @JsonProperty("client_id") String client_id,
            @JsonProperty("client_secret") String client_secret,
            @JsonProperty("audience") String audience,
            @JsonProperty("grant_type") String grant_type
    ) {
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.audience = audience;
        this.grant_type = grant_type;
    }

    // ---------------------------------------------------------------
    // CORE METHODS
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return "Auth0LoginRequest{" +
                "client_id='" + client_id + '\'' +
                ", client_secret='" + client_secret + '\'' +
                ", audience='" + audience + '\'' +
                ", grant_type='" + grant_type + '\'' +
                '}';
    }


    // ---------------------------------------------------------------
    // CORE METHODS
    // ---------------------------------------------------------------

    // ---------------------------------------------------------------
    // GETTERS AND SETTERS
    // ---------------------------------------------------------------


    public String getClient_secret() {
        return client_secret;
    }

    public String getClient_id() {
        return client_id;
    }

    public String getAudience() {
        return audience;
    }

    public String getGrant_type() {
        return grant_type;
    }
}
