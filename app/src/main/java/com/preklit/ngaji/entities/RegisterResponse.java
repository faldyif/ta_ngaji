package com.preklit.ngaji.entities;

import com.squareup.moshi.Json;

/**
 * Created by Faldy on 4/17/2018.
 */

public class RegisterResponse {

    @Json(name = "message")
    String message;

    public String getMessage() {
        return message;
    }
}
