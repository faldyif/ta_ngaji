package com.preklit.ngaji.entities;

import com.squareup.moshi.Json;

import java.util.Date;

/**
 * Created by faldyikhwanfadila on 30/04/18.
 */

public class Event {

    @Json(name = "id")
    Integer id;
    @Json(name = "event_type")
    Integer eventType;
    @Json(name = "short_place_name")
    String shortPlaceName;
    @Json(name = "latitude")
    Double latitude;
    @Json(name = "longitude")
    Double longitude;
    @Json(name = "is_available")
    Boolean isAvailable;
    @Json(name = "start_time")
    Date startTime;
    @Json(name = "end_time")
    Date endTime;

    public boolean section = false;

    public Event() {
    }

    public Integer getId() {
        return id;
    }

    public Integer getEventType() {
        return eventType;
    }

    public String getShortPlaceName() {
        return shortPlaceName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEventType(Integer eventType) {
        this.eventType = eventType;
    }

    public void setShortPlaceName(String shortPlaceName) {
        this.shortPlaceName = shortPlaceName;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setSection(boolean section) {
        this.section = section;
    }
}
