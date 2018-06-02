package com.preklit.ngaji.entities;

import com.preklit.ngaji.R;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.Json;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public Date getDateStart() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public Date getDateEnd() {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
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
