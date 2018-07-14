package com.preklit.ngaji.entities;

import com.squareup.moshi.Json;

/**
 * Created by faldyikhwanfadila on 03/07/18.
 */

public class EventModificationRequest {

    @Json(name = "id")
    Integer id;
    @Json(name = "event_id")
    String eventType;
    @Json(name = "start_time")
    String startTime;
    @Json(name = "end_time")
    String endTime;
    @Json(name = "short_place_name")
    String shortPlaceName;
    @Json(name = "latitude")
    Double latitude;
    @Json(name = "longitude")
    Double longitude;
    @Json(name = "request_by_teacher")
    Integer requestByTeacher;
    @Json(name = "request_reason")
    String requestReason;
    @Json(name = "approved")
    Integer approved;
    @Json(name = "approval_datetime")
    String approvalDatetime;
    @Json(name = "approval_reason")
    String approvalReason;
    @Json(name = "created_at")
    String createdAt;
    @Json(name = "updated_at")
    String updatedAt;

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

    public Integer getRequestByTeacher() {
        return requestByTeacher;
    }

    public void setRequestByTeacher(Integer requestByTeacher) {
        this.requestByTeacher = requestByTeacher;
    }

    public String getRequestReason() {
        return requestReason;
    }

    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public Integer getApproved() {
        return approved;
    }

    public void setApproved(Integer approved) {
        this.approved = approved;
    }

    public String getApprovalDatetime() {
        return approvalDatetime;
    }

    public void setApprovalDatetime(String approvalDatetime) {
        this.approvalDatetime = approvalDatetime;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
