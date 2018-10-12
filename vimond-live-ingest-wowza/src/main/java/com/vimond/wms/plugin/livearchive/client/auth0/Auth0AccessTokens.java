package com.vimond.wms.plugin.livearchive.client.auth0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * .
 *
 * @author Vimond Media Solution AS
 * @since 2018-10-11
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Auth0AccessTokens {

    // ---------------------------------------------------------------
    // STATE
    // ---------------------------------------------------------------

    private final String access_token;
    private final String token_type;

    // ---------------------------------------------------------------
    // CONSTRUCTOR AND FACTORY METHODS
    // ---------------------------------------------------------------

    @JsonCreator
    public Auth0AccessTokens(
            @JsonProperty("access_token") String access_token,
            @JsonProperty("token_type") String token_type
    ) {
        this.access_token = access_token;
        this.token_type = token_type;
    }


    // ---------------------------------------------------------------
    // CORE METHODS
    // ---------------------------------------------------------------

    @Override
    public String toString() {
        return "Auth0LoginResponse{" +
                "access_token='" + access_token + '\'' +
                ", token_type='" + token_type + '\'' +
                '}';
    }


    // ---------------------------------------------------------------
    // CORE METHODS
    // ---------------------------------------------------------------

    // ---------------------------------------------------------------
    // GETTERS AND SETTERS
    // ---------------------------------------------------------------


    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public String getAuthorizationHeader() {
        return this.getToken_type() + " " + this.getAccess_token();
    }
}
