package com.preklit.ngaji.entities;

import android.content.Intent;

import com.preklit.ngaji.R;
import com.squareup.moshi.Json;

import java.util.Date;

/**
 * Created by faldyikhwanfadila on 30/04/18.
 */

public class Event {

    @Json(name = "id")
    Integer id;
    @Json(name = "event_type")
    String eventType;
    @Json(name = "short_place_name")
    String shortPlaceName;
    @Json(name = "latitude")
    Double latitude;
    @Json(name = "longitude")
    Double longitude;
    @Json(name = "start_time")
    String startTime;
    @Json(name = "end_time")
    String endTime;
    @Json(name = "status")
    String status;
    @Json(name = "teacher_rank")
    Integer teacherRank;
    @Json(name = "location_details")
    String locationDetails;
    @Json(name = "points_offered")
    Integer pointsOffered;
    @Json(name = "teacher")
    UserPrivate teacher;
    @Json(name = "student")
    UserPrivate student;

    public Event() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getShortPlaceName() {
        return shortPlaceName;
    }

    public void setShortPlaceName(String shortPlaceName) {
        this.shortPlaceName = shortPlaceName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    public Integer getPointsOffered() {
        return pointsOffered;
    }

    public void setPointsOffered(Integer pointsOffered) {
        this.pointsOffered = pointsOffered;
    }

    public UserPrivate getTeacher() {
        return teacher;
    }

    public void setTeacher(UserPrivate teacher) {
        this.teacher = teacher;
    }

    public UserPrivate getStudent() {
        return student;
    }

    public void setStudent(UserPrivate student) {
        this.student = student;
    }

    public Integer getTeacherRank() {
        return teacherRank;
    }

    public void setTeacherRank(Integer teacherRank) {
        this.teacherRank = teacherRank;
    }

    public String getTeacherRankDescription() {
        switch(teacherRank) {
            case 1:
                return "Pengajar Level Perunggu";
            case 2:
                return "Pengajar Level Perak";
            case 3:
                return "Pengajar Level Emas";
            default:
                break;
        }
        return null;
    }

    public int getTeacherRankMedalColor() {
        switch(teacherRank) {
            case 1:
                return R.color.medal_bronze;
            case 2:
                return R.color.medal_silver;
            case 3:
                return R.color.medal_gold;
            default:
                break;
        }
        return 0;
    }
}
