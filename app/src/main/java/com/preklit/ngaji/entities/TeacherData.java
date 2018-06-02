package com.preklit.ngaji.entities;

import com.squareup.moshi.Json;

/**
 * Created by faldyikhwanfadila on 02/06/18.
 */

public class TeacherData {

    @Json(name = "id")
    String id;
    @Json(name = "registered_from")
    String registeredFrom;
    @Json(name = "minimum_points")
    String minimumPoints;
    @Json(name = "home_short_name")
    String homeShortName;
    @Json(name = "home_latitude")
    String homeLatitude;
    @Json(name = "home_longitude")
    String homeLongitude;

    public String getId() {
        return id;
    }

    public String getRegisteredFrom() {
        return registeredFrom;
    }

    public String getMinimumPoints() {
        return minimumPoints;
    }

    public String getHomeShortName() {
        return homeShortName;
    }

    public void setHomeShortName(String homeShortName) {
        this.homeShortName = homeShortName;
    }

    public String getHomeLatitude() {
        return homeLatitude;
    }

    public void setHomeLatitude(String homeLatitude) {
        this.homeLatitude = homeLatitude;
    }

    public String getHomeLongitude() {
        return homeLongitude;
    }

    public void setHomeLongitude(String homeLongitude) {
        this.homeLongitude = homeLongitude;
    }
}
