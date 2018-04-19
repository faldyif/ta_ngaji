package com.preklit.ngaji.entities;

import com.squareup.moshi.Json;

/**
 * Created by Faldy on 4/17/2018.
 */

public class AccessToken {

    @Json(name = "token_type")
    String tokenType;
    @Json(name = "expires_in")
    String expiresIn;
    @Json(name = "access_token")
    String accessToken;
    @Json(name = "refresh_token")
    String refreshToken;

    public String getTokenType() {
        return tokenType;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
