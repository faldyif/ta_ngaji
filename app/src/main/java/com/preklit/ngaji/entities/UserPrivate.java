package com.preklit.ngaji.entities;

import com.squareup.moshi.Json;

import java.util.Date;

/**
 * Created by faldyikhwanfadila on 29/05/18.
 */

public class UserPrivate {

    @Json(name = "id")
    Integer id;
    @Json(name = "name")
    String name;
    @Json(name = "whatsapp_number")
    String whatsappNumber;
    @Json(name = "profile_pic_url")
    String profilePicUrl;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getWhatsappNumber() {
        return whatsappNumber;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }
}
