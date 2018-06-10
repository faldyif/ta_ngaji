package com.preklit.ngaji.entities;

import com.squareup.moshi.Json;

/**
 * Created by faldyikhwanfadila on 02/06/18.
 */

public class CreateResponse {

    @Json(name = "error")
    Boolean error;

    public Boolean getError() {
        return error;
    }
}
