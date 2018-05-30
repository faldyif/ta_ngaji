package com.preklit.ngaji.entities;

import com.squareup.moshi.Json;

import java.util.Date;

/**
 * Created by faldyikhwanfadila on 29/05/18.
 */

public class TeacherFreeTime {

    @Json(name = "id")
    Integer id;
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
    @Json(name = "distance")
    Double distance;
    @Json(name = "teacher")
    UserPrivate teacher;
    @Json(name = "teacher_rank")
    Integer teacherRank;
    @Json(name = "points")
    Integer points;

    public Integer getId() {
        return id;
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

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public Double getDistance() {
        return distance;
    }

    public UserPrivate getTeacher() {
        return teacher;
    }

    public Integer getTeacherRank() {
        return teacherRank;
    }

    public Integer getPoints() {
        return points;
    }
}
