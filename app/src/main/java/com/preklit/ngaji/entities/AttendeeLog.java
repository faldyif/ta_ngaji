package com.preklit.ngaji.entities;

import com.preklit.ngaji.R;
import com.squareup.moshi.Json;

/**
 * Created by faldyikhwanfadila on 30/04/18.
 */

public class AttendeeLog {

    @Json(name = "id")
    Integer id;
    @Json(name = "unique_code")
    String uniqueCode;
    @Json(name = "check_in_time")
    String checkInTime;
    @Json(name = "note_to_student")
    String noteToStudent;
    @Json(name = "note_to_teacher")
    String noteToNextTeacher;
    @Json(name = "points_earned")
    Integer pointsEarned;
    @Json(name = "bonus_points")
    Integer bonusPoints;
    @Json(name = "bonus_reason")
    String bonusReason;

    public AttendeeLog() {
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getNoteToStudent() {
        return noteToStudent;
    }

    public void setNoteToStudent(String noteToStudent) {
        this.noteToStudent = noteToStudent;
    }

    public String getNoteToNextTeacher() {
        return noteToNextTeacher;
    }

    public void setNoteToNextTeacher(String noteToNextTeacher) {
        this.noteToNextTeacher = noteToNextTeacher;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public Integer getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(Integer bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public String getBonusReason() {
        return bonusReason;
    }

    public void setBonusReason(String bonusReason) {
        this.bonusReason = bonusReason;
    }

    public Integer getId() {

        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
